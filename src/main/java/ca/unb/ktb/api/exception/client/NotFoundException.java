package ca.unb.ktb.api.exception.client;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception used to signal that a specific resource could not be found.
 * */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends ClientErrorException {

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
