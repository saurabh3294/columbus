package com.proptiger.exception;

public class LeadPostException extends ProAPIException {
    private static final long serialVersionUID = 4317114365316616103L;

    public LeadPostException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
