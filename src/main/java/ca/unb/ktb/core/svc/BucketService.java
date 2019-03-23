package ca.unb.ktb.core.svc;

import ca.unb.ktb.api.BucketController;
import ca.unb.ktb.api.dto.response.BucketSummaryResponse;
import ca.unb.ktb.api.dto.response.UserSummaryResponse;
import ca.unb.ktb.api.exception.client.BadRequestException;
import ca.unb.ktb.api.exception.client.UnauthorizedException;
import ca.unb.ktb.application.dao.BucketDAO;
import ca.unb.ktb.application.dao.UserBucketRelationshipDAO;
import ca.unb.ktb.application.dao.UserDAO;
import ca.unb.ktb.core.model.Bucket;
import ca.unb.ktb.core.model.User;
import ca.unb.ktb.core.model.UserBucketRelationship;
import ca.unb.ktb.core.model.validation.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BucketService {

    @Autowired private ItemService itemService;

    @Autowired private UserService userService;

    @Autowired private UserDAO userDAO;

    @Autowired private BucketDAO bucketDAO;

    @Autowired private UserBucketRelationshipDAO userBucketRelationshipDAO;

    /**
     * Create a new {@link Bucket} that is associated to a given user.
     *
     * The bucket provided must be valid. The {@link Bucket}'s owner field will be overwritten with the {@link User}
     * found using the ownerId parameter. The id field is set to null to prevent this method from being used to
     * overwrite a bucket already persisted.
     *
     * @param ownerId The id of the {@link User} that will own the {@link Bucket}.
     * @param bucket The {@link Bucket} to create.
     * @return A summary of the {@link Bucket} once persisted in the database.
     * @throws BadRequestException If a {@link User} with the provided id cannot be found.
     * */
    public BucketSummaryResponse createBucket(final Long ownerId, final Bucket bucket) {
        User bucketOwner = userDAO.findById(ownerId).orElseThrow(() ->
                new BadRequestException(String.format("Unable to find a record with id %d.", ownerId)));

        bucket.setOwner(bucketOwner);
        bucket.setId(null);

        Bucket savedBucket = saveBucket(bucket);
        return adaptBucketToBucketSummary(savedBucket);
    }

    /**
     * Create a new {@link Bucket} that is associated to a given {@link User}, that is duplicated from an existing bucket.
     *
     * Duplicated buckets will inherit all fields from the parent bucket (except bucket owner).
     *
     * If the bucket with the id provided is private, the bucket will only be duplicated if the owner of the bucket
     * matches the id of the childBucketOwnerId param. Conversely, if the childBucketOwnerId param matches the owner of
     * the bucket, the bucket may be duplicated regardless of whether it is private or public.
     *
     * @param newBucketOwnerId The id of the new owner of the {@link Bucket}.
     * @param bucketId The id of the {@link Bucket} that is to be duplicated.
     * @return A summary of the duplicated {@link Bucket} once persisted in the database.
     * @throws BadRequestException If a {@link User} with childBucketOwnerId does not exist.
     * @throws BadRequestException If a {@link Bucket} with parentBucketId does not exist.
     * @throws UnauthorizedException If the {@link Bucket} is private but owners do not match.
     * */
    public BucketSummaryResponse duplicateBucket(final Long newBucketOwnerId, final Long bucketId) {
        User childBucketOwner = userDAO.findById(newBucketOwnerId).orElseThrow(() ->
                new BadRequestException(String.format("Unable to find a record with id %d.", newBucketOwnerId)));

        Bucket parentBucket = bucketDAO.findById(bucketId).orElseThrow(() ->
                new BadRequestException(String.format("Unable to find a record with id %d.", bucketId)));

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
     * Retrieve a list of {@link Bucket}'s associated to a given {@link User}.
     *
     * @param ownerId The id of the {@link User} that owns the {@link Bucket}.
     * @param publicOnly Returns only {@link Bucket} that are public.
     * @return A list of {@link Bucket} summaries.
     * @throws BadRequestException If a {@link User} with the given ownerId does not exist.
     * */
    public List<BucketSummaryResponse> findBuckets(final Long ownerId, final boolean publicOnly) {
        User bucketOwner = userDAO.findById(ownerId).orElseThrow(() ->
                new BadRequestException(String.format("Unable to find a record with id %d.", ownerId)));

        List<Bucket> buckets = publicOnly ? bucketDAO.findAllByOwnerAndIsPublicTrue(bucketOwner) :
                bucketDAO.findAllByOwner(bucketOwner);

        return buckets.stream()
                .map(BucketService::adaptBucketToBucketSummary)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve a specific {@link Bucket} by bucket id.
     *
     * If the bucket is private and the publicOnly parameter is true (i.e. the bucket with the given id is private),
     * then an {@link UnauthorizedException} is thrown.
     *
     * @param bucketId The id of the {@link Bucket} to retrieve.
     * @param publicOnly Specify whether {@link Bucket} is retrieved only if it is public.
     * @return A summary of a {@link Bucket}, if found.
     * @throws BadRequestException If a {@link Bucket} with the given id cannot be found.
     * @throws UnauthorizedException If the {@link Bucket} with the given id is private, but the publicOnly param is true.
     * */
    public BucketSummaryResponse findBucketById(final Long bucketId, final boolean publicOnly) {
        Bucket bucket = bucketDAO.findById(bucketId).orElseThrow(() ->
                new BadRequestException(String.format("Unable to find a record with id %d.", bucketId)));

        if(publicOnly && !bucket.getIsPublic()) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        return adaptBucketToBucketSummary(bucket);
    }

    /**
     * Retrieve a list of {@link Bucket}'s with a bucket name given in the query string.
     *
     * @param queryString The {@link Bucket} name query string.
     * @param pageable Specify how the results should be paged.
     * @return a list of {@link BucketSummaryResponse}'s for buckets with a bucket name that matches the query string.
     * */
    public List<BucketSummaryResponse> findBucketsByName(final String queryString, final Pageable pageable) {
        List<Bucket> queriedBuckets = bucketDAO.findAllByNameLike(queryString, pageable);

        return queriedBuckets.stream()
                .map(BucketService::adaptBucketToBucketSummary)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve a list of {@link User}s that are following a given {@link Bucket}.
     *
     * @param bucketId The id of the {@link Bucket} used in the query.
     * @param publicOnly Specify whether the {@link Bucket} is visible.
     * @return List of bucket summaries.
     * @throws UnauthorizedException If the {@link Bucket} is not public but publicOnly flag is true.
     * */
    public List<UserSummaryResponse> findFollowers(final Long bucketId, final boolean publicOnly) {
        Bucket bucket = bucketDAO.findById(bucketId).orElseThrow(() ->
                new BadRequestException(String.format("Unable to find a record with id %d.", bucketId)));

        if(publicOnly && !bucket.getIsPublic()) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        return userBucketRelationshipDAO.findAllByFollowing(bucket).stream()
                .map(UserBucketRelationship::getFollower)
                .map(userService::adaptUserToSummary)
                .collect(Collectors.toList());
    }

    /**
     * Partially update a {@link Bucket} with a given id.
     *
     * All non-null fields in the partialBucket are used to overwrite the same fields in the bucket currently persisted
     * in the database.
     *
     * Partial bucket owner field is ignored, because the bucket ownership cannot be transferred to a new {@link User}.
     *
     * The partial bucket provided must have the owner field specified. Although it is not used to update
     * the persisted bucket, it is used to verify the user that owns the partial bucket matches the user that owns the
     * bucket with the given id. Specifically, this is used by the {@link BucketController} to verify that the user id
     * provided as a path variable matches the owner of the bucket with the id provided as a path variable.
     *
     * @param partialBucket The partial {@link Bucket} used to update the bucket.
     * @param bucketId The id of the {@link Bucket} to patch.
     * @return A summary of the patched {@link Bucket}, once persisted in the database.
     * @throws BadRequestException If a {@link Bucket} with the given bucketId cannot be found.
     * @throws BadRequestException If the owner of the partial {@link Bucket} does not match the owner of the bucket with the
     * given id.
     * */
    public BucketSummaryResponse patchBucket(final Bucket partialBucket, final Long bucketId) {
        Bucket persistedBucket = bucketDAO.findById(bucketId).orElseThrow(() ->
                        new BadRequestException(String.format("Unable to find a record with id %d.", bucketId)));

        if(!Objects.equals(persistedBucket.getOwner().getId(), partialBucket.getOwner().getId())) {
            throw new BadRequestException(String.format("Owner of bucket with id %d does not match url path variable for user id %d.",
                    persistedBucket.getOwner().getId(), bucketId));
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
     * The bucket provided must have the owner field specified. It is used to verify the user that owns the
     * bucket provided matches the user that owns the bucket with the given id. Specifically, this is used by the
     * {@link BucketController} to verify that the user id provided as a path variable matches the
     * owner of the bucket with the id provided as a path variable.
     *
     * @param bucket The {@link Bucket} used to update the persisted bucket.
     * @param bucketId The id of the {@link Bucket} to patch.
     * @return A summary of the updated {@link Bucket}, once persisted in the database.
     * @throws BadRequestException If a {@link Bucket} with the given bucketId cannot be found.
     * @throws BadRequestException If the owner of the partial {@link Bucket} does not match the owner of the bucket with the
     * given id.
     * */
    public BucketSummaryResponse updateBucket(final Bucket bucket, final Long bucketId) {
        Bucket persistedBucket = bucketDAO.findById(bucketId).orElseThrow(() ->
                new BadRequestException(String.format("Unable to find a record with id %d.", bucketId)));

        if(!Objects.equals(persistedBucket.getOwner().getId(), bucket.getOwner().getId())) {
            throw new BadRequestException(String.format("Owner of bucket with id %d does not match url path variable for user id %d.",
                    persistedBucket.getOwner().getId(), bucket.getOwner().getId()));
        }

        bucket.setId(persistedBucket.getId());

        Bucket childBucket = saveBucket(persistedBucket);
        return adaptBucketToBucketSummary(childBucket);
    }

    /**
     * Delete a bucket. The {@link Bucket}'s id field must be non-null.
     *
     * The bucket provided must have the owner field specified. It is used to verify the user that owns the
     * bucket provided matches the user that owns the bucket with the given id. Specifically, this is used by the
     * {@link BucketController} to verify that the user id provided as a path variable matches the
     * owner of the bucket with the id provided as a path variable.
     *
     * @param bucket The {@link Bucket} to delete.
     * @throws BadRequestException If the owner of the {@link Bucket} does not match the owner of the bucket with the given id.
     * */
    public void deleteBucket(final Bucket bucket) {
        Bucket persistedBucket = bucketDAO.findById(bucket.getId()).orElseThrow(() ->
                new BadRequestException(String.format("Unable to find a record with id %d.", bucket.getId())));

        if(!Objects.equals(persistedBucket.getOwner().getId(), bucket.getOwner().getId())) {
            throw new BadRequestException(String.format("Owner of bucket with id %d does not match url path variable for user id %d.",
                    persistedBucket.getOwner().getId(), bucket.getOwner().getId()));
        }

        bucketDAO.delete(persistedBucket);
    }

    /**
     * Save a {@link Bucket}.
     *
     * If the bucket's isPublic field is null, a default value of true is used.
     *
     * Performs constraint validation.
     *
     * @param bucket The {@link Bucket} to save.
     * @return The {@link Bucket} once persisted in the database.
     * @throws BadRequestException If the user does not meet validation constraints.
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
     * Build a {@link BucketSummaryResponse} DTO of a {@link Bucket} entity.
     *
     * @param bucket The {@link Bucket} to be used to build a {@link BucketSummaryResponse}.
     * @return A summary of the bucket.
     * */
    public static BucketSummaryResponse adaptBucketToBucketSummary(final Bucket bucket) {
        Long ownerId = Objects.nonNull(bucket.getOwner()) ? bucket.getOwner().getId() : null;
        return new BucketSummaryResponse(bucket.getId(),
                ownerId,
                bucket.getName(),
                bucket.getIsPublic(),
                bucket.getDescription());
    }
}
