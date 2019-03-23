package ca.unb.ktb.core.svc.exception;

/**
 * Generic service-layer exception.
 * */

abstract class ServiceException extends RuntimeException {

    ServiceException(String message) {
        super(message);
    }

    ServiceException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
