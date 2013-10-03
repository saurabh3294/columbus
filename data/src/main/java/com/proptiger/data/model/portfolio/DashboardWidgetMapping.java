package com.proptiger.data.model.portfolio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

@Entity
@Table(name = "dashboard_widget_mapping")
@ResourceMetaInfo(name = "DashboardWidgetMapping")
public class DashboardWidgetMapping {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	@FieldMetaInfo( displayName = "Dashboard Widget Mapping Id",  description = "Dashboard Widget Mapping Id")
	private Integer id;
	
	@Column(name = "dashboard_id")
	@FieldMetaInfo( displayName = "Dashboard Id",  description = "Dashboard Id")
	private Integer dashboardId;
	
	@Column(name = "widget_id")
	@FieldMetaInfo( displayName = "Widget Id",  description = "Widget Id")
	private Integer widgetId;
	
	@Column(name = "widget_row_position")
	@FieldMetaInfo( displayName = "Widget Row Position",  description = "Widget Row Position")
	private int widgetRowPosition;
	
	@Column(name = "widget_column_position")
	@FieldMetaInfo( displayName = "Widget Column Position",  description = "Widget Column Position")
	private int widgetColumnPosition;
	
	@Column(name = "status")
	@FieldMetaInfo( displayName = "Created Time",  description = "Created Time")
	private String status;
	
	@Column(name = "created_at")
	@FieldMetaInfo( displayName = "Created Time",  description = "Created Time")
	private Date createdAt;
	
	@Column(name = "updated_at")
	@FieldMetaInfo( displayName = "Updated Time",  description = "Updated Time")
	private Date updatedAt;

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
	 * @return the dashboardId
	 */
	public Integer getDashboardId() {
		return dashboardId;
	}

	/**
	 * @param dashboardId the dashboardId to set
	 */
	public void setDashboardId(Integer dashboardId) {
		this.dashboardId = dashboardId;
	}

	/**
	 * @return the widgetId
	 */
	public Integer getWidgetId() {
		return widgetId;
	}

	/**
	 * @param widgetId the widgetId to set
	 */
	public void setWidgetId(Integer widgetId) {
		this.widgetId = widgetId;
	}

	/**
	 * @return the widgetRowPosition
	 */
	public int getWidgetRowPosition() {
		return widgetRowPosition;
	}

	/**
	 * @param widgetRowPosition the widgetRowPosition to set
	 */
	public void setWidgetRowPosition(int widgetRowPosition) {
		this.widgetRowPosition = widgetRowPosition;
	}

	/**
	 * @return the widgetColumnPosition
	 */
	public int getWidgetColumnPosition() {
		return widgetColumnPosition;
	}

	/**
	 * @param widgetColumnPosition the widgetColumnPosition to set
	 */
	public void setWidgetColumnPosition(int widgetColumnPosition) {
		this.widgetColumnPosition = widgetColumnPosition;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the createdAt
	 */
	public Date getCreatedAt() {
		return createdAt;
	}

	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	/**
	 * @return the updatedAt
	 */
	public Date getUpdatedAt() {
		return updatedAt;
	}

	/**
	 * @param updatedAt the updatedAt to set
	 */
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	
}
