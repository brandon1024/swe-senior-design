package ca.unb.ktb.api.exception.client;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception used to signal that a client is forbidden from accessing a given resource.
 * */
@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class ForbiddenException extends ClientErrorException {

    public ForbiddenException(String message) {
        super(message);
    }
}
