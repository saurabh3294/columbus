package com.proptiger.data.meta;

/**
 * @author Rajeev Pandey
 *
 */
public enum DataType {

	STRING("String"),
	INTEGER("Integer"),
	DOUBLE("Double"),
	FLOAT("Float"),
	LONG("Long"),
	DATE("Date"),
	CURRENCY("Currency"),
	BOOLEAN("Boolean"),
	
	DEFAULT("Default");
	
	
	private String type;
	
	private DataType(String t) {
		this.type = t;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public static DataType valueOfIgnoreCase(String typeStr){
		if(typeStr != null && typeStr.contains(".")){
			String[] arr = typeStr.split("\\.");
			typeStr = arr[arr.length - 1];
		}
		for(DataType dt: values()){
			if(dt.getType().equalsIgnoreCase(typeStr)){
				return dt;
			}
				
		}
		throw new IllegalArgumentException("Illegal DataType string "+typeStr);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getType();
	}
}
