package com.proptiger.data.event.repo;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public class EventTypeProcessorDao extends DynamicTableDao {

    // TODO Using FIQL selector
    public Object getOldValueOfEventTypeOnLastMonth(
            String hostName,
            String dbName,
            String tableName,
            String primaryKeyName,
            Object PrimaryKeyValue,
            String attributeName,
            String transactionKeyName,
            Object transactionKeyValue,
            String effectiveDateName,
            Date lasteffectiveDate,
            Map<String, List<Object>> filterMap) {

        String queryString = "";
        try {
            
            String conditionStr = convertMapOfListToSql(filterMap);
            
            /**
             * The query which will get the last value based on the latest value
             * before first day of the month.
             */

            queryString = "SELECT %s FROM %s.%s WHERE %s=%s AND %s<%s AND %s<'%s' %s ORDER BY %s DESC, %s DESC LIMIT 1";
            queryString = String.format(
                    queryString,
                    attributeName,
                    dbName,
                    tableName,
                    primaryKeyName,
                    PrimaryKeyValue,
                    transactionKeyName,
                    transactionKeyValue,
                    effectiveDateName,
                    conversionService.convert(lasteffectiveDate, String.class),
                    conditionStr,
                    effectiveDateName,
                    transactionKeyName);
            
            List<Map<String, Object>> results = runDynamicTableQuery(queryString);
            if (!results.isEmpty()) {
                return results.get(0).get(attributeName);
            }
            else {
                return null;
            }
        }
        catch (Exception e) {
            logger.error(" ERROR IN QUERY " + queryString + " \n ERROR QUERY FORMATION : " + e.getMessage() + "\n ");
            e.printStackTrace();
            return null;
        }

    }
}
