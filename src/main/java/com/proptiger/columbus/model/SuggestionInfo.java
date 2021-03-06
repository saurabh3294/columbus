package com.proptiger.columbus.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "proptiger.typeahead_suggestions")
@JsonInclude(Include.NON_NULL)
public class SuggestionInfo {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "entity_type_id")
    private int  entityTypeId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "suggestion_type_id")    
    private SuggestionObjectType suggestionObjectType;

    @Column(name = "display_text_format")
    private String  displayTextFormat;

    @Column(name = "typeahead_id_format")
    private String  typeaheadIdFormat;

    @Column(name = "redirect_url_format")
    private String  redirectUrlFormat;

    @Column(name = "redirect_url_filters")
    private String  redirectUrlFilters;

    @Override
    public String toString() {
        return (this.getEntityTypeId() + ":" + String.valueOf(this.getSuggestionObjectType() + ":" + this.displayTextFormat));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getEntityTypeId() {
        return entityTypeId;
    }

    public void setEntityTypeId(int entityTypeId) {
        this.entityTypeId = entityTypeId;
    }

    public SuggestionObjectType getSuggestionObjectType() {
        return suggestionObjectType;
    }

    public void setSuggestionObjectType(SuggestionObjectType suggestionObjectType) {
        this.suggestionObjectType = suggestionObjectType;
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
