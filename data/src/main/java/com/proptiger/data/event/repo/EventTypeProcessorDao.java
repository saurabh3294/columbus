package com.proptiger.data.event.repo;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Repository;

@Repository
public class EventTypeProcessorDao extends DynamicTableDao {
    @Autowired
    private ConversionService conversionService;

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
            String transactionDateName,
            Date lastDate,
            Map<String, Object> filterMap) {

        String queryString = "";
        String otherQuery = "";
        try {
            String conditionStr = convertMapToSql(filterMap);
            /**
             * The query which will get the last value based on the latest value
             * before first day of the month.
             */

            queryString = "SELECT %s,%s FROM %s.%s WHERE %s=%s AND %s<%s AND %s<%s %s ORDER BY %s DESC LIMIT 1";
            /**
             * The query which will get the last value based on the first value
             * on the current month.
             */
            otherQuery = "SELECT %s,%s FROM %s.%s WHERE %s=%s AND %s<%s AND %s>%s %s ORDER BY %s ASC LIMIT 1";
            queryString = String.format(
                    queryString,
                    attributeName,
                    transactionDateName,
                    dbName,
                    tableName,
                    primaryKeyName,
                    PrimaryKeyValue,
                    transactionKeyName,
                    transactionKeyValue,
                    transactionDateName,
                    conversionService.convert(lastDate, String.class),
                    conditionStr,
                    transactionDateName);
            otherQuery = String.format(
                    otherQuery,
                    attributeName,
                    transactionDateName,
                    dbName,
                    tableName,
                    primaryKeyName,
                    PrimaryKeyValue,
                    transactionKeyName,
                    transactionKeyValue,
                    transactionDateName,
                    conversionService.convert(lastDate, String.class),
                    conditionStr,
                    transactionDateName);
            /**
             * Formation of query based on the retrieving the value after union
             * query after sorting date in ascending order. The preference is
             * given to the value that occurred before first day of current
             * month.
             */
            queryString = "( " + queryString
                    + " ) UNION ( "
                    + otherQuery
                    + " ) ORDER BY "
                    + transactionDateName
                    + " ASC LIMIT 1";

            List<Map<String, Object>> results = runDynamicTableQuery(queryString);
            return results.get(0).get(attributeName);
        }
        catch (Exception e) {
            logger.error(" ERROR IN QUERY FORMATION " + e.getMessage());
            return null;
        }

    }
}
