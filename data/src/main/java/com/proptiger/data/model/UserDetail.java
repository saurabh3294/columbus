package com.proptiger.data.model;

import java.io.IOException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.util.JsonLoader;

/**
 * B2b User Detail Model
 * 
 * @author Azitabh Ajit
 * 
 */

@JsonInclude(Include.NON_NULL)
@Entity
@Table(name = "b2b_user_details")
@JsonFilter("fieldFilter")
@JsonAutoDetect(
        fieldVisibility = Visibility.ANY,
        getterVisibility = Visibility.NONE,
        isGetterVisibility = Visibility.NONE)
public class UserDetail extends BaseModel {
    private static final long serialVersionUID = -6720993214144916804L;

    @Id
    @JsonIgnore
    private Integer  id;

    @Transient
    private JsonNode preference;

    @JsonIgnore
    @Column(name = "preference")
    private String   stringPreference;

    @Column(name = "created_at")
    private Date     createdAt = new Date();

    @Column(name = "updated_at")
    private Date     updatedAt = new Date();

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
        try {
            this.preference = JsonLoader.fromString(preference);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.stringPreference = preference;
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

    @PostLoad
    public void setJsonPreference() {
        try {
            this.preference = JsonLoader.fromString(this.stringPreference);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}