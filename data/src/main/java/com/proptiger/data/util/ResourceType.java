package com.proptiger.data.util;

/**
 * @author Rajeev Pandey
 *
 */
public enum ResourceType {
	LISTING("listing"),
	DASHBOARD("dashboard");
	
	private String type;
	
	private ResourceType(String t){
		this.type = t;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
}
