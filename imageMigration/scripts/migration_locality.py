#!/usr/bin/env python

from gevent import monkey
monkey.patch_all()

import os
import logging
import requests
import MySQLdb as mysql
from gevent.pool import Pool

# Problems
# 1. Handle Null in IMAGE_CATEGORY                      [ Solved ] - Automatically will not be selected
# 2. Locality_Images has multiple images per object_id  [ Solved ]

# Configurations
###################################################
env = 'develop'
config = dict(
    processes   =   5,
    objectInfo  =   {
                        'objectType'    :   'locality',
                        'imageType'     :   ['mall', 'road', 'school', 'hospital', 'other'],
                        'addWaterMark'  :   'true'
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
    # Object table
    table = '`proptiger`.`LOCALITY_IMAGE_MIGRATION`'

    def __init__(self, object_type, image_type, add_watermark):
        self.objectType = object_type
        self.imageType = image_type
        self.addWaterMark = add_watermark

    @property
    def images(self):
        sql = "SELECT LOCALITY_ID, CONCAT('/locality/', IMAGE_NAME), IMAGE_ID, IMAGE_DISPLAY_NAME, IMAGE_DESCRIPTION FROM "+ Object.table +" WHERE IMAGE_CATEGORY='%s' AND migration_status!='Done';" % self.imageType
        Object.cur.execute(sql)
        res = Object.cur.fetchall()
        for i in res:
            # _xyz will not be used as params
            img = dict(
                objectType      = self.objectType,
                objectId        = i[0],
                imageType       = self.imageType,
                addWaterMark    = self.addWaterMark,
                title           = i[3],
                description     = i[4],
                _relative_path  = i[1],
                _path           = config['env'][env]['images_dir'] + i[1],
                _uniq_id        = i[2],
                _response       = {}
            )
            yield img

    @classmethod
    def update_status(cls, status, img):
        sql = "UPDATE "+ Object.table +" SET `migration_status` = %s WHERE `LOCALITY_IMAGE`.`IMAGE_ID` = %s;"
        Object.cur.execute(sql, (status, img['_uniq_id']))
        if status == 'Done':
            sql = "UPDATE `project`.`locality_image` SET `SERVICE_IMAGE_ID` = %s WHERE `IMAGE_NAME` = %s;"
            Object.cur.execute(sql, (img['_response']['data']['id'], img['_relative_path']))


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
        path = img['_path']
        return os.path.isfile(path) and os.access(path, os.R_OK)

    @classmethod
    def get_params(cls, d):
        # _xyz will be dropped
        params = dict([ (k, v) for k,v in d.iteritems() if not k.startswith('_') ])
        return params

    @classmethod
    def post(cls, img):
        data, files = cls.get_params(img), {}
        files['image'] = open(img['_path'], 'rb')
        r = requests.post(cls.url, files = files, data = data)
        if not r.json()['statusCode'].startswith('2'): # Temporary Check
            raise Exception('Error ! :: %s' % r.json())
        img['_response'] = r.json()

    @classmethod
    def acknowledge(cls, img, status):
        ack = dict(text = status['text'])
        ack.update(img)
        # Update database
        Object.update_status(status['text'], img)
        # Log
        text = "%(objectType)s :: %(objectId)s :: %(imageType)s :: %(_path)s :: %(text)s" % ack
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
