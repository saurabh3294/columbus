package com.proptiger.data.model.portfolio;

/**
 * @author Rajeev Pandey
 *
 */
public enum DocumentType {

	DEMAND_LETTER("Demand Letter"),
	BUILDER_DOC("Builder Doc");
	
	private String type;
	
	private DocumentType(String str){
		this.type = str;
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
