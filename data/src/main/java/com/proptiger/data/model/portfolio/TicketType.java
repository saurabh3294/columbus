package com.proptiger.data.model.portfolio;

/**
 * @author Rajeev Pandey
 *
 */
public enum TicketType {

	PAYMENTS("Payments"),
	CONSTRUCTION("Construction");
	
	private String type;
	
	private TicketType(String t){
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
		return this.type;
	}
}
