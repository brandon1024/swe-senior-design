package com.unb.beforeigo.api;

import com.unb.beforeigo.api.dto.response.BucketSummaryResponse;
import com.unb.beforeigo.api.dto.response.UserBucketRelationshipSummaryResponse;
import com.unb.beforeigo.api.exception.client.UnauthorizedException;
import com.unb.beforeigo.core.svc.UserBucketRelationshipService;
import com.unb.beforeigo.infrastructure.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
public class UserBucketRelationshipController {

    @Autowired UserBucketRelationshipService userBucketRelationshipService;

    /**
     *  Create a user-bucket relationship. Once persisted, the user will be following the bucket.
     *
     * @param initiatorId The id of the user that is following the bucket.
     * @param bucketId The id of the bucket that the user is following.
     * @param currentUser The principal user.
     * @return A summary of the relationship. HTTP CREATED.
     * @throws UnauthorizedException If the principal user does not match the initiator.
     * */
    @RequestMapping(value = "/users/{id}/following_bucket", method = RequestMethod.POST)
    public ResponseEntity<UserBucketRelationshipSummaryResponse> createUserBucketRelationship(@PathVariable(name = "id") final Long initiatorId,
                                                                                              @RequestParam(name = "id") final Long bucketId,
                                                                                              @AuthenticationPrincipal final UserPrincipal currentUser) {
        if(!Objects.equals(currentUser.getId(), initiatorId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        UserBucketRelationshipSummaryResponse relationship = userBucketRelationshipService.createUserBucketRelationship(initiatorId, bucketId);
        return new ResponseEntity<>(relationship, HttpStatus.CREATED);
    }

    /**
     * Get a list of buckets that the given user is following.
     *
     * @param subjectId The id of the user.
     * @return A list of bucket summaries representing all the buckets the user is following. HTTP OK.
     * */
    @RequestMapping(value = "/users/{id}/following_bucket", method = RequestMethod.GET)
    public ResponseEntity<List<BucketSummaryResponse>> findUsersFollowingBucket(@PathVariable(name = "id") final Long subjectId) {
        List<BucketSummaryResponse> response = userBucketRelationshipService.findBucketsFollowedByUser(subjectId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Delete a user-bucket relationship.
     *
     * @param initiatorId The id of the user is following the bucket.
     * @param subjectId The id of the bucket that the user is following.
     * @param currentUser The principal user.
     * @return HTTP OK.
     * @throws UnauthorizedException If the principal user does not match the initiator.
     * */
    @RequestMapping(value = "/users/{id}/following_bucket", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUserBucketRelationship(@PathVariable(value = "id") final Long initiatorId,
                                                    @RequestParam(value = "id") final Long subjectId,
                                                    @AuthenticationPrincipal final UserPrincipal currentUser) {
        if(!Objects.equals(currentUser.getId(), initiatorId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        userBucketRelationshipService.deleteUserBucketRelationship(initiatorId, subjectId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
