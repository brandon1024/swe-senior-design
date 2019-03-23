package ca.unb.ktb.api.exception.client;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception used to signal that a request could not be processed because of conflict in the current state of the
 * resource requested.
 * */
@ResponseStatus(value = HttpStatus.CONFLICT)
public class ConflictException extends ClientErrorException {

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
