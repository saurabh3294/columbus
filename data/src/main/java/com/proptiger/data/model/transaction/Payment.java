/**
 * 
 */
package com.proptiger.data.model.transaction;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.core.model.BaseModel;

/**
 * @author mandeep
 *
 */
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "proptiger.payments")
public class Payment extends BaseModel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    
    @Column(name="transaction_id")
    private int transactionId;
    
    @Column(name="status_id")
    private int statusId;
    private int amount;

    @Column(name="type_id")
    private int typeId;
    
    @Column(name="citrus_pay_gateway_transaction_id")
    private String citrusPayGatewayTransactionId;
    
    @Column(name="gateway_transaction_id")
    private long gatewayTransactionId;

    @Column(name="payment_gateway_response_id")
    private Integer paymentGatewayResponseId;
    
    @Column(name="created_at")
    private Date createdAt;

    @Column(name="updated_at")    
    private Date updatedAt;

    @ManyToOne
    @JoinColumn(name="type_id", insertable=false, updatable=false)
    private MasterPaymentType masterPaymentType;

    @ManyToOne
    @JoinColumn(name="status_id", insertable=false, updatable=false)
    private MasterPaymentStatus masterPaymentStatus;

    @ManyToOne
    @JoinColumn(name="transaction_id", insertable=false, updatable=false)
    private Transaction transaction;

    @PreUpdate
    public void update() {
        updatedAt = new Date();
    }

    @PrePersist
    public void create() {
        updatedAt = new Date();
        createdAt = new Date();
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
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

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public String getCitrusPayGatewayTransactionId() {
        return citrusPayGatewayTransactionId;
    }

    public void setCitrusPayGatewayTransactionId(String citrusPayGatewayTransactionId) {
        this.citrusPayGatewayTransactionId = citrusPayGatewayTransactionId;
    }

    public long getGatewayTransactionId() {
        return gatewayTransactionId;
    }

    public void setGatewayTransactionId(long gatewayTransactionId) {
        this.gatewayTransactionId = gatewayTransactionId;
    }

    public Integer getPaymentGatewayResponseId() {
        return paymentGatewayResponseId;
    }

    public void setPaymentGatewayResponseId(Integer paymentGatewayResponseId) {
        this.paymentGatewayResponseId = paymentGatewayResponseId;
    }

    public MasterPaymentType getMasterPaymentType() {
        return masterPaymentType;
    }

    public void setMasterPaymentType(MasterPaymentType masterPaymentType) {
        this.masterPaymentType = masterPaymentType;
    }

    public MasterPaymentStatus getMasterPaymentStatus() {
        return masterPaymentStatus;
    }

    public void setMasterPaymentStatus(MasterPaymentStatus masterPaymentStatus) {
        this.masterPaymentStatus = masterPaymentStatus;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }
}
