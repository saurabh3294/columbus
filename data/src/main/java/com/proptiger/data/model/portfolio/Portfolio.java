package com.proptiger.data.model.portfolio;

import java.util.List;

import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 * @author Rajeev Pandey
 *
 */
@ResourceMetaInfo(name = "Portfolio")
public class Portfolio {

	@FieldMetaInfo(displayName = "id", description = "Portfolio Id")
	private int id;
	
	@FieldMetaInfo(dataType = DataType.CURRENCY, displayName = "originalVaue", description = "Original Vaue")
	private double originalVaue;
	
	@FieldMetaInfo(dataType = DataType.CURRENCY, displayName = "currentValue", description = "Current Value")
	private double currentValue;
	
	@FieldMetaInfo(dataType = DataType.OBJECT, displayName = "overallReturn", description = "Overall Return")
	private OverallReturn overallReturn;

	@FieldMetaInfo(dataType = DataType.ARRAY, displayName = "properties", description = "Properties")
	private List<Integer> propertiesId;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getOriginalVaue() {
		return originalVaue;
	}

	public void setOriginalVaue(double originalVaue) {
		this.originalVaue = originalVaue;
	}

	public double getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(double currentValue) {
		this.currentValue = currentValue;
	}

	public OverallReturn getOverallReturn() {
		return overallReturn;
	}

	public void setOverallReturn(OverallReturn overallReturn) {
		this.overallReturn = overallReturn;
	}

	public List<Integer> getPropertiesId() {
		return propertiesId;
	}

	public void setPropertiesId(List<Integer> propertiesId) {
		this.propertiesId = propertiesId;
	}

	
}
