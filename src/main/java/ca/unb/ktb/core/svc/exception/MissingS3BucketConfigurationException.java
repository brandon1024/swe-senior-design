package ca.unb.ktb.core.svc.exception;

public class MissingS3BucketConfigurationException extends ServiceException {

    public MissingS3BucketConfigurationException(String message) {
        super(message);
    }

    public MissingS3BucketConfigurationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
