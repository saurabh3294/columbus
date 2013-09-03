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
	@Override
	public String toString() {
		return "PropertyRequestParams [fields=" + fields + ", filters="
				+ filters + ", sort=" + sort + ", facets=" + facets
				+ ", stats=" + stats + ", start=" + start + ", rows=" + rows
				+ "]";
	}
    
    
}
