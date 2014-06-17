package com.proptiger.data.constants;

public interface ResponseCodes {

    public static String SUCCESS                     = "2XX";
    // Request errors
    public static String UNAUTHORIZED                = "401";
    public static String RESOURCE_NOT_FOUND          = "404";
    public static String BAD_REQUEST                 = "4XX";
    public static String NAME_ALREADY_EXISTS         = "499";
    public static String SEARCH_QUERY_ALREADY_EXISTS = "498";
    public static String BAD_CREDENTIAL                 = "497";

    public static String REQUEST_PARAM_INVALID       = BAD_REQUEST;
    public static String AUTHENTICATION_ERROR        = BAD_REQUEST;

    // server error
    public static String INTERNAL_SERVER_ERROR       = "5XX";
    public static String DATABASE_CONNECTION_ERROR   = INTERNAL_SERVER_ERROR;

}