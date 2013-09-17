package com.proptiger.data.model.meta;


/**
 * Contains all the information relate to a field in data model class.
 * These information could be used by client and will serve as a central repository
 * for field name, display name and their data type information.
 * 
 * @author Rajeev Pandey
 *
 */
public class FieldMetaData {
	private String name;
	private String displayName;
	private String description;
	private String dataType;
	private boolean editable;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
}
