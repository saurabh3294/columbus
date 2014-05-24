/**
 * 
 */
package com.proptiger.data.model;

import com.proptiger.data.util.PageType;

/**
 * 
 *
 */
public class URLDetail {
    private String   URL;
    private PageType pageType;
    private String   cityName;
    private Integer  cityId;
    private Integer  localityId;
    private Integer  projectId;
    private Integer  builderId;
    private Integer  suburbId;
    private Integer  propertyId;
    private String   bedrooms;
    /*
     * @TODO merge these three fields into one.
     * Then either all of them should be present or
     * entire object null. Find a way ??
     */
    private String   minPriceRange;
    private String   maxPriceRange;
    private String   priceUnitName;

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

    public String getURL() {
        return URL;
    }

    public void setURL(String uRL) {
        URL = uRL;
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

    public String getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(String bedrooms) {
        this.bedrooms = bedrooms;
    }

    public String getMinPriceRange() {
        return minPriceRange;
    }

    public void setMinPriceRange(String minPriceRange) {
        this.minPriceRange = minPriceRange;
    }

    public String getMaxPriceRange() {
        return maxPriceRange;
    }

    public void setMaxPriceRange(String maxPriceRange) {
        this.maxPriceRange = maxPriceRange;
    }

    public String getPriceUnitName() {
        return priceUnitName;
    }

    public void setPriceUnitName(String priceUnitName) {
        this.priceUnitName = priceUnitName;
    }

}
