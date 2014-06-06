package com.proptiger.data.model;

import java.io.IOException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.util.JsonLoader;
import com.proptiger.data.enums.Application;

/**
 * B2b User Detail Model
 * 
 * @author Azitabh Ajit
 * 
 */

@JsonInclude(Include.NON_NULL)
@Entity(name = "user_preferences")
@JsonFilter("fieldFilter")
public class UserPreference extends BaseModel {
    private static final long serialVersionUID = -6720993214144916804L;

    @Id
    @JsonIgnore
    private Integer           id;

    @Column(name = "user_id")
    private Integer           userId;

    @Enumerated(EnumType.STRING)
    private Application       app;

    @Transient
    private JsonNode          preference;

    @JsonIgnore
    @Column(name = "preference")
    private String            stringPreference;

    @Column(name = "created_at")
    private Date              createdAt        = new Date();

    @Column(name = "updated_at")
    private Date              updatedAt        = new Date();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public JsonNode getPreference() {
        return preference;
    }

    public void setPreference(JsonNode preference) {
        this.stringPreference = preference.toString();
        this.preference = preference;
    }

    public String getStringPreference() {
        return stringPreference;
    }

    public void setStringPreference(String preference) {
        this.stringPreference = preference;
        convertStringPreferenceToJsonPreference();
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Application getApp() {
        return app;
    }

    public void setApp(Application app) {
        this.app = app;
    }

    @PostLoad
    public void setJsonPreference() {
        convertStringPreferenceToJsonPreference();
    }

    private void convertStringPreferenceToJsonPreference() {
        try {
            this.preference = JsonLoader.fromString(stringPreference);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}