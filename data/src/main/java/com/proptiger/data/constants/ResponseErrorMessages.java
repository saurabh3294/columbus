package com.proptiger.data.constants;

/**
 * @author Rajeev Pandey
 *
 */
public interface ResponseErrorMessages {
	static String SOME_ERROR_OCCURED = "some error occured, please try again later";
	static String DATABASE_CONNECTION_ERROR = "database connection error";
	static String REQUEST_PARAM_CONVERSION_ERROR = "request parameter is not valid";
	static String REQUEST_PARAM_INVALID = "request parameter is not valid";
	static String INVALID_NAME_ATTRIBUTE = "Invalid name attribute";
	static String DUPLICATE_NAME_RESOURCE = "Resource with same name exist";
	static String SOLR_DOWN = "Solr Down, please bear with us";
	static String INVALID_CONTENT_TYPE = "Invalid Content-Type in request header";
	static String INVALID_REQUEST_METHOD_URL_AND_BODY = "Invalid combination of request method, url and body";
}
