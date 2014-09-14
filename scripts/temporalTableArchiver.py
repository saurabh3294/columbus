import MySQLdb
import MySQLdb.cursors 
import pprint
import json
import sys
import subprocess
import io
import time
import shlex
from time import gmtime, strftime
import threading
from Queue import Queue
import logging
import signal

# This script will serve the purpose to optimise images for faster load on web 
# using imagemagick library
#
# Various log files are maintained which will have successfully uploaded image information,
# Upload failed images information and similarly successfully deleted and deletion failed
# images information.
#

# number Of Threads
numberOfThreads = 1

# initialize logging
logging.basicConfig(filename = "archiveLogging.txt", level=logging.INFO)

logging.info(" Table Archiving Started ")

# Configuration =========================================================
env    = 'develop' # To set develop environment or production environment
config = dict(  
        develop   = {
        'mysqlsrc'   :   {
            'host'      :   'localhost',
            'user'      :   'root',
            'passwd'    :   'root',
            'cursorclass':  MySQLdb.cursors.DictCursor
            },
        'mysqldest'   :   {
            'host'      :   'localhost',#'192.168.1.8',
            'user'      :   'root',
            'passwd'    :   'root',
            'db'        :   'archives',
            'cursorclass':  MySQLdb.cursors.DictCursor
            }
        },
        production = {
        'mysqlsrc'   :   {
            'host'      :   'noida-1.proptiger-ws.com',
            'user'      :   'qa',
            'passwd'    :   'Proptiger123',
            'db'        :   'proptiger',
            'cursorclass':  MySQLdb.cursors.DictCursor
            },
        'mysqldest'   :   {
            'host'      :   'noida-1.proptiger-ws.com',
            'user'      :   'qa',
            'passwd'    :   'Proptiger123',
            'db'        :   'proptiger',
            'cursorclass':  MySQLdb.cursors.DictCursor
            }   
        }
)
scriptconfig = {
                    "days": 120, 
                    "limit":1000,
                    "total_migrated":10000,
                    "run_time":1,
                    "progress":10,
                    "tables":{
                        "days":10, 
                        "limit":1000,
                        "total_migrated":10000,
                        "run_time":1,
                        "progress":10000
                    } 
                }

logging.info(" Enviornment Used : "+env)

archiveDatabase = "archives"
# Open database connection
srcdb = MySQLdb.connect(**config[env]['mysqlsrc'])
destdb = MySQLdb.connect(**config[env]['mysqldest'])

srccursor = srcdb.cursor()
destcursor = destdb.cursor()

# create subprocess map.
subprocessMap = {}

def customLogging(text):
    global logFile
    log(text+"\n", logFile)

def log(text, logFile) :
    f = open(logFile,'ab+')
    f.write(text)
    f.write('\n')
    f.close()

def createTable(row, createTable, dropIndexStr, tableLogging, dcursor):
    global archiveDatabase

    tableLogging.info(" Table being created on Archive Server ")
    
    try:
        status = dcursor.execute(createTable['Create Table'])
        
        # drop indexes
        query = "ALTER TABLE %s.%s %s" % (archiveDatabase, row['table_name'], dropIndexStr)
        status = dcursor.execute(query)

        query = "ALTER TABLE %s.%s engine=archive, auto_increment = 0 " % (archiveDatabase, row['table_name'])
        dcursor.execute(query)

        #Rename table to current month
        new_table_name = row['table_name'] + "_" + strftime("%Y_%b_%d", gmtime()) 
        query = "RENAME TABLE %s.%s to %s.%s" % (archiveDatabase, row['table_name'], archiveDatabase, new_table_name)
        dcursor.execute(query)
    except Exception as e:
        query = " DROP TABLE %s.%s" % (archiveDatabase, row['table_name'])
        dcursor.execute(query)
        raise Exception()
    
    return new_table_name
    
def handleCreateTable(row, tableLogging, dcursor, scursor):
    global archiveDatabase
    
    tableLogging.info(" Table Creation queries to be retreived from source server. ")
    query  = "SHOW CREATE TABLE %s.%s" % (row['table_schema'], row['table_name'])
    scursor.execute(query)
    createTableData = scursor.fetchone()
    
    query = "SHOW INDEX FROM %s.%s WHERE key_name != 'PRIMARY'" % (row['table_schema'], row['table_name'])
    scursor.execute(query)
    indexes = scursor.fetchall()
    dropIndexStr = ""
    for index in indexes:
        dropIndexStr += ", DROP INDEX %s " % (index['Key_name'])

    dropIndexStr = dropIndexStr.strip(',')

    return createTable(row, createTableData, dropIndexStr, tableLogging, dcursor)

    
def checkAndCreateTableOnArchive(row, tableLogging, dcursor, scursor):
    global archiveDatabase
    
    regexp = "^%s_[[:digit:]]{4}_[[:alpha:]]+_[[:digit:]]{1,2}$" % (row['table_name'])
    query = "SELECT table_name from information_schema.tables where table_name regexp '%s' and table_schema = '%s' order by create_time desc limit 1 " % (regexp, archiveDatabase)
    tableLogging.info(" CHECK TABLE QUERY "+query)
    dcursor.execute(query)
    rows = dcursor.fetchall()
    if(len(rows) > 0):
        tableLogging.info(" Table Found ")
        return [True, rows[0]['table_name']]

    new_table_name = handleCreateTable(row, tableLogging, dcursor, scursor)

    return [False, new_table_name] 

def verifyTableStructure(row, new_table_name, tableLogging, dcursor, scursor):
    global archiveDatabase

    tableLogging.info(" Table Columns being verified ")

    query_template = "SELECT column_name, column_type, data_type from information_schema.columns where table_schema = '%s' and table_name = '%s' order by column_name "

    squery = query_template % (row['table_schema'], row['table_name'])
    scursor.execute(squery)
    srows = scursor.fetchall()
    
    dquery = query_template % (archiveDatabase, new_table_name)
    dcursor.execute(dquery)
    drows = dcursor.fetchall()
    if( len(srows) != len(drows) ):
        return 0

    # if all column names are same then no need to create the map. just check them sequentially.
    i=0
    for srow in srows:
        flag = 1
        drow = drows[i]

        # comparing each column name and its attributes. They all should be same.
        for key in srow:
            flag &= (srow[key] == drow[key])

        i = i+1
        # if any of the key comparision fails then both table columns are not equal hence returning false.
        if flag == 0:
            return 0

    return 1

def getArchivingConfig(row, scriptconfig):
    table_name = row['table_name']
    table_schema = row['table_schema']
    # setting config
    rows_limit = scriptconfig['limit']
    run_time = scriptconfig['run_time']
    last_days = scriptconfig['days']
    total_migrated = scriptconfig['total_migrated']
    progress = scriptconfig['progress']
    if scriptconfig.has_key(table_name) :
        table_config = scriptconfig[table_name]
        if table_config.has_key('limit'):
            rows_limit = table_config['limit']
        if table_config.has_key('run_time'):
            run_time = table_config['run_time']
        if table_config.has_key('days'):
            last_days = table_config['days']
        if table_config.has_key('total_migrated'):
            total_migrated = table_config['total_migrated']
        if table_config.has_key('progress'):
            progress = table_config['progress']
    
    return [rows_limit, run_time, last_days, total_migrated, progress]

def handleTableArchiving(row, new_table_name, tableLogging, scriptconfig, dcursor, scursor):
    global archiveDatabase, config, env, subprocessMap
    table_name = row['table_name']
    table_schema = row['table_schema']
    mysql_src = config[env]['mysqlsrc']
    mysql_dest = config[env]['mysqldest']
    
    rows_limit, run_time, last_days, total_migrated, progress = getArchivingConfig(row, scriptconfig)

    filename = "%s-%s-%s.txt" % (row['table_schema'], row['table_name'], strftime("%Y-%m-%d--%H:%M:%S", gmtime()) )

    commandStr = "mk-archiver --source h=%s,u=%s,p=%s,D=%s,t=%s --dest h=%s,u=%s,p=%s,D=%s,t=%s --commit-each --limit %d --where '_t_transaction_date < now() - interval %d day' --progress %d --statistics" % (mysql_src['host'], mysql_src['user'], mysql_src['passwd'], table_schema, table_name, mysql_dest['host'], mysql_dest['user'], mysql_dest['passwd'], archiveDatabase, new_table_name, rows_limit, last_days, progress)

    tableLogging.info(commandStr)
    tableLogging.info(" Command Log Filename "+filename)

    with io.open(filename, 'wb') as writer:
        p = subprocess.Popen(commandStr, stdout=subprocess.PIPE, shell=True)
        subprocessMap[new_table_name] = p
        print " data archiving started "

        for line in iter(p.stdout.readline, ''):
            print line
            writer.write(line)

        print "PID "+str(p.pid)
        #output, error = p.communicate()
        p.wait()
        status = p.returncode
        tableLogging.info(" Data Migration Finished with Status Code " + str(status))
        del subprocessMap[new_table_name]
        #print " status return code "+str(status)
        ## some error has occurred.
        #if status < 0:
        #    return 0
        #else:
        #    return 1

def handleTableReplication(row):
    global destdb, srcdb, scriptconfig, logging
    
    tableLogging = logging.getLogger(row['table_schema']+"."+row['table_name'])

    dcursor = destdb.cursor()
    scursor = srcdb.cursor()

    new_table_name = ""
    new_table_status, new_table_name = checkAndCreateTableOnArchive(row, tableLogging, dcursor, scursor)
    status = verifyTableStructure(row, new_table_name, tableLogging, dcursor, scursor)
    if status == 0:
        tableLogging.info(" Table Columns Info do not match. Hence stopping the data transfer ")
        new_table_name = handleCreateTable(row, tableLogging, dcursor, scursor)

    status = handleTableArchiving(row, new_table_name, tableLogging, scriptconfig, dcursor, scursor)

    scursor.close()
    dcursor.close()

# the worker thread pulls an item from queue and process it.
def worker():
    while True:
        if q.empty():
            continue
        item = q.get()
        handleTableReplication(item)
        q.task_done()

def signalHandler(signum, frame):
    global q, subprocessMap
    print " Wait ! script being closed "
    while q.empty() == False:
        q.get()
        q.task_done()

    for key in subprocessMap:
        subprocessMap.terminate()


signal.signal(signal.SIGINT, signalHandler)

# Create the queue and thread pool.
q = Queue()
for i in range(numberOfThreads):
    logging.info(" Thread %d Being Created" % (i+1))
    t = threading.Thread(target=worker)
    t.daemon = True # thread dies when main thread (only non-daemon thread) exits 
    t.start()

query = "SELECT table_schema, table_name,engine, table_rows, auto_increment from information_schema.tables where table_name like '_t_%' and table_schema NOT IN ('information_schema', 'archives') order by table_name"
#query = "SELECT table_schema, table_name,engine, table_rows, auto_increment from information_schema.tables where table_name like '_t_%' and table_schema != 'information_schema' and table_name = '_t_listing_prices_bk3'  and table_schema != 'archives' order by table_name"

logging.info(" Retrieving Table list "+query)

srccursor.execute(query)
rows = srccursor.fetchall()
for row in rows:
   logging.info(" Table Row being put in queue"+json.dumps(row))
   q.put(row)

q.join()

logging.shutdown()
srcdb.close()
destdb.close()
