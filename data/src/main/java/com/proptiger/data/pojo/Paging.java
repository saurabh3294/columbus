package com.proptiger.data.pojo;

/**
 * @author Rajeev Pandey
 *
 */
public class Paging {

	private int start;
	private int rows;
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getRows() {
		return rows;
	}
	public void setRows(int rows) {
		this.rows = rows;
	}
	@Override
	public String toString() {
		return "Paging [start=" + start + ", rows=" + rows + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + rows;
		result = prime * result + start;
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
		Paging other = (Paging) obj;
		if (rows != other.rows)
			return false;
		if (start != other.start)
			return false;
		return true;
	}
	
	
}