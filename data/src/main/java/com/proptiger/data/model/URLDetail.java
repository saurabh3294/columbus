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
	
	private String URL;
	private PageType pageType;
	private String cityName;
	private Integer cityId;
	private Integer localityId;
	private Integer projectId;
	private Integer builderId;
	private Integer suburbId;
	
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

}
