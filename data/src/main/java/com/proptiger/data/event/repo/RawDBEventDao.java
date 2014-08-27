package com.proptiger.data.event.repo;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Repository;

import com.proptiger.data.event.model.DBRawEventTableLog;

/**
 * 
 * @author sahil
 * 
 */
@Repository
public class RawDBEventDao extends DynamicTableDao {

    @Autowired
    private ConversionService conversionService;

    public List<Map<String, Object>> getRawDBEventByTableNameAndId(DBRawEventTableLog tableLog) {

        /* *
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
            logger.error(" QUERY " + queryString + " FORMATION FAILED " + e.getMessage());
            e.printStackTrace();
        }

        return runDynamicTableQuery(queryString);
    }

    public Map<String, Object> getOldRawDBEvent(
            DBRawEventTableLog tableLog,
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

        return null;
    }

    public Map<String, Object> getLatestTransaction(DBRawEventTableLog tableLog) {
        String queryString = "";
        queryString = "SELECT * FROM " + tableLog.getDbName()
                + "."
                + tableLog.getTableName()
                + " ORDER BY "
                + tableLog.getTransactionKeyName()
                + " DESC limit 1";
        logger.info(queryString);

        List<Map<String, Object>> results = runDynamicTableQuery(queryString);
        if (results != null && results.size() > 0) {
            return results.get(0);
        }

        return null;
    }

    public Map<String, Object> getRawEventDataOnTransactionId(DBRawEventTableLog tableLog, Object transactionKeyValue) {
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

        return null;
    }

}
