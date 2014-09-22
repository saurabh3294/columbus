package com.proptiger.data.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "cms.coupons")
@JsonFilter("fieldFilter")
@JsonInclude(Include.NON_NULL)
public class Coupon extends BaseModel {
    
    /**
     * 
     */
    private static final long serialVersionUID = 5289136799621192512L;

    public enum CouponStatus {
        purchased, redeemed, refunded;
    }
    
    @Id
    @GeneratedValue
    @Column(name = "id")
    private int id;
    
    @Column(name = "coupon_code")
    private String couponCode;
    
    @Column(name = "user_id")
    private int user_id;
    
    @OneToOne
    @JoinColumn(name = "coupon_catalogue_id", insertable = false, updatable = false)
    private CouponCatalogue couponCatalogue;
    
    @Column(name = "transaction_id")
    private int transactionId;
    
    @Column(name = "refund_transaction_id")
    private Integer refundTransactionId;
    
    @Column(name = "coupon_status")
    @Enumerated(EnumType.STRING)
    private CouponStatus couponStatus;
    
    @Column(name = "expiry_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiryAt;
    
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @Column(name = "redeemed_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date redeemedAt;
    
    @Column(name = "refunded_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date refundedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public CouponCatalogue getCouponCatalogue() {
        return couponCatalogue;
    }

    public void setCouponCatalogue(CouponCatalogue couponCatalogue) {
        this.couponCatalogue = couponCatalogue;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getRefundTransactionId() {
        return refundTransactionId;
    }

    public void setRefundTransactionId(Integer refundTransactionId) {
        this.refundTransactionId = refundTransactionId;
    }

    public CouponStatus getCouponStatus() {
        return couponStatus;
    }

    public void setCouponStatus(CouponStatus couponStatus) {
        this.couponStatus = couponStatus;
    }

    public Date getExpiryAt() {
        return expiryAt;
    }

    public void setExpiryAt(Date expiryAt) {
        this.expiryAt = expiryAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getRedeemedAt() {
        return redeemedAt;
    }

    public void setRedeemedAt(Date redeemedAt) {
        this.redeemedAt = redeemedAt;
    }

    public Date getRefundedAt() {
        return refundedAt;
    }

    public void setRefundedAt(Date refundedAt) {
        this.refundedAt = refundedAt;
    }

}
