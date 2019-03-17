package ca.unb.ktb.infrastructure;

import lombok.Data;

/**
 * Simple POJO which represents a single AWS S3 Bucket.
 * */

@Data
public class AmazonS3Bucket {

    private String name;

    private String region;

    public String getBaseUrl() {
        return String.format("s3://%s/", this.name);
    }

    public String getFullUrl(final String objectKey) {
        return this.getBaseUrl() + objectKey;
    }
}
