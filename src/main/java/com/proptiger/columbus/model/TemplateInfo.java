package com.proptiger.columbus.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "proptiger.typeahead_templates")
@JsonInclude(Include.NON_NULL)
public class TemplateInfo {
    
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "template_type")
    private String  templateType;

    @Column(name = "display_text_format")
    private String  displayTextFormat;

    @Column(name = "redirect_url_format")
    private String  redirectUrlFormat;

    @Column(name = "redirect_url_filters")
    private String  redirectUrlFilters;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
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

    public String getRedirectUrlFilters() {
        return redirectUrlFilters;
    }

    public void setRedirectUrlFilters(String redirectUrlFilters) {
        this.redirectUrlFilters = redirectUrlFilters;
    }

    @Override
    public String toString() {
        return (this.getTemplateType());
    }

}
