package com.unb.beforeigo.core.svc;

import com.unb.beforeigo.api.dto.response.BucketSummaryResponse;
import com.unb.beforeigo.api.exception.client.BadRequestException;
import com.unb.beforeigo.api.exception.client.UnauthorizedException;
import com.unb.beforeigo.application.dao.BucketDAO;
import com.unb.beforeigo.application.dao.UserDAO;
import com.unb.beforeigo.core.model.Bucket;
import com.unb.beforeigo.core.model.User;
import com.unb.beforeigo.core.model.validation.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BucketService {

    @Autowired private ItemService itemService;

    @Autowired private UserDAO userDAO;

    @Autowired private BucketDAO bucketDAO;

    /**
     * Create a new {@link Bucket} that is associated to a given user.
     *
     * The bucket provided must be valid. The {@link Bucket#owner} field will be overwritten with the user found using the
     * ownerId parameter. The {@link Bucket#id} field is set to null to prevent this method from being used to
     * overwrite a bucket already persisted.
     *
     * @param ownerId the id of the user that will own the bucket
     * @param bucket the bucket to create
     * @return a summary of the bucket once persisted in the database
     * @throws BadRequestException if a user with the provided id cannot be found
     * */
    public BucketSummaryResponse createBucket(final Long ownerId, final Bucket bucket) {
        User bucketOwner = userDAO.findById(ownerId)
                .orElseThrow(() ->
                        new BadRequestException("Unable to find a record with id " + ownerId));

        bucket.setOwner(bucketOwner);
        bucket.setId(null);

        Bucket savedBucket = saveBucket(bucket);
        return adaptBucketToBucketSummary(savedBucket);
    }

    /**
     * Create a new {@link Bucket} that is associated to a given user, that is duplicated from an existing bucket.
     *
     * Duplicated buckets will inherit all fields from the parent bucket (except {@link Bucket#owner}).
     *
     * If the bucket with the id provided is private, the bucket will only be duplicated if the owner of the bucket
     * matches the id of the childBucketOwnerId param. Conversely, if the childBucketOwnerId param matches the owner of
     * the bucket, the bucket may be duplicated regardless of whether it is private or public.
     *
     * @param newBucketOwnerId the id of the new owner of the bucket
     * @param bucketId the id of the bucket that is to be duplicated
     * @return a summary of the duplicated bucket once persisted in the database
     * @throws BadRequestException if a user with childBucketOwnerId does not exist
     * @throws BadRequestException if a bucket with parentBucketId does not exist
     * @throws UnauthorizedException if the bucket is private but owners do not match.
     * */
    public BucketSummaryResponse duplicateBucket(final Long newBucketOwnerId, final Long bucketId) {
        User childBucketOwner = userDAO.findById(newBucketOwnerId)
                .orElseThrow(() ->
                        new BadRequestException("Unable to find a record with id " + newBucketOwnerId));

        Bucket parentBucket = bucketDAO.findById(bucketId)
                .orElseThrow(() ->
                        new BadRequestException("Unable to find a record with id " + bucketId));

        if(!parentBucket.getIsPublic() && !Objects.equals(parentBucket.getOwner().getId(), newBucketOwnerId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        parentBucket.setOwner(childBucketOwner);
        parentBucket.setId(null);

        Bucket newBucket = saveBucket(parentBucket);
        itemService.duplicateBucketItems(newBucketOwnerId, parentBucket.getId(), newBucket.getId());

        return adaptBucketToBucketSummary(newBucket);
    }

    /**
     * Retrieve a list of {@link Bucket}'s associated to a given user.
     *
     * @param ownerId the id of the user that owns the buckets
     * @param publicOnly returns only buckets that are public
     * @return a list of bucket summaries
     * @throws BadRequestException if a user with the given ownerId does not exist
     * */
    public List<BucketSummaryResponse> findBuckets(final Long ownerId, final boolean publicOnly) {
        User bucketOwner = userDAO.findById(ownerId)
                .orElseThrow(() ->
                        new BadRequestException("Unable to find a record with id " + ownerId));

        List<Bucket> buckets = publicOnly ? bucketDAO.findAllByOwnerAndIsPublicTrue(bucketOwner) :
                bucketDAO.findAllByOwner(bucketOwner);

        return buckets.stream()
                .map(this::adaptBucketToBucketSummary)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve a specific {@link Bucket} by bucket id.
     *
     * If the bucket is private and the publicOnly parameter is true (i.e. the bucket with the given id is private),
     * then an UnauthorizedException is thrown.
     *
     * @param bucketId the id of the bucket to retrieve
     * @param publicOnly specify whether bucket is retrieved only if it is public
     * @return a summary of a bucket, if found.
     * @throws BadRequestException if a bucket with the given id cannot be found.
     * @throws UnauthorizedException if the bucket with the given id is private, but the publicOnly param is true.
     * */
    public BucketSummaryResponse findBucketById(final Long bucketId, final boolean publicOnly) {
        Bucket bucket = bucketDAO.findById(bucketId)
                .orElseThrow(() ->
                        new BadRequestException("Unable to find a record with id " + bucketId));

        if(publicOnly && !bucket.getIsPublic()) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        return adaptBucketToBucketSummary(bucket);
    }

    /**
     * Partially update a {@link Bucket} with a given id.
     *
     * All non-null fields in the partialBucket are used to overwrite the same fields in the bucket currently persisted
     * in the database.
     *
     * Partial bucket owner field is ignored, because the bucket ownership cannot be transferred to a new user.
     *
     * The partial bucket provided must have the owner field specified. Although it is not used to update
     * the persisted bucket, it is used to verify the user that owns the partial bucket matches the user that owns the
     * bucket with the given id. Specifically, this is used by the {@link com.unb.beforeigo.api.BucketController} to
     * verify that the user id provided as a path variable matches the owner of the bucket with the id provided as a path
     * variable.
     *
     * @param partialBucket the partial bucket used to update the bucket
     * @param bucketId the id of the bucket to patch
     * @return a summary of the patched bucket, once persisted in the database
     * @throws BadRequestException if a bucket with the given bucketId cannot be found
     * @throws BadRequestException if the owner of the partial bucket does not match the owner of the bucket with the
     * given id. See the api note.
     * */
    public BucketSummaryResponse patchBucket(final Bucket partialBucket, final Long bucketId) {
        Bucket persistedBucket = bucketDAO.findById(bucketId)
                .orElseThrow(() -> new BadRequestException("Unable to find bucket with id " + bucketId));

        if(!Objects.equals(persistedBucket.getOwner().getId(), partialBucket.getOwner().getId())) {
            throw new BadRequestException("Owner of bucket with id " + bucketId +
                    " does not match partial bucket owner with user id " + partialBucket.getOwner().getId() + ".");
        }

        if(Objects.nonNull(partialBucket.getName())) {
            persistedBucket.setName(partialBucket.getName());
        }

        if(Objects.nonNull(partialBucket.getIsPublic())) {
            persistedBucket.setIsPublic(partialBucket.getIsPublic());
        }

        if(Objects.nonNull(partialBucket.getDescription())) {
            persistedBucket.setDescription(partialBucket.getDescription());
        }

        Bucket childBucket = saveBucket(persistedBucket);
        return adaptBucketToBucketSummary(childBucket);
    }

    /**
     * Completely update a {@link Bucket} with a given id.
     *
     * All fields in the bucket parameter are used to overwrite the fields in the bucket currently persisted
     * in the database.
     *
     * API Note: he bucket provided must have the owner field specified, and it must match the bucket with the provided
     * bucketId. This is used to prevent the transfer of ownership of a bucket.
     *
     * @param bucket the bucket used to update the persisted bucket
     * @param bucketId the id of the bucket to patch
     * @return a summary of the updated bucket, once persisted in the database
     * @throws BadRequestException if a bucket with the given bucketId cannot be found
     * @throws BadRequestException if the owner of the partial bucket does not match the owner of the bucket with the
     * given id. See the api note.
     * */
    public BucketSummaryResponse updateBucket(final Bucket bucket, final Long bucketId) {
        Bucket persistedBucket = bucketDAO.findById(bucketId)
                .orElseThrow(() -> new BadRequestException("Unable to find bucket with id " + bucketId));

        if(!Objects.equals(persistedBucket.getOwner().getId(), bucket.getOwner().getId())) {
            throw new BadRequestException("Owner of bucket with id " + bucketId +
                    " does not match url path variable for user id " + bucket.getOwner().getId() + ".");
        }

        bucket.setId(persistedBucket.getId());

        Bucket childBucket = saveBucket(persistedBucket);
        return adaptBucketToBucketSummary(childBucket);
    }

    /**
     * Delete a given bucket. The {@link Bucket#id} field must be non-null.
     *
     * The bucket provided must have the owner field specified. It is used to verify the user that owns the
     * bucket provided matches the user that owns the bucket with the given id. Specifically, this is used by the
     * {@link com.unb.beforeigo.api.BucketController} to verify that the user id provided as a path variable matches the
     * owner of the bucket with the id provided as a path variable.
     *
     * @param bucket the bucket to delete
     * @throws BadRequestException if the owner of the bucket does not match the owner of the bucket with the given id.
     * See the api note.
     * */
    public void deleteBucket(final Bucket bucket) {
        Bucket persistedBucket = bucketDAO.findById(bucket.getId())
                .orElseThrow(() -> new BadRequestException("Unable to find bucket with id " + bucket.getId()));

        if(!Objects.equals(persistedBucket.getOwner().getId(), bucket.getOwner().getId())) {
            throw new BadRequestException("Owner of bucket with id " + persistedBucket.getOwner().getId() +
                    " does not match url path variable for user id " + bucket.getOwner().getId() + ".");
        }

        bucketDAO.delete(persistedBucket);
    }

    /**
     * Save a bucket.
     *
     * If the {@link Bucket#isPublic} field is null, a default value of true is used.
     *
     * Performs constraint validation.
     *
     * @param bucket the bucket to save
     * @return the bucket once persisted in the database
     * @throws BadRequestException if the user does not meet validation constraints.
     * @see org.springframework.data.jpa.repository.JpaRepository#save(Object)
     * */
    private Bucket saveBucket(final Bucket bucket) {
        if(Objects.isNull(bucket.getIsPublic())) {
            bucket.setIsPublic(true);
        }

        EntityValidator.validateEntity(bucket, () ->
                new BadRequestException("cannot save bucket that does not meet validation constraints"));
        return bucketDAO.save(bucket);
    }

    /**
     * Build a UserSummaryResponse DTO of a User entity.
     *
     * @param bucket the bucket to be used to build a BucketSummaryResponse
     * @return a summary of the bucket
     * */
    public BucketSummaryResponse adaptBucketToBucketSummary(final Bucket bucket) {
        Long ownerId = Objects.nonNull(bucket.getOwner()) ? bucket.getOwner().getId() : null;
        return new BucketSummaryResponse(bucket.getId(),
                ownerId,
                bucket.getName(),
                bucket.getIsPublic(),
                bucket.getDescription());
    }
}
