package ca.unb.ktb.core.svc;

import ca.unb.ktb.api.dto.response.BucketSummaryResponse;
import ca.unb.ktb.api.exception.client.BadRequestException;
import ca.unb.ktb.api.exception.client.UnauthorizedException;
import ca.unb.ktb.application.dao.BucketDAO;
import ca.unb.ktb.core.model.Bucket;
import ca.unb.ktb.core.model.Item;
import ca.unb.ktb.core.model.User;
import ca.unb.ktb.core.model.UserBucketRelationship;
import ca.unb.ktb.core.model.validation.EntityValidator;
import ca.unb.ktb.infrastructure.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class BucketService {

    @Autowired private BucketDAO bucketDAO;

    @Autowired private ItemService itemService;

    @Autowired private UserBucketRelationshipService userBucketRelationshipService;

    /**
     * Create a new {@link Bucket}. The principal user will take ownership of the new bucket.
     *
     * @param bucket The {@link Bucket} to create.
     * @return The {@link Bucket} once persisted in the database.
     * */
    public Bucket createBucket(final Bucket bucket) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        bucket.setOwner(new User(currentUser.getId()));
        bucket.setId(null);

        LOG.info("User {} creating new bucket with name {}", currentUser.getId(), bucket.getName());
        LOG.debug("Bucket details: {}", bucket.toString());

        return saveBucket(bucket);
    }

    /**
     * Create a new {@link Bucket} that is duplicated from an existing bucket. The principal user will take ownership of
     * the new bucket.
     *
     * Duplicated buckets will inherit all fields from the parent bucket (except bucket owner).
     *
     * The bucket will only be duplicated if:
     * - the bucket is public, or
     * - the principal user owns the bucket.
     *
     * If the conditions above are not satisfied, an exception will be thrown.
     *
     * @param bucketId The id of the {@link Bucket} that will be duplicated.
     * @return The duplicated {@link Bucket} once persisted in the database.
     * @see BucketService#findBucketById(Long)
     * @see ItemService#duplicateBucketItems(Long, Long)
     * */
    public Bucket duplicateBucket(final Long bucketId) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Bucket originalBucket = findBucketById(bucketId);

        LOG.info("User {} duplicating bucket {}", currentUser.getId(), originalBucket.getId());

        originalBucket.setOwner(new User(currentUser.getId()));
        originalBucket.setId(null);

        Bucket newBucket = saveBucket(originalBucket);
        itemService.duplicateBucketItems(originalBucket.getId(), newBucket.getId());

        return newBucket;
    }

    /**
     * Retrieve a list of {@link Bucket}s owned by a given {@link User}.
     *
     * If the owner id does not match the id of the principal user, only public buckets are returned.
     *
     * @param ownerId The id of the {@link User} that owns the {@link Bucket}.
     * @return A list of {@link Bucket}s.
     * @see BucketDAO#findAllByOwner(User)
     * @see BucketDAO#findAllByOwnerAndIsPublicTrue(User)
     * */
    public List<Bucket> findBucketsByOwner(final Long ownerId) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(Objects.equals(ownerId, currentUser.getId())) {
            return bucketDAO.findAllByOwner(new User(currentUser.getId()));
        }

        return bucketDAO.findAllByOwnerAndIsPublicTrue(new User(currentUser.getId()));
    }

    /**
     * Retrieve a specific {@link Bucket} by id.
     *
     * If the bucket is private and the principal user is not the owner of the bucket, then an
     * {@link UnauthorizedException} is thrown.
     *
     * @param bucketId The id of the {@link Bucket}.
     * @return A {@link Bucket}, if found.
     * @throws BadRequestException If a {@link Bucket} with the given id cannot be found.
     * @throws UnauthorizedException If the {@link Bucket} with the given id is private, bucket the principal user is
     * not the owner of the bucket.
     * */
    public Bucket findBucketById(final Long bucketId) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Bucket bucket = bucketDAO.findById(bucketId).orElseThrow(() ->
                new BadRequestException(String.format("Unable to find a record with id %d.", bucketId)));

        /* if bucket is private and principal user does not own the bucket */
        if(!Objects.equals(currentUser.getId(), bucket.getOwner().getId()) && !bucket.getIsPublic()) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        return bucket;
    }

    /**
     * Retrieve a list of {@link Bucket}s with a bucket name that partially matches a given query string.
     *
     * Buckets with names that partially match the query string will only be returned if:
     * - the bucket is public, or
     * - the principal user owns the bucket.
     *
     * @param queryString The {@link Bucket} name query string.
     * @return List of {@link Bucket}s with a bucket name that partially matches a given query string.
     * @see BucketDAO#findAllByNameLike(String, Long)
     * */
    public List<Bucket> findBucketsByName(final String queryString) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return bucketDAO.findAllByNameLike(queryString, currentUser.getId());
    }

    /**
     * Retrieve a list of {@link User}s that are following a given {@link Bucket}.
     *
     * @param bucketId The id of the {@link Bucket} used in the query.
     * @return List of {@link User}s that are following a given {@link Bucket}.
     * @see UserBucketRelationshipService#findAllUsersFollowingBucket(Bucket)
     * */
    public List<User> findFollowers(final Long bucketId) {
        Bucket persistedBucket = findBucketById(bucketId);

        return userBucketRelationshipService.findAllUsersFollowingBucket(persistedBucket);
    }

    /**
     * Retrieve a list of {@link Bucket}s that were recently created by {@link User}s that are followed by the user
     * with the given user id.
     *
     * @param userId The {@link User}.
     * @param pageable Specify how the results should be paged.
     * @return A list of {@link Bucket}s that were recently created by {@link User}s that are followed by the user
     * with the given user id.
     * @see BucketDAO#retrieveBucketsRecentlyCreatedByFollowedUsers(Long, Pageable)
     * */
    public List<Bucket> findBucketsRecentlyCreatedByFollowedUsers(final Long userId, final Pageable pageable) {
        return bucketDAO.retrieveBucketsRecentlyCreatedByFollowedUsers(userId, pageable);
    }

    /**
     * Retrieve a list of {@link Bucket}s that were recently created by the {@link User} with the given id.
     *
     * @param userId The {@link User}.
     * @param pageable Specify how the results should be paged.
     * @return A list of {@link Bucket}s that were recently created by the {@link User} that the given id.
     * */
    public List<Bucket> findBucketsRecentlyCreatedByUser(final Long userId, final Pageable pageable) {
        return bucketDAO.retrieveBucketsCreatedByUser(userId, pageable);
    }

    /**
     * Retrieve a count of the number of {@link Bucket}s owned by a {@link User} with a given user id.
     *
     * If userId matches the id of the principal user, then a count of public and private buckets are returned.
     * Otherwise, only a count of public buckets are returned.
     *
     * @param userId The id of the {@link User}.
     * @return The number of {@link Bucket}s owned by the {@link User}.
     * @see BucketDAO#countAllByOwner(User)
     * @see BucketDAO#countAllByOwnerAndIsPublicIsTrue(User)
     * */
    public Long getBucketCount(final Long userId) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(Objects.equals(userId, currentUser.getId())) {
            return bucketDAO.countAllByOwner(new User(userId));
        }

        return bucketDAO.countAllByOwnerAndIsPublicIsTrue(new User(userId));
    }

    /**
     * Partially update a {@link Bucket} with a given id.
     *
     * All non-null fields in the partialBucket are used to overwrite the same fields in the bucket currently persisted
     * in the database.
     *
     * The bucket owner field is ignored, because the bucket ownership cannot be transferred to a new {@link User}.
     *
     * @param partialBucket The partial {@link Bucket} used to update the bucket.
     * @param bucketId The id of the {@link Bucket} to patch.
     * @return The patched {@link Bucket}, once persisted in the database.
     * @see BucketService#findBucketByIdOwnedByPrincipal(Long)
     * */
    public Bucket patchBucket(final Bucket partialBucket, final Long bucketId) {
        Bucket persistedBucket = findBucketByIdOwnedByPrincipal(bucketId);

        if(Objects.nonNull(partialBucket.getName())) {
            persistedBucket.setName(partialBucket.getName());
        }

        if(Objects.nonNull(partialBucket.getIsPublic())) {
            persistedBucket.setIsPublic(partialBucket.getIsPublic());
        }

        if(Objects.nonNull(partialBucket.getDescription())) {
            persistedBucket.setDescription(partialBucket.getDescription());
        }

        LOG.info("User {} patching bucket {}", persistedBucket.getOwner().getId(), persistedBucket.getId());

        return saveBucket(persistedBucket);
    }

    /**
     * Completely overwrite a {@link Bucket} that is currently persisted in the database. All bucket fields are updated.
     *
     * The bucket owner field is ignored, because the bucket ownership cannot be transferred to a new {@link User}.
     *
     * @param bucket The {@link Bucket} used to update the persisted bucket.
     * @param bucketId The id of the {@link Bucket} to patch.
     * @return The updated {@link Bucket}, once persisted in the database.
     * @see BucketService#findBucketByIdOwnedByPrincipal(Long)
     * */
    public Bucket updateBucket(final Bucket bucket, final Long bucketId) {
        Bucket persistedBucket = findBucketByIdOwnedByPrincipal(bucketId);

        bucket.setId(persistedBucket.getId());
        bucket.setOwner(persistedBucket.getOwner());

        LOG.info("User {} updating bucket {}", bucket.getOwner().getId(), bucket.getId());

        return saveBucket(persistedBucket);
    }

    /**
     * Delete a {@link Bucket}.
     *
     * All {@link UserBucketRelationship}s with the bucket are also removed, along with all the {@link Item}s contained
     * by the bucket.
     *
     * @param bucket The {@link Bucket} to delete.
     * @throws UnauthorizedException If the principal user does not own the bucket.
     * @see UserBucketRelationshipService#deleteUserBucketRelationships(Bucket)
     * @see ItemService#deleteItems(Bucket)
     * */
    public void deleteBucket(final Bucket bucket) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!Objects.equals(currentUser.getId(), bucket.getOwner().getId())) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        LOG.info("User {} deleting bucket {}", currentUser.getId(), bucket.getId());

        userBucketRelationshipService.deleteUserBucketRelationships(bucket);
        itemService.deleteItems(bucket);
        bucketDAO.delete(bucket);
    }

    /**
     * Find a {@link Bucket} by id and verify that it is owned by the principal user.
     *
     * @param bucketId The id of the {@link Bucket} to fetch.
     * @return The {@link Bucket} with the given id that is owned by the principal user.
     * @throws UnauthorizedException If the principal user does not own the bucket.
     * @see BucketService#findBucketById(Long)
     * */
    private Bucket findBucketByIdOwnedByPrincipal(final Long bucketId) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Bucket persistedBucket = findBucketById(bucketId);

        if(!Objects.equals(currentUser.getId(), persistedBucket.getOwner().getId())) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        return persistedBucket;
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
                new BadRequestException("Cannot save bucket that does not meet validation constraints"));
        return bucketDAO.save(bucket);
    }

    /**
     * Build a {@link BucketSummaryResponse} DTO of a {@link Bucket} entity.
     *
     * @param bucket The {@link Bucket} to be used to build a {@link BucketSummaryResponse}.
     * @return A summary of the {@link Bucket}.
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
