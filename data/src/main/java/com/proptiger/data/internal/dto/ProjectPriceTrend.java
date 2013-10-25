package com.proptiger.data.internal.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * This class contains price trend for project id and type id.
 * @author Rajeev Pandey
 *
 */
public class ProjectPriceTrend {

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
}
