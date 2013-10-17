package com.proptiger.data.model.portfolio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Payment plan model
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "portfolio_listings_payment_plan")
public class PortfolioListingPaymentPlan {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "created_at")
	private Integer id;
	
	@Column(name = "user_property_id")
	private Integer forumUserPropertyId;
	
	@Column(name = "installment_no")
	private int installmentNumber;
	
	@Column(name = "amount")
	private Double amount;
	
	@Column(name = "due_date")
	private Date dueDate;
	
	@Column(name = "is_paid")
	private boolean paid;
	
	@Column(name = "payment_source")
	private PaymentSource paymentSource;
	
	@Column(name = "payment_date")
	private Date paymentDate;
	
	@Column(name = "submitted_by")
	private Integer submittedBy;
	
	@Column(name = "installment_name")
	private String installmentName;
	
	@Column(name = "payment_plan")
	private String paymentPlan;
	
	@Column(name = "component_name")
	private String componentName;
	
	@Column(name = "component_value")
	private Double componentValue;
	
	private Date createdAt;
	
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
	 * @return the forumUserPropertyId
	 */
	public Integer getForumUserPropertyId() {
		return forumUserPropertyId;
	}

	/**
	 * @param forumUserPropertyId the forumUserPropertyId to set
	 */
	public void setForumUserPropertyId(Integer forumUserPropertyId) {
		this.forumUserPropertyId = forumUserPropertyId;
	}

	/**
	 * @return the installmentNumber
	 */
	public int getInstallmentNumber() {
		return installmentNumber;
	}

	/**
	 * @param installmentNumber the installmentNumber to set
	 */
	public void setInstallmentNumber(int installmentNumber) {
		this.installmentNumber = installmentNumber;
	}

	/**
	 * @return the amount
	 */
	public Double getAmount() {
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}

	/**
	 * @return the dueDate
	 */
	public Date getDueDate() {
		return dueDate;
	}

	/**
	 * @param dueDate the dueDate to set
	 */
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	/**
	 * @return the paid
	 */
	public boolean isPaid() {
		return paid;
	}

	/**
	 * @param paid the paid to set
	 */
	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	/**
	 * @return the paymentSource
	 */
	public PaymentSource getPaymentSource() {
		return paymentSource;
	}

	/**
	 * @param paymentSource the paymentSource to set
	 */
	public void setPaymentSource(PaymentSource paymentSource) {
		this.paymentSource = paymentSource;
	}

	/**
	 * @return the paymentDate
	 */
	public Date getPaymentDate() {
		return paymentDate;
	}

	/**
	 * @param paymentDate the paymentDate to set
	 */
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	/**
	 * @return the submittedBy
	 */
	public Integer getSubmittedBy() {
		return submittedBy;
	}

	/**
	 * @param submittedBy the submittedBy to set
	 */
	public void setSubmittedBy(Integer submittedBy) {
		this.submittedBy = submittedBy;
	}

	/**
	 * @return the installmentName
	 */
	public String getInstallmentName() {
		return installmentName;
	}

	/**
	 * @param installmentName the installmentName to set
	 */
	public void setInstallmentName(String installmentName) {
		this.installmentName = installmentName;
	}

	/**
	 * @return the paymentPlan
	 */
	public String getPaymentPlan() {
		return paymentPlan;
	}

	/**
	 * @param paymentPlan the paymentPlan to set
	 */
	public void setPaymentPlan(String paymentPlan) {
		this.paymentPlan = paymentPlan;
	}

	/**
	 * @return the componentName
	 */
	public String getComponentName() {
		return componentName;
	}

	/**
	 * @param componentName the componentName to set
	 */
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	/**
	 * @return the componentValue
	 */
	public Double getComponentValue() {
		return componentValue;
	}

	/**
	 * @param componentValue the componentValue to set
	 */
	public void setComponentValue(Double componentValue) {
		this.componentValue = componentValue;
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
