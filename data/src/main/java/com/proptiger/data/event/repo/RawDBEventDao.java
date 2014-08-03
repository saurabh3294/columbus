package com.proptiger.data.event.repo;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Repository;

/**
 * 
 * @author sahil
 * 
 */
@Repository
public class RawDBEventDao extends DynamicTableDao{
    @Autowired
    private ConversionService conversionService;

    public List<Map<String, Object>> getRawDBEventByTableNameAndDate(
            String hostName,
            String dbName,
            String tableName,
            String dateAttributeName,
            Date dateAttributeValue) {

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
                    + dateAttributeName
                    + " > "
                    + conversionService.convert(dateAttributeValue, String.class)
                    + " ORDER BY "
                    + dateAttributeName
                    + " ASC ";
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }

        return runDynamicTableQuery(queryString);
    }

    public Map<String, Object> getRaw(
            String hostname,
            String dbName,
            String tableName,
            String transactionKeyName,
            String transactionKeyValue,
            String primaryKeyName,
            Object primaryKeyValue) {
        
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
                    + " ORDER BY "
                    + transactionKeyName
                    + " DESC limit 1";
        List<Map<String, Object>> results = runDynamicTableQuery(queryString);
        if(results != null && results.size() > 0){
            return results.get(0);
        }
        
        return null;

    }
}
