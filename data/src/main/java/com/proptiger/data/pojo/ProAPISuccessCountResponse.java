package com.proptiger.data.pojo;

import com.proptiger.data.constants.ResponseCodes;

/**
 * This class represents a response object that would contain totalCount along
 * with data and status code
 * @author Rajeev Pandey
 *
 */
public class ProAPISuccessCountResponse implements ProAPIResponse{
	private String statusCode;
	private long totalCount;
	private Object data;
	
	public ProAPISuccessCountResponse(Object data, long count){
		super();
		this.statusCode = ResponseCodes.SUCCESS;
		this.data = data;
		this.totalCount = count;
	}
	
	@Override
	public String getStatusCode() {
		return statusCode;
	}
	@Override
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public long getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}
	
	
}
