package com.proptiger.data.model.portfolio;


/**
 * @author Rajeev Pandey
 *
 */
public enum ConstructionStage {
	UNDER_CONSTRUCTION("Under Construction");
	
	private String stage;
	
	private ConstructionStage(String stage){
		this.stage = stage;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}
	
	@Override
	public String toString() {
		return this.stage;
	}
}
