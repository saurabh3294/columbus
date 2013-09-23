package com.proptiger.data.model.portfolio;

import java.util.Date;

import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 * @author Rajeev Pandey
 *
 */
@ResourceMetaInfo(name = "PropertyPaymentPlan")
public class PropertyPaymentPlan {

	@FieldMetaInfo(displayName = "id", description = "Payment Plan Id")
	private int id;
	
	@FieldMetaInfo(displayName = "date", description = "date")
	private Date date;
	
	@FieldMetaInfo(displayName = "Amount", description = "Amount")
	private double amount;
	
	@FieldMetaInfo(displayName = "Construction Phase", description = "Construction Phase")
	private String constructionPhase;
	
	@FieldMetaInfo(dataType = DataType.OBJECT, displayName = "Status", description = "Status")
	private PaymentStatus status;
	
	@FieldMetaInfo(displayName = "Externally Funded", description = "Externally Funded")
	private boolean externallyFunded;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getConstructionPhase() {
		return constructionPhase;
	}

	public void setConstructionPhase(String constructionPhase) {
		this.constructionPhase = constructionPhase;
	}

	public PaymentStatus getStatus() {
		return status;
	}

	public void setStatus(PaymentStatus status) {
		this.status = status;
	}

	public boolean isExternallyFunded() {
		return externallyFunded;
	}

	public void setExternallyFunded(boolean externallyFunded) {
		this.externallyFunded = externallyFunded;
	}
	
}
