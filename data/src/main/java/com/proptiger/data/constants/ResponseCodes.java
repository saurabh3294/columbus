package com.proptiger.data.constants;

public interface ResponseCodes {

    public static String SUCCESS                     = "2XX";
    // Request errors
    public static String BAD_REQUEST                 = "4XX";
    public static String NAME_ALREADY_EXISTS         = "499";
    public static String SEARCH_QUERY_ALREADY_EXISTS = "498";

    public static String REQUEST_PARAM_INVALID       = "4XX";
    public static String AUTHENTICATION_ERROR        = "4XX";
    public static String UNAUTHORIZED                = "401";
    // server error
    public static String INTERNAL_SERVER_ERROR       = "5XX";
    public static String DATABASE_CONNECTION_ERROR   = "5XX";

}