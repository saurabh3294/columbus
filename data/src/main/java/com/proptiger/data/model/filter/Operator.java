package com.proptiger.data.model.filter;

/**
 * @author Rajeev Pandey
 *
 */
public enum Operator {
	AND("and"), 
	RANGE("range"), 
	EQUAL("equal"), 
	FROM("from"), TO("to"), 
	GEODISTANCE("geoDistance"), 
	LAT("lat"), 
	LON("lon"), 
	DISTANCE("distance");

	private String operator;

	private Operator(String o) {
		this.operator = o;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.operator;
	}
}
