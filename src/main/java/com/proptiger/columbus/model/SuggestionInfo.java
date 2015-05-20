package com.proptiger.columbus.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "proptiger.typeahead_suggestion_types")
@JsonInclude(Include.NON_NULL)
public class SuggestionInfo {

    @Id
    @Column(name = "id")    
    private Integer id;
    
    @Column(name = "entity_type")    
    private String entityType;
    
    @Column(name = "suggestion_type")    
    private String suggestionType;
    
    @Column(name = "display_text_format")    
    private String displayTextFormat;
    
    @Column(name = "typeahead_id_format")    
    private String typeaheadIdFormat;

    @Column(name = "redirect_url_format")    
    private String redirectUrlFormat;
    
    @Column(name = "redirect_url_filters")    
    private String redirectUrlFilters;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getSuggestionType() {
        return suggestionType;
    }

    public void setSuggestionType(String suggestionType) {
        this.suggestionType = suggestionType;
    }

    public String getDisplayTextFormat() {
        return displayTextFormat;
    }

    public void setDisplayTextFormat(String displayTextFormat) {
        this.displayTextFormat = displayTextFormat;
    }

    public String getRedirectUrlFormat() {
        return redirectUrlFormat;
    }

    public void setRedirectUrlFormat(String redirectUrlFormat) {
        this.redirectUrlFormat = redirectUrlFormat;
    }

    public String getTypeaheadIdFormat() {
        return typeaheadIdFormat;
    }

    public void setTypeaheadIdFormat(String typeaheadIdFormat) {
        this.typeaheadIdFormat = typeaheadIdFormat;
    }

    public String getRedirectUrlFilters() {
        return redirectUrlFilters;
    }

    public void setRedirectUrlFilters(String redirectUrlFilters) {
        this.redirectUrlFilters = redirectUrlFilters;
    }
}
