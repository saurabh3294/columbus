package com.proptiger.data.model.b2b;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.data.meta.ResourceMetaInfo;
import com.proptiger.data.model.BaseModel;

/**
 * BebUserSelection model object
 * 
 * @author Aziitabh Ajit
 * 
 */

@ResourceMetaInfo
@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "b2b_user_selections")
@JsonFilter("fieldFilter")
public class UserSelection extends BaseModel {
    @Id
    private Integer       id;

    @Column(name = "user_id")
    private Integer       userId;

    @Column(name = "selection_type")
    @Enumerated(EnumType.STRING)
    private SelectionType selectionType;

    @Column(name = "selection")
    private String        selection;

    @Column(name = "created_at")
    private Date          createdAt;

    @Column(name = "updated_at")
    private Date          updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public SelectionType getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(SelectionType selectionType) {
        this.selectionType = selectionType;
    }

    public String getSelection() {
        return selection;
    }

    public void setSelection(String selection) {
        this.selection = selection;
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

    private enum SelectionType {
        Catchment, Graph
    }
}