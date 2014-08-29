#!/usr/bin/python
import threading
from time import sleep
import MySQLdb
import subprocess
import os
import sys
import commands
import urllib
import requests
import datetime
import time
import string
import sys
import boto
import boto.s3
import os
from boto.s3.key  import Key
import pprint
import json

# This script will serve the purpose to optimise images for faster load on web 
# using imagemagick library
#
# Various log files are maintained which will have successfully uploaded image information,
# Upload failed images information and similarly successfully deleted and deletion failed
# images information.
#

# Configuration =========================================================
env    = 'develop' # To set develop environment or production environment
config = dict(  
    #url     =  'hssttp://www.proptiger.com/data/v1/entity/image/',
    url  =  'http://localhost:9080/data/v1/entity/image/',
#   idListFile =    'IdsList.txt',
        develop   = {
                            'server'    :   'localhost',
                            'port'      :   '8080',
                            'mysql'     :   {
                                                           'host'      :   'localhost',
                                                           'user'      :   'root',
                                                           'passwd'    :   'root',
                                                           'db'        :   'proptiger'
                                                    }
                    },
       production = {
                                'server'    :   'noida-1.proptiger-ws.com',
                                'port'          :   '8080',
                                'mysql'         :   {
                                                           'host'      :   'noida-1.proptiger-ws.com',
                                                           'user'      :   'qa',
                                                           'passwd'    :   'Proptiger123',
                                                           'db'        :   'proptiger'
                                                    }
                    }
)
logFile = "logImageError.txt" 
#==========================================configuration=============

def logErr(text, logFile, imgData, imgId) :
    text = text + "::" + imgData['objectType'] + ":" + str(imgId) + ":" + str(imgData['objectId']) + ":" + imgData['imageType'] 
    log(text, logFile)

def logUploadSucc(text, logFile, imgData, imgId, r) :
    newId = r.json()['data']['id']
    newObjectId = r.json()['data']['objectId']
    text = text + "::" + imgData['objectType'] + ":" + str(imgId) + ":" + str(imgData['objectId']) + ":"+ imgData['imageType'] + ":" + str(newId) + ":" + str(newObjectId)
    log(text, logFile)

def logDelSucc(text, logFile, imgData, imgId) :
    text = text + "::" + imgData['objectType'] + ":" + str(imgId) + ":" + str(imgData['objectId']) + ":" + imgData['imageType'] 
    log(text, logFile)

def logging(text):
    global logFile
    log(text+"\n", logFile)

def log(text, logFile) :
    f = open(logFile,'ab+')
    f.write(text)
    f.write('\n')
    f.close()
class ThreadPool:
    def __init__(self, workerThreadCount):
        self.__workerThreads = []
        self.__resizeLock = threading.Condition(threading.Lock())
        self.__taskLock = threading.Condition(threading.Lock())
        self.__tasks = []
        self.__isJoining = False
        self.__setWorkerThreads(workerThreadCount)

    def __setWorkerThreads(self, workerThreadCount):
        while workerThreadCount > len(self.__workerThreads):
            newThread = WorkerThread(self)
            logging("worker thread created.")
            self.__workerThreads.append(newThread)
            newThread.start()

        while workerThreadCount < len(self.__workerThreads):
            self.__workerThreads[0].goAway()
            del self.__workerThreads[0]

    def queueTask(self, task, args=None, taskCallback=None):
            if self.__isJoining == True:
                    return False
            if not callable(task):
                    return False
        
            self.__taskLock.acquire()
            try:
                    self.__tasks.append((task, args, taskCallback))
                    return True
            finally:
                    self.__taskLock.release()

    def getNextTask(self):
            self.__taskLock.acquire()
            try:
                    if self.__tasks == []:
                        return (None, None, None)
                    else:
                        return self.__tasks.pop(0)
            finally:
                    self.__taskLock.release()

    def joinAll(self, waitForTasks = True, waitForThreads = True):
            self.__isJoining = True

            if waitForTasks:
                while   self.__tasks != []:
                    sleep(.1)
                
                self.__resizeLock.acquire()
            try:
                self.__setWorkerThreads(0)
                self.__isJoining = True
               
                if waitForThreads:
                    for t in self.__workerThreads:
                        t.join()
                        del t
    
                self.__isJoining = False
            finally:
                self.__resizeLock.release()

class WorkerThread(threading.Thread):
        threadSleepTime = 0.000001

        def __init__(self, pool):
            threading.Thread.__init__(self)
            self.__pool = pool
            self.__isDying = False
        
        def run(self):
            while self.__isDying == False:
                    cmd, args, callback = self.__pool.getNextTask()
                 # If there's nothing to do, just sleep a bit
                    if cmd is None:
                        sleep(WorkerThread.threadSleepTime)
                    elif callback is None:
                        cmd(args)
                    else:
                        callback(cmd(args))
        def goAway(self):
            self.__isDying = True




if __name__ == "__main__":
    logging("Image Optimisation Script")
    logging("===========================")
    

    # Create a pool with three worker threads
    pool = ThreadPool(29) 
    
    def task(data):
        cmd = ['identify', data[1]]
        output = subprocess.Popen(cmd, stdout=subprocess.PIPE ).communicate()[0]
        logging(output)
        length=string.find(data[1],".")
        imageName = data[1][0:length]
        extension = data[1][length:]
        width = data[10]
        height = data[11]
        #a hack because we know that quality can be only one of these
        quality=95

        if (data[10]*data[11]>520*400):
            quality=str(65)+"%"
        else: 
                    quality=str(70)+"%"
            
        newImgName = imageName+'-o'+extension
        resolutionStr = str(width)+'X'+str(height)
        tmp = ['convert', data[1], '-resize', resolutionStr ,'-strip', '-interlace', 'plane', '-quality', quality,newImgName ]
        logging(json.dumps(tmp))
        subprocess.Popen(tmp, stdout=subprocess.PIPE ).communicate()[0]
        logging("\n** new imagename is **\n")
        logging(newImgName)
        #os.remove(data[1])
        #change it to upload 
        uploadImg(data, newImgName) 


    def uploadImg(data, newImgName):      
        try:
            logging("\ninside upload imag\n")
            s3url = "s3://im.proptiger.com/"
            path = data[14]
            s3bucket = s3url+path
            s3command = ["s3cmd", "put", newImgName, s3bucket]
            logging("\n IMAGE UPLOADED PATH "+s3bucket+","+newImgName)
            p = subprocess.Popen(s3command, stdout=subprocess.PIPE).communicate()[0]
            status = p.poll()
            if(status < 0):
                logging("\n ERROR "+s3bucket+","+newImgName)
            logging(json.dumps(s3command))

            #if isinstance(t, int ) :
            #   logging("inside if"                 )
            #   k.make_public()
            #                    
            #   logUploadSucc("Upload Success", 'UploadSuccess.txt', data[1])
            #   logging("uploaded sucessfully")
            #   return 1
            #else :
            #   logErr("Upload Failed", 'UploadFailed.txt',data[1])
            #   logging("upload failed")
            #   return -1

        except Exception: 
                return -1

    def get_param(data) :
        params = {}
        params['objectId'] = data[3]        
        params['imageType'] = data[6]
        params['objectType'] = data[7]      
        params['addWaterMark'] = 'false'
        params['migration_status'] = 'Done'
        if data[4] is not None :
            params['altText'] = data[4]
        if data[5] is not None :
            params['priority'] = data[5]
        if data[8] is not None :
            params['title'] = data[8]
        if data[9] is not None :
            dt = data[9]
            mdt = dt - datetime.timedelta(0,5*60*60 + 30*60)
            dtstr = mdt.strftime('%Y-%m-%dT%H:%M:%SZ')
            params['takenAt'] = dtstr
            return params
    

    
    # Open database connection
    db = MySQLdb.connect(**config[env]['mysql'] )

    # prepare a cursor object using cursor() method
    cursor = db.cursor()

        #cursor.execute("SELECT I.id, concat('http://im.proptiger.com.s3.amazonaws.com/',I.path, I.original_name),I.original_name, I.object_id, I.alt_text AS altText, I.priority, IT.type AS imageType, OT.type AS objectType, I.title, I.taken_at AS takenAt ,I.width AS width,I.height as height FROM Image I JOIN ImageType IT ON (I.ImageType_id = IT.id) JOIN ObjectType OT ON (IT.ObjectType_id = OT.id) WHERE ((I.active = 1) AND (I.object_id IN (1,2) OR I.id IN (301647,301889,494492742,422386,375840,338627,301647))) ORDER BY I.ID")
        

    cursor.execute("SELECT I.id, concat('http://im.proptiger.com.s3.amazonaws.com/',I.path,I.id),I.original_name, I.object_id, I.alt_text AS altText, I.priority, IT.type AS imageType, OT.type AS objectType, I.title, I.taken_at AS takenAt, I.path, concat('http://im.proptiger.com.s3.amazonaws.com/',I.path,I.watermark_name) FROM Image I JOIN ImageType IT ON (I.ImageType_id = IT.id) JOIN ObjectType OT ON (IT.ObjectType_id = OT.id) JOIN RESI_PROJECT RP ON (RP.city_id in (" + sys.argv[1] + ") AND RP.ACTIVE = 1) JOIN RESI_PROJECT_TYPES RPT ON (RP.PROJECT_ID=RPT.PROJECT_ID  AND (I.object_id=RP.PROJECT_ID OR I.object_id=RPT.TYPE_ID)) WHERE (I.active = 1 AND I.migration_status!='Done') GROUP BY I.id ORDER BY I.ID ")

    #cursor.execute(" SELECT I.id, concat('http://im.proptiger.com.s3.amazonaws.com/',I.path, I.original_name),I.original_name, I.object_id, I.alt_text AS altText, I.priority, IT.type AS imageType, OT.type AS objectType, I.title, I.taken_at AS takenAt FROM Image I JOIN ImageType IT ON (I.ImageType_id = IT.id) JOIN ObjectType OT ON (IT.ObjectType_id = OT.id) WHERE I.active = 1 AND (I.object_id IN (1,2 ) OR (I.id IN ((301647,301889,495342,422386,375840,338627,301647)) )) AND NOT(I.migration_status='Done')  ORDER BY I.ID")


    
    rows = cursor.fetchall()
    cols = []
    count = 0
    totalImages = 0
    logging("****************Start***************")
    logging(" FOUND "+str(len(rows)))
    for row in rows:
        for col in row:
            cols.append(col)
        imId = cols.pop(0)
        logging(str(imId))
        url = cols.pop(0)
        img = cols.pop(0)
        logging(img)
        length = string.find(img,".")
        extension = img[length:]
        objectId = cols.pop(0)
        altText = cols.pop(0)
        priority = cols.pop(0)
        imageType = cols.pop(0)
        objectType = cols.pop(0)
        title = cols.pop(0)
        takenAt = cols.pop(0)
        path = cols.pop(0)
        oldImageUrl = cols.pop(0)
        logging("path == "+path)
        resolutions = ['130-100','360-270','520-400','1336-768','220-120','280-200','320-220','360-240','420-280','480-320','520-340','1040-780','380-280','940-720','680-580','800-620','940-720','520-400'] 
        #['-130-100','-360-270','-520-400','-1336-768']
        resolutions= [130,100,360,270,520,400,1336,768,220,120,280,200,320,220,360,240,420,280,480,320,520,340,1040,780,380,280,940,720,680,580,800,620,940,720,520,400]#[130,100,360,270,520,400,1336,768]
        hyphon='-'
        count = count + 1
        logging(" TOTAL  DOMAINS "+str(count))
        i=0;
        
        try:
            while(i<18):
                totalImages = totalImages + 1
                width=resolutions[2*i]
                height=resolutions[2*i+1]
                logging("**printing resolutions url ** ")
                newUrl=str(url)+hyphon+str(width)+hyphon+str(height)+extension
                logging(str(newUrl))
                data = requests.get(str(oldImageUrl))
                imgId=str(imId)+hyphon+str(width)+hyphon+str(height)+extension
                logging(" DOWNLOAD IMAGE URL"+oldImageUrl)
                with open(imgId, 'wb') as f:
                    for chunk in data.iter_content(1024):
                         f.write(chunk)
                #imgId=imgId+hyphon+str(width)+hyphon+str(height)+extension
                returnVal= pool.queueTask(task, (img, imgId, newUrl, objectId, altText, priority, imageType, objectType, title, takenAt,width,height,db,cursor,path,width,height), None)
                logging(" \nprinting return value \n")
                #logging(returnVal)
                if(returnVal==-1):
                    break
                if(i==3):
                    query = "UPDATE proptiger.Image SET migration_status='Done' WHERE Image.id="+str(imId)
                    logging(query)
                    cursor.execute("UPDATE proptiger.Image SET migration_status='Done' WHERE Image.id=%s",imId)
                db.commit()
                logging(" TOTAL IMAGES "+str(totalImages))
                i=i+1
        except IOError as e:
            logging("I/O error({0}): {1}".format(e.errno, e.strerror))

        logging("done")
    # When all tasks are finished, allow the threads to terminate
    pool.joinAll()
