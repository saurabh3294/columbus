package com.proptiger.data.notification.exception;

import com.proptiger.core.exception.ProAPIException;

/**
 * 
 * @author Sahil Garg
 * 
 */

public class NotificationMediumNotFoundException extends ProAPIException {
    private static final long serialVersionUID = -1L;

    public NotificationMediumNotFoundException() {
        super();
    }

    public NotificationMediumNotFoundException(String s) {
        super(s);
    }
    public NotificationMediumNotFoundException(String responseCode, String message) {
        super(responseCode, message);
    }
}
