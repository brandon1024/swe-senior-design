package com.unb.beforeigo.api.exception.client;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception used to signal that a HTTP method is not allowed for a given API endpoint.
 * */
@ResponseStatus(value = HttpStatus.METHOD_NOT_ALLOWED)
public class MethodNotAllowedException extends ClientErrorException {

    public MethodNotAllowedException(String message) {
        super(message);
    }
}
