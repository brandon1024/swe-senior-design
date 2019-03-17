package ca.unb.ktb.api.exception;

public abstract class HttpException extends RuntimeException {

    public HttpException(String message) {
        super(message);
    }

    public HttpException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
