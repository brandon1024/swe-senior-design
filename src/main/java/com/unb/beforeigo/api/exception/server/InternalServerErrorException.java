package com.unb.beforeigo.api.exception.server;

import com.unb.beforeigo.api.exception.HttpException;
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
