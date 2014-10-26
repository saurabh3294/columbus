package com.proptiger.data.notification.exception;

import com.proptiger.core.exception.ProAPIException;

/**
 * 
 * @author Sahil Garg
 * 
 */

public class MultipleGCMRegistrationIdFoundException extends ProAPIException {
    private static final long serialVersionUID = -1L;

    public MultipleGCMRegistrationIdFoundException() {
        super();
    }

    public MultipleGCMRegistrationIdFoundException(String s) {
        super(s);
    }
    public MultipleGCMRegistrationIdFoundException(String responseCode, String message) {
        super(responseCode, message);
    }
}
