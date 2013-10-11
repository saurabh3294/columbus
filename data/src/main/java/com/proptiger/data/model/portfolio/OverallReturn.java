package com.proptiger.data.model.portfolio;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.util.DoubletoIntegerConverter;

/**
 * @author Rajeev Pandey
 *
 */
@ResourceMetaInfo(name = "OverallReturn")
public class OverallReturn {
	@FieldMetaInfo(dataType = DataType.STRING, displayName = "returnType", description = "Return Type")
	private ReturnType returnType;
	
	@FieldMetaInfo(dataType = DataType.CURRENCY, displayName = "changeAmount", description = "Change Amount")
	@JsonSerialize(converter=DoubletoIntegerConverter.class)
	private double changeAmount;
	
	@FieldMetaInfo(displayName = "changePercent", description = "Change Percent")
	private double changePercent;

	public ReturnType getReturnType() {
		return returnType;
	}

	public void setReturnType(ReturnType returnType) {
		this.returnType = returnType;
	}

	public double getChangeAmount() {
		return changeAmount;
	}

	public void setChangeAmount(double changeAmount) {
		this.changeAmount = changeAmount;
	}

	public double getChangePercent() {
		return changePercent;
	}

	public void setChangePercent(double changePercent) {
		this.changePercent = changePercent;
	}
	
	
	
}
