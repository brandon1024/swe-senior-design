package ca.unb.ktb.infrastructure.security.exception;

import org.springframework.security.core.AuthenticationException;

public class SignatureGenerationException extends AuthenticationException {

    public SignatureGenerationException(String message) {
        super(message);
    }

    public SignatureGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
