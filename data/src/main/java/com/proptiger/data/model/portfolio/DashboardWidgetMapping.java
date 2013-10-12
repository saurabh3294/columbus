package com.proptiger.data.model.portfolio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.model.resource.Resource;

/**
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "dashboard_widget_mapping")
@ResourceMetaInfo(name = "DashboardWidgetMapping")
public class DashboardWidgetMapping implements Resource{

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonIgnore//ignoring this field from serialization as this is not needed
	private Integer id;
	
	@Column(name = "dashboard_id")
	@FieldMetaInfo( displayName = "Dashboard Id",  description = "Dashboard Id")
	@JsonIgnore//ignoring this field from serialization as this object will be pard of Dashboard object
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
	@FieldMetaInfo(dataType = DataType.STRING, displayName = "Status",  description = "Status")
	@Enumerated(EnumType.STRING)
	private WidgetDisplayStatus status;
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "updated_at")
	private Date updatedAt;

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
	public WidgetDisplayStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(WidgetDisplayStatus status) {
		this.status = status;
	}
	public void update(int widgetRowPosition, int widgetColumnPosition, WidgetDisplayStatus status){
		this.widgetRowPosition = widgetRowPosition;
		this.widgetColumnPosition = widgetColumnPosition;
		this.status = status;
	}
	@PreUpdate
    public void preUpdate(){
    	updatedAt = new Date();
    }
    @PrePersist
    public void prePersist(){
    	createdAt = new Date();
    	updatedAt = createdAt;
    }

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}
}
