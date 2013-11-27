package com.proptiger.data.constants;

/**
 * @author Rajeev Pandey
 *
 */
public interface ResponseErrorMessages {
	static String SOME_ERROR_OCCURED = "Some error occurred, please try again later";
	static String DATABASE_CONNECTION_ERROR = "Database connection error";
	static String REQUEST_PARAM_CONVERSION_ERROR = "request parameter is not valid";
	static String REQUEST_PARAM_INVALID = "Request parameter is not valid";
	static String INVALID_NAME_ATTRIBUTE = "Invalid name attribute";
	static String DUPLICATE_NAME_RESOURCE = "Resource with same name exist";
	static String DUPLICATE_RESOURCE = "Duplicate resource";
	static String SOLR_DOWN = "Solr Down, please bear with us";
	static String INVALID_CONTENT_TYPE = "Invalid Content-Type in request header";
	static String INVALID_REQUEST_METHOD_URL_AND_BODY = "Invalid combination of request method, url and body";
	static String RESOURCE_ALREADY_EXIST = "Resource already exist";
	static String INVALID_FORMAT_IN_REQUEST = "Invalid format in request parameter value";
	static String LEAD_COULD_NOT_POST = "Lead could not post";
	static String MAIL_SENDING_ERROR = "Mail could not not be sent";
	static String USER_NAME_PASSWORD_INCORRECT = "User name or password not correct";
	static String AUTHENTICATION_ERROR = "Authentication error";
}
