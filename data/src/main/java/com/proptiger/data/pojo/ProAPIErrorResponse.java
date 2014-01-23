package com.proptiger.data.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * This class represents a error response.
 * 
 * @author Rajeev Pandey
 * 
 */
public class ProAPIErrorResponse implements ProAPIResponse {

	private String statusCode;
	private ProError error;
	
	@JsonInclude(Include.NON_NULL)
	private Object data;

	public ProAPIErrorResponse(String statusCode, String errorMsg) {
		this.statusCode = statusCode;
		this.error = new ProError(errorMsg);
	}

	public ProAPIErrorResponse(String statusCode, String errorMsg, Object data) {
        this.statusCode = statusCode;
        this.error = new ProError(errorMsg);
        this.data = data;
    }

	@Override
	public String getStatusCode() {
		// TODO Auto-generated method stub
		return statusCode;
	}

	@Override
	public void setStatusCode(String code) {
		this.statusCode = code;
	}

	public ProError getError() {
		return error;
	}

	public void setError(ProError error) {
		this.error = error;
	}

	static class ProError {
		private String msg;

		public ProError() {
			super();
		}

		public ProError(String msg) {
			super();
			this.msg = msg;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}

		@Override
		public String toString() {
			return "{msg=" + msg + "}";
		}
		
		
	}

	@Override
	public String toString() {
		return "{statusCode=" + statusCode + ", error="
				+ error + "}";
	}

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
	
}
