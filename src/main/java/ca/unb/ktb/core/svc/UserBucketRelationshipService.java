package ca.unb.ktb.core.svc;

import ca.unb.ktb.api.dto.response.UserBucketRelationshipSummaryResponse;
import ca.unb.ktb.api.exception.client.BadRequestException;
import ca.unb.ktb.api.exception.client.UnauthorizedException;
import ca.unb.ktb.application.dao.UserBucketRelationshipDAO;
import ca.unb.ktb.core.model.Bucket;
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
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserBucketRelationshipService {

    @Autowired private UserBucketRelationshipDAO userBucketRelationshipDAO;

    @Autowired private UserService userService;

    @Autowired private BucketService bucketService;

    /**
     * Create a {@link User}-{@link Bucket} relationship. Once persisted, the user will be following the bucket.
     *
     * Only public buckets may be followed. The principal user cannot follow a bucket they own.
     *
     * @param bucketId The id of the {@link Bucket} that the {@link User} wishes to follow.
     * @return A {@link UserBucketRelationship}, once persisted in the database.
     * @throws BadRequestException If the principal user owns the bucket.
     * @see UserService#findUserById(Long)
     * @see BucketService#findBucketById(Long)
     * */
    public UserBucketRelationship createUserBucketRelationship(final Long bucketId) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User follower = userService.findUserById(currentUser.getId());
        Bucket following = bucketService.findBucketById(bucketId);

        if(Objects.equals(following.getOwner().getId(), currentUser.getId())) {
            throw new BadRequestException("Users may not follow their own bucket.");
        }

        LOG.info("Creating new user-bucket relationship between user {} and bucket {}", follower.getId(), following.getId());

        return saveUserBucketRelationship(new UserBucketRelationship(follower, following));
    }

    /**
     * Retrieve a list of {@link Bucket}s that are followed by a given {@link User}.
     *
     * @param userId The id of the {@link User}.
     * @return A list of {@link Bucket}s that are followed by the {@link User}.
     * @see UserService#findUserById(Long)
     * @see UserBucketRelationshipDAO#findAllByFollower(User)
     * */
    public List<Bucket> findBucketsFollowedByUser(final Long userId) {
        User follower = userService.findUserById(userId);

        return userBucketRelationshipDAO.findAllByFollower(follower).stream()
                .map(UserBucketRelationship::getFollowing)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve a list of {@link Bucket}s that are followed by a given {@link User}.
     *
     * @param bucket The {@link Bucket}.
     * @return A list of {@link User}s that are following the {@link Bucket}.
     * @see UserBucketRelationshipDAO#findAllByFollowing(Bucket)
     * */
    public List<User> findAllUsersFollowingBucket(final Bucket bucket) {
        return userBucketRelationshipDAO.findAllByFollowing(bucket).stream()
                .map(UserBucketRelationship::getFollower)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve a list of {@link Bucket}s that were recently followed by {@link User}s that are followed by a given user.
     *
     * @param userId The id of the {@link User}.
     * @param pageable Specify how the results should be paged.
     * @return A list of {@link UserBucketRelationship}s that represent the {@link User}-{@link Bucket} relationships
     * between users followed by the gievn user.
     * @see UserBucketRelationshipDAO#retrieveBucketsFollowedByFollowedUsers(Long, Pageable)
     * */
    public List<UserBucketRelationship> findBucketsRecentlyFollowedByFollowedUsers(final Long userId, final Pageable pageable) {
        return userBucketRelationshipDAO.retrieveBucketsFollowedByFollowedUsers(userId, pageable);
    }

    /**
     * Retrieve a list of {@link Bucket}s that were recently followed by a given user.
     *
     * @param userId The id of the {@link User}.
     * @param pageable Specify how the results should be paged.
     * @return A list of {@link UserBucketRelationship}s between a {@link User} and {@link Bucket}s they recently created.
     * @see UserBucketRelationshipDAO#retrieveBucketsFollowedByUser(Long, Pageable)
     * */
    public List<UserBucketRelationship> findBucketsRecentlyFollowedByUser(final Long userId, final Pageable pageable) {
        return userBucketRelationshipDAO.retrieveBucketsFollowedByUser(userId, pageable);
    }

    /**
     * Delete a {@link UserBucketRelationship} between the principal user and the given {@link Bucket} id.
     *
     * @param bucketId The id of the {@link Bucket} that is being followed by the principal user.
     * @throws BadRequestException If the principal user is not following the given {@link Bucket}.
     * @see BucketService#findBucketById(Long)
     * @see UserBucketRelationshipDAO#findByFollowerAndFollowing(User, Bucket)
     * */
    public void deleteUserBucketRelationship(final Long bucketId) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User follower = userService.findUserById(currentUser.getId());
        Bucket following = bucketService.findBucketById(bucketId);

        LOG.info("User {} unfollowing bucket {}", currentUser.getId(), following.getId());

        UserBucketRelationship relationship = userBucketRelationshipDAO.findByFollowerAndFollowing(follower, following).orElseThrow(() ->
                new BadRequestException("Unable to find a relationship with the given information."));

        userBucketRelationshipDAO.delete(relationship);
    }

    /**
     * Delete all relationships with a given {@link Bucket}. The principal user must be the owner of
     * the bucket, or an {@link UnauthorizedException} is thrown.
     *
     * @param bucket The {@link Bucket} for which relationships will be removed.
     * @throws UnauthorizedException If the principal user is not the owner of the {@link Bucket}.
     * @see UserBucketRelationshipDAO#findAllByFollowing(Bucket)
     * */
    public void deleteUserBucketRelationships(final Bucket bucket) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!Objects.equals(currentUser.getId(), bucket.getOwner().getId())) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        LOG.info("User {} deleting all user-bucket relationships with bucket {}", currentUser.getId(), bucket.getId());

        List<UserBucketRelationship> relationships = userBucketRelationshipDAO.findAllByFollowing(bucket);
        userBucketRelationshipDAO.deleteAll(relationships);
    }

    /**
     * Save a {@link UserBucketRelationship}. Performs constraint validation.
     *
     * @param userBucketRelationship The {@link UserBucketRelationship} to save.
     * @return The {@link UserBucketRelationship} once persisted in the database.
     * @throws BadRequestException If the item does not meet validation constraints.
     * */
    private UserBucketRelationship saveUserBucketRelationship(final UserBucketRelationship userBucketRelationship) {
        EntityValidator.validateEntity(userBucketRelationship, () ->
                new BadRequestException("cannot save UserBucketRelationship that does not meet validation constraints"));

        return userBucketRelationshipDAO.save(userBucketRelationship);
    }

    /**
     * Build a {@link UserBucketRelationshipSummaryResponse} DTO of an {@link UserBucketRelationship} entity.
     *
     * @param userBucketRelationship The {@link UserBucketRelationship} to be used to build a
     * {@link UserBucketRelationshipSummaryResponse}.
     * @return A summary of the relationship.
     * */
    public UserBucketRelationshipSummaryResponse adaptUserBucketRelationshipToSummary(final UserBucketRelationship userBucketRelationship) {
        return new UserBucketRelationshipSummaryResponse(userBucketRelationship.getFollower().getId(),
                userBucketRelationship.getFollowing().getId());
    }
}
