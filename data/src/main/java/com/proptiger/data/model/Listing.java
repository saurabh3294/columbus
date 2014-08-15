package com.proptiger.data.model;

import java.util.Date;
import java.util.List;

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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.data.enums.ListingCategory;
import com.proptiger.data.enums.Status;

/**
 * 
 * @author azi
 * @author Rajeev
 * 
 */
@Entity
@Table(name = "cms.listings")
@JsonFilter("fieldFilter")
@JsonInclude(Include.NON_NULL)
public class Listing extends BaseModel {
    private static final long    serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer              id;

    @Column(name = "option_id")
    private Integer              propertyId;

    @Column(name = "phase_id")
    private Integer              phaseId;

    @Column(name = "tower_id")
    private Integer              towerId;

    @Column(name = "floor")
    private Integer              floor;

    @Column(name = "additional_features")
    private String               additionalFeatures;

    @Enumerated(EnumType.STRING)
    @Column(name = "listing_category")
    private ListingCategory      listingCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status               status;

    @Column(name = "booking_status_id")
    private Integer              bookingStatusId;

    @Column(name = "seller_id")
    private Integer              sellerId;

    @Column(name = "current_price_id")
    private Integer              currentPriceId;

    @Column(name = "updated_by")
    private Integer              updatedBy;

    @Column(name = "created_at")
    private Date                 createdAt;

    @Column(name = "updated_at")
    private Date                 updatedAt;

    @Transient
    private ListingPrice         currentListingPrice;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "listingId", cascade = CascadeType.ALL)
    private List<ProjectSupply>  projectSupply;

    @OneToMany(mappedBy = "listingId", fetch = FetchType.LAZY)
    private List<ListingPrice>   listingPrices;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false, insertable = false, updatable = false)
    private Property             property;

    @Transient
    private List<ListingAmenity> listingAmenities;

    @Transient
    private OtherInfo            otherInfo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Integer propertyId) {
        this.propertyId = propertyId;
    }

    public Integer getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(Integer phaseId) {
        this.phaseId = phaseId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<ProjectSupply> getProjectSupply() {
        return projectSupply;
    }

    public void setProjectSupply(List<ProjectSupply> projectSupply) {
        this.projectSupply = projectSupply;
    }

    public List<ListingPrice> getListingPrices() {
        return listingPrices;
    }

    public void setListingPrices(List<ListingPrice> listingPrices) {
        this.listingPrices = listingPrices;
    }

    public Integer getTowerId() {
        return towerId;
    }

    public void setTowerId(Integer towerId) {
        this.towerId = towerId;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getAdditionalFeatures() {
        return additionalFeatures;
    }

    public void setAdditionalFeatures(String additionalFeatures) {
        this.additionalFeatures = additionalFeatures;
    }

    public ListingCategory getListingCategory() {
        return listingCategory;
    }

    public void setListingCategory(ListingCategory listingCategory) {
        this.listingCategory = listingCategory;
    }

    public Integer getBookingStatusId() {
        return bookingStatusId;
    }

    public void setBookingStatusId(Integer bookingStatusId) {
        this.bookingStatusId = bookingStatusId;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getCurrentPriceId() {
        return currentPriceId;
    }

    public void setCurrentPriceId(Integer currentPriceId) {
        this.currentPriceId = currentPriceId;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public ListingPrice getCurrentListingPrice() {
        return currentListingPrice;
    }

    public void setCurrentListingPrice(ListingPrice currentListingPrice) {
        this.currentListingPrice = currentListingPrice;
    }

    public List<ListingAmenity> getListingAmenities() {
        return listingAmenities;
    }

    public void setListingAmenities(List<ListingAmenity> listingAmenities) {
        this.listingAmenities = listingAmenities;
    }

    public OtherInfo getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(OtherInfo otherInfo) {
        this.otherInfo = otherInfo;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
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

    /**
     * This object will serve purpose when option id not present while creating
     * listing. So these information will be used to find matching property
     * object and will be used and if not found then a new property object will
     * be created in database with Logical option category.
     * 
     * @author Rajeev Pandey
     *
     */
    private static class OtherInfo {
        private Integer size;
        private Integer bedrooms;
        private Integer bathrooms;
        private Integer projectId;

        public Integer getSize() {
            return size;
        }

        public void setSize(Integer size) {
            this.size = size;
        }

        public Integer getBedrooms() {
            return bedrooms;
        }

        public void setBedrooms(Integer bedrooms) {
            this.bedrooms = bedrooms;
        }

        public Integer getBathrooms() {
            return bathrooms;
        }

        public void setBathrooms(Integer bathrooms) {
            this.bathrooms = bathrooms;
        }

        public Integer getProjectId() {
            return projectId;
        }

        public void setProjectId(Integer projectId) {
            this.projectId = projectId;
        }

    }
}