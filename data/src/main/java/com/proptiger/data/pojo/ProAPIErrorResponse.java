package com.proptiger.data.pojo;

/**
 * This class represents a error response.
 * 
 * @author Rajeev Pandey
 * 
 */
public class ProAPIErrorResponse implements PropAPIResponse {

	private String statusCode;
	private ProError error;

	public ProAPIErrorResponse(String statusCode, String errorMsg) {
		super();
		this.statusCode = statusCode;
		this.error = new ProError(errorMsg);
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
	}
}
