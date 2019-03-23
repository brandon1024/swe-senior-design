package ca.unb.ktb.core.svc.exception;

/**
 * Service-layer exception, thrown in the event an {@link ca.unb.ktb.infrastructure.AmazonS3Bucket} was not found in
 * {@link ca.unb.ktb.infrastructure.AmazonS3BucketConfiguration}.
 *
 * Typically, this exception will indicate that a bucket was not specified through externalized configuration.
 * */

public class MissingS3BucketConfigurationException extends ServiceException {

    public MissingS3BucketConfigurationException(String message) {
        super(message);
    }

    public MissingS3BucketConfigurationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
