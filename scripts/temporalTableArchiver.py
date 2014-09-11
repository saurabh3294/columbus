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

# This script will serve the purpose to optimise images for faster load on web 
# using imagemagick library
#
# Various log files are maintained which will have successfully uploaded image information,
# Upload failed images information and similarly successfully deleted and deletion failed
# images information.
#

# number Of Threads
numberOfThreads = 1

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
            'host'      :   'localhost',
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
                    "progress":10000,
                    "tables":{
                        "days":10, 
                        "limit":1000,
                        "total_migrated":10000,
                        "run_time":1,
                        "progress":10000
                    } 
                }

archiveDatabase = "archives"
# Open database connection
srcdb = MySQLdb.connect(**config[env]['mysqlsrc'])
destdb = MySQLdb.connect(**config[env]['mysqldest'])

srccursor = srcdb.cursor()
destcursor = destdb.cursor()



def logging(text):
    global logFile
    log(text+"\n", logFile)

def log(text, logFile) :
    f = open(logFile,'ab+')
    f.write(text)
    f.write('\n')
    f.close()

def createTable(row, createTable, dropIndexStr, dcursor):
    global archiveDatabase
    status = dcursor.execute(createTable['Create Table'])

    # drop indexes
    query = "ALTER TABLE %s.%s %s" % (archiveDatabase, row['table_name'], dropIndexStr)
    status = dcursor.execute(query)

    query = "ALTER TABLE %s.%s engine=archive, auto_increment = 0 " % (archiveDatabase, row['table_name'])
    dcursor.execute(query)
    
def handleCreateTable(row, dcursor, scursor):
    global archiveDatabase

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

    createTable(row, createTableData, dropIndexStr, dcursor)

    
def checkAndCreateTableOnArchive(row, dcursor, scursor):
    global archiveDatabase

    query = "SELECT * from information_schema.tables where table_name = '%s' and table_schema = '%s'" % (row['table_name'], archiveDatabase)
    dcursor.execute(query)
    rows = dcursor.fetchall()
    if(len(rows) > 0):
        return 1

    status = handleCreateTable(row, dcursor, scursor)

    return status 

def verifyTableStructure(row, dcursor, scursor):
    global archiveDatabase
    query_template = "SELECT column_name, column_type, data_type from information_schema.columns where table_schema = '%s' and table_name = '%s' order by column_name "

    squery = query_template % (row['table_schema'], row['table_name'])
    scursor.execute(squery)
    srows = scursor.fetchall()
    
    dquery = query_template % (archiveDatabase, row['table_name'])
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

def handleTableArchiving(row, scriptconfig, dcursor, scursor):
    global archiveDatabase
    table_name = row['table_name']
    table_schema = row['table_schema']
    
    rows_limit, run_time, last_days, total_migrated, progress = getArchivingConfig(row, scriptconfig)

    filename = "%s-%s-%s.txt" % (row['table_schema'], row['table_name'], strftime("%Y-%m-%d--%H:%M:%S", gmtime()) )

    #command = ["mk-archiver", "--source", "h=localhost,u=root,p=root,D=%s,t=%s" % (table_schema, table_name), "--dest", "h=localhost,u=root,p=root,D=%s,t=%s" % (archiveDatabase, table_name), "--commit-each", "--limit", str(rows_limit), "--where", "'_t_transaction_date < now() - interval %d day'" % (last_days)]#, "--progress", str(progress), "--statistics"]
    commandStr = "mk-archiver --source h=localhost,u=root,p=root,D=%s,t=%s --dest h=localhost,u=root,p=root,D=%s,t=%s --commit-each --limit %d --where '_t_transaction_date < now() - interval %d day' --progress %d --statistics" % (table_schema, table_name, archiveDatabase, table_name, rows_limit, last_days, progress)
    #, "--progress", str(progress), "--statistics"]
    print commandStr
    #output = subprocess.check_output(commandStr, shell=True, stderr=subprocess.STDOUT)
    #print output
    with io.open(filename, 'wb') as writer:
        p = subprocess.Popen(commandStr, stdout=subprocess.PIPE, shell=True)
        for line in iter(p.stdout.readline, ''):
            writer.write(line)

        print "PID "+str(p.pid)
        #output, error = p.communicate()
        p.wait()
        status = p.returncode
        print output
        #print " status return code "+str(status)
        ## some error has occurred.
        #if status < 0:
        #    return 0
        #else:
        #    return 1

def handleTableReplication(row):
    global destdb, srcdb, scriptconfig

    dcursor = destdb.cursor()
    scursor = srcdb.cursor()
    status = checkAndCreateTableOnArchive(row, dcursor, scursor)
    print "table created"
    print "compare table columns"
    status = verifyTableStructure(row, dcursor, scursor)
    if status == 0:
        print "column comparision failed"
        return 0

    print "comparison successfull"
    print "transfer data"
    status = handleTableArchiving(row, scriptconfig, dcursor, scursor)

    scursor.close()
    dcursor.close()

# the worker thread pulls an item from queue and process it.
def worker():
    while True:
        if q.empty():
            continue
        print "threading"
        item = q.get()
        print json.dumps(item)
        handleTableReplication(item)
        q.task_done()

# Create the queue and thread pool.
q = Queue()
for i in range(numberOfThreads):
    t = threading.Thread(target=worker)
    t.daemon = True # thread dies when main thread (only non-daemon thread) exits 
    t.start()

query = "SELECT table_schema, table_name,engine, table_rows, auto_increment from information_schema.tables where table_name like '_t_%' and table_schema != 'information_schema' and table_name = '_t_listing_prices_bk1'  and table_schema != 'archives' order by table_name"
srccursor.execute(query)
rows = srccursor.fetchall()
for row in rows:
   print json.dumps(row)
   q.put(row)

q.join()
