package com.proptiger.data.internal.dto;

import java.util.List;

import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.model.portfolio.DashboardWidgetMapping;

/**
 * This POJO acts as a data transfer object for Dashboard object
 * @author Rajeev Pandey
 *
 */
@ResourceMetaInfo(name = "Dashboard")
public class DashboardDto{
	@FieldMetaInfo( displayName = "Dashboard Id",  description = "Dashboard Id")
	private Integer id;
	
	@FieldMetaInfo( displayName = "Dashboard Name",  description = "Dashboard Name")
	private String name;
	
	@FieldMetaInfo( displayName = "Total Rows",  description = "Total Rows")
	private int totalRow;
	
	@FieldMetaInfo( displayName = "Total Columns",  description = "Total Columns")
	private int totalColumn;
	
	@FieldMetaInfo( displayName = "User Id",  description = "User Id")
	private Integer userId;
	
	@FieldMetaInfo(dataType = DataType.ARRAY, displayName = "Widgets",  description = "Widgets")
	private List<DashboardWidgetMapping> widgets;
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
	public int getTotalRow() {
		return totalRow;
	}
	/**
	 * @param totalRows the totalRows to set
	 */
	public void setTotalRow(int totalRows) {
		this.totalRow = totalRows;
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
	/**
	 * @return the widgets
	 */
	public List<DashboardWidgetMapping> getWidgets() {
		return widgets;
	}
	/**
	 * @param widgets the widgets to set
	 */
	public void setWidgets(List<DashboardWidgetMapping> widgets) {
		this.widgets = widgets;
	}
	
}