package com.proptiger.data.model.portfolio;

/**
 * @author Rajeev Pandey
 *
 */
public enum PaymentStatus {
	PAID("Paid"),
	DEMAND_PENDINNG("Demand Pending");
	
	private String status;
	
	private PaymentStatus(String status){
		this.status = status;
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
