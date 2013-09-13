/**
 * 
 */
package com.proptiger.data.pojo;

import java.util.Set;


/**
 * This class represents the request that include all components of a database/solr query,
 * that includes selct part, filtering part, sorting part etc.
 * 
 * @author mandeep
 * @author Rajeev Pandey
 */
public class Selector {
    private Set<String> fields;
    private String filters;
    private Set<SortBy> sort;
    private String facets;
    private String stats;
    private Paging paging;
    
    private Set<String> groupBy;
    
    private Float radius;
    private Float latitude;
    private Float longitude;

    
    public Set<String> getFields() {
		return fields;
	}
	public void setFields(Set<String> fields) {
		this.fields = fields;
	}
	public String getFilters() {
        return filters;
    }
    public void setFilters(String filters) {
        this.filters = filters;
    }
    
    public Paging getPaging() {
		return paging;
	}
	public void setPaging(Paging paging) {
		this.paging = paging;
	}
	public Set<String> getGroupBy() {
		return groupBy;
	}
	public void setGroupBy(Set<String> groupBy) {
		this.groupBy = groupBy;
	}
	public Set<SortBy> getSort() {
		return sort;
	}
	public void setSort(Set<SortBy> sort) {
		this.sort = sort;
	}
	public String getFacets() {
        return facets;
    }
    public void setFacets(String facets) {
        this.facets = facets;
    }
    public String getStats() {
        return stats;
    }
    public void setStats(String stats) {
        this.stats = stats;
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
		return "Selector [fields=" + fields + ", filters=" + filters
				+ ", sort=" + sort + ", facets=" + facets + ", stats=" + stats
				+ ", paging=" + paging + ", groupBy=" + groupBy + ", radius="
				+ radius + ", latitude=" + latitude + ", longitude="
				+ longitude + "]";
	}
    
}
