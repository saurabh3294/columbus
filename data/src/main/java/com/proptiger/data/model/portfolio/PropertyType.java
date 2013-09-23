package com.proptiger.data.model.portfolio;

/**
 * @author Rajeev Pandey
 *
 */
public enum PropertyType {

	APARTMENT("Appartment"),
	PLOT("Plot");
	
	private String type;
	
	private PropertyType(String type){
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return this.type;
	}
}
