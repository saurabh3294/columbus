package com.proptiger.exception;

import com.proptiger.core.constants.ResponseCodes;

/**
 * This is a wrapper over RuntimeException. To make code cleaner, for all non
 * recoverable exception this class should be used
 * 
 * @author Rajeev Pandey
 * 
 */
public class ProAPIException extends RuntimeException implements APIException{

    private static final long serialVersionUID = 4182555505392936914L;

    protected String          responseCode     = ResponseCodes.INTERNAL_SERVER_ERROR;
    protected Object          data;

    public ProAPIException(Throwable ex) {
        super(ex);
    }

    public ProAPIException(String responseCode, String message){
    	super(message);
    	this.responseCode = responseCode;
    }
    public ProAPIException(String msg) {
        super(msg);
    }

    public ProAPIException() {
        super();
    }

    public ProAPIException(String message, Throwable ex) {
        super(message, ex);
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
