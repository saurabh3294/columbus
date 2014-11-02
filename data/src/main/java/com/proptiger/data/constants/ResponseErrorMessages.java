package com.proptiger.data.constants;

import com.proptiger.data.util.Constants;

/**
 * @author Rajeev Pandey
 * 
 */
public interface ResponseErrorMessages {
    static String SOME_ERROR_OCCURED                  = "Some error occurred, please try again later";
    static String DATABASE_CONNECTION_ERROR           = "Database connection error";
    static String REQUEST_PARAM_CONVERSION_ERROR      = "request parameter is not valid";
    static String REQUEST_PARAM_INVALID               = "Request parameter is not valid";
    static String INVALID_NAME_ATTRIBUTE              = "Invalid name attribute";
    static String DUPLICATE_NAME_RESOURCE             = "Resource with same name exist";
    static String DUPLICATE_RESOURCE                  = "Duplicate resource";
    static String SOLR_DOWN                           = "Solr Down, please bear with us";
    static String INVALID_CONTENT_TYPE                = "Invalid Content-Type in request header";
    static String INVALID_REQUEST_METHOD_URL_AND_BODY = "Invalid combination of request method, url and body";
    static String RESOURCE_ALREADY_EXIST              = "Resource already exist";
    static String INVALID_FORMAT_IN_REQUEST           = "Invalid format in request parameter value";
    static String LEAD_COULD_NOT_POST                 = "Lead could not post";
    static String MAIL_SENDING_ERROR                  = "Mail could not not be sent";
    static String USER_NAME_PASSWORD_INCORRECT        = "User name or password not correct";
    static String AUTHENTICATION_ERROR                = "Authentication error";
    static String UNAUTHORIZED                        = "Unauthorized";
    static String BAD_REQUEST                         = "Bad Request";
    static String INVALID_USER_PREFERENCE             = "Invalid User Preference";
    static String LIMIT_OF_COMPOSITE_API_EXCEEDED     = "Maximum limit of" + Constants.LIMIT_OF_COMPOSITE_APIs
                                                              + " APIs allowed in composite call crossed";
    static String BAD_CREDENTIAL                      = "Bad credentials";
    static String LOG_MESSAGE_ERROR                   = "Log message Empty or Log level invalid";

    static String INVALID_PASSWORD                    = "Invalid password";
    static String OLD_PASSWORD_REQUIRED               = "Old password required";
    static String NEW_PASS_CONFIRM_PASS_NOT_MATCHED   = "New password and confirm password do not match";
    static String INVALID_EMAIL                       = "Invalid email address";
    static String INVALID_USERNAME_NAME_LEN           = "Invalid username length";
    static String INVALID_COUNTRY                     = "Invalid country";
    static String INVALID_CONTACT_NUMBER              = "Invalid contact number";
    static String EMAIL_ALREADY_REGISTERED            = "Email already registered";

    static String EMAIL_NOT_REGISTERED                = "Sorry, that email id is not registered with us.";
    static String PASSWORD_RECOVERY_MAIL_SENT         = "Password recovery mail sent";
    static String CAPTCHA_REQUIRED                    = "Captcha Required";
    static String SESSION_EXPIRED_DUPLICATE_LOGIN     = "Session expired, duplicate login";
    static String OTP_REQUIRED                        = "Please enter OTP sent on your registered email";
    static String WRONG_OTP                           = "Wrong OTP";
    static String OTP_EXPIRED                         = "OTP expired, sent new OTP";
    
    static String NON_B2B_USER                        = "Invalid userid and password. Please send mail to datalabs@proptiger.com for verifying userid and password.";
    static String EXPIRED_PERMISSION_B2B_USER         = "Thanks for using our product. Validity of your subscription has expired. To continue using this service, please connect with your relationship manager or send us mail at datalabs@proptiger.com";
    static String ACCESS_DENIED                       = "Access is denied";
}
