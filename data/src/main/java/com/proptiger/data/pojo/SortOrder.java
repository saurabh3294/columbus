package com.proptiger.data.pojo;

/**
 * @author Rajeev Pandey
 *
 */
public enum SortOrder {
	
	ASC("asc"), DESC("desc");
	
	private String sortOrder;
	
	private SortOrder(String val) {
		this.sortOrder = val;
	}

	public String getSortOrder() {
		return sortOrder;
	}
	
}