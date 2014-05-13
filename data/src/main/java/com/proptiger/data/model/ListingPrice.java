package com.proptiger.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.proptiger.data.model.b2b.Status;
import com.proptiger.data.model.enums.DataVersion;

/**
 * 
 * @author azi
 * 
 */
@Entity
@Table(name = "cms.listing_prices")
@JsonFilter("fieldFilter")
// @SqlResultSetMapping(name = "CustomListingPrice", classes = {
// @ConstructorResult(
// targetClass =
// com.proptiger.data.model.ListingPrice.CustomCurrentListingPrice.class,
// columns = {
// @ColumnResult(name = "listingId", type = Integer.class),
// @ColumnResult(name = "pricePerUnitArea", type = Integer.class),
// @ColumnResult(name = "effectiveMonth", type = Date.class) }) })
public class ListingPrice extends BaseModel {
    private static final long serialVersionUID = 878870501041637665L;

    @Id
    private int               id;

    @Column(name = "listing_id")
    private int               listingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", insertable = false, updatable = false)
    private Listing           listing;

    @Enumerated(EnumType.STRING)
    private DataVersion       version;

    @Column(name = "effective_date")
    private Date              effectiveDate;

    @Column(name = "price_per_unit_area")
    private int               pricePerUnitArea;

    @Enumerated(EnumType.STRING)
    private Status            status;

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

    public int getPricePerUnitArea() {
        return pricePerUnitArea;
    }

    public void setPricePerUnitArea(int pricePerUnitArea) {
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public static class CustomCurrentListingPrice {
        private int     listingId;
        private Integer pricePerUnitArea;
        private Date    effectiveMonth;

        public CustomCurrentListingPrice(int listingId, int pricePerUnitArea, Date effectiveMonth) {
            this.listingId = listingId;
            this.pricePerUnitArea = pricePerUnitArea;
            this.effectiveMonth = effectiveMonth;
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
}