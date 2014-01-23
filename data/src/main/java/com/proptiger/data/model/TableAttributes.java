package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.data.meta.FieldMetaInfo;

@Entity
@Table(name = "cms.table_attributes")
@JsonFilter("fieldFilter")
@JsonInclude(Include.NON_NULL)
public class TableAttributes  implements BaseModel {
	@Id
	@Column(name="id")
	@FieldMetaInfo( displayName = "Id",  description = "Id")
	private long id;
	
	@Column(name="table_name")
	@FieldMetaInfo( displayName = "Table Name",  description = "Table Name")
	private String tableName;
	
	@Column(name="table_id")
	@FieldMetaInfo( displayName = "Table Id",  description = "Table Id")
	private int tableId;
	
	@Column(name="attribute_name")
	@FieldMetaInfo( displayName = "Attribute Name",  description = "Attribute Name")
	private String attributeName;
	
	@Column(name="attribute_value")
	@FieldMetaInfo( displayName = "Attribute Value",  description = "Attribute Value")
	private String attributeValue;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public int getTableId() {
		return tableId;
	}

	public void setTableId(int tableId) {
		this.tableId = tableId;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getAttributeValue() {
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}
	
	

}
