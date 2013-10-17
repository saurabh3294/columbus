package com.proptiger.data.model.portfolio;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "portfolio_listings_price")
@ResourceMetaInfo(name = "PortfolioListingPrice")
public class PortfolioListingPrice {

	@Id
	@FieldMetaInfo(displayName = "Portfolio Listing Price Id", description = "Portfolio Listing Price Id")
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@FieldMetaInfo(displayName = "Amount", description = "Amount")
	@Column(name = "amount")
	private Double amount;
	
	@FieldMetaInfo(displayName = "Component Name", description = "Component Name")
	@Column(name = "component_name")
	private String componentName;
	
	@ManyToOne
	@JsonIgnore
	@JoinColumn(name = "portfolio_listings_id", referencedColumnName="id")
	private PortfolioListing portfolioListing;
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "updated_at")
	private Date updatedAt;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getComponentName() {
		return componentName;
	}
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	public void setPortfolioListing(PortfolioListing portfolioListing) {
		this.portfolioListing = portfolioListing;
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
}
