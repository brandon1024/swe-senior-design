package ca.unb.ktb.api;

import ca.unb.ktb.api.dto.response.BucketSummaryResponse;
import ca.unb.ktb.api.dto.response.UserSummaryResponse;
import ca.unb.ktb.api.exception.client.BadRequestException;
import ca.unb.ktb.api.exception.client.UnauthorizedException;
import ca.unb.ktb.core.model.Bucket;
import ca.unb.ktb.core.model.User;
import ca.unb.ktb.core.svc.BucketService;
import ca.unb.ktb.core.svc.UserService;
import ca.unb.ktb.infrastructure.security.UserPrincipal;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class BucketController {

    @Autowired private BucketService bucketService;

    @Autowired private UserService userService;

    /**
     * Create a new {@link Bucket}. The owner id must match principal user id.
     *
     * @param ownerId The id of the {@link User} that owns the {@link Bucket}.
     * @param bucket A valid {@link Bucket} with all required fields.
     * @return A new {@link Bucket} once persisted in the database.
     * @see BucketController#validateUserIsPrincipal(Long)
     * @see BucketService#createBucket(Bucket)
     * */
    @ApiOperation(
            value = "Create a new bucket.",
            response = BucketSummaryResponse.class
    )
    @RequestMapping(
            value = "/users/{userId}/buckets",
            method = RequestMethod.POST,
            consumes = "application/json"
    )
    public ResponseEntity<BucketSummaryResponse> createBucket(@PathVariable(name = "userId") final Long ownerId,
                                                              @RequestBody final Bucket bucket) {
        validateUserIsPrincipal(ownerId);

        Bucket newBucket = bucketService.createBucket(bucket);
        BucketSummaryResponse response = bucketService.adaptBucketToBucketSummary(newBucket);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Create a new {@link Bucket} from an existing bucket. The principal user will become the owner of the new bucket.
     *
     * The owner id must match the principal user id.
     *
     * @param ownerId The id of the {@link User} that will own the new {@link Bucket}.
     * @param bucketId The id of the {@link Bucket} that is to be duplicated.
     * @return a new {@link Bucket} once persisted in the database.
     * @see BucketController#validateUserIsPrincipal(Long)
     * @see BucketService#duplicateBucket(Long)
     * */
    @ApiOperation(
            value = "Create a new bucket from an existing bucket.",
            response = BucketSummaryResponse.class
    )
    @RequestMapping(
            value = "/users/{id}/buckets",
            method = RequestMethod.POST,
            consumes = "application/json",
            params = {"from"}
    )
    public ResponseEntity<BucketSummaryResponse> duplicateBucket(@PathVariable(name = "id") final Long ownerId,
                                                                 @RequestParam(name = "from") final Long bucketId) {
        validateUserIsPrincipal(ownerId);

        Bucket duplicatedBucket = bucketService.duplicateBucket(bucketId);
        BucketSummaryResponse response = bucketService.adaptBucketToBucketSummary(duplicatedBucket);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieve a list of {@link Bucket}s owned by a given {@link User}.
     *
     * If the owner id does not match the id of the principal user, only public buckets are returned.
     *
     * @param ownerId Id of the {@link User} that owns the {@link Bucket}s.
     * @return A list of {@link Bucket}s associated to a given {@link User}.
     * @see BucketService#findBucketsByOwner(Long)
     * */
    @ApiOperation(
            value = "Retrieve a list of buckets associated to a specific user.",
            response = BucketSummaryResponse.class,
            responseContainer = "List"
    )
    @RequestMapping(
            value = "/users/{id}/buckets",
            method = RequestMethod.GET
    )
    public ResponseEntity<List<BucketSummaryResponse>> findBuckets(@PathVariable(name = "id") final Long ownerId) {
        List<BucketSummaryResponse> response = bucketService.findBucketsByOwner(ownerId).parallelStream()
                .map(bucketService::adaptBucketToBucketSummary)
                .collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieve a specific {@link Bucket} by id.
     *
     * The bucket will only be returned if:
     * - the bucket is public, or
     * - the principal user owns the bucket.
     *
     * @param ownerId Id of the {@link User} that owns the {@link Bucket}.
     * @param bucketId Id of the {@link Bucket}.
     * @return The bucket with the given id.
     * @see BucketController#validateBucketURI(Long, Long)
     * @see BucketService#findBucketById(Long)
     * */
    @ApiOperation(
            value = "Retrieve a specific bucket associated to a specific user.",
            response = BucketSummaryResponse.class
    )
    @RequestMapping(
            value = "/users/{ownerId}/buckets/{bucketId}",
            method = RequestMethod.GET
    )
    public ResponseEntity<BucketSummaryResponse> findBucketById(@PathVariable(name = "ownerId") final Long ownerId,
                                                                @PathVariable(name = "bucketId") final Long bucketId) {
        Bucket bucket = validateBucketURI(ownerId, bucketId);
        BucketSummaryResponse response = bucketService.adaptBucketToBucketSummary(bucket);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieve a list of {@link User}s that are following the given {@link Bucket}.
     *
     * @param ownerId The owner of the {@link Bucket}.
     * @param bucketId The id of the {@link Bucket}.
     * @return List of {@link User}s that are following a given {@link Bucket}.
     * @see BucketController#validateBucketURI(Long, Long)
     * @see BucketService#findFollowers(Long)
     * */
    @ApiOperation(
            value = "Retrieve a list of users that are following the given bucket.",
            response = UserSummaryResponse.class,
            responseContainer = "List"
    )
    @RequestMapping(
            value = "/users/{ownerId}/bucket/{bucketId}/followers",
            method = RequestMethod.GET
    )
    public ResponseEntity<List<UserSummaryResponse>> findFollowing(@PathVariable(name = "ownerId") final Long ownerId,
                                                                   @PathVariable(name = "bucketId") final Long bucketId) {
        validateBucketURI(ownerId, bucketId);
        List<UserSummaryResponse> response = bucketService.findFollowers(bucketId).parallelStream()
                .map(userService::adaptUserToSummary)
                .collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Update a subset of fields in an existing {@link Bucket}. Only non-null fields are updated.
     *
     * @param ownerId Id of the {@link User} that owns the {@link Bucket}.
     * @param bucketId Id of the {@link Bucket} that will be patched.
     * @param bucket A partial {@link Bucket}.
     * @return The updated {@link Bucket}.
     * @see BucketController#validateUserIsPrincipal(Long)
     * @see BucketController#validateBucketURI(Long, Long)
     * @see BucketService#patchBucket(Bucket, Long)
     * */
    @ApiOperation(
            value = "Update a subset of fields in an existing Bucket. Only non-null fields are updated.",
            response = BucketSummaryResponse.class
    )
    @RequestMapping(
            value = "/users/{ownerId}/buckets/{bucketId}",
            method = RequestMethod.PATCH,
            consumes = "application/json"
    )
    public ResponseEntity<BucketSummaryResponse> patchBucket(@PathVariable(value = "ownerId") final Long ownerId,
                                                             @PathVariable(value = "bucketId") final Long bucketId,
                                                             @RequestBody final Bucket bucket) {
        validateUserIsPrincipal(ownerId);
        validateBucketURI(ownerId, bucketId);
        Bucket patchedBucket = bucketService.patchBucket(bucket, bucketId);
        BucketSummaryResponse response = bucketService.adaptBucketToBucketSummary(patchedBucket);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Completely overwrite a {@link Bucket} that is currently persisted in the database. All bucket fields are updated.
     *
     * @param ownerId Id of the {@link User} that owns the {@link Bucket}.
     * @param bucketId Id of the {@link Bucket} that will be updated.
     * @param bucket A {@link Bucket} to update.
     * @return The {@link Bucket} bucket.
     * @see BucketController#validateUserIsPrincipal(Long)
     * @see BucketController#validateBucketURI(Long, Long)
     * @see BucketService#updateBucket(Bucket, Long)
     * */
    @ApiOperation(
            value = "Completely update a bucket that currently persisted in the database.",
            response = BucketSummaryResponse.class
    )
    @RequestMapping(
            value = "/users/{ownerId}/buckets/{bucketId}",
            method = RequestMethod.PUT,
            consumes = "application/json"
    )
    public ResponseEntity<BucketSummaryResponse> updateBucket(@PathVariable(value = "ownerId") final Long ownerId,
                                                              @PathVariable(value = "bucketId") final Long bucketId,
                                                              @RequestBody final Bucket bucket) {
        validateUserIsPrincipal(ownerId);
        validateBucketURI(ownerId, bucketId);
        Bucket updatedBucket = bucketService.updateBucket(bucket, bucketId);
        BucketSummaryResponse response = bucketService.adaptBucketToBucketSummary(updatedBucket);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Delete a {@link Bucket}.
     *
     * @param ownerId Id of the {@link User} that owns the {@link Bucket}.
     * @param bucketId Id of the {@link Bucket} that will be patched.
     * @return Empty response.
     * @see BucketController#validateUserIsPrincipal(Long)
     * @see BucketController#validateBucketURI(Long, Long)
     * @see BucketService#deleteBucket(Bucket)
     * */
    @ApiOperation(value = "Delete a bucket.")
    @RequestMapping(
            value = "/users/{ownerId}/buckets/{bucketId}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<?> deleteBucket(@PathVariable(value = "ownerId") final Long ownerId,
                                          @PathVariable(value = "bucketId") final Long bucketId) {
        validateUserIsPrincipal(ownerId);
        Bucket bucket = validateBucketURI(ownerId, bucketId);
        bucketService.deleteBucket(bucket);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Ensure a {@link Bucket} with the given id exists, and is owned by the {@link User} with the given id.
     *
     * @param ownerId The owner of the {@link Bucket}.
     * @param bucketId The {@link Bucket}.
     * @return The {@link Bucket}, if the bucket exists and is owned by the {@link User} with the given id.
     * @throws BadRequestException If the {@link User} with the given id does not own the {@link Bucket} with the given id.
     * @see BucketService#findBucketById(Long)
     * */
    private Bucket validateBucketURI(final Long ownerId, final Long bucketId) {
        Bucket bucket = bucketService.findBucketById(ownerId);
        if(!Objects.equals(ownerId, bucket.getOwner().getId())) {
            throw new BadRequestException(String.format("Unable to find bucket with id %d and owner %d.", bucketId, ownerId));
        }

        return bucket;
    }

    /**
     * Ensure the given {@link User} id matches the principal user id.
     *
     * @param userId The {@link User} id.
     * @throws UnauthorizedException If the given {@link User} id does not match the principal user id.
     * */
    private void validateUserIsPrincipal(final Long userId) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!Objects.equals(userId, currentUser.getId())) {
            throw new UnauthorizedException("Insufficient permissions.");
        }
    }
}
