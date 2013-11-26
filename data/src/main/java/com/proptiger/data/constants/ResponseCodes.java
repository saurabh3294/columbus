package com.proptiger.data.constants;

public interface ResponseCodes {

	public static String SUCCESS = "2XX";
	//Request errors
	public static String BAD_REQUEST = "4XX";
	public static String REQUEST_PARAM_INVALID = "4XX";
	public static String AUTHENTICATION_ERROR = "4XX";
	public static String UNAUTHORIZED = "401";
	//server error
	public static String INTERNAL_SERVER_ERROR = "5XX";
	public static String DATABASE_CONNECTION_ERROR = "5XX";
	
}