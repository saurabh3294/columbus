package com.proptiger.data.model.portfolio;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.model.BaseModel;

/**
 * @author Rajeev Pandey
 *
 */
@ResourceMetaInfo
@JsonFilter("fieldFilter")
public class OverallReturn implements BaseModel {
	@FieldMetaInfo(dataType = DataType.STRING, displayName = "returnType", description = "Return Type")
	private ReturnType returnType;
	
	@FieldMetaInfo(dataType = DataType.CURRENCY, displayName = "changeAmount", description = "Change Amount")
	private BigDecimal changeAmount;
	
	@FieldMetaInfo(dataType = DataType.DOUBLE, displayName = "changePercent", description = "Change Percent")
	private BigDecimal changePercent;

	public ReturnType getReturnType() {
		return returnType;
	}

	public void setReturnType(ReturnType returnType) {
		this.returnType = returnType;
	}

	public BigDecimal getChangeAmount() {
		return changeAmount;
	}

	public void setChangeAmount(BigDecimal changeAmount) {
		this.changeAmount = changeAmount;
	}

	public BigDecimal getChangePercent() {
		return changePercent;
	}

	public void setChangePercent(BigDecimal changePercent) {
		this.changePercent = changePercent;
	}
	
	
	
}
