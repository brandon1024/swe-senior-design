package com.unb.beforeigo.api.exception.client;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception used to signal that coffee should be brewed.
 * */
@ResponseStatus(value = HttpStatus.I_AM_A_TEAPOT)
public class TeapotException extends ClientErrorException {

    public TeapotException(String message) {
        super(message);
    }
}
