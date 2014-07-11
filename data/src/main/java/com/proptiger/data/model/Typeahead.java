/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * 
 * @author mukand
 */
@JsonAutoDetect(
        fieldVisibility = Visibility.ANY,
        getterVisibility = Visibility.NONE,
        isGetterVisibility = Visibility.NONE)
@JsonInclude(Include.NON_NULL)
@JsonFilter("fieldFilter")
public class Typeahead extends BaseModel {




	private static final long serialVersionUID = 2096261268711516512L;

    @Field(value = "id")
    private String            id;

    @Transient
    private boolean           authorized       = true;

    @Field(value = "TYPEAHEAD_LABEL")
    private String            label;

    @Field(value = "TYPEAHEAD_LABEL_NGRAMS")
    @JsonIgnore
    private String            labelNgrams;

    @Field(value = "TYPEAHEAD_CITY")
    private String            city;

    @Field(value = "tp_locality")
    private String            locality;

    @Field(value = "TYPEAHEAD_REDIRECT_URL")
    private String            redirectUrl;

    @Field(value = "TYPEAHEAD_TYPE")
    private String            type;

    @Field(value = "TYPEAHEAD_DISPLAY_TEXT")
    private String            displayText;

    @Field(value = "TYPEAHEAD_CORE_TEXT")
    private String            coreText;

    @Field(value = "TYPEAHEAD_CORE_TEXT_NGRAMS")
    private String            coreTextNgrams;

    @Field(value = "LOCALITY_URL")
    private String            localityURL;

    @Field(value = "TYPEAHEAD_LABEL_LOWERCASE")
    private String            labelLowercase;

    @Field(value = "TYPEAHEAD_LOCALITY_AVG_PRICE_PER_UNIT_AREA")
    private Double            localityAvgPricePerUnitArea;

    @Field(value = "TYPEAHEAD_LOCALITY_PRICE_APPRECIATION_6MONTHS")
    private Double            localityPriceRise6Months;

    @Field(value = "TYPEAHEAD_LOCALITY_UNITS_LAUNCHED_6MONTHS")
    private Integer           localityUnitsLaunched6Months;

    @Field(value = "TYPEAHEAD_LOCALITY_UNITS_SOLD_6MONTHS")
    private Integer           localityUnitsSold6Months;

    @Field(value = "TYPEAHEAD_LOCALITY_UNITS_DELIVERED_6MONTHS")
    private Integer           localityUnitsDelivered6Months;

    private List<String> suggestion;

	public List<String> getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(List<String> suggestion) {
		this.suggestion = suggestion;
	}
	
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabelNgrams() {
        return labelNgrams;
    }

    public void setLabelNgrams(String labelNgrams) {
        this.labelNgrams = labelNgrams;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public String getCoreText() {
        return coreText;
    }

    public void setCoreText(String coreText) {
        this.coreText = coreText;
    }

    public String getCoreTextNgrams() {
        return coreTextNgrams;
    }

    public void setCoreTextNgrams(String coreTextNgrams) {
        this.coreTextNgrams = coreTextNgrams;
    }

    public String getLocalityURL() {
        return localityURL;
    }

    public void setLocalityURL(String localityURL) {
        this.localityURL = localityURL;
    }

    public String getLabelLowercase() {
        return labelLowercase;
    }

    public void setLabelLowercase(String labelLowercase) {
        this.labelLowercase = labelLowercase;
    }

    public Double getLocalityAvgPricePerUnitArea() {
        return localityAvgPricePerUnitArea;
    }

    public void setLocalityAvgPricePerUnitArea(Double localityAvgPricePerUnitArea) {
        this.localityAvgPricePerUnitArea = localityAvgPricePerUnitArea;
    }

    public Double getLocalityPriceRise6Months() {
        return localityPriceRise6Months;
    }

    public void setLocalityPriceRise6Months(Double localityPriceRise6Months) {
        this.localityPriceRise6Months = localityPriceRise6Months;
    }

    public Integer getLocalityUnitsLaunched6Months() {
        return localityUnitsLaunched6Months;
    }

    public void setLocalityUnitsLaunched6Months(Integer localityUnitsLaunched6Months) {
        this.localityUnitsLaunched6Months = localityUnitsLaunched6Months;
    }

    public Integer getLocalityUnitsSold6Months() {
        return localityUnitsSold6Months;
    }

    public void setLocalityUnitsSold6Months(Integer localityUnitsSold6Months) {
        this.localityUnitsSold6Months = localityUnitsSold6Months;
    }

    public Integer getLocalityUnitsDelivered6Months() {
        return localityUnitsDelivered6Months;
    }

    public void setLocalityUnitsDelivered6Months(Integer localityUnitsDelivered6Months) {
        this.localityUnitsDelivered6Months = localityUnitsDelivered6Months;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }
}
