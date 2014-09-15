package com.proptiger.exception;

import org.springframework.http.HttpStatus;

/**
 * Custom Bad Request Exception Class
 * 
 * @author Azitabh Ajit
 * 
 */

public class BadRequestException extends ProAPIException {
    private static final long serialVersionUID = -1L;

    public BadRequestException() {
        super(HttpStatus.BAD_REQUEST.toString(), HttpStatus.BAD_REQUEST.name());
    }

    public BadRequestException(String s) {
        super(HttpStatus.BAD_REQUEST.toString(), s);
    }

    public BadRequestException(String responseCode, String message) {
        super(responseCode, message);
    }
}
