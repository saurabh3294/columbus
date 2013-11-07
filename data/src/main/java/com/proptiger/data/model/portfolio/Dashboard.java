package com.proptiger.data.model.portfolio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.model.resource.NamedResource;
import com.proptiger.data.model.resource.Resource;

/**
 * Dashboard model object
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "dashboards")
public class Dashboard implements NamedResource, Resource{
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "total_row")
	private int totalRow;
	
	@Column(name = "total_column")
	private int totalColumn;
	
	@Column(name = "user_id")
	private Integer userId;

	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "updated_at")
	private Date updatedAt;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id",  nullable = false, insertable = false, updatable = false)
	@JsonIgnore
	private ForumUser forumUser;
	
	@OneToMany(mappedBy = "dashboardId", fetch = FetchType.EAGER)
	private List<DashboardWidgetMapping> widgets;
	
	/**
	 * @return the id
	 */
	@Override
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	@Override
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
	 * @return the totalColumns
	 */
	public int getTotalColumn() {
		return totalColumn;
	}

	/**
	 * @param totalColumns the totalColumns to set
	 */
	public void setTotalColumn(int totalColumns) {
		this.totalColumn = totalColumns;
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
	
	public void addWidget(DashboardWidgetMapping widget) {
		if(this.widgets == null){
			this.widgets = new ArrayList<>();
		}
		this.widgets.add(widget);
	}

	public static Builder getBuilder(String name, Integer userId){
		return new Builder(name, userId);
	}
	
	/**
     * A Builder class used to create new Dashboard object.
     */
    public static class Builder {
    	private Dashboard dashboard;
    	//required
    	private String name;
    	private Integer userId;
    	//optional
    	private int totalRows;
    	private int totalColumns;
    	private Integer id;
    	
    	Builder(String name, Integer userId){
    		this.name = name;
    		this.userId = userId;
    	}
    	
    	public Dashboard build(){
    		dashboard = new Dashboard();
    		dashboard.name = name;
    		dashboard.totalColumn = totalColumns;
    		dashboard.totalRow = totalRows;
    		dashboard.userId = userId;
    		dashboard.id = id;
    		return dashboard;
    	}

		/**
		 * @param totalRows the totalRows to set
		 */
		public Builder setTotalRows(int totalRows) {
			this.totalRows = totalRows;
			return this;
		}
		/**
		 * @param totalColumns the totalColumns to set
		 */
		public Builder setTotalColumns(int totalColumns) {
			this.totalColumns = totalColumns;
			return this;
		}

		/**
		 * @return the Builder
		 */
		public Builder setId(Integer id) {
			this.id =  id;
			return this;
		}

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
    
    public void update(String name, int totalColumns, int totalRows){
    	this.name = name;
    	this.totalColumn = totalColumns;
    	this.totalRow = totalRows;
    }
}
