#!/usr/bin/env python
import MySQLdb as mysql
import subprocess
import os
import sys
import logging

# This script will initiate the solr Indexing for
# project ids whose coupon option has expired or
# coupon is now not available

# Configurations
###################################################
env = 'develop'
solrIndexFilePath = ''
config = dict(
    env         =   {
                        'develop'   :   {
                                            'server'    :   'localhost',
                                            'mysql'     :   {
                                                                'host'      :   'localhost',
                                                                'user'      :   'root',
                                                                'passwd'    :   'root',
                                                                'db'        :   'cms'
                                                            }
                                        },
                        'production':   {
                                            'server'    :   'noida-1.proptiger-ws.com',
                                            'mysql'     :   {
                                                                'host'      :   '172.16.1.7',
                                                                'user'      :   'proptigeruser',
                                                                'passwd'    :   'proptigeruser@10',
                                                                'db'        :   'cms'
                                                            }
                                        }
                    }
)
###################################################

# Logging
logger = logging.getLogger('Coupon')
handler = logging.StreamHandler()
handler.setFormatter(logging.Formatter(fmt='%(levelname)s :: %(message)s'))
logger.addHandler(handler)
logger.setLevel(logging.INFO)

def updateInLog(text) :
	f = open('Log.txt','ab+')
	f.write(text)
	f.write('\n')
	f.close()

def readFromLog():
	if os.path.isfile('Log.txt'):
		f = open('Log.txt','r')
		data = f.read().replace("\n",",")
		data = data.split(",")
		return data
	else :
		return []

def list_2str(iterable):
    return '%s' % ', '.join(str(item) for item in iterable)

def updateSolr():
	conn = mysql.connect(**config['env'][env]['mysql'])
    	cur = conn.cursor()
    	conn.autocommit(True)
	
	#Query to fetch option ids from coupon_catalogue for the coupon which is either expired or not available
	sql = "select option_id from coupon_catalogue where purchase_expiry_at < now() or inventory_left = 0"
        cur.execute(sql)
        res = cur.fetchall()
	option_ids = []
	for i in res:
		option_ids.append(i[0])
	
	#Fetching processed coupon ids from Log.txt file for which solr Indexing has been done.
	processed_option_ids = readFromLog();
	if processed_option_ids:
		for processed_option_id in processed_option_ids:
			if processed_option_id != "":
				if long(processed_option_id) in option_ids:
					option_ids.remove(long(processed_option_id))
		logger.info('Option ids for which solr indexing update needed')
		logger.info(list_2str(option_ids))

	#If option ids list is empty then stopping the script.
	if not option_ids:
		logger.info('Option_id list is empty')
		sys.exit("Stopping Script .....")

	# Query to fetch project ids list for which solr Indexing need to be initiated
	sql = "select distinct rp.project_id from resi_project rp join resi_project_options rpo on (rp.project_id = rpo.project_id) where rp.version = 'Website' and rp.residential_flag = 'Residential' and rp.status = 'Active' and rpo.options_id in (" + list_2str(option_ids) +")"

	cur.execute(sql)
        res = cur.fetchall()
	project_ids = []
	for j in res:
		project_ids.append(j[0])
	
	# Checking for SolrIndex.php process for running
	cmd = ['ps', 'aux']
	output = subprocess.Popen( cmd, stdout=subprocess.PIPE ).communicate()[0]
        
        # If SolrIndex.php pid is not present then initiating solrIndexing for above project ids
	if "solrIndex.php" not in output:
		# Finding absolute path of SolrIndex.php file
		#cmd = ['locate','-br', '^solrIndex.php']
		#solrIndex = subprocess.Popen(cmd, stdout=subprocess.PIPE ).communicate()[0].replace("\n","")
		cmd = ['php', solrIndexFilePath, list_2str(project_ids)]

		# Intiating sorl Indexing process
		subprocess.Popen( cmd, stdout=subprocess.PIPE ).communicate()[0]
		updateInLog(list_2str(option_ids))
		
		# Redis clearing
		cmd = ['redis-cli','flushall']
		output = subprocess.Popen( cmd, stdout=subprocess.PIPE ).communicate()[0]
		logger.info(output)
	else:
		logger.info('SolrIndex.php is Running')
 
# Main
if __name__ == '__main__':
	updateSolr()	
