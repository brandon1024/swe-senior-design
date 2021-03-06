package ca.unb.ktb.api.exception.server;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception used to signal that the server is unable to process a request because the requested resource
 * is not implemented.
 * */
@ResponseStatus(value = HttpStatus.NOT_IMPLEMENTED)
public class NotImplementedException extends ServerErrorException {

    public NotImplementedException(String message) {
        super(message);
    }

    public NotImplementedException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
