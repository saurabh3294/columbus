/**
 * 
 */
package com.proptiger.data.model.filter;

import java.util.Set;


/**
 * @author mandeep
 * 
 */
public class PropertyRequestParams {
    private Set<String> fields;
    private String filters;
    private Set<SortBy> sort;
    private String facets;
    private String stats;
    private Integer start = 0;
    private Integer rows = 10;
    
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
    public Integer getStart() {
        return start;
    }
    public void setStart(Integer start) {
        this.start = start;
    }
    public Integer getRows() {
        return rows;
    }
    public void setRows(Integer rows) {
        this.rows = rows;
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
		return "PropertyRequestParams [fields=" + fields + ", filters="
				+ filters + ", sort=" + sort + ", facets=" + facets
				+ ", stats=" + stats + ", start=" + start + ", rows=" + rows
				+ ", radius=" + radius + ", latitude=" + latitude
				+ ", longitude=" + longitude + "]";
	}
	
    
    
}
