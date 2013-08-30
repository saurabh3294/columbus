/**
 * 
 */
package com.proptiger.data.model.filter;


/**
 * @author mandeep
 * 
 */
public class PropertyFilter {
    private String fields;
    private String filters;
    private String sort;
    private String facets;
    private String stats;
    private Integer start = 0;
    private Integer rows = 10;

    public String getFields() {
        return fields;
    }
    public void setFields(String fields) {
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
    public String getSort() {
        return sort;
    }
    public void setSort(String sort) {
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
}
