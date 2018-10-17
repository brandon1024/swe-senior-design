package com.unb.beforeigo.api.exception.server;

import com.unb.beforeigo.api.exception.HttpException;

abstract class ServerErrorException extends HttpException {

    ServerErrorException(String message) {
        super(message);
    }
}
