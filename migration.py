#!/usr/bin/env python

import os
import logging
import requests
import MySQLdb as mysql
import multiprocessing

# Configs
config = dict(
    processes   =   5,
    server      =   'noida-1.proptiger-ws.com',
    port        =   '8080',
    images_dir  =   '/home/sysadmin/public_html/images',
    mysql       =   {
                        'host'      :   '172.16.1.7',
                        'user'      :   'proptigeruser',
                        'passwd'    :   'proptigeruser@10',
                        'db'        :   'proptiger'
                    },
    objectInfo  =   {
                        'objectType'    :   'builder',
                        'imageType'     :   'logo',
                        'waterMarkFlag' :   'false'
                    }
)

# Logging
logger = logging.getLogger('migration')
handler = logging.StreamHandler()
handler.setFormatter(logging.Formatter(fmt='%(levelname)s :: %(message)s'))
logger.addHandler(handler)
logger.setLevel(logging.INFO)

# Object Class
class Object(object):
    conn = mysql.connect(**config['mysql'])
    cur = conn.cursor()
    conn.autocommit(True)

    def __init__(self, object_type, image_type, watermark_flag):
        self.objectType = object_type
        self.imageType = image_type
        self.waterMarkFlag = watermark_flag

    @property
    def images(self):
        sql = "SELECT BUILDER_ID, BUILDER_IMAGE FROM `proptiger`.`RESI_BUILDER` WHERE migration_status!='Done';"
        Object.cur.execute(sql)
        res = Object.cur.fetchall()
        for i in res:
            img = dict(
                objectType      = self.objectType,
                objectId        = i[0],
                imageType       = self.imageType,
                path            = config['images_dir'] + i[1],
                waterMarkFlag   =   self.waterMarkFlag
            )
            yield img

    @classmethod
    def update_status(cls, status, obj_id):
        sql = "UPDATE `proptiger`.`RESI_BUILDER` SET `migration_status` = %s WHERE `RESI_BUILDER`.`BUILDER_ID` = %s;"
        Object.cur.execute(sql, (status, obj_id))


# Upload Class
class Upload(object):
    url = 'http://%(server)s:%(port)s/data/v1/entity/image' % config
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
    pool = multiprocessing.Pool(processes=config['processes'])
    obj = Object(config['objectInfo']['objectType'], config['objectInfo']['imageType'], config['objectInfo']['waterMarkFlag'])
    map(Upload(), obj.images)
