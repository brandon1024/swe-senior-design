package com.unb.beforeigo.api.exception.client;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception used to signal that a given resource has been intentionally and permanently removed.
 * */
@ResponseStatus(value = HttpStatus.GONE)
public class GoneException extends ClientErrorException {

    public GoneException(String message) {
        super(message);
    }
}
