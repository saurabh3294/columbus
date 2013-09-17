package com.proptiger.data.service.pojo;

/**
 * A intermediate response object that will act as response carrier from Dao layer to backwards.
 * @author Rajeev Pandey
 *
 * @param <T>
 */
public class SolrServiceResponse<T> {

	
	/**
	 * This contains the total number of results fetched from data source
	 */
	private long totalResultCount;
	/**
	 * Actual result from the data source
	 */
	private T result;
	
	public long getTotalResultCount() {
		return totalResultCount;
	}
	public void setTotalResultCount(long totalResultCount) {
		this.totalResultCount = totalResultCount;
	}
	public T getResult() {
		return result;
	}
	public void setResult(T result) {
		this.result = result;
	}
	
	
}
