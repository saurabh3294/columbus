#!/usr/bin/env python

from gevent import monkey
monkey.patch_all()

import os
import logging
import requests
import MySQLdb as mysql
from gevent.pool import Pool

# Problems:
# 1. objectID i.e. FLOOR_PLAN_ID is not unique id of the table                      [ Solved ]
# 2. Add '-bkp' to image names, fall back to original name if '-bkp' not found      [ Solved ]
# 3. We beleive all the gif images are already watermarked                          [ Solved ]

# Configurations
###################################################
env = 'develop'
config = dict(
    processes   =   5,
    objectInfo  =   {
                        'objectType'    :   'property',
                        'imageType'     :   ['floorPlan'],
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
    table = '`proptiger`.`RESI_FLOOR_PLANS_MIGRATION`'

    def __init__(self, object_type, image_type, add_watermark):
        self.objectType = object_type
        self.imageType = image_type
        self.addWaterMark = add_watermark

    @classmethod
    def add_bkp(cls, path):
        sp = os.path.split(path)
        fname = list(os.path.splitext(sp[1]))
        fname[0] = fname[0] + '-bkp'
        return os.path.join(sp[0], "".join(fname))

    @classmethod
    def create_path(cls, path):
        base = config['env'][env]['images_dir']
        path = path.strip()
        if path.startswith('../../images_new'):
            path = path[len('..'):]
        return base + path

    @property
    def images(self):
        sql = """
            SELECT
                FP.TYPE_ID, FP.IMAGE_URL, FP.FLOOR_PLAN_ID, FP.NAME, FP.DISPLAY_ORDER 
            FROM
            """ + Object.table + """ AS FP INNER JOIN `project`.`resi_project_options` AS PT
                ON FP.TYPE_ID = PT.OPTIONS_ID
            WHERE
                FP.migration_status!='Done';
        """
        Object.cur.execute(sql)
        res = Object.cur.fetchall()
        for i in res:
            # _xyz will not be used as params
            img = dict(
                objectType      = self.objectType,
                objectId        = i[0],
                imageType       = self.imageType,
                title           = i[3],
                priority        = i[4],
                _uniq_id        = i[2],
                _relative_path  = i[1],
                _response       = {}
            )
            # Decide `waterMark` & `path`
            water = self.addWaterMark
            path  = i[1]
            if "floor-plan" not in path:
                water = 'false'
            else:
                img.update(dict(
                    _orig_path = Object.create_path(path)
                ))
                path = Object.add_bkp(path)
                if path.endswith(".gif"):
                    water = 'false'
            img.update(dict(
                _path           = Object.create_path(path),
                addWaterMark    = water
            ))
            yield img

    @classmethod
    def update_status(cls, status, img):
        sql = "UPDATE "+ Object.table +" SET `migration_status` = %s WHERE `FLOOR_PLAN_ID` = %s;"
        Object.cur.execute(sql, (status, img['_uniq_id']))
        if status == 'Done':
            sql = "UPDATE `project`.`resi_floor_plans` SET `SERVICE_IMAGE_ID` = %s WHERE `IMAGE_URL` = %s;"
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
                not_found = True
                if '_orig_path' in img:
                    img['_path'] = img['_orig_path']
                    del img['_orig_path']
                    img['addWaterMark'] = 'false'
                    not_found = False if cls.validate(img) else not_found
                if not_found:
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
