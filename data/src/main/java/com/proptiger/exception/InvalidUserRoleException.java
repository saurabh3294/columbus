package com.proptiger.exception;

import org.springframework.security.core.AuthenticationException;

public class InvalidUserRoleException extends AuthenticationException{

    private static final long serialVersionUID = 1L;

    public InvalidUserRoleException(String msg) {
        super(msg);
    }

}
