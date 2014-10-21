package com.proptiger.data.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "cms.coupon_catalogue")
@JsonFilter("fieldFilter")
@JsonInclude(Include.NON_NULL)
public class CouponCatalogue extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = -8648856984205854234L;

    @Id
    @GeneratedValue
    @Column(name = "id")
    private int               id;

    @Column(name = "option_id")
    private int               propertyId;

    @Column(name = "coupon_price")
    private int               couponPrice;

    @Column(name = "discount")
    private int               discount;

    @Transient
    private Integer           discountPricePerUnitArea;

    @Column(name = "purchase_expiry_at")
    private Date              purchaseExpiryAt;

    @Column(name = "redeem_expiry_hours")
    private int               redeemExpiryHours;

    @Column(name = "total_inventory")
    private int               totalInventory;

    @Column(name = "inventory_left")
    private Integer           inventoryLeft;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", insertable = false, updatable = false)
    private Property          property;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date              createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonIgnore
    private Date              updatedAt;

    @Column(name = "email")
    @JsonIgnore
    private String            builderEmail;

    @JsonIgnore
    @Transient
    private List<String>      listBuilderEmail = new ArrayList<String>();

    @PostLoad
    public void postPopulateFields() {
        if (builderEmail != null && !builderEmail.isEmpty()) {
            listBuilderEmail = Arrays.asList(builderEmail.split(";"));
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    public int getCouponPrice() {
        return couponPrice;
    }

    public void setCouponPrice(int couponPrice) {
        this.couponPrice = couponPrice;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public Date getPurchaseExpiryAt() {
        return purchaseExpiryAt;
    }

    public void setPurchaseExpiryAt(Date purchaseExpiryAt) {
        this.purchaseExpiryAt = purchaseExpiryAt;
    }

    public int getRedeemExpiryHours() {
        return redeemExpiryHours;
    }

    public void setRedeemExpiryHours(int redeemExpiryHours) {
        this.redeemExpiryHours = redeemExpiryHours;
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

    public int getTotalInventory() {
        return totalInventory;
    }

    public void setTotalInventory(int totalInventory) {
        this.totalInventory = totalInventory;
    }

    public Integer getInventoryLeft() {
        return inventoryLeft;
    }

    public void setInventoryLeft(Integer inventoryLeft) {
        this.inventoryLeft = inventoryLeft;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public String getBuilderEmail() {
        return builderEmail;
    }

    public void setBuilderEmail(String builderEmail) {
        this.builderEmail = builderEmail;
    }

    public Integer getDiscountPricePerUnitArea() {
        return discountPricePerUnitArea;
    }

    public void setDiscountPricePerUnitArea(Integer discountPricePerUnitArea) {
        this.discountPricePerUnitArea = discountPricePerUnitArea;
    }

    public List<String> getListBuilderEmail() {
        return listBuilderEmail;
    }

    public void setListBuilderEmail(List<String> listBuilderEmail) {
        this.listBuilderEmail = listBuilderEmail;
    }

}
