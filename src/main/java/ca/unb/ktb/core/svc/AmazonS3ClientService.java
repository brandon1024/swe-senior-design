package ca.unb.ktb.core.svc;

import ca.unb.ktb.infrastructure.AmazonS3Bucket;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class AmazonS3ClientService {

    /**
     * Upload a {@link MultipartFile} to AWS S3.
     *
     * Files are uploaded with the provided {@link ObjectMetadata} to the S3 bucket represented by the {@link AmazonS3Bucket}
     * configuration.
     *
     * Files are stored in the bucket using the provided path, the MD5 hash of the file content, and the original
     * filename:
     * s3://{bucket name}/{object path}/{md5 hash}.{original filename}
     *
     * File are uploaded synchronously, and this function will return the key for the object.
     *
     * @param file The {@link MultipartFile} to upload to S3.
     * @param fileMetadata Metadata to attach to the object in S3.
     * @param bucket Details of the S3 bucket which will receive the new file.
     * @param objectPath Path to the new file within the bucket.
     * @return The object key.
     * @throws com.amazonaws.AmazonServiceException If the request was correctly submitted, but AWS was unable to process the request.
     * @throws com.amazonaws.AmazonClientException If the AmazonS3 client was unable to parse the response from AWS, or unable to get a response.
     * @throws InterruptedException If the transfer was interrupted unexpectedly.
     * @throws IOException If the transfer could not be completed unexpectedly.
     * @throws NoSuchAlgorithmException If the MD5 hash of the file contents could not be computed.
     * */
    public String multipartFileUpload(final MultipartFile file, final ObjectMetadata fileMetadata,
                                      final AmazonS3Bucket bucket, final String objectPath)
            throws InterruptedException, IOException, NoSuchAlgorithmException {
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(bucket.getRegion())
                .withCredentials(new EnvironmentVariableCredentialsProvider())
                .withPathStyleAccessEnabled(true)
                .build();
        TransferManager tm = TransferManagerBuilder.standard()
                .withS3Client(s3Client)
                .build();

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] digest = md5.digest(file.getBytes());
        String hashString = new BigInteger(1, digest).toString(16);
        String objectKey = String.format("%s/%s.%s", objectPath, hashString, file.getOriginalFilename());

        LOG.info("Uploading multipart file {} to AWS S3 with key {}", file.getOriginalFilename(), objectKey);
        PutObjectRequest objectRequest = new PutObjectRequest(bucket.getName(), objectKey, file.getInputStream(),
                fileMetadata);
        tm.upload(objectRequest).waitForCompletion();
        return objectKey;
    }

    /**
     * Generate a pre-signed URL which allows public read access for the object stored in the given {@link AmazonS3Bucket}.
     *
     * Generated URLs have a TTL of 30 minutes.
     *
     * URLs are generated with path style enabled to ensure that bucket's with dots '.' do not result in invalid
     * certificate errors in the client.
     *
     * @param bucket The {@link AmazonS3Bucket} from which to generate a pre-signed URL.
     * @param objectKey The key to the object in the bucket.
     * @return A {@link Optional} containing the pre-signed {@link URL} to the object with the given key, or an empty
     * Optional if no such object exists.
     * @throws SdkClientException If there were any problems pre-signing the request for the Amazon S3 resource.
     * */
    public Optional<URL> generatePreSignedObjectURL(final AmazonS3Bucket bucket, final String objectKey) {
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(bucket.getRegion())
                .withCredentials(new EnvironmentVariableCredentialsProvider())
                .withPathStyleAccessEnabled(true)
                .build();

        if(!s3Client.doesObjectExist(bucket.getName(), objectKey)) {
            LOG.info("No object exists in AWS S3 with key {}", objectKey);
            return Optional.empty();
        }

        Date expiration = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(expiration);
        cal.add(Calendar.MINUTE, 30);

        LOG.info("Generating pre-signed URL for object {} in bucket {}", objectKey, bucket.getName());
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket.getName(), objectKey)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(cal.getTime());

        URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
        return Optional.of(url);
    }
}
