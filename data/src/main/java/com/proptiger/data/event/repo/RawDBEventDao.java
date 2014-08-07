package com.proptiger.data.event.repo;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Repository;

/**
 * 
 * @author sahil
 * 
 */
@Repository
public class RawDBEventDao extends DynamicTableDao {
    
    @Autowired
    private ConversionService conversionService;

    public List<Map<String, Object>> getRawDBEventByTableNameAndDate(
            String hostName,
            String dbName,
            String tableName,
            String transactionAttributeName,
            Long transactionAttributeValue,
            Map<String, Object> conditionKeyValue) {

        /* *
         * The rows will sorted in ascending order by their current time. As
         * processing will take place accordingly.
         */
        String queryString = "";
        try {
            
            queryString = "SELECT * FROM " + dbName
                    + "."
                    + tableName
                    + " WHERE "
                    + transactionAttributeName
                    + " > '"
                    + transactionAttributeValue
                    + mapConditionToSQLCondition(conditionKeyValue)
                    + "' ORDER BY "
                    + transactionAttributeName
                    + " ASC limit 1";
            logger.info(queryString);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }

        return runDynamicTableQuery(queryString);
    }

    public Map<String, Object> getOldRawDBEvent(
            String hostname,
            String dbName,
            String tableName,
            String transactionKeyName,
            Object transactionKeyValue,
            String primaryKeyName,
            Object primaryKeyValue,
            Map<String, Object> conditionKeyValue) {

        String queryString = "";
        queryString = "SELECT * FROM " + dbName
                + "."
                + tableName
                + " WHERE "
                + transactionKeyName
                + " < "
                + transactionKeyValue
                + " AND "
                + primaryKeyName
                + " = "
                + primaryKeyValue
                + mapConditionToSQLCondition(conditionKeyValue)
                + " ORDER BY "
                + transactionKeyName
                + " DESC limit 1";
        logger.info(queryString);

        List<Map<String, Object>> results = runDynamicTableQuery(queryString);
        if (results != null && results.size() > 0) {
            return results.get(0);
        }

        return null;

    }
    
    
}
