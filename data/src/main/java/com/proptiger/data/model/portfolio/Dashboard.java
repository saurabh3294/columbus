package com.proptiger.data.model.portfolio;

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

import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.model.resource.NamedResource;

/**
 * Dashboard model object
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "dashboards")
@ResourceMetaInfo(name = "Dashboard")
public class Dashboard implements NamedResource{
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	@FieldMetaInfo( displayName = "Dashboard Id",  description = "Dashboard Id")
	private Integer id;
	
	@Column(name = "name")
	@FieldMetaInfo( displayName = "Dashboard Name",  description = "Dashboard Name")
	private String name;
	
	@Column(name = "total_row")
	@FieldMetaInfo( displayName = "Total Rows",  description = "Total Rows")
	private int totalRows;
	
	@Column(name = "total_column")
	@FieldMetaInfo( displayName = "Total Columns",  description = "Total Columns")
	private int totalColumns;
	
	@Column(name = "user_id")
	@FieldMetaInfo( displayName = "User Id",  description = "User Id")
	private Integer userId;

	@Column(name = "created_at")
	@FieldMetaInfo( displayName = "Created Time",  description = "Created Time")
	private Date createdAt;
	
	@Column(name = "updated_at")
	@FieldMetaInfo( displayName = "Updated Time",  description = "Updated Time")
	private Date updatedAt;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id",  nullable = false, insertable = false, updatable = false)
	private ForumUser forumUser;
	
	@OneToMany(mappedBy = "dashboardId", fetch = FetchType.EAGER)
	private List<DashboardWidgetMapping> dashboardWidgetMapping;
	
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
	 * @return the totalColumns
	 */
	public int getTotalColumns() {
		return totalColumns;
	}

	/**
	 * @param totalColumns the totalColumns to set
	 */
	public void setTotalColumns(int totalColumns) {
		this.totalColumns = totalColumns;
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

	
	/**
	 * @return the dashboardWidgetMapping
	 */
	public List<DashboardWidgetMapping> getDashboardWidgetMapping() {
		return dashboardWidgetMapping;
	}

	/**
	 * @param dashboardWidgetMapping the dashboardWidgetMapping to set
	 */
	public void setDashboardWidgetMapping(
			List<DashboardWidgetMapping> dashboardWidgetMapping) {
		this.dashboardWidgetMapping = dashboardWidgetMapping;
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
    		dashboard.totalColumns = totalColumns;
    		dashboard.totalRows = totalRows;
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
    	this.totalColumns = totalColumns;
    	this.totalRows = totalRows;
    }
}
