package ca.unb.ktb.infrastructure;

import ca.unb.ktb.core.svc.exception.MissingS3BucketConfigurationException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Component
@ConfigurationProperties(prefix = "awss3")
@Data
@Slf4j
public class AmazonS3BucketConfiguration {

    public static final String userProfileImageBucket = "USER_PROFILE";

    private Map<String, AmazonS3Bucket> buckets;

    /**
     * Retrieve a {@link AmazonS3Bucket} configuration using the provided key for the bucket.
     *
     * @param bucketkey The bucket key.
     * @return The AmazonS3Bucket configuration POJO.
     * @throws MissingS3BucketConfigurationException If a bucket with the provided key does not exist.
     * */
    public AmazonS3Bucket getBucket(final String bucketkey) {
        AmazonS3Bucket bucket = buckets.get(bucketkey);

        if(Objects.isNull(bucket)) {
            LOG.warn("Attempted to retrieve an S3 Bucket configuration that could not be found." +
                    "Perhaps a misconfiguration of Spring?");
            throw new MissingS3BucketConfigurationException(String.format("Missing AWS S3 configuration; could not" +
                            "retrieve bucket configuration with key %s.",
                    AmazonS3BucketConfiguration.userProfileImageBucket));
        }

        return bucket;
    }
}
