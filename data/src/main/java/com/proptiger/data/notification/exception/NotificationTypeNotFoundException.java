package com.proptiger.data.notification.exception;

import com.proptiger.core.exception.ProAPIException;

/**
 * 
 * @author Sahil Garg
 * 
 */

public class NotificationTypeNotFoundException extends ProAPIException {
    private static final long serialVersionUID = -1L;

    public NotificationTypeNotFoundException() {
        super();
    }

    public NotificationTypeNotFoundException(String s) {
        super(s);
    }
    public NotificationTypeNotFoundException(String responseCode, String message) {
        super(responseCode, message);
    }
}
