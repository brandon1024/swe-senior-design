package ca.unb.ktb.api;

import ca.unb.ktb.api.dto.response.BucketSummaryResponse;
import ca.unb.ktb.api.dto.response.UserBucketRelationshipSummaryResponse;
import ca.unb.ktb.api.exception.client.UnauthorizedException;
import ca.unb.ktb.core.model.Bucket;
import ca.unb.ktb.core.model.UserBucketRelationship;
import ca.unb.ktb.core.svc.BucketService;
import ca.unb.ktb.core.svc.UserBucketRelationshipService;
import ca.unb.ktb.infrastructure.security.UserPrincipal;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class UserBucketRelationshipController {

    @Autowired private UserBucketRelationshipService userBucketRelationshipService;

    @Autowired private BucketService bucketService;

    /**
     *  Create a {@link ca.unb.ktb.core.model.User}-{@link ca.unb.ktb.core.model.Bucket} relationship. Once persisted,
     *  the user will be following the bucket.
     *
     * @param initiatorId The id of the {@link ca.unb.ktb.core.model.User} that is following the
     * {@link ca.unb.ktb.core.model.Bucket}.
     * @param bucketId The id of the {@link ca.unb.ktb.core.model.Bucket} that the {@link ca.unb.ktb.core.model.User}
     * is following.
     * @param auth The authentication token.
     * @return A summary of the relationship.
     * @throws UnauthorizedException If the principal user does not match the initiator.
     * */
    @ApiOperation(
            value = "",
            response = UserBucketRelationshipSummaryResponse.class
    )
    @RequestMapping(
            value = "/users/{id}/following_bucket",
            method = RequestMethod.POST
    )
    public ResponseEntity<UserBucketRelationshipSummaryResponse> createUserBucketRelationship(
            @PathVariable(name = "id") final Long initiatorId,
            @RequestParam(name = "id") final Long bucketId,
            @AuthenticationPrincipal final Authentication auth) {
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
        if(!Objects.equals(currentUser.getId(), initiatorId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        UserBucketRelationship relationship = userBucketRelationshipService.createUserBucketRelationship(bucketId);
        UserBucketRelationshipSummaryResponse response = userBucketRelationshipService.adaptUserBucketRelationshipToSummary(relationship);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get a list of {@link ca.unb.ktb.core.model.Bucket}s that the given {@link ca.unb.ktb.core.model.User} is following.
     *
     * @param subjectId The id of the {@link ca.unb.ktb.core.model.User}.
     * @return A list of {@link BucketSummaryResponse} representing all the {@link ca.unb.ktb.core.model.Bucket} the
     * {@link ca.unb.ktb.core.model.User} is following.
     * */
    @ApiOperation(
            value = "",
            response = BucketSummaryResponse.class,
            responseContainer = "List"
    )
    @RequestMapping(
            value = "/users/{id}/following_bucket",
            method = RequestMethod.GET
    )
    public ResponseEntity<List<BucketSummaryResponse>> findUsersFollowingBucket(
            @PathVariable(name = "id") final Long subjectId) {
        List<Bucket> buckets = userBucketRelationshipService.findBucketsFollowedByUser(subjectId);
        List<BucketSummaryResponse> response = buckets.parallelStream()
                .map(bucketService::adaptBucketToBucketSummary)
                .collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Delete a {@link ca.unb.ktb.core.model.User}-{@link ca.unb.ktb.core.model.Bucket} relationship.
     *
     * @param initiatorId The id of the {@link ca.unb.ktb.core.model.User} is following the
     * {@link ca.unb.ktb.core.model.Bucket}.
     * @param subjectId The id of the {@link ca.unb.ktb.core.model.Bucket} that the{@link ca.unb.ktb.core.model.User}
     * is following.
     * @param auth Authentication principal.
     * @return Empty Response.
     * @throws UnauthorizedException If the principal user does not match the initiator.
     * */
    @ApiOperation(value = "")
    @RequestMapping(
            value = "/users/{id}/following_bucket",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<?> deleteUserBucketRelationship(
            @PathVariable(value = "id") final Long initiatorId,
            @RequestParam(value = "id") final Long subjectId,
            @AuthenticationPrincipal final Authentication auth) {
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
        if(!Objects.equals(currentUser.getId(), initiatorId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        userBucketRelationshipService.deleteUserBucketRelationship(subjectId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
