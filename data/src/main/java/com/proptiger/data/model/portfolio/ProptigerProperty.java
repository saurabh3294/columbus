package com.proptiger.data.model.portfolio;

import java.util.Date;

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

	@FieldMetaInfo(displayName = "id", description = "Property Id")
	private int id;
	
	@FieldMetaInfo(displayName = "tower", description = "Tower")
	private int tower;
	
	@FieldMetaInfo(dataType = DataType.DATE, displayName = "purchaseDate", description = "Purchase Date")
	private Date purchaseDate;
	
	@FieldMetaInfo(dataType = DataType.OBJECT, displayName = "property", description = "Property")
	private Property property;
	
	@FieldMetaInfo(dataType = DataType.CURRENCY, displayName = "purchasePrice", description = "Purchase Price")
	private double purchasePrice;
	
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
	
	
	
}
