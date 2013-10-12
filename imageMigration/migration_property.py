#!/usr/bin/env python

import os
import logging
import requests
import MySQLdb as mysql
import multiprocessing

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

    @property
    def images(self):
        sql = "SELECT TYPE_ID, IMAGE_URL, FLOOR_PLAN_ID, NAME, DISPLAY_ORDER FROM `proptiger`.`RESI_FLOOR_PLANS` WHERE migration_status!='Done';"
        Object.cur.execute(sql)
        res = Object.cur.fetchall()
        for i in res:
            img = dict(
                objectType      = self.objectType,
                objectId        = i[0],
                imageType       = self.imageType,
                uniq_id         = i[2],
                title           = i[3],
                priority        = i[4],
            )
            # Decide `waterMark` & `path`
            water = self.addWaterMark
            path  = i[1]
            if "floor-plan" not in path:
                water = 'false'
            else:
                img.update(dict(orig_path = config['env'][env]['images_dir'] + path))
                path = Object.add_bkp(path)
                if path.endswith(".gif"):
                    water = 'false'
            img.update(dict(
                path            = config['env'][env]['images_dir'] + path,
                addWaterMark    = water
            ))
            yield img

    @classmethod
    def update_status(cls, status, obj_id):
        sql = "UPDATE `proptiger`.`RESI_FLOOR_PLANS` SET `migration_status` = %s WHERE `RESI_FLOOR_PLANS`.`FLOOR_PLAN_ID` = %s;"
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
        Object.update_status(status['text'], img['uniq_id'])
        # Log
        text = "%(objectType)s :: %(objectId)s :: %(imageType)s :: %(path)s :: %(text)s" % ack
        log = status['log']
        log(text)

    @classmethod
    def upload(cls, img):
        try:
            if not cls.validate(img):
                not_found = True
                if 'orig_path' in img:
                    img['path'] = img['orig_path']
                    del img['orig_path']
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
    pool = multiprocessing.Pool(processes=config['processes'])
    for t in config['objectInfo']['imageType']:
        obj = Object(config['objectInfo']['objectType'], t, config['objectInfo']['addWaterMark'])
        pool.map(Upload(), obj.images)
