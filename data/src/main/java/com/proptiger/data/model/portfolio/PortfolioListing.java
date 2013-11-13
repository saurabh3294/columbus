package com.proptiger.data.model.portfolio;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
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
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.proptiger.data.meta.DataType;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.model.Bank;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.model.ProjectType;
import com.proptiger.data.model.resource.NamedResource;
import com.proptiger.data.model.resource.Resource;

/**
 * This is a model object corresponding to a addressable property
 * @author Rajeev Pandey
 *
 */
@Entity
@Table(name = "portfolio_listings")
@ResourceMetaInfo
public class PortfolioListing implements NamedResource, Resource{

	@Id
	@FieldMetaInfo(displayName = "PortfolioListing Id", description = "PortfolioListing Id")
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer listingId;
	
	//custom fields start
	@Transient
	@FieldMetaInfo(displayName = "Project Name", description = "Project Name")
	private String projectName;
	@Transient
	private String builderName;
	@Transient
	private String locality;
	@Transient
	private String completionDate;
	@Transient
	private String projectStatus;
	
	@Transient
	@JsonIgnore
	private String cityName;
	//custom fields ends
	@Transient
	@FieldMetaInfo(dataType = DataType.OBJECT, displayName = "overallReturn", description = "Overall Return")
	@JsonUnwrapped
	private OverallReturn overallReturn;
	
	@FieldMetaInfo(displayName = "Type Id", description = "Type Id")
	@Column(name = "type_id")
	private Integer typeId;
	
	@FieldMetaInfo(displayName = "User Id", description = "User Id")
	@Column(name = "user_id")
	private Integer userId;
	
	@FieldMetaInfo(displayName = "Tower", description = "Tower")
	@Column(name = "tower")
	private String tower;
	
	@FieldMetaInfo(displayName = "Unit Number", description = "Unit Number")
	@Column(name = "unit_no")
	private String unitNo;
	
	@FieldMetaInfo(displayName = "Floor Number", description = "Floor Number")
	@Column(name = "floor_no")
	private int floorNo;
	
	@FieldMetaInfo(dataType = DataType.DATE, displayName = "Purchase Date", description = "Purchase Date")
	@Column(name = "purchased_date")
	private Date purchaseDate;
	
	@FieldMetaInfo(displayName = "Property Name", description = "Property Name")
	@Column(name = "property_name")
	private String name;
	
	@FieldMetaInfo(displayName = "Base Price", description = "Base Price")
	@Column(name = "base_price")
	private Double basePrice;
	
	@FieldMetaInfo(displayName = "Total Price", description = "Total Price")
	@Column(name = "total_price")
	private Double totalPrice;
	
	@FieldMetaInfo(displayName = "Current Price", description = "Current Price")
	@Transient
	private Double currentPrice;
	
	@FieldMetaInfo(displayName = "Goal Amount", description = "Goal Amount")
	@Column(name = "goal_amount")
	private Double goalAmount;
	
	@FieldMetaInfo(dataType = DataType.STRING, displayName = "Purchased For", description = "Purchased For")
	@Column(name = "purchased_for")
	@Enumerated(EnumType.STRING)
	private PurchasedFor purchasedFor;
	
	@FieldMetaInfo(dataType = DataType.STRING, displayName = "Property Id", description = "Property Id")
	@Column(name = "loan_status")
	@Enumerated(EnumType.STRING)
	private LoanStatus loanStatus;
	
	@FieldMetaInfo(dataType = DataType.STRING, displayName = "Property Id", description = "Property Id")
	@Column(name = "bank_id")
	private Integer bankId;
	
	@FieldMetaInfo(displayName = "Bank Id", description = "Bank Id")
	@Column(name = "loan_amount")
	private Double loanAmount;
	
	@FieldMetaInfo(displayName = "Property Id", description = "Property Id")
	@Column(name = "loan_availed_amount")
	private Double loanAvailedAmount;
	
	@FieldMetaInfo(dataType = DataType.STRING, displayName = "Property Id", description = "Property Id")
	@Column(name = "transaction_type")
	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;
	
	@Column(name = "interested_sell")
	@FieldMetaInfo(displayName = "Interested To Sell", description = "Interested To Sell")
	private Boolean interestedToSell = false;
	
	@Column(name = "interested_sell_on")
	private Date interestedToSellOn;
	
	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private ListingStatus listingStatus = ListingStatus.ACTIVE;
	
	
	
	@Column(name = "created_at")
	private Date createdAt;
	
	@Column(name = "updated_at")
	private Date updatedAt;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "type_id", nullable = false, insertable = false, updatable = false)
	@JsonUnwrapped
	private ProjectType projectType = null;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id",  nullable = false, insertable = false, updatable = false)
	@JsonIgnore
	private ForumUser forumUser;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bank_id",  nullable = false, insertable = false, updatable = false)
	@JsonIgnore
	private Bank bank;
	
	@OneToMany(mappedBy = "portfolioListing", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<PortfolioListingPrice> listingPrice;
	
	@OneToMany(mappedBy = "portfolioListing", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<PortfolioListingPaymentPlan> listingPaymentPlan;
	
	@Override
	public Integer getId() {
		return listingId;
	}

	@Override
	public void setId(Integer id) {
		this.listingId = id;
	}

	
	public Integer getListingId() {
		return listingId;
	}

	public void setListingId(Integer listingId) {
		this.listingId = listingId;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public String getTower() {
		return tower;
	}
	public void setTower(String tower) {
		this.tower = tower;
	}
	public String getUnitNo() {
		return unitNo;
	}
	public void setUnitNo(String unitNo) {
		this.unitNo = unitNo;
	}
	public int getFloorNo() {
		return floorNo;
	}
	public void setFloorNo(int floorNo) {
		this.floorNo = floorNo;
	}
	public Date getPurchaseDate() {
		return purchaseDate;
	}
	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
	public Double getBasePrice() {
		return basePrice;
	}
	public void setBasePrice(Double basePrice) {
		this.basePrice = basePrice;
	}
	public Double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}
	public Double getGoalAmount() {
		return goalAmount;
	}
	public void setGoalAmount(Double goalAmount) {
		this.goalAmount = goalAmount;
	}
	public PurchasedFor getPurchasedFor() {
		return purchasedFor;
	}
	public void setPurchasedFor(PurchasedFor purchasedFor) {
		this.purchasedFor = purchasedFor;
	}
	public LoanStatus getLoanStatus() {
		return loanStatus;
	}
	public void setLoanStatus(LoanStatus loanStatus) {
		this.loanStatus = loanStatus;
	}
	public Double getLoanAmount() {
		return loanAmount;
	}
	public void setLoanAmount(Double loanAmount) {
		this.loanAmount = loanAmount;
	}
	public Double getLoanAvailedAmount() {
		return loanAvailedAmount;
	}
	public void setLoanAvailedAmount(Double loanAvailedAmount) {
		this.loanAvailedAmount = loanAvailedAmount;
	}
	public TransactionType getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(TransactionType transactionType) {
		this.transactionType = transactionType;
	}
	public ProjectType getProjectType() {
		return projectType;
	}
	public void setProjectType(ProjectType projectTypes) {
		this.projectType = projectTypes;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
		
	}
	public Double getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(Double currentPrice) {
		this.currentPrice = currentPrice;
	}

	public Integer getBankId() {
		return bankId;
	}
	public void setBankId(Integer bankId) {
		this.bankId = bankId;
	}

	public Set<PortfolioListingPrice> getListingPrice() {
		return listingPrice;
	}

	public void setListingPrice(Set<PortfolioListingPrice> listingPrice) {
		this.listingPrice = listingPrice;
	}

	
	public Set<PortfolioListingPaymentPlan> getListingPaymentPlan() {
		return listingPaymentPlan;
	}

	public void setListingPaymentPlan(
			Set<PortfolioListingPaymentPlan> listingPaymentPlans) {
		this.listingPaymentPlan = listingPaymentPlans;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Boolean getInterestedToSell() {
		return interestedToSell;
	}

	public void setInterestedToSell(Boolean interestedToSell) {
		this.interestedToSell = interestedToSell;
	}

	public Date getInterestedToSellOn() {
		return interestedToSellOn;
	}

	public void setInterestedToSellOn(Date interestedToSellOn) {
		this.interestedToSellOn = interestedToSellOn;
	}

	public OverallReturn getOverallReturn() {
		return overallReturn;
	}

	public void setOverallReturn(OverallReturn overallReturn) {
		this.overallReturn = overallReturn;
	}

	public ListingStatus getListingStatus() {
		return listingStatus;
	}

	public void setListingStatus(ListingStatus status) {
		this.listingStatus = status;
	}

	
	public String getBuilderName() {
		return builderName;
	}

	public void setBuilderName(String builderName) {
		this.builderName = builderName;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(String completionDate) {
		this.completionDate = completionDate;
	}

	public String getProjectStatus() {
		return projectStatus;
	}

	public void setProjectStatus(String projectStatus) {
		this.projectStatus = projectStatus;
	}

	
	public ForumUser getForumUser() {
		return forumUser;
	}

	public void setForumUser(ForumUser forumUser) {
		this.forumUser = forumUser;
	}

	
	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
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
    
    public void update(PortfolioListing toUpdate){
    	this.name = toUpdate.name;
    	this.basePrice = toUpdate.basePrice;
    	this.floorNo = toUpdate.floorNo;
    	this.goalAmount = toUpdate.goalAmount;
    	this.loanAmount = toUpdate.loanAmount;
    	this.loanAvailedAmount = toUpdate.loanAvailedAmount;
    	this.loanStatus = toUpdate.loanStatus;
    	this.typeId = toUpdate.typeId;
    	this.purchaseDate = toUpdate.purchaseDate;
    	this.purchasedFor = toUpdate.purchasedFor;
    	this.totalPrice = toUpdate.totalPrice;
    	this.tower = toUpdate.tower;
    	this.transactionType = toUpdate.transactionType;
    	this.unitNo = toUpdate.unitNo;
    }
}
