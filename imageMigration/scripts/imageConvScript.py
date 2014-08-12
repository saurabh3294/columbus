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

# This script will serve the purpose to convert images with CMYK colorspace to sRGB colorspace
# by using the ImageMagick library. 
#
# IdsList.txt: Input text file which will have all the Image IDs separated by comma 
# and they will be taken as input image IDs whose image to be converted.
#
# An SQL query is executed that will provide all the image informations required, then 
# image colorspace is checked whether it is CMYK or sRGB, if it is CMYK, then it will
# be converted to sRGB colorspace and then uploaded else in other case image will be
# uploaded without conversion.
#
# Various log files are maintained which will have successfully uploaded image information,
# Upload failed images information and similarly successfully deleted and deletion failed
# images information.
#

# Configuration =========================================================
env    = 'develop' # To set develop environment or production environment
config = dict(	
	url	 =	'http://www.proptiger.com/data/v1/entity/image/',
	#url	 = 	'http://localhost:8080/data/v1/entity/image/',
	idListFile = 	'IdsList.txt',
        develop   =	{
                         	'server'	:   'localhost',
                        	'port'		:   '9080',
                        	'mysql'		:   {
                                                           'host'      :   'localhost',
                                                           'user'      :   'root',
                                                           'passwd'    :   'root',
                                                           'db'        :   'proptiger'
                                               	    }
                   	},
       production =	{
                            	'server'   	:   'noida-1.proptiger-ws.com',
                            	'port'      	:   '8080',
                             	'mysql'     	:   {
                                                           'host'      :   'noida-1.proptiger-ws.com',
                                                           'user'      :   'qa',
                                                           'passwd'    :   'Proptiger123',
                                                           'db'        :   'proptiger'
                                                    }
                  	}
)

#==========================================configuration=============

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
			print "worker thread created."
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
            		while	self.__tasks != []:
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
	print "Image Conversion Script"
	print "==========================="
	

	# Create a pool with three worker threads
	pool = ThreadPool(4) 
	CMYK_IMG = 'CMYK'
	
	def task(data):
		cmd = ['identify', data[0]] # indentifying image using imageMagick software
		output = subprocess.Popen( cmd, stdout=subprocess.PIPE ).communicate()[0]
		print output
		if CMYK_IMG in output:
			tmp = ['convert', data[0], '-colorspace', 'sRGB', '-quality', '100%', 'mod_'+data[0]] #converting CMYK image to sRGB
			subprocess.Popen(tmp, stdout=subprocess.PIPE ).communicate()[0]
			newImgName = 'mod_'+data[0]
			os.remove(data[0])
			updateImg(data, newImgName)
			
		else :
			newImgName = data[0]
			updateImg(data, newImgName)


	def updateImg(data, newImgName) :
		try:
			url = config['url']
			imgData = get_param(data)
			print 'IMG DATA :', imgData
			delUrl = url + str(data[1])
			delres = requests.delete(delUrl) # deleting old image
			print delres.content
			if not delres.json()['statusCode'].startswith('2') :
				logErr("Delete Failed", 'DeleteFailed.txt', imgData, data[1])
				os.remove(newImgName)
				return
			logDelSucc("Delete Success", 'DeleteSucc.txt', imgData, data[1])			

			files = {'image': open(newImgName, 'rb')}
			r = requests.post(url, files=files, data = imgData) # uploading modified image
			print r.content
			if not r.json()['statusCode'].startswith('2') :
				logErr("Upload Failed", 'UploadFailed.txt', imgData, data[1])
				os.remove(newImgName)
				return
			logUploadSucc("Upload Success", 'UploadSuccess.txt', imgData, data[1], r)

			os.remove(newImgName) #removing image
		except Exception: 
  					pass


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

	def log(text, logFile) :
		f = open(logFile,'ab+')
		f.write(text)
		f.write('\n')
		f.close()

	def get_param(data) :
		params = {}
		params['objectId'] = data[3]		
		params['imageType'] = data[6]
		params['objectType'] = data[7]		
		params['addWaterMark'] = 'false'
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
	
	# Reading idList file
	#file = open(config['idListFile'], 'r')
	#ids = file.read()
	#file.close()	
	
	# Open database connection
	db = MySQLdb.connect(**config[env]['mysql'] )

	# prepare a cursor object using cursor() method
	cursor = db.cursor()

	# execute SQL query using execute() method.
	cursor.execute("SELECT I.id, concat('http://im.proptiger.com/',I.path, I.original_name), I.original_name, I.object_id, I.alt_text AS altText, I.priority, IT.type AS imageType, OT.type AS objectType, I.title, I.taken_at AS takenAt FROM Image I JOIN ImageType IT ON (I.ImageType_id = IT.id) JOIN ObjectType OT ON (IT.ObjectType_id = OT.id) WHERE I.active = 1  ORDER BY I.ID")	

	rows = cursor.fetchall()
	cols = []
	for row in rows:
		for col in row:
			cols.append(col)
		print "\n"
		imId = cols.pop(0)
		url = cols.pop(0)
		img = cols.pop(0)
		objectId = cols.pop(0)
		altText = cols.pop(0)
		priority = cols.pop(0)
		imageType = cols.pop(0)
		objectType = cols.pop(0)
		title = cols.pop(0)
		takenAt = cols.pop(0)
		try:
			data = requests.get(str(url), stream=True)
			with open(img, 'wb') as f:
				for chunk in data.iter_content(1024):
					f.write(chunk)

			pool.queueTask(task, (img, imId, url, objectId, altText, priority, imageType, objectType, title, takenAt), None)
		except IOError as e:
			print "I/O error({0}): {1}".format(e.errno, e.strerror)

	# When all tasks are finished, allow the threads to terminate
    	pool.joinAll()
	
