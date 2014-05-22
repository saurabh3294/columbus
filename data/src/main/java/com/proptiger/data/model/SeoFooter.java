package com.proptiger.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "seo_footer")
@JsonFilter("fieldFilter")
@JsonInclude(Include.NON_NULL)
public class SeoFooter extends BaseModel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    @Column(name="url")
    @JsonIgnore
    String url;
    
    @Column(name="footer_json")
    String footerUrls;
    
    @Column(name = "page_type")
    @JsonIgnore
    String pageType;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFooterUrls() {
        return footerUrls;
    }

    public void setFooterUrls(String footerUrls) {
        this.footerUrls = footerUrls;
    }

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }
}
