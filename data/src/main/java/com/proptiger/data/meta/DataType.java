package com.proptiger.data.meta;

/**
 * @author Rajeev Pandey
 *
 */
public enum DataType {

	STRING("String"),
	INTEGER("Integer"),
	DOUBLE("Double"),
	LONG("Long"),
	DATE("Date"),
	CURRENCY("currency");
	
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
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.getType();
	}
}
