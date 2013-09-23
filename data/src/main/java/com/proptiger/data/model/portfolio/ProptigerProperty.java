package com.proptiger.data.model.portfolio;

import java.util.Date;
import java.util.List;

import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.model.Property;

/**
 * @author Rajeev Pandey
 *
 */
@ResourceMetaInfo(name = "ProptigerProperty")
public class ProptigerProperty {

	@FieldMetaInfo(displayName = "Property Id", description = "Property Id")
	private int id;
	
	@FieldMetaInfo(displayName = "Tower", description = "Tower")
	private int tower;
	
	@FieldMetaInfo(dataType = DataType.DATE, displayName = "Purchase Date", description = "Purchase Date")
	private Date purchaseDate;
	
	@FieldMetaInfo(dataType = DataType.OBJECT, displayName = "Property", description = "Property")
	private Property property;
	
	@FieldMetaInfo(dataType = DataType.CURRENCY, displayName = "Purchase Price", description = "Purchase Price")
	private double purchasePrice;
	
	@FieldMetaInfo(dataType = DataType.ARRAY, displayName = "Payment Plans", description = "Payment Plans")
	private List<PropertyPaymentPlan> paymentPlans;
	
	@FieldMetaInfo(dataType = DataType.ARRAY, displayName = "Documents", description = "Documents")
	private List<PropertyDocument> documents;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTower() {
		return tower;
	}
	public void setTower(int tower) {
		this.tower = tower;
	}
	public Date getPurchaseDate() {
		return purchaseDate;
	}
	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
	
	public Property getProperty() {
		return property;
	}
	public void setProperty(Property property) {
		this.property = property;
	}
	public double getPurchasePrice() {
		return purchasePrice;
	}
	public void setPurchasePrice(double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}
	public List<PropertyPaymentPlan> getPaymentPlans() {
		return paymentPlans;
	}
	public void setPaymentPlans(List<PropertyPaymentPlan> paymentPlans) {
		this.paymentPlans = paymentPlans;
	}
	public List<PropertyDocument> getDocuments() {
		return documents;
	}
	public void setDocuments(List<PropertyDocument> documents) {
		this.documents = documents;
	}
	
}
