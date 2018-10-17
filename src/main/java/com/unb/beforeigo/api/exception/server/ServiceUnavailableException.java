package com.unb.beforeigo.api.exception.server;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception used to signal that the server is unable to process a request because the requested service is currently
 * unavailable.
 * */
@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
public class ServiceUnavailableException extends ServerErrorException {

    public ServiceUnavailableException(String message) {
        super(message);
    }
}
