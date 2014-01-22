package com.proptiger.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ManyToAny;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proptiger.data.model.portfolio.SubscriptionType;

@Entity
@Table(name="subscription")
public class Subscription implements BaseModel {
	
	@Id
	@Column(name = "id")
	private int id;
	
	@Column(name = "table_name")
	private String tableName;
	
	@Column(name = "table_id")
	private int tableId;
	
	@Column(name = "user_id")
	private int userId;
	
	@Column(name = "is_subscribed")
	private String isSubscribed = "1";
	
	@ManyToOne
	@JoinColumn(name = "subscription_type_id", insertable = false, updatable = false)
	private SubscriptionType subscriptionType;
	
	@Column(name = "reason")
	private String reason;
	
	@Column(name = "subscription_type_id")
	private int subscriptionTypeId;
	
	@Column(name = "created_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	
	@Column(name = "updated_by")
	@JsonIgnore
	private int updatedBy = 0;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
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

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getIsSubscribed() {
		return isSubscribed;
	}

	public void setIsSubscribed(String isSubscribed) {
		this.isSubscribed = isSubscribed;
	}

	public SubscriptionType getSubscriptionType() {
		return subscriptionType;
	}

	public void setSubscriptionType(SubscriptionType subscriptionType) {
		this.subscriptionType = subscriptionType;
	}

	public int getSubscriptionTypeId() {
		return subscriptionTypeId;
	}

	public void setSubscriptionTypeId(int subscriptionTypeId) {
		this.subscriptionTypeId = subscriptionTypeId;
	}
	
	@PrePersist
	public void prePersist(){
		this.createdAt = new Date();
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public int getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(int updatedBy) {
		this.updatedBy = updatedBy;
	}

}
