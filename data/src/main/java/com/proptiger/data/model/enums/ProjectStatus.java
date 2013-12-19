package com.proptiger.data.model.enums;

/**
 * Project status constants
 * @author Rajeev Pandey
 *
 */
public enum ProjectStatus {

	ON_HOLD("On Hold"),
	CANCELLED("Cancelled"),
	NOT_LAUNCHED("Not Launched"),
	OCCUPIED("Occupied"),
	READY_FOR_POSSESSION("Ready for Possession")
	;
	
	private ProjectStatus(String str){
		this.status = str;
	}
	private String status;
	
	@Override
	public String toString() {
		return this.status;
	}

	public String getStatus() {
		return status;
	}
}
