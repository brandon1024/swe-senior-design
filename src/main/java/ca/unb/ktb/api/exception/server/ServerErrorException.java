package ca.unb.ktb.api.exception.server;

import ca.unb.ktb.api.exception.HttpException;

abstract class ServerErrorException extends HttpException {

    ServerErrorException(String message) {
        super(message);
    }

    ServerErrorException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
