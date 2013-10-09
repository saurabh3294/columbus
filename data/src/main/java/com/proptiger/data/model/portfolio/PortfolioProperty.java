package com.proptiger.data.model.portfolio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.model.ProjectType;
import com.proptiger.data.model.resource.NamedResource;

/**
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "portfolio_property")
@ResourceMetaInfo(name = "PortfolioProperty")
public class PortfolioProperty implements NamedResource{

	@Id
	@FieldMetaInfo(displayName = "Property Id", description = "Property Id")
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
	@FieldMetaInfo(displayName = "Property Id", description = "Property Id")
	@Column(name = "project_type_id")
	private Integer projectTypeId;
	
	@FieldMetaInfo(displayName = "User Id", description = "User Id")
	@Column(name = "user_id")
	private Integer userId;
	
	@FieldMetaInfo(displayName = "Tower", description = "Tower")
	@Column(name = "tower")
	private int tower;
	
	@FieldMetaInfo(displayName = "Property Id", description = "Property Id")
	@Column(name = "unit_no")
	private String unitNo;
	
	@FieldMetaInfo(displayName = "Property Id", description = "Property Id")
	@Column(name = "floor_no")
	private int floorNo;
	
	@FieldMetaInfo(dataType = DataType.DATE, displayName = "Purchase Date", description = "Purchase Date")
	@Column(name = "purchased_date")
	private Date purchaseDate;
	
	@FieldMetaInfo(displayName = "Property Id", description = "Property Id")
	@Column(name = "property_name")
	private String name;
	
	@FieldMetaInfo(displayName = "Property Id", description = "Property Id")
	@Column(name = "base_price")
	private Double basePrice;
	
	@FieldMetaInfo(displayName = "Property Id", description = "Property Id")
	@Column(name = "total_price")
	private Double totalPrice;
	
	@FieldMetaInfo(displayName = "Property Id", description = "Property Id")
	@Column(name = "goal_amount")
	private Double goalAmount;
	
	@FieldMetaInfo(dataType = DataType.STRING, displayName = "Property Id", description = "Property Id")
	@Column(name = "purchased_for")
	@Enumerated(EnumType.STRING)
	private PurchasedFor purchasedFor;
	
	@FieldMetaInfo(dataType = DataType.STRING, displayName = "Property Id", description = "Property Id")
	@Column(name = "payment_plan")
	@Enumerated(EnumType.STRING)
	private PaymentPlan paymentPlan;
	
	@FieldMetaInfo(dataType = DataType.STRING, displayName = "Property Id", description = "Property Id")
	@Column(name = "loan_status")
	@Enumerated(EnumType.STRING)
	private LoanStatus loanStatus;
	
	@FieldMetaInfo(displayName = "Property Id", description = "Property Id")
	@Column(name = "loan_amount")
	private Double loanAmount;
	
	@FieldMetaInfo(displayName = "Property Id", description = "Property Id")
	@Column(name = "loan_availed_amount")
	private Double loanAvailedAmount;
	
	@FieldMetaInfo(dataType = DataType.STRING, displayName = "Property Id", description = "Property Id")
	@Column(name = "transaction_type")
	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;
	
	@FieldMetaInfo(displayName = "Created At", description = "Created At")
	@Column(name = "created_at")
	private Date createdAt;
	
	@FieldMetaInfo(displayName = "Updated At", description = "Updated At")
	@Column(name = "updated_at")
	private Date updatedAt;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "project_type_id",  nullable = false, insertable = false, updatable = false)
	private ProjectType projectType;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id",  nullable = false, insertable = false, updatable = false)
	private ForumUser forumUser;
	
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
	 * @return the projectTypeId
	 */
	public Integer getProjectTypeId() {
		return projectTypeId;
	}

	/**
	 * @param projectTypeId the projectTypeId to set
	 */
	public void setProjectTypeId(Integer projectTypeId) {
		this.projectTypeId = projectTypeId;
	}

	/**
	 * @return the tower
	 */
	public int getTower() {
		return tower;
	}

	/**
	 * @param tower the tower to set
	 */
	public void setTower(int tower) {
		this.tower = tower;
	}

	/**
	 * @return the unitNo
	 */
	public String getUnitNo() {
		return unitNo;
	}

	/**
	 * @param unitNo the unitNo to set
	 */
	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}

	/**
	 * @return the floorNo
	 */
	public int getFloorNo() {
		return floorNo;
	}

	/**
	 * @param floorNo the floorNo to set
	 */
	public void setFloorNo(int floorNo) {
		this.floorNo = floorNo;
	}

	/**
	 * @return the purchaseDate
	 */
	public Date getPurchaseDate() {
		return purchaseDate;
	}

	/**
	 * @param purchaseDate the purchaseDate to set
	 */
	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	/**
	 * @return the basePrice
	 */
	public Double getBasePrice() {
		return basePrice;
	}

	/**
	 * @param basePrice the basePrice to set
	 */
	public void setBasePrice(Double basePrice) {
		this.basePrice = basePrice;
	}

	/**
	 * @return the totalPrice
	 */
	public Double getTotalPrice() {
		return totalPrice;
	}

	/**
	 * @param totalPrice the totalPrice to set
	 */
	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	/**
	 * @return the goalAmount
	 */
	public Double getGoalAmount() {
		return goalAmount;
	}

	/**
	 * @param goalAmount the goalAmount to set
	 */
	public void setGoalAmount(Double goalAmount) {
		this.goalAmount = goalAmount;
	}

	/**
	 * @return the purchasedFor
	 */
	public PurchasedFor getPurchasedFor() {
		return purchasedFor;
	}

	/**
	 * @param purchasedFor the purchasedFor to set
	 */
	public void setPurchasedFor(PurchasedFor purchasedFor) {
		this.purchasedFor = purchasedFor;
	}

	/**
	 * @return the paymentPlan
	 */
	public PaymentPlan getPaymentPlan() {
		return paymentPlan;
	}

	/**
	 * @param paymentPlan the paymentPlan to set
	 */
	public void setPaymentPlan(PaymentPlan paymentPlan) {
		this.paymentPlan = paymentPlan;
	}

	/**
	 * @return the loanStatus
	 */
	public LoanStatus getLoanStatus() {
		return loanStatus;
	}

	/**
	 * @param loanStatus the loanStatus to set
	 */
	public void setLoanStatus(LoanStatus loanStatus) {
		this.loanStatus = loanStatus;
	}

	/**
	 * @return the loanAmount
	 */
	public Double getLoanAmount() {
		return loanAmount;
	}

	/**
	 * @param loanAmount the loanAmount to set
	 */
	public void setLoanAmount(Double loanAmount) {
		this.loanAmount = loanAmount;
	}

	/**
	 * @return the loanAvailedAmount
	 */
	public Double getLoanAvailedAmount() {
		return loanAvailedAmount;
	}

	/**
	 * @param loanAvailedAmount the loanAvailedAmount to set
	 */
	public void setLoanAvailedAmount(Double loanAvailedAmount) {
		this.loanAvailedAmount = loanAvailedAmount;
	}

	/**
	 * @return the transactionType
	 */
	public TransactionType getTransactionType() {
		return transactionType;
	}

	/**
	 * @param transactionType the transactionType to set
	 */
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
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
	 * @return the projectTypes
	 */
	public ProjectType getProjectType() {
		return projectType;
	}

	/**
	 * @param projectTypes the projectTypes to set
	 */
	public void setProjectType(ProjectType projectTypes) {
		this.projectType = projectTypes;
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
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
		
	}
	
}
