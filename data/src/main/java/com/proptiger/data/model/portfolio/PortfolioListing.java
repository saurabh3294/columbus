package com.proptiger.data.model.portfolio;

import java.io.IOException;
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

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.proptiger.data.enums.DataType;
import com.proptiger.data.enums.portfolio.ListingStatus;
import com.proptiger.data.enums.portfolio.LoanStatus;
import com.proptiger.data.enums.portfolio.PurchasedFor;
import com.proptiger.data.enums.portfolio.TransactionType;
import com.proptiger.data.internal.dto.mail.ListingAddMail;
import com.proptiger.data.internal.dto.mail.ListingLoanRequestMail;
import com.proptiger.data.internal.dto.mail.ListingResaleMail;
import com.proptiger.data.meta.FieldMetaInfo;
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.model.Bank;
import com.proptiger.data.model.BaseModel;
import com.proptiger.data.model.ForumUser;
import com.proptiger.data.model.Property;
import com.proptiger.data.model.image.Image;

/**
 * This is a model object corresponding to a addressable property
 * 
 * @author Rajeev Pandey
 * 
 */
@Entity
@Table(name = "portfolio_listings")
@ResourceMetaInfo
@JsonFilter("fieldFilter")
public class PortfolioListing extends BaseModel{

    public enum Source {
        portfolio("portfolio"), lead("lead"), backend("backend");

        public String source;

        Source(String source) {
            this.source = source;
        }
    }

    private static final long                serialVersionUID = -6567536809813945234L;

    @Id
    @FieldMetaInfo(displayName = "PortfolioListing Id", description = "PortfolioListing Id")
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer                          listingId;

    @FieldMetaInfo(displayName = "Project Name", description = "Project Name")
    @Column(name = "project_name")
    private String                           projectName;
    @Column(name = "locality_label")
    private String                           locality;
    @Column(name = "locality_id")
    private Integer                          localityId;
    @Column(name = "city_label")
    private String                           cityName;
    @Column(name = "city_id")
    private Integer                          cityId;

    // custom fields start
    @Transient
    private Integer                          oldProjectId;
    @Transient
    private String                           builderName;
    @Transient
    private Date                             completionDate;
    @Transient
    private String                           projectStatus;
    @Transient
    private List<Image>                      propertyImages;
    // custom fields ends

    @Transient
    @FieldMetaInfo(dataType = DataType.OBJECT, displayName = "overallReturn", description = "Overall Return")
    @JsonUnwrapped
    private OverallReturn                    overallReturn;

    @FieldMetaInfo(displayName = "Type Id", description = "Type Id")
    @Column(name = "type_id")
    private Integer                          typeId;

    @FieldMetaInfo(displayName = "User Id", description = "User Id")
    @Column(name = "user_id")
    private Integer                          userId;

    @FieldMetaInfo(displayName = "Tower", description = "Tower")
    @Column(name = "tower")
    private String                           tower;

    @FieldMetaInfo(displayName = "Unit Number", description = "Unit Number")
    @Column(name = "unit_no")
    private String                           unitNo;

    @FieldMetaInfo(displayName = "Floor Number", description = "Floor Number")
    @Column(name = "floor_no")
    private String                           floorNo;

    @FieldMetaInfo(displayName = "Phase", description = "Phase")
    @Column(name = "phase")
    private String                           phase;

    @FieldMetaInfo(displayName = "Listing Size", description = "Listing Size")
    @Column(name = "size")
    private Double                           listingSize;

    @Transient
    private String                           listingMeasure   = "sq ft";

    @FieldMetaInfo(dataType = DataType.DATE, displayName = "Purchase Date", description = "Purchase Date")
    @Column(name = "purchased_date")
    private Date                             purchaseDate;

    @FieldMetaInfo(displayName = "Property Name", description = "Property Name")
    @Column(name = "property_name")
    private String                           name;

    @FieldMetaInfo(displayName = "Base Price", description = "Base Price")
    @Column(name = "base_price")
    private Double                           basePrice;

    @FieldMetaInfo(displayName = "Total Price", description = "Total Price")
    @Column(name = "total_price")
    private Double                           totalPrice;

    @FieldMetaInfo(displayName = "Current Price", description = "Current Price")
    @Transient
    private Double                           currentPrice;

    @FieldMetaInfo(displayName = "Goal Amount", description = "Goal Amount")
    @Column(name = "goal_amount")
    private Double                           goalAmount;

    @FieldMetaInfo(dataType = DataType.STRING, displayName = "Purchased For", description = "Purchased For")
    @Column(name = "purchased_for")
    @Enumerated(EnumType.STRING)
    private PurchasedFor                     purchasedFor;

    @FieldMetaInfo(dataType = DataType.STRING, displayName = "Property Id", description = "Property Id")
    @Column(name = "loan_status")
    @Enumerated(EnumType.STRING)
    private LoanStatus                       loanStatus;

    @FieldMetaInfo(dataType = DataType.STRING, displayName = "Property Id", description = "Property Id")
    @Column(name = "bank_id")
    private Integer                          bankId;

    @FieldMetaInfo(displayName = "Bank Id", description = "Bank Id")
    @Column(name = "loan_amount")
    private Double                           loanAmount;

    @FieldMetaInfo(displayName = "Property Id", description = "Property Id")
    @Column(name = "loan_availed_amount")
    private Double                           loanAvailedAmount;

    @FieldMetaInfo(dataType = DataType.STRING, displayName = "Transaction Type", description = "Transaction Type")
    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionType                  transactionType;

    @Column(name = "interested_sell")
    @FieldMetaInfo(displayName = "Interested To Sell", description = "Interested To Sell")
    private Boolean                          interestedToSell = false;

    @Column(name = "interested_sell_on")
    private Date                             interestedToSellOn;

    @Column(name = "interested_loan")
    @FieldMetaInfo(displayName = "Interested To Home Loan", description = "Interested To Home Loan")
    private Boolean                          interestedToLoan = false;

    @Column(name = "interested_loan_on")
    private Date                             interestedToLoanOn;

    @Column(name = "loan_type")
    private String                           loanType;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ListingStatus                    listingStatus    = ListingStatus.ACTIVE;

    @Column(name = "created_at")
    private Date                             createdAt;

    @Column(name = "updated_at")
    private Date                             updatedAt;

    @Column(name = "isBroker")
    // @JsonIgnore
    private Boolean                          isBroker;

    @Column(name = "source_type")
    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @JsonDeserialize(using = SourceTypeDeserializer.class)
    private Source                           sourceType = Source.portfolio;

    @Column(name = "lead_user")
    private String                           leadUser;

    @Column(name = "lead_email")
    private String                           leadEmail;

    @Column(name = "lead_contact")
    private Long                             leadContact;

    @Column(name = "lead_country_id")
    private Integer                          leadCountryId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id", nullable = false, insertable = false, updatable = false)
    @JsonUnwrapped
    private Property                         property;

    @FieldMetaInfo(displayName = "Deleted Flag", description = "Whether a listing has been soft deleted")
    @Column(name = "deleted_flag")
    public Boolean                           deletedFlag  =  false;

    @FieldMetaInfo(displayName = "Reason", description = "Reason for deleting a listing")
    @Column(name = "reason")
    @JsonIgnore
    public String                            reason;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    private ForumUser                        forumUser;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_id", nullable = false, insertable = false, updatable = false)
    @JsonIgnore
    private Bank                             bank;

    @OneToMany(mappedBy = "portfolioListing", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<PortfolioListingPrice>       otherPrices;

    @OneToMany(mappedBy = "portfolioListing", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<PortfolioListingPaymentPlan> listingPaymentPlan;

    @FieldMetaInfo(displayName = "Project Id", description = "Project Id")
    @Column(name = "project_id")
    private Integer                          projectId;

    @Column(name = "active_enquiries_count")
    private Integer                          activeEnquiriesCount;

    public Integer getId() {
        return listingId;
    }

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

    public String getFloorNo() {
        return floorNo;
    }

    public void setFloorNo(String floorNo) {
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

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

    public Set<PortfolioListingPrice> getOtherPrices() {
        return otherPrices;
    }

    public void setOtherPrices(Set<PortfolioListingPrice> listingPrice) {
        this.otherPrices = listingPrice;
    }

    public Set<PortfolioListingPaymentPlan> getListingPaymentPlan() {
        return listingPaymentPlan;
    }

    public void setListingPaymentPlan(Set<PortfolioListingPaymentPlan> listingPaymentPlans) {
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

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
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

    public Integer getLocalityId() {
        return localityId;
    }

    public void setLocalityId(Integer localityId) {
        this.localityId = localityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Double getListingSize() {
        return listingSize;
    }

    public void setListingSize(Double size) {
        this.listingSize = size;
    }

    public Boolean getInterestedToLoan() {
        return interestedToLoan;
    }

    public void setInterestedToLoan(Boolean interestedToLoan) {
        this.interestedToLoan = interestedToLoan;
    }

    public Date getInterestedToLoanOn() {
        return interestedToLoanOn;
    }

    public void setInterestedToLoanOn(Date interestedToLoanOn) {
        this.interestedToLoanOn = interestedToLoanOn;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public String getListingMeasure() {
        return listingMeasure;
    }

    public void setListingMeasure(String listingMeasure) {
        this.listingMeasure = listingMeasure;
    }

    public Integer getOldProjectId() {
        return oldProjectId;
    }

    public void setOldProjectId(Integer projectId) {
        this.oldProjectId = projectId;
    }

    public List<Image> getPropertyImages() {
        return propertyImages;
    }

    public void setPropertyImages(List<Image> propertyImages) {
        this.propertyImages = propertyImages;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Date();
    }

    @PrePersist
    public void prePersist() {
        createdAt = new Date();
        updatedAt = createdAt;
    }

    public void update(PortfolioListing toUpdate) {
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
        this.bankId = toUpdate.bankId;
        this.listingSize = toUpdate.listingSize;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public Boolean getIsBroker() {
        return isBroker;
    }

    public void setIsBroker(Boolean isBroker) {
        this.isBroker = isBroker;
    }

    public Source getSourceType() {
        return sourceType;
    }

    public void setSourceType(Source sourceType) {
        this.sourceType = sourceType;
    }

    public String getLeadUser() {
        return leadUser;
    }

    public void setLeadUser(String leadUser) {
        this.leadUser = leadUser;
    }

    public String getLeadEmail() {
        return leadEmail;
    }

    public void setLeadEmail(String leadEmail) {
        this.leadEmail = leadEmail;
    }

    public Long getLeadContact() {
        return leadContact;
    }

    public void setLeadContact(Long leadContact) {
        this.leadContact = leadContact;
    }

    public Integer getLeadCountryId() {
        return leadCountryId;
    }

    public void setLeadCountryId(Integer leadCountryId) {
        this.leadCountryId = leadCountryId;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Boolean getDeleted_flag() {
        return deletedFlag;
    }

    public void setDeleted_flag(Boolean deletedFlag) {
        this.deletedFlag = deletedFlag;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public Integer getActiveEnquiriesCount() {
        return activeEnquiriesCount;
    }

    public void setActiveEnquiriesCount(Integer activeEnquiriesCount) {
        this.activeEnquiriesCount = activeEnquiriesCount;
    }

    class SourceTypeDeserializer extends JsonDeserializer<String> {
        @Override
        public String deserialize(JsonParser parser, DeserializationContext context) 
                throws IOException, JsonProcessingException {
            return parser.getValueAsString();
        }
    }
    
    /**
     * Creating listing loan request object details
     * 
     * @param listing
     * @return
     */
    public ListingLoanRequestMail createListingLoanRequestObj() {
        ForumUser forumUser = this.getForumUser();
        ListingLoanRequestMail listingLoanRequestMail = new ListingLoanRequestMail();
        listingLoanRequestMail.setProjectCity(this.getCityName());
        listingLoanRequestMail.setProjectName(this.getProjectName());
        listingLoanRequestMail.setUserName(this.getForumUser().getUsername());
        listingLoanRequestMail.setEmail(forumUser.getEmail());
        listingLoanRequestMail.setMobile(forumUser.getContact() + "");
        return listingLoanRequestMail;
    }

    /**
     * Creating listing resale mail object
     * 
     * @param listing
     * @return
     */
    public ListingResaleMail createListingResaleMailObj(String websiteHost) {
        String url = websiteHost + this.getProperty().getURL();
        ForumUser forumUser = this.getForumUser();
        ListingResaleMail listingResaleMail = new ListingResaleMail();
        listingResaleMail.setBuilder(this.getBuilderName());
        listingResaleMail.setLocality(this.getLocality());
        listingResaleMail.setProjectCity(this.getCityName());
        listingResaleMail.setProjectName(this.getProjectName());
        listingResaleMail.setPropertyLink(url.toString());
        listingResaleMail.setPropertyName(this.getName());
        listingResaleMail.setUserName(forumUser.getUsername());
        listingResaleMail.setEmail(forumUser.getEmail());
        listingResaleMail.setMobile(forumUser.getContact() + "");
        listingResaleMail.setListingSize(this.getListingSize());
        listingResaleMail.setMeasure(this.getProperty().getMeasure());
        listingResaleMail.setUnitName(this.getProperty().getUnitName());
        return listingResaleMail;
    }
    /**
     * Creating listing add mail object
     * 
     * @param listing
     * @return
     */
    public ListingAddMail createListingAddMailObject() {
        ListingAddMail listingAddMail = new ListingAddMail();
        listingAddMail.setPropertyName(this.getName());
        listingAddMail.setPurchaseDate(this.getPurchaseDate());
        listingAddMail.setTotalPrice(this.getTotalPrice());
        listingAddMail.setUserName(this.getForumUser().getUsername());
        return listingAddMail;
    }

    /**
     * Updating sell interest of user for listing
     */
    public void updateInterestedToSell(
            Boolean interestedToSell) {
        this.setInterestedToSell(interestedToSell);
        this.setInterestedToSellOn(new Date());
    }
    /**
     * Updating loan interest of user for listing
     */
    public void updateLoanInterest(
            Boolean interestedToLoan,
            String loanType) {
        this.setInterestedToLoan(interestedToLoan);
        this.setInterestedToLoanOn(new Date());
        this.setLoanType(loanType);
    }
}
