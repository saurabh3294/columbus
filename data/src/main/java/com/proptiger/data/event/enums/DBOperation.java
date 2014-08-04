package com.proptiger.data.event.enums;

import java.util.HashMap;
import java.util.Map;

public enum DBOperation {
    INSERT, UPDATE, DELETE;
    
    private static Map<String, DBOperation> dbOperationMapping = new HashMap<>();
    
    static {
        dbOperationMapping.put("I", DBOperation.INSERT);
        dbOperationMapping.put("U", DBOperation.UPDATE);
        dbOperationMapping.put("D", DBOperation.DELETE);
    }
    
    public static DBOperation getDBOperationEnum (String v) {
        return dbOperationMapping.get(v);
    }
}
