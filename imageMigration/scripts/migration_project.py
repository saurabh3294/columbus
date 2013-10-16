#!/usr/bin/env python

from gevent import monkey
monkey.patch_all()

import os
import logging
import requests
import MySQLdb as mysql
from gevent.pool import Pool

# Problems:
# 1. imageType strings in ImageType table and PROJECT_PLAN_IMAGES table doesn't match   [ Solved ]
# 2. objectID i.e. PROJECT_ID is not unique id of the table                             [ Solved ]
# 3. Add '-bkp' to image names, fall back to original name if '-bkp' not found          [ Solved ]

# Configurations
###################################################
env = 'develop'
config = dict(
    processes   =   5,
    objectInfo  =   {
                        'objectType'    :   'project',
                        'imageType'     :   [
                                                'applicationForm',
                                                'clusterPlan',
                                                'constructionStatus',
                                                'layoutPlan',
                                                'locationPlan',
                                                'main',
                                                'masterPlan',
                                                'paymentPlan',
                                                'priceList',
                                                'sitePlan'
                                            ],
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

    # `PROJECT_PLAN_IMAGES` translation
    type_translate = dict(
        main                = 'Project Image', 
        masterPlan          = 'Master Plan', 
        locationPlan        = 'Location Plan', 
        clusterPlan         = 'Cluster Plan', 
        constructionStatus  = 'Construction Status', 
        layoutPlan          = 'Layout Plan', 
        paymentPlan         = 'Payment Plan', 
        sitePlan            = 'Site Plan', 
        priceList           = 'Price List', 
        applicationForm     = 'Application Form'
    )

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
        sql = "SELECT PROJECT_ID, PLAN_IMAGE, TITLE, PROJECT_PLAN_ID FROM `proptiger`.`PROJECT_PLAN_IMAGES` WHERE PLAN_TYPE='%s' AND STATUS='1' AND migration_status!='Done';"
        sql = sql % Object.type_translate[self.imageType]
        Object.cur.execute(sql)
        res = Object.cur.fetchall()
        for i in res:
            img = dict(
                objectType      = self.objectType,
                objectId        = i[0],
                imageType       = self.imageType,
                title           = i[2],
                uniq_id         = i[3],
                addWaterMark    = self.addWaterMark
            )
            # Decide `path`
            path  = i[1]
            img.update(dict(orig_path = config['env'][env]['images_dir'] + path))
            path = Object.add_bkp(path)
            img.update(dict(
                path    = config['env'][env]['images_dir'] + path
            ))
            yield img

    @classmethod
    def update_status(cls, status, obj_id):
        sql = "UPDATE `proptiger`.`PROJECT_PLAN_IMAGES` SET `migration_status` = %s WHERE `PROJECT_PLAN_IMAGES`.`PROJECT_PLAN_ID` = %s;"
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
        p = data['path'].strip()
        if p.startswith('../../images_new'):
            p = p[len('../../images_new'):]
        files['image'] = open(p, 'rb')
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
