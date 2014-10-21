package com.proptiger.exception;

import com.proptiger.core.constants.ResponseCodes;

public class ResourceAlreadyExistException extends ProAPIException {

    /**
	 * 
	 */
    private static final long serialVersionUID = 8841380016882708109L;

    public ResourceAlreadyExistException(String msg, String responseCode, Object data) {
        this(msg, responseCode);
        this.data = data;
    }

    public ResourceAlreadyExistException(String msg, String responseCode) {
        super(msg);
        this.responseCode = responseCode;
    }

    public ResourceAlreadyExistException(String msg) {
        super(msg);
        this.responseCode = ResponseCodes.BAD_REQUEST;
    }
}
