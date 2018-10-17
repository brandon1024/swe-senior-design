package com.unb.beforeigo.api.exception;

public abstract class HttpException extends RuntimeException {

    public HttpException(String message) {
        super(message);
    }
}
