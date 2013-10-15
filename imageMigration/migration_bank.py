#!/usr/bin/env python

from gevent import monkey
monkey.patch_all()

import os
import logging
import requests
import MySQLdb as mysql
from gevent.pool import Pool

# Configurations
###################################################
env = 'develop'
config = dict(
    processes   =   5,
    objectInfo  =   {
                        'objectType'    :   'bank',
                        'imageType'     :   ['logo'],
                        'addWaterMark'  :   'false'
                    },
    env         =   {
                        'develop'   :   {
                                            'server'    :   'localhost',
                                            'port'      :   '8080',
                                            'images_dir':   '/home/yugal/Desktop',
                                            'mysql'     :   {
                                                                'host'      :   'localhost',
                                                                'user'      :   'root',
                                                                'passwd'    :   'root',
                                                                'db'        :   'proptiger'
                                                            }
                                        },
                        'production':   {
                                            'server'    :   'noida-1.proptiger-ws.com',
                                            'port'      :   '8080',
                                            'images_dir':   '/home/sysadmin/public_html/images',
                                            'mysql'     :   {
                                                                'host'      :   '172.16.1.7',
                                                                'user'      :   'proptigeruser',
                                                                'passwd'    :   'proptigeruser@10',
                                                                'db'        :   'proptiger'
                                                            }
                                        }
                    }
)
###################################################

# Logging
logger = logging.getLogger('migration')
handler = logging.StreamHandler()
handler.setFormatter(logging.Formatter(fmt='%(levelname)s :: %(message)s'))
logger.addHandler(handler)
logger.setLevel(logging.INFO)

# Object Class
class Object(object):
    conn = mysql.connect(**config['env'][env]['mysql'])
    cur = conn.cursor()
    conn.autocommit(True)

    def __init__(self, object_type, image_type, add_watermark):
        self.objectType = object_type
        self.imageType = image_type
        self.addWaterMark = add_watermark

    @property
    def images(self):
        sql = "SELECT BANK_ID, CONCAT('/bank_list/', BANK_LOGO) FROM `proptiger`.`BANK_LIST` WHERE migration_status!='Done';"
        Object.cur.execute(sql)
        res = Object.cur.fetchall()
        for i in res:
            img = dict(
                objectType      = self.objectType,
                objectId        = i[0],
                imageType       = self.imageType,
                path            = config['env'][env]['images_dir'] + i[1],
                addWaterMark    = self.addWaterMark
            )
            yield img

    @classmethod
    def update_status(cls, status, obj_id):
        sql = "UPDATE `proptiger`.`BANK_LIST` SET `migration_status` = %s WHERE `BANK_LIST`.`BANK_ID` = %s;"
        Object.cur.execute(sql, (status, obj_id))


# Upload Class
class Upload(object):
    url = 'http://%(server)s:%(port)s/data/v1/entity/image' % config['env'][env]
    status = dict(
        done = dict(text='Done', log=logger.info),
        failed = dict(text='Failed', log=logger.error),
        not_found = dict(text='Not Found', log=logger.warn)
    )

    @classmethod
    def validate(cls, img):
        path = img['path']
        return os.path.isfile(path) and os.access(path, os.R_OK)

    @classmethod
    def post(cls, img):
        data, files = img.copy(), {}
        files['image'] = open(data['path'], 'rb')
        del data['path']
        r = requests.post(cls.url, files = files, data = data)
        if not r.json()['statusCode'].startswith('2'): # Temporary Check
            raise Exception('Error ! :: %s' % r.json())

    @classmethod
    def acknowledge(cls, img, status):
        ack = dict(text = status['text'])
        ack.update(img)
        # Update database
        Object.update_status(status['text'], img['objectId'])
        # Log
        text = "%(objectType)s :: %(objectId)s :: %(imageType)s :: %(path)s :: %(text)s" % ack
        log = status['log']
        log(text)

    @classmethod
    def upload(cls, img):
        try:
            if not cls.validate(img):
                cls.acknowledge(img, cls.status['not_found'])
                return
            cls.post(img)
            cls.acknowledge(img, cls.status['done'])
        except Exception, e:
            logger.debug(e)
            cls.acknowledge(img, cls.status['failed'])

    def __call__(self, arg):
        Upload.upload(arg)


# Main
if __name__ == '__main__':
    pool = Pool(config['processes'])
    for t in config['objectInfo']['imageType']:
        obj = Object(config['objectInfo']['objectType'], t, config['objectInfo']['addWaterMark'])
        pool.map(Upload(), obj.images)
