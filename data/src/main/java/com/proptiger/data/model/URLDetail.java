/**
 * 
 */
package com.proptiger.data.model;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.proptiger.data.util.PageType;

/**
 * 
 *
 */
public class URLDetail {
    @NotNull(message = "The url field should not be empty.")
    private String   url;

    @NotNull(message = "The template Id field should not be empty.")
    private String   templateId;

    private PageType pageType;
    private String   cityName;
    private Integer  cityId;
    private Integer  localityId;
    private Integer  projectId;
    private Integer  builderId;
    private Integer  suburbId;
    private Integer  propertyId;
    private Integer  bedrooms;
    private String   PriceRange;
    private String   fallBackUrl;

    public Integer getBuilderId() {
        return builderId;
    }

    public void setBuilderId(Integer builderId) {
        this.builderId = builderId;
    }

    public Integer getSuburbId() {
        return suburbId;
    }

    public void setSuburbId(Integer suburbId) {
        this.suburbId = suburbId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String uRL) {
        this.url = uRL;
    }

    public PageType getPageType() {
        return pageType;
    }

    public void setPageType(PageType pageType) {
        this.pageType = pageType;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Integer getLocalityId() {
        return localityId;
    }

    public void setLocalityId(Integer localityId) {
        this.localityId = localityId;
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

    public Integer getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(Integer bedrooms) {
        this.bedrooms = bedrooms;
    }

    public String getPriceRange() {
        return PriceRange;
    }

    public void setPriceRange(String priceRange) {
        PriceRange = priceRange;
    }

    public String getFallBackUrl() {
        return fallBackUrl;
    }

    public void setFallBackUrl(String fallBackUrl) {
        this.fallBackUrl = fallBackUrl;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    @Override
    public String toString() {
        return ToStringBuilder
                .reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
