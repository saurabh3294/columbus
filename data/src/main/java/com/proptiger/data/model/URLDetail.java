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
    private String   url;
    private PageType pageType;
    private String   cityName;
    private Integer  cityId;
    private Integer  localityId;
    private Integer  projectId;
    private Integer  builderId;
    private Integer  suburbId;
    private Integer  propertyId;
    private Integer   bedrooms;
    /*
     * @TODO merge these three fields into one.
     * Then either all of them should be present or
     * entire object null. Find a way ??
     */
    private String   PriceRange;
    
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

}
