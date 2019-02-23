package ca.unb.ktb.api.exception.server;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception used to signal that the server is unable to process a request due to an internal server error.
 * */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends ServerErrorException {

    public InternalServerErrorException(String message) {
        super(message);
    }
}
