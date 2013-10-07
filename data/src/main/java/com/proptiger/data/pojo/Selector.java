/**
 * 
 */
package com.proptiger.data.pojo;

import java.util.List;
import java.util.Map;
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
    private Map<String, List<Map<String, Map<String, Object>>>> filters;
    private Set<SortBy> sort;
    private Paging paging = new Paging();
    
    public Set<String> getFields() {
		return fields;
	}
	public void setFields(Set<String> fields) {
		this.fields = fields;
	}
	public Map<String, List<Map<String, Map<String, Object>>>> getFilters() {
        return filters;
    }
    public void setFilters(Map<String, List<Map<String, Map<String, Object>>>> filters) {
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
	@Override
	public String toString() {
	    return ToStringBuilder.reflectionToString(this);
	}
}
