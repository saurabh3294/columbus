package com.proptiger.data.pojo.response;

import java.io.Serializable;

/**
 * A intermediate response object that will act as response carrier from Dao
 * layer to backwards.
 * 
 * @author Rajeev Pandey
 * 
 * @param <T>
 */
public class PaginatedResponse<T> implements Serializable {
    private static final long serialVersionUID = -33522478817858871L;
    /**
     * This contains the total number of results fetched from data source
     */
    private long totalCount;
    /**
     * Actual result from the data source
     */
    private T    results;

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public T getResults() {
        return results;
    }

    public void setResults(T results) {
        this.results = results;
    }

}
