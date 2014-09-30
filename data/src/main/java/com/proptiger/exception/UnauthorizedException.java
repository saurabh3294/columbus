package com.proptiger.exception;

/**
 * Unauthorized Exception
 * 
 * @author azi
 * 
 */
public class UnauthorizedException extends ProAPIException {
    private static final long serialVersionUID = 1L;

    public UnauthorizedException() {
        super();
    }
    
    public UnauthorizedException(String responseCode, String message) {
        super(responseCode, message);
    }
    
}