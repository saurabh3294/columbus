package com.proptiger.exception;

public class ConstraintViolationException extends ProAPIException {

    /**
	 * 
	 */
    private static final long serialVersionUID = 2735788844408093724L;

    public ConstraintViolationException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
