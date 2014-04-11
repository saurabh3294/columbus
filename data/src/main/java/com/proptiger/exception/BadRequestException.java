package com.proptiger.exception;

/**
 * Custom Bad Request Exception Class
 * 
 * @author Azitabh Ajit
 * 
 */

public class BadRequestException extends IllegalArgumentException {
    private static final long serialVersionUID = -1L;

    public BadRequestException() {
        super();
    }

    public BadRequestException(String s) {
        super(s);
    }
}
