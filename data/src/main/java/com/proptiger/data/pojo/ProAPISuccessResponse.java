package com.proptiger.data.pojo;

import com.proptiger.data.constants.ResponseCodes;

/**
 * This class represents a successful response
 * @author Rajeev Pandey
 *
 */
public class ProAPISuccessResponse implements ProAPIResponse {

	private String statusCode;
	private Object data;
	
	public ProAPISuccessResponse() {
		super();
	}

	public ProAPISuccessResponse(Object data) {
		super();
		this.statusCode = ResponseCodes.SUCCESS;
		this.data = data;
	}

	public ProAPISuccessResponse(String statusCode, Object data) {
		super();
		this.statusCode = statusCode;
		this.data = data;
	}

	@Override
	public String getStatusCode() {
		return statusCode;
	}

	@Override
	public void setStatusCode(String code) {
		this.statusCode = code;
	}

//	@JsonRawValue
	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	
}
