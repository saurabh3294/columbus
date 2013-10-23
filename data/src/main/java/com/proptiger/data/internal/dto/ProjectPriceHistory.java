package com.proptiger.data.internal.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class contains price history for project id and type id.
 * @author Rajeev Pandey
 *
 */
public class ProjectPriceHistory {

	private Integer projectId;
	private Integer typeId;
	private List<PriceDetail> prices;
	
	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public List<PriceDetail> getPrices() {
		return prices;
	}

	public void setPrices(List<PriceDetail> prices) {
		this.prices = prices;
	}

	public void addPrice(PriceDetail price){
		if(this.prices == null){
			this.prices = new ArrayList<PriceDetail>();
		}
		this.prices.add(price);
	}
	/**
	 * Date wise price
	 * @author Rajeev Pandey
	 *
	 */
	public static class PriceDetail{
		private double price;
		private Date effectiveDate;
		
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
		}
		
	}
}
