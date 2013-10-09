package com.proptiger.data.dto;

import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.model.resource.Resource;

/**
 * This POJO acts as a data transfer object for Dashboard object
 * @author Rajeev Pandey
 *
 */
@ResourceMetaInfo(name = "Dashboard")
public class DashboardDto implements Resource{
	@FieldMetaInfo( displayName = "Dashboard Id",  description = "Dashboard Id")
	private Integer id;
	
	@FieldMetaInfo( displayName = "Dashboard Name",  description = "Dashboard Name")
	private String name;
	
	@FieldMetaInfo( displayName = "Total Rows",  description = "Total Rows")
	private int totalRows;
	
	@FieldMetaInfo( displayName = "Total Columns",  description = "Total Columns")
	private int totalColumn;
	
	@FieldMetaInfo( displayName = "User Id",  description = "User Id")
	private Integer userId;
	
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the totalRows
	 */
	public int getTotalRows() {
		return totalRows;
	}
	/**
	 * @param totalRows the totalRows to set
	 */
	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}
	/**
	 * @return the totalColumn
	 */
	public int getTotalColumn() {
		return totalColumn;
	}
	/**
	 * @param totalColumn the totalColumn to set
	 */
	public void setTotalColumn(int totalColumn) {
		this.totalColumn = totalColumn;
	}
	/**
	 * @return the userId
	 */
	public Integer getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
}
