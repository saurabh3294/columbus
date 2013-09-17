/**
 * 
 */
package com.proptiger.data.pojo;

import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * This class represents the request that include all components of a database/solr query,
 * that includes selct part, filtering part, sorting part etc.
 * 
 * @author mandeep
 * @author Rajeev Pandey
 */
public class Selector {
    private Set<String> fields;
    private Object filters;
    private Set<SortBy> sort;
    private Paging paging = new Paging();
    private Float radius;
    private Float latitude;
    private Float longitude;

    public Set<String> getFields() {
		return fields;
	}
	public void setFields(Set<String> fields) {
		this.fields = fields;
	}
	public Object getFilters() {
        return filters;
    }
    public void setFilters(Object filters) {
        this.filters = filters;
    }
    
    public Paging getPaging() {
		return paging;
	}
	public void setPaging(Paging paging) {
		this.paging = paging;
	}
	public Set<SortBy> getSort() {
		return sort;
	}
	public void setSort(Set<SortBy> sort) {
		this.sort = sort;
	}
	public Float getRadius() {
		return radius;
	}
	public void setRadius(Float radius) {
		this.radius = radius;
	}
	public Float getLatitude() {
		return latitude;
	}
	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}
	public Float getLongitude() {
		return longitude;
	}
	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}
	@Override
	public String toString() {
	    return ToStringBuilder.reflectionToString(this);
	}
    
}
