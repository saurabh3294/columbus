package com.proptiger.data.meta;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Rajeev Pandey
 *
 */
public enum DataType {
	STRING,
	INTEGER,
	DOUBLE,
	FLOAT,
	LONG,
	DATE,
	CURRENCY,
	BOOLEAN,
	DEFAULT;

	private static Map<String, DataType> typeMap = new HashMap<String, DataType>();

	static {
	    typeMap.put("int", INTEGER);
	    typeMap.put("integer", INTEGER);
	    typeMap.put("string", STRING);
	    typeMap.put("double", DOUBLE);
        typeMap.put("float", FLOAT);
        typeMap.put("long", LONG);
        typeMap.put("date", DATE);
        typeMap.put("currency", CURRENCY);
        typeMap.put("boolean", BOOLEAN);
	}

	public static DataType valueOfIgnoreCase(String typeStr){
		if(typeStr != null && typeStr.contains(".")){
			String[] arr = typeStr.split("\\.");
			typeStr = arr[arr.length - 1];
		}

		DataType dataType = typeMap.get(typeStr.toLowerCase());

		if (dataType == null) {
	        throw new IllegalArgumentException("Illegal DataType string " + typeStr);
		}

		return dataType;
	}
}
