package com.proptiger.data.model.portfolio;

import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 * @author Rajeev Pandey
 *
 */
@ResourceMetaInfo(name = "OverallReturn")
public class OverallReturn {
	@FieldMetaInfo(dataType = DataType.STRING, displayName = "returnType", description = "Return Type")
	private ReturnType returnType;
	
	@FieldMetaInfo(dataType = DataType.CURRENCY, displayName = "changeAmount", description = "Change Amount")
	private double changeAmount;
	
	@FieldMetaInfo(displayName = "changePercent", description = "Change Percent")
	private float changePercent;

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

	public float getChangePercent() {
		return changePercent;
	}

	public void setChangePercent(float changePercent) {
		this.changePercent = changePercent;
	}
	
	
	
}
