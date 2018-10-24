package com.unb.beforeigo.api;

import com.unb.beforeigo.api.dto.response.BucketSummaryResponse;
import com.unb.beforeigo.api.exception.client.UnauthorizedException;
import com.unb.beforeigo.core.model.Bucket;
import com.unb.beforeigo.core.model.User;
import com.unb.beforeigo.core.svc.BucketService;
import com.unb.beforeigo.infrastructure.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/")
@Slf4j
public class BucketController {

    @Autowired private BucketService bucketService;

    /**
     * Create a new {@link Bucket}.
     *
     * @param ownerId The id of the user that owns the bucket
     * @param bucket A valid bucket with all necessary fields
     * @return a new bucket once persisted in the database
     * @throws UnauthorizedException if the id of the currently authenticated user does not match the path variable id
     * @see BucketService#createBucket(Long, Bucket)
     * */
    @RequestMapping(value = "/users/{id}/buckets", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<BucketSummaryResponse> createBucket(@PathVariable(name = "id") final Long ownerId,
                                                              @RequestBody final Bucket bucket,
                                                              @AuthenticationPrincipal final UserPrincipal currentUser) {
        if(!Objects.equals(currentUser.getId(), ownerId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        BucketSummaryResponse response = bucketService.createBucket(ownerId, bucket);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Create a new {@link Bucket} from an existing bucket.
     *
     * @param ownerId The id of the user that owns the bucket
     * @param parentBucketId The id of the bucket that is to be duplicated
     * @return a new bucket once persisted in the database
     * @throws UnauthorizedException if the id of the currently authenticated user does not match the path variable id
     * @see BucketService#duplicateBucket(Long, Long)
     * */
    @RequestMapping(value = "/users/{id}/buckets", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<BucketSummaryResponse> duplicateBucket(@PathVariable(name = "id") final Long ownerId,
                                                                 @RequestParam(name = "from") final Long parentBucketId,
                                                                 @AuthenticationPrincipal final UserPrincipal currentUser) {
        if(!Objects.equals(currentUser.getId(), ownerId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        BucketSummaryResponse response = bucketService.duplicateBucket(ownerId, parentBucketId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieve a list of {@link Bucket}'s associated to a specific user.
     *
     * If the owner id does not match the id of the principal user, only public buckets are returned.
     *
     * @param ownerId id of the user that owns the buckets.
     * @return a list of all buckets associated to a given user. If the user and principal have matching ids, public and
     * private buckets are returned, otherwise only returns public buckets
     * @see BucketService#findBuckets(Long, boolean)
     * */
    @RequestMapping(value = "/users/{id}/buckets", method = RequestMethod.GET)
    public ResponseEntity<List<BucketSummaryResponse>> findBuckets(@PathVariable(name = "id") final Long ownerId,
                                                                   @AuthenticationPrincipal final UserPrincipal currentUser) {
        List<BucketSummaryResponse> response = bucketService.findBuckets(ownerId, !Objects.equals(currentUser.getId(), ownerId));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieve a specific {@link Bucket} associated to a specific user.
     *
     * If the owner id does not match the id of the principal user, the bucket is only returned if public.
     *
     * @param ownerId id of the user that owns the buckets.
     * @param bucketId id of the bucket
     * @return bucket associated to a given user. If the user and principal have matching ids, public or private bucket
     * may be returned, otherwise only returns a public bucket
     * @see BucketService#findBucketById(Long, boolean)
     * */
    @RequestMapping(value = "/users/{ownerId}/buckets/{bucketId}", method = RequestMethod.GET)
    public ResponseEntity<BucketSummaryResponse> findBucketById(@PathVariable(name = "ownerId") final Long ownerId,
                                                                @PathVariable(name = "bucketId") final Long bucketId,
                                                                @AuthenticationPrincipal final UserPrincipal currentUser) {
        BucketSummaryResponse response = bucketService.findBucketById(ownerId, !Objects.equals(currentUser.getId(), ownerId));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Update fields in a {@link Bucket} that is currently persisted in the database. Only non-null bucket fields are
     * updated.
     *
     * The id of the authenticated principal must match the path variable ownerId. The id of the owner of the bucket
     * must also match the path variable ownerId.
     *
     * @param ownerId id of the user that owns the bucket
     * @param bucketId id of the bucket that will be patched
     * @param bucket A partial bucket used to patch a persisted bucket
     * @return the patched bucket
     * @throws UnauthorizedException if the id of the currently authenticated user does not match the path variable id
     * @see BucketService#patchBucket(Bucket, Long)
     * */
    @RequestMapping(value = "/users/{ownerId}/buckets/{bucketId}", method = RequestMethod.PATCH,
            consumes = "application/json")
    public ResponseEntity<BucketSummaryResponse> patchBucket(@PathVariable(value = "ownerId") final Long ownerId,
                                                             @PathVariable(value = "bucketId") final Long bucketId,
                                                             @RequestBody final Bucket bucket,
                                                             @AuthenticationPrincipal final UserPrincipal currentUser) {
        if(!Objects.equals(currentUser.getId(), ownerId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        bucket.setOwner(new User(ownerId));

        BucketSummaryResponse response = bucketService.patchBucket(bucket, bucketId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Completely update a {@link Bucket} that is currently persisted in the database. All bucket fields are updated.
     *
     * The id of the authenticated principal must match the path variable ownerId. The id of the owner of the bucket
     * must also match the path variable ownerId.
     *
     * @param ownerId id of the user that owns the bucket
     * @param bucketId id of the bucket that will be updated
     * @param bucket A bucket to update
     * @return the updated bucket
     * @throws UnauthorizedException if the id of the currently authenticated user does not match the path variable id
     * @see BucketService#updateBucket(Bucket, Long)
     * */
    @RequestMapping(value = "/users/{ownerId}/buckets/{bucketId}", method = RequestMethod.PUT,
            consumes = "application/json")
    public ResponseEntity<BucketSummaryResponse> updateBucket(@PathVariable(value = "ownerId") final Long ownerId,
                                                              @PathVariable(value = "bucketId") final Long bucketId,
                                                              @RequestBody final Bucket bucket,
                                                              @AuthenticationPrincipal final UserPrincipal currentUser) {
        if(!Objects.equals(currentUser.getId(), ownerId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        bucket.setOwner(new User(ownerId));

        BucketSummaryResponse response = bucketService.updateBucket(bucket, bucketId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Delete a bucket.
     *
     * The id of the authenticated principal must match the path variable ownerId. The id of the owner of the bucket
     * must also match the path variable ownerId.
     *
     * @param ownerId id of the user that owns the bucket
     * @param bucketId id of the bucket that will be patched
     * @throws UnauthorizedException if the id of the currently authenticated user does not match the path variable id
     * @see BucketService#deleteBucket(Bucket)
     * */
    @RequestMapping(value = "/users/{ownerId}/buckets/{bucketId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteBucket(@PathVariable(value = "ownerId") final Long ownerId,
                                          @PathVariable(value = "bucketId") final Long bucketId,
                                          @AuthenticationPrincipal final UserPrincipal currentUser) {
        if(!Objects.equals(currentUser.getId(), ownerId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        Bucket bucket = new Bucket();
        bucket.setId(bucketId);
        bucket.setOwner(new User(ownerId));

        bucketService.deleteBucket(bucket);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
