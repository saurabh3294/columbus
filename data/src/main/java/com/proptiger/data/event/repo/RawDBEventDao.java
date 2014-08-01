package com.proptiger.data.event.repo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author sahil
 * 
 */
public interface RawDBEventDao {

    public List<Map<String, Object>> getRawDBEventByTableNameAndDate(
            String hostName,
            String dbName,
            String tableName,
            String dateAttributeName,
            Date dateAttributeValue);

}
