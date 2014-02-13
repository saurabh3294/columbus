package com.proptiger.data.pojo;

import java.io.Serializable;

/**
 * @author Rajeev Pandey
 *
 */
public class SortBy implements Serializable {

	private String field;
	private SortOrder sortOrder;
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public SortOrder getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}
	
	
	@Override
	public String toString() {
		return "SortBy [field=" + field + ", sortOrder=" + sortOrder + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result
				+ ((sortOrder == null) ? 0 : sortOrder.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SortBy other = (SortBy) obj;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		if (sortOrder != other.sortOrder)
			return false;
		return true;
	}
	
	
	
}
