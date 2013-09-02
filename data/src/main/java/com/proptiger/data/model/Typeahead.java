/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.proptiger.data.model;

import org.apache.solr.client.solrj.beans.Field;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author mukand
 */
public class Typeahead {
    @Field(value="id")
    @JsonProperty(value="id")
    private String id;
    
    @Field(value="TYPEAHEAD_LABEL")
    @JsonProperty(value="typeahead_label")  
    private String typeahead_label;
    
    @Field(value="TYPEAHEAD_LABEL_NGRAMS")
    @JsonProperty(value="typeahead_label_ngrams")  
    private String typeahead_label_ngrams;
    
    @Field(value="TYPEAHEAD_CITY")
    @JsonProperty(value="typeahead_city")  
    private String typeahead_city;
    
    @Field(value="TYPEAHEAD_REDIRECT_URL")
    @JsonProperty(value="typeahead_redirect_url")  
    private String typeahead_redirect_url;
    
    @Field(value="TYPEAHEAD_TYPE")
    @JsonProperty(value="typeahead_type")  
    private String typeahead_type;
    
    @Field(value="TYPEAHEAD_DISPLAY_TEXT")
    @JsonProperty(value="typeahead_display_text")  
    private String typeahead_display_text;
    
    @Field(value="TYPEAHEAD_CORE_TEXT")
    @JsonProperty(value="typeahead_core_text")  
    private String typeahead_core_text;
    
    @Field(value="TYPEAHEAD_CORE_TEXT_NGRAMS")
    @JsonProperty(value="typeahead_core_text_ngrams")  
    private String typeahead_core_text_ngrams;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeahead_label() {
        return typeahead_label;
    }

    public void setTypeahead_label(String typeahead_label) {
        this.typeahead_label = typeahead_label;
    }

    public String getTypeahead_label_ngrams() {
        return typeahead_label_ngrams;
    }

    public void setTypeahead_label_ngrams(String typeahead_label_ngrams) {
        this.typeahead_label_ngrams = typeahead_label_ngrams;
    }

    public String getTypeahead_city() {
        return typeahead_city;
    }

    public void setTypeahead_city(String typeahead_city) {
        this.typeahead_city = typeahead_city;
    }

    public String getTypeahead_redirect_url() {
        return typeahead_redirect_url;
    }

    public void setTypeahead_redirect_url(String typeahead_redirect_url) {
        this.typeahead_redirect_url = typeahead_redirect_url;
    }

    public String getTypeahead_type() {
        return typeahead_type;
    }

    public void setTypeahead_type(String typeahead_type) {
        this.typeahead_type = typeahead_type;
    }

    public String getTypeahead_display_text() {
        return typeahead_display_text;
    }

    public void setTypeahead_display_text(String typeahead_display_text) {
        this.typeahead_display_text = typeahead_display_text;
    }

    public String getTypeahead_core_text() {
        return typeahead_core_text;
    }

    public void setTypeahead_core_text(String typeahead_core_text) {
        this.typeahead_core_text = typeahead_core_text;
    }

    public String getTypeahead_core_text_ngrams() {
        return typeahead_core_text_ngrams;
    }

    public void setTypeahead_core_text_ngrams(String typeahead_core_text_ngrams) {
        this.typeahead_core_text_ngrams = typeahead_core_text_ngrams;
    }
}
