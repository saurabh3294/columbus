package com.proptiger.data.model;

import java.io.IOException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.hibernate.service.jta.platform.internal.JOnASJtaPlatform;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Entity
@Table(name = "RESI_PROJECT_ERROR")
@JsonFilter("fieldFilter")
@JsonInclude(Include.NON_NULL)
public class ProjectError extends BaseModel {

    public static enum ErrorType {
        rate("rate"), status("status"), propdetails("propdetails"), other("other");

        private String type;

        ErrorType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public static ErrorType StringToEnum(String type) {

            for (ErrorType errorType : ErrorType.values()) {
                if (errorType.type.equals(type))
                    return errorType;
            }

            return null;
        }
    }

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private int       id;

    @Column(name = "ERROR_TYPE")
    @Enumerated(EnumType.STRING)
    @JsonDeserialize(using = StringToEnum.class)
    @NotNull(message = "Error Type field should not be empty.")
    private ErrorType errorType;

    @Column(name = "DETAILS")
    @NotBlank(message = "Details field should not be empty.")
    private String    details;

    @Column(name = "EMAIL")
    @NotBlank(message = "email field should not be empty.")
    private String    email;

    @Column(name = "PROJECT_ID")
    @NotNull(message = "project Id field should contain valid id.")
    private Integer   projectId;

    @Column(name = "PROJECT_TYPE_ID")
    private Integer   propertyId = 0;

    @Column(name = "IMAGE_URL")
    private String    imageUrl   = "";

    @Column(name = "URL")
    @NotBlank(message = "email field should not be empty.")
    private String    url;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE")
    private Date      date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorType errorType) {
        this.errorType = errorType;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public Integer getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Integer propertyId) {
        this.propertyId = propertyId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public static class StringToEnum extends JsonDeserializer<ErrorType> {

        @Override
        public ErrorType deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
                JsonProcessingException {
            return ErrorType.valueOf(jp.getText().toLowerCase());
        }

    }

    @PrePersist
    public void prePersist() {
        this.date = new Date();
    }
}
