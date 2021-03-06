package ca.unb.ktb.api.exception.client;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception used to signal that a client initiated an invalid request.
 * */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestException extends ClientErrorException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
