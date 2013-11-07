package com.proptiger.data.service.portfolio;

/**
 * @author Rajeev Pandey
 *
 */
public enum LeadSaleType {

	PRIMARY(1), RESALE(0);
	private Integer type;
	private LeadSaleType(Integer t){
		this.type = t;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
}
