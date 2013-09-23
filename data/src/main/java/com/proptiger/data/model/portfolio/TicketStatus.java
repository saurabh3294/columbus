package com.proptiger.data.model.portfolio;

public enum TicketStatus {

	OPEN("Open"),
	REOPENED("Reopened"),
	CLOSED("Closed"),
	REOPENED_CLOSED("Reopened-Closed");
	
	private String status;
	
	private TicketStatus(String s){
		this.status = s;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return this.status;
	}
}
