package com.unb.beforeigo.core.svc;

import com.unb.beforeigo.api.dto.response.BucketSummaryResponse;
import com.unb.beforeigo.api.dto.response.UserBucketRelationshipSummaryResponse;
import com.unb.beforeigo.api.exception.client.BadRequestException;
import com.unb.beforeigo.api.exception.client.UnauthorizedException;
import com.unb.beforeigo.application.dao.BucketDAO;
import com.unb.beforeigo.application.dao.UserBucketRelationshipDAO;
import com.unb.beforeigo.application.dao.UserDAO;
import com.unb.beforeigo.core.model.Bucket;
import com.unb.beforeigo.core.model.User;
import com.unb.beforeigo.core.model.UserBucketRelationship;
import com.unb.beforeigo.core.model.validation.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserBucketRelationshipService {

    @Autowired UserDAO userDAO;

    @Autowired BucketDAO bucketDAO;

    @Autowired UserBucketRelationshipDAO userBucketRelationshipDAO;

    /**
     * Create a user-bucket relationship. Once persisted, the user will be following the bucket.
     *
     * @param initiatorId The id of the user that will be following the bucket.
     * @param bucketId The id of the bucket that the user wishes to follow.
     * @return A summary of the user-bucket relationship once persisted in the database.
     * @throws BadRequestException If a user or bucket cannot be found, or if the user tries to follow a bucket they own.
     * @throws UnauthorizedException If the user tries to follow a private bucket.
     * */
    public UserBucketRelationshipSummaryResponse createUserBucketRelationship(final Long initiatorId,
                                                                              final Long bucketId) {
        User follower = userDAO.findById(initiatorId)
                .orElseThrow(() -> new BadRequestException("Unable to find user with id " + initiatorId));

        Bucket following = bucketDAO.findById(bucketId)
                .orElseThrow(() -> new BadRequestException("Unable to find bucket with id " + bucketId));

        if(Objects.equals(following.getOwner().getId(), initiatorId)) {
            throw new BadRequestException("Users may not follow their own bucket.");
        }

        if(!following.getIsPublic()) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        UserBucketRelationship relationship = new UserBucketRelationship(follower, following);
        relationship = saveUserBucketRelationship(relationship);
        return adaptUserToSummary(relationship);
    }

    /**
     * Retrieve a list of buckets that are followed by a given user.
     *
     * @param userId The id of the user in question.
     * @return A list of bucket summaries for each bucket that the user is following.
     * @throws BadRequestException If a user cannot be found with the given id.
     * */
    public List<BucketSummaryResponse> findBucketsFollowedByUser(final Long userId) {
        User follower = userDAO.findById(userId)
                .orElseThrow(() -> new BadRequestException("Unable to find user with id " + userId));

        return userBucketRelationshipDAO.findAllByFollower(follower).stream()
                .map(UserBucketRelationship::getFollowing)
                .map(BucketService::adaptBucketToBucketSummary)
                .collect(Collectors.toList());
    }

    /**
     * Delete a user relationship.
     *
     * @param initiatorId The id of the user that is following the bucket.
     * @param bucketId The id of the bucket that is being followed.
     * */
    public void deleteUserBucketRelationship(final Long initiatorId, final Long bucketId) {
        UserBucketRelationship relationship = userBucketRelationshipDAO.findByFollowerAndFollowing(new User(initiatorId), new Bucket(bucketId))
                .orElseThrow(() -> new BadRequestException("Unable to find a relationship with the given information."));

        userBucketRelationshipDAO.delete(relationship);
    }

    /**
     * Save a UserBucketRelationship. Performs constraint validation.
     *
     * @param userBucketRelationship The relationship to save.
     * @return The relationship once persisted in the database.
     * @throws BadRequestException If the item does not meet validation constraints.
     * */
    private UserBucketRelationship saveUserBucketRelationship(final UserBucketRelationship userBucketRelationship) {
        EntityValidator.validateEntity(userBucketRelationship, () ->
                new BadRequestException("cannot save UserBucketRelationship that does not meet validation constraints"));

        return userBucketRelationshipDAO.save(userBucketRelationship);
    }

    /**
     * Build a UserBucketRelationshipSummaryResponse DTO of an UserBucketRelationship entity.
     *
     * @param userBucketRelationship The relationship to be used to build a UserBucketRelationshipSummaryResponse.
     * @return A summary of the relationship.
     * */
    public static UserBucketRelationshipSummaryResponse adaptUserToSummary(final UserBucketRelationship userBucketRelationship) {
        return new UserBucketRelationshipSummaryResponse(userBucketRelationship.getFollower().getId(),
                userBucketRelationship.getFollowing().getId());
    }
}
