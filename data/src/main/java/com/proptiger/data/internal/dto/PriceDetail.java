package com.proptiger.data.internal.dto;

import java.util.Date;

/**
 * Date wise price
 * @author Rajeev Pandey
 *
 */
public class PriceDetail{
	private double price;
	private Date effectiveDate;
	private String date;
	
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public Date getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
		this.date = effectiveDate.toString();
	}
	public String getDate() {
		return date;
	}
	
}