package com.proptiger.data.event.repo;

import java.util.Map;

/**
 * 
 * @author sahil
 * 
 */
public interface RawDBEventDao {

    public Map<String, String> getRawDBEventByTableNameAndDate(
            String tableName,
            String dateAttributeName,
            String dateAttributeValue);

}
