package com.proptiger.exception;

/**
 * Custom Bad Request Exception Class
 * 
 * @author Azitabh Ajit
 * 
 */

public class BadRequestException extends ProAPIException {
    private static final long serialVersionUID = -1L;

    public BadRequestException() {
        super();
    }

    public BadRequestException(String s) {
        super(s);
    }
    public BadRequestException(String responseCode, String message) {
        super(responseCode, message);
    }
}
