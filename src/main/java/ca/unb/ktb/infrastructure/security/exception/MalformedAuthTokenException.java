package ca.unb.ktb.infrastructure.security.exception;

import org.springframework.security.core.AuthenticationException;

public class MalformedAuthTokenException extends AuthenticationException {

    public MalformedAuthTokenException(String message) {
        super(message);
    }

    public MalformedAuthTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
