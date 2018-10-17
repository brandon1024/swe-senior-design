package com.unb.beforeigo.infrastructure.security.exception;

import org.springframework.security.core.AuthenticationException;

public class MissingAuthTokenException extends AuthenticationException {

    public MissingAuthTokenException(String message) {
        super(message);
    }

    public MissingAuthTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
