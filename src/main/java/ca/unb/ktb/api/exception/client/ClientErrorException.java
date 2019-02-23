package ca.unb.ktb.api.exception.client;

import ca.unb.ktb.api.exception.HttpException;

abstract class ClientErrorException extends HttpException {

    ClientErrorException(String message) {
        super(message);
    }
}
