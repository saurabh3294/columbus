package com.proptiger.exception;

import com.proptiger.core.constants.ResponseCodes;

public class ResourceNotFoundException extends ProAPIException {
    private static final long serialVersionUID = 1L;
    protected String          responseCode     = ResponseCodes.RESOURCE_NOT_FOUND;

    public ResourceNotFoundException(Throwable ex) {
        super(ex);
    }

    public ResourceNotFoundException(String msg) {
        super(msg);
    }

    public ResourceNotFoundException() {
        super();
    }

    public ResourceNotFoundException(String message, Throwable ex) {
        super(message, ex);
    }
}