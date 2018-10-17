package com.unb.beforeigo.api.exception.client;

import com.unb.beforeigo.api.exception.HttpException;

abstract class ClientErrorException extends HttpException {

    ClientErrorException(String message) {
        super(message);
    }
}
