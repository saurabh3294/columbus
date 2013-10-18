package com.proptiger.data.model.portfolio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 * Payment plan model
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "portfolio_listings_payment_plan")
@ResourceMetaInfo(name = "PortfolioListingPaymentPlan")
public class PortfolioListingPaymentPlan {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	@FieldMetaInfo(displayName = "Portfolio Listing Payment Plan Id", description = "Portfolio Listing Price Id")
	private Integer id;
	
	@Column(name = "installment_no")
	@FieldMetaInfo(displayName = "Installment Number", description = "Installment Number")
	private int installmentNumber;
	
	@FieldMetaInfo(displayName = "Amount", description = "Amount")
	@Column(name = "amount")
	private Double amount;
	
	@FieldMetaInfo(displayName = "Due Date", description = "Due Date")
	@Column(name = "due_date")
	private Date dueDate;
	
	@FieldMetaInfo(dataType = DataType.STRING, displayName = "Payment Plan", description = "Payment Plan")
	@Column(name = "payment_plan")
	@Enumerated(EnumType.STRING)
	private PaymentPlan paymentPlan;
	
	@FieldMetaInfo(dataType = DataType.STRING, displayName = "Payment Status", description = "Payment Status")
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private PaymentStatus status;
	
	@FieldMetaInfo(dataType = DataType.STRING, displayName = "Payment Source", description = "Payment Source")
	@Column(name = "payment_source")
	@Enumerated(EnumType.STRING)
	private PaymentSource paymentSource;
	
	@FieldMetaInfo(displayName = "Payment Date", description = "Payment Date")
	@Column(name = "payment_date")
	private Date paymentDate;
	
	@FieldMetaInfo(displayName = "Installment Name", description = "Installment Name")
	@Column(name = "installment_name")
	private String installmentName;
	
	@FieldMetaInfo(displayName = "Component Name", description = "Component Name")
	@Column(name = "component_name")
	private String componentName;
	
	@FieldMetaInfo(displayName = "Component Value", description = "Component Value")
	@Column(name = "component_value")
	private Double componentValue;
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "updated_at")
	private Date updatedAt;
	
	@ManyToOne
	@JsonIgnore
	@JoinColumn(name = "portfolio_listings_id", referencedColumnName="id")
	private PortfolioListing portfolioListing;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public int getInstallmentNumber() {
		return installmentNumber;
	}
	public void setInstallmentNumber(int installmentNumber) {
		this.installmentNumber = installmentNumber;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}
	public PaymentPlan getPaymentPlan() {
		return paymentPlan;
	}
	public void setPaymentPlan(PaymentPlan paymentPlan) {
		this.paymentPlan = paymentPlan;
	}
	public PaymentSource getPaymentSource() {
		return paymentSource;
	}
	public void setPaymentSource(PaymentSource paymentSource) {
		this.paymentSource = paymentSource;
	}
	public Date getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	public String getInstallmentName() {
		return installmentName;
	}
	public void setInstallmentName(String installmentName) {
		this.installmentName = installmentName;
	}
	public String getComponentName() {
		return componentName;
	}
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	public Double getComponentValue() {
		return componentValue;
	}
	public void setComponentValue(Double componentValue) {
		this.componentValue = componentValue;
	}
	
	public void setPortfolioListing(PortfolioListing portfolioListing) {
		this.portfolioListing = portfolioListing;
	}
	
	public PortfolioListing getPortfolioListing() {
		return portfolioListing;
	}
	public PaymentStatus getStatus() {
		return status;
	}
	public void setStatus(PaymentStatus status) {
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
}