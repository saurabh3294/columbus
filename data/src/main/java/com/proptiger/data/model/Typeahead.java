/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.model;

import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.proptiger.data.meta.ResourceMetaInfo;

/**
 *
 * @author mukand
 */
@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, isGetterVisibility=Visibility.NONE)
@JsonInclude(Include.NON_NULL)
@JsonFilter("fieldFilter")
@ResourceMetaInfo(name="Typeahead")
public class Typeahead implements BaseModel {
    @Field(value="id")
    private String id;
    
    @Field(value="TYPEAHEAD_LABEL")
    private String label;
    
    @Field(value="TYPEAHEAD_LABEL_NGRAMS")
    @JsonIgnore
    private String labelNgrams;
    
    @Field(value="TYPEAHEAD_CITY")
    private String city;
    
    @Field(value="TYPEAHEAD_REDIRECT_URL")
    private String redirectUrl;
    
    @Field(value="TYPEAHEAD_TYPE")
    private String type;
    
    @Field(value="TYPEAHEAD_DISPLAY_TEXT")
    private String displayText;
    
    @Field(value="TYPEAHEAD_CORE_TEXT")
    private String coreText;
    
    @Field(value="TYPEAHEAD_CORE_TEXT_NGRAMS")
    private String coreTextNgrams;

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
}
