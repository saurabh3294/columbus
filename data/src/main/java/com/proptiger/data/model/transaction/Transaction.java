/**
 * 
 */
package com.proptiger.data.model.transaction;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.data.model.BaseModel;
import com.proptiger.data.model.enums.transaction.TransactionStatus;
import com.proptiger.data.model.user.User;

/**
 * @author mandeep
 * 
 */
@JsonInclude(Include.NON_EMPTY)
@Entity
@Table(name = "proptiger.transactions")
public class Transaction extends BaseModel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer           id;
    private int               amount;
    
    @Column(name="type_id")
    private int               typeId;

    @Column(name="product_id")
    private int               productId;

    @Column(name="user_id")
    private int               userId;

    @Column(name="status_id")
    private int               statusId = TransactionStatus.Incomplete.getId();

    private String            notes;

    private String            code;
    
    @Column(name="created_at")
    private Date              createdAt;

    @Column(name="updated_at")
    private Date              updatedAt;

    @ManyToOne
    @JoinColumn(name="type_id", insertable=false, updatable=false)
    private MasterTransactionType masterTransactionType;

    @ManyToOne
    @JoinColumn(name="status_id", insertable=false, updatable=false)
    private MasterTransactionStatus masterTransactionStatus;

    @OneToMany(mappedBy="transactionId")
    private List<Payment> payments;

    @Transient
    private User user;

    @Transient
    private Object product;
    
    @PreUpdate
    public void update() {
        updatedAt = new Date();
    }

    @PrePersist
    public void create() {
        updatedAt = new Date();
        createdAt = new Date();
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public MasterTransactionType getMasterTransactionType() {
        return masterTransactionType;
    }

    public void setMasterTransactionType(MasterTransactionType masterTransactionType) {
        this.masterTransactionType = masterTransactionType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public MasterTransactionStatus getMasterTransactionStatus() {
        return masterTransactionStatus;
    }

    public void setMasterTransactionStatus(MasterTransactionStatus masterTransactionStatus) {
        this.masterTransactionStatus = masterTransactionStatus;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getProduct() {
        return product;
    }

    public void setProduct(Object product) {
        this.product = product;
    }
}
