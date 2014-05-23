package com.proptiger.data.model;

import java.io.IOException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.util.JsonLoader;
import com.proptiger.exception.ProAPIException;

@Entity
@Table(name = "seo_footer")
@JsonInclude(Include.NON_NULL)
public class SeoFooter extends BaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Column(name = "url")
    @JsonIgnore
    @Id
    String                    url;

    @Column(name = "footer_json")
    @JsonIgnore
    String                    footerJson;

    @Column(name = "page_type")
    @JsonIgnore
    String                    pageType;

    @Transient
    @JsonUnwrapped
    JsonNode                  footerUrls;

    @PostLoad
    public void setJsonPreference() {
        if (this.footerJson != null) {
            try {
                this.footerUrls = JsonLoader.fromString(this.footerJson);
            }
            catch (IOException e) {
                throw new ProAPIException(e);
            }
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFooterJson() {
        return footerJson;
    }

    public void setFooterJson(String footerJson) {
        this.footerJson = footerJson;
    }

    public String getPageType() {
        return pageType;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public JsonNode getFooterUrls() {
        return footerUrls;
    }

    public void setFooterUrls(JsonNode footerUrls) {
        this.footerUrls = footerUrls;
    }
}
