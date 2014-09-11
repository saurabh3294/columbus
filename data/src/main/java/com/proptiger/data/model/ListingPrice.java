package com.proptiger.data.model;

import java.io.Serializable;
import java.util.Calendar;
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
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.data.enums.DataVersion;
import com.proptiger.data.enums.Status;
import com.proptiger.data.util.DateUtil;
import com.proptiger.exception.ProAPIException;

/**
 * 
 * @author azi
 * 
 */
@Entity
@Table(name = "cms.listing_prices")
@JsonFilter("fieldFilter")
@JsonInclude(Include.NON_NULL)
public class ListingPrice extends BaseModel {
    private static final long serialVersionUID = 878870501041637665L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int               id;

    @Column(name = "listing_id")
    private int               listingId;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", insertable = false, updatable = false)
    private Listing           listing;

    @Enumerated(EnumType.STRING)
    private DataVersion       version;

    @Column(name = "effective_date")
    private Date              effectiveDate;

    @Column(name = "price_per_unit_area")
    private Integer               pricePerUnitArea;

    @Column(name = "plot_cost_per_unit_area")
    private Integer           plotCostPerUnitArea;

    @Column(name = "construction_cost_per_unit_area")
    private Integer           constructionCostPerUnitArea;

    @Column(name = "price")
    private Integer           price;

    @Column(name = "other_charges")
    private Integer           otherCharges;

    @Enumerated(EnumType.STRING)
    private Status            status;

    @Column(name = "comment")
    private String            comment;

    @Column(name = "updated_by")
    private int               updatedBy;

    @Column(name = "created_at")
    private Date              createdAt;

    @Column(name = "updated_at")
    private Date              updatedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getListingId() {
        return listingId;
    }

    public void setListingId(int listingId) {
        this.listingId = listingId;
    }

    public Listing getListing() {
        return listing;
    }

    public void setListing(Listing listing) {
        this.listing = listing;
    }

    public DataVersion getVersion() {
        return version;
    }

    public void setVersion(DataVersion version) {
        this.version = version;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Integer getPricePerUnitArea() {
        return pricePerUnitArea;
    }

    public void setPricePerUnitArea(Integer pricePerUnitArea) {
        this.pricePerUnitArea = pricePerUnitArea;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(int updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Integer getPlotCostPerUnitArea() {
        return plotCostPerUnitArea;
    }

    public void setPlotCostPerUnitArea(Integer plotCostPerUnitArea) {
        this.plotCostPerUnitArea = plotCostPerUnitArea;
    }

    public Integer getConstructionCostPerUnitArea() {
        return constructionCostPerUnitArea;
    }

    public void setConstructionCostPerUnitArea(Integer constructionCostPerUnitArea) {
        this.constructionCostPerUnitArea = constructionCostPerUnitArea;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = new Date();
    }

    @PrePersist
    public void prePersist() {
        Calendar cal = Calendar.getInstance();
        this.createdAt = cal.getTime();
        this.updatedAt = createdAt;
        cal.set(Calendar.DAY_OF_MONTH, 1);
        this.effectiveDate = cal.getTime();
    }

    public static class CustomCurrentListingPrice implements Serializable {
        private static final long serialVersionUID = 1L;
        private int               listingId;
        private Integer           pricePerUnitArea;
        private Date              effectiveMonth;

        public CustomCurrentListingPrice(int listingId, String pricePerUnitArea, String effectiveMonth) {
            try {
                this.listingId = listingId;
                this.pricePerUnitArea = Integer.valueOf(pricePerUnitArea);
                this.effectiveMonth = DateUtil.parseYYYYmmddStringToDate(effectiveMonth);
            }
            catch (Exception e) {
                throw new ProAPIException("Exception in CustomListingPrice Constructor", e);
            }
        }

        public int getListingId() {
            return listingId;
        }

        public void setListingId(int listingId) {
            this.listingId = listingId;
        }

        public Integer getPricePerUnitArea() {
            return pricePerUnitArea;
        }

        public void setPricePerUnitArea(Integer pricePerUnitArea) {
            this.pricePerUnitArea = pricePerUnitArea;
        }

        public Date getEffectiveMonth() {
            return effectiveMonth;
        }

        public void setEffectiveMonth(Date effectiveMonth) {
            this.effectiveMonth = effectiveMonth;
        }
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getOtherCharges() {
        return otherCharges;
    }

    public void setOtherCharges(Integer otherCharges) {
        this.otherCharges = otherCharges;
    }
}