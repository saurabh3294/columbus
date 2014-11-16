/**
 * 
 */
package com.proptiger.data.model;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.proptiger.data.enums.seo.BuilderPropertyTypes;
import com.proptiger.data.enums.seo.PropertyType;
import com.proptiger.data.enums.seo.TaxonomyPropertyTypes;
import com.proptiger.data.model.seo.URLCategories;
import com.proptiger.data.util.PageType;

/**
 * 
 *
 */
public class URLDetail {
    @NotNull(message = "The url field should not be empty.")
    private String                url;

    @NotNull(message = "The template Id field should not be empty.")
    private String                templateId;

    private PageType              pageType;
    private String                cityName;
    private Integer               cityId;
    private Integer               localityId;
    private Integer               projectId;
    private Integer               builderId;
    private Integer               suburbId;
    private Integer               propertyId;
    private Integer               portfolioId;
    private Integer               bedrooms;
    private Integer               minBudget;
    private Integer               maxBudget;
    private String                fallBackUrl;
    private String                areaType;
    private PropertyType          urlPropertyTypeCategory;
    private TaxonomyPropertyTypes taxonomyPropertyType;
    private BuilderPropertyTypes  builderPropertyType;
    private Integer               objectId;
    private Integer               imageId;
    private String                localityName;
    private String                suburbName;
    private URLCategories         urlCategory;

    /**
     * Default Value of bedroomString is empty string. Do not change it.
     */
    private String                bedroomString = "";
    /**
     * Default Value of priceString is empty string. Do not change it.
     */
    private String                priceString   = "";
    private String                propertyType;
    private String                appendingString;
    private String                overviewType;

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
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public Integer getMinBudget() {
        return minBudget;
    }

    public void setMinBudget(Integer minBudget) {
        this.minBudget = minBudget;
    }

    public Integer getMaxBudget() {
        return maxBudget;
    }

    public void setMaxBudget(Integer maxBudget) {
        this.maxBudget = maxBudget;
    }

    public String getBedroomString() {
        return bedroomString;
    }

    public void setBedroomString(String bedroomString) {
        this.bedroomString = bedroomString;
    }

    public String getPriceString() {
        return priceString;
    }

    public void setPriceString(String priceString) {
        this.priceString = priceString;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public Integer getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(Integer portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getAppendingString() {
        return appendingString;
    }

    public void setAppendingString(String appendingString) {
        this.appendingString = appendingString;
    }

    public String getOverviewType() {
        return overviewType;
    }

    public void setOverviewType(String overviewType) {
        this.overviewType = overviewType;
    }

    public Integer getObjectId() {
        return objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public String getAreaType() {
        return areaType;
    }

    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }

    public PropertyType getUrlPropertyType() {
        return urlPropertyTypeCategory;
    }

    public void setUrlPropertyType(PropertyType urlPropertyType) {
        this.urlPropertyTypeCategory = urlPropertyType;
    }

    public PropertyType getUrlPropertyTypeCategory() {
        return urlPropertyTypeCategory;
    }

    public void setUrlPropertyTypeCategory(PropertyType urlPropertyTypeCategory) {
        this.urlPropertyTypeCategory = urlPropertyTypeCategory;
    }

    public TaxonomyPropertyTypes getTaxonomyPropertyType() {
        return taxonomyPropertyType;
    }

    public void setTaxonomyPropertyType(TaxonomyPropertyTypes taxonomyPropertyType) {
        this.taxonomyPropertyType = taxonomyPropertyType;
    }

    public BuilderPropertyTypes getBuilderPropertyType() {
        return builderPropertyType;
    }

    public void setBuilderPropertyType(BuilderPropertyTypes builderPropertyType) {
        this.builderPropertyType = builderPropertyType;
    }

    public String getLocalityName() {
        return this.localityName;
    }

    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }

    public String getSuburbName() {
        return suburbName;
    }

    public void setSuburbName(String suburbName) {
        this.suburbName = suburbName;
    }

    public URLCategories getUrlCategory() {
        return urlCategory;
    }

    public void setUrlCategory(URLCategories urlCategory) {
        this.urlCategory = urlCategory;
    }
}
