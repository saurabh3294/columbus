package com.proptiger.data.event.repo;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.proptiger.data.event.model.RawEventTableDetails;

/**
 * 
 * @author sahil
 * 
 */
@Repository
public class RawDBEventDao extends DynamicTableDao {

    /**
     * Returns the rows from DB in the form of list of Key, Value map for the
     * given table details in RawEventTableDetails
     * 
     * @param tableLog
     * @return
     */
    public List<Map<String, Object>> getRawDBEventByTableNameAndId(RawEventTableDetails tableLog) {

        /**
         * The rows will sorted in ascending order by their current time. As
         * processing will take place accordingly.
         */
        String queryString = "";
        try {

            queryString = "SELECT * FROM " + tableLog.getDbName()
                    + "."
                    + tableLog.getTableName()
                    + " WHERE "
                    + tableLog.getTransactionKeyName()
                    + " > '"
                    + tableLog.getLastTransactionKeyValue()
                    + "' "
                    + convertMapOfListToSql(tableLog.getFilterMap())
                    + " ORDER BY "
                    + tableLog.getTransactionKeyName()
                    + " ASC ";
        }
        catch (Exception e) {
            logger.error("QUERY " + queryString + " FORMATION FAILED " + e.getMessage());
            e.printStackTrace();
        }
        return runDynamicTableQuery(queryString);
    }

    /**
     * Returns Old Transaction from DB corresponding to the given PrimaryKey and
     * tableLog
     * 
     * @param tableLog
     * @param transactionKeyValue
     * @param primaryKeyValue
     * @param uniqueKeysValuesMap
     * @return
     */
    public Map<String, Object> getOldRawDBEvent(
            RawEventTableDetails tableLog,
            Object transactionKeyValue,
            Object primaryKeyValue,
            Map<String, Object> uniqueKeysValuesMap) {

        String queryString = "";
        queryString = "SELECT * FROM " + tableLog.getDbName()
                + "."
                + tableLog.getTableName()
                + " WHERE "
                + tableLog.getTransactionKeyName()
                + " < "
                + transactionKeyValue
                + " AND "
                + tableLog.getPrimaryKeyName()
                + " = '"
                + primaryKeyValue
                + "' "
                + convertMapOfListToSql(tableLog.getFilterMap())
                + convertMapToSql(uniqueKeysValuesMap)
                + " ORDER BY "
                + tableLog.getTransactionKeyName()
                + " DESC limit 1";

        List<Map<String, Object>> results = runDynamicTableQuery(queryString);
        if (results != null && results.size() > 0) {
            return results.get(0);
        }
        logger.error("Old Value not found DB. Query: " + queryString);
        return null;
    }

    /**
     * Returns the latest transaction for the given table details
     * 
     * @param tableLog
     * @return
     */
    public Map<String, Object> getLatestTransaction(RawEventTableDetails tableLog) {
        String queryString = "";
        queryString = "SELECT * FROM " + tableLog.getDbName()
                + "."
                + tableLog.getTableName()
                + " ORDER BY "
                + tableLog.getTransactionKeyName()
                + " DESC limit 1";

        List<Map<String, Object>> results = runDynamicTableQuery(queryString);
        if (results != null && results.size() > 0) {
            return results.get(0);
        }
        logger.error("No transactions found in DB. Query: " + queryString);
        return null;
    }

    /**
     * Return a TransactionRow for a given transaction id corresponding to the
     * transaction table details given in RawEventTableDetails
     * 
     * @param tableLog
     * @param transactionKeyValue
     * @return
     */
    public Map<String, Object> getRawEventDataOnTransactionId(RawEventTableDetails tableLog, Object transactionKeyValue) {
        String queryString = " SELECT * FROM " + tableLog.getDbName()
                + "."
                + tableLog.getTableName()
                + " WHERE "
                + tableLog.getTransactionKeyName()
                + "="
                + transactionKeyValue;

        List<Map<String, Object>> results = runDynamicTableQuery(queryString);
        if (results != null && results.size() > 0) {
            return results.get(0);
        }
        logger.error("No transaction found in DB. Query: " + queryString);
        return null;
    }

}
