package com.unb.beforeigo.api;

import com.unb.beforeigo.api.exception.client.BadRequestException;
import com.unb.beforeigo.application.dao.BucketDAO;
import com.unb.beforeigo.application.dao.UserDAO;
import com.unb.beforeigo.core.model.Bucket;
import com.unb.beforeigo.core.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/")
@Slf4j
public class BucketController {

    @Autowired private BucketDAO bucketDAO;

    @Autowired private UserDAO userDAO;

    /**
     * Create a new {@link Bucket}.
     *
     * @param ownerId The id of the user that owns the bucket
     * @param bucket A valid bucket with all necessary fields
     * @return a new bucket once persisted in the database
     * @throws BadRequestException if a the bucket provided has a non-null id field
     * @throws BadRequestException if a user with the given owner id cannot be found
     * */
    @RequestMapping(value = "/users/{id}/buckets", method = RequestMethod.POST, consumes = "application/json")
    public Bucket createBucket(@PathVariable(name = "id") final Long ownerId,
                               @RequestBody Bucket bucket) {
        if(bucket.getId() != null) {
            throw new BadRequestException("Cannot create a bucket with a specific bucket id " + bucket.getId());
        }

        User bucketOwner = userDAO.findById(ownerId)
                .orElseThrow(() ->
                        new BadRequestException("Unable to find a record with id " + ownerId));

        bucket.setOwner(bucketOwner);

        return bucketDAO.save(bucket);
    }

    /**
     * Retrieve a list of {@link Bucket}'s associated to a specific user.
     *
     * @param ownerId id of the user that owns the buckets.
     * @return a list of buckets associated to a given user
     * @throws BadRequestException if a user with the given owner id cannot be found
     * */
    @RequestMapping(value = "/users/{id}/buckets", method = RequestMethod.GET)
    public List<Bucket> findBuckets(@PathVariable(name = "id") final Long ownerId) {
        User bucketOwner = userDAO.findById(ownerId)
                .orElseThrow(() ->
                        new BadRequestException("Unable to find a record with id " + ownerId));

        Bucket queryBucket = new Bucket();
        queryBucket.setOwner(bucketOwner);

        return bucketDAO.findAll(Example.of(queryBucket));
    }

    /**
     * Retrieve a specific {@link Bucket}'s associated to a specific user.
     *
     * @param ownerId id of the user that owns the buckets.
     * @param bucketId id of the bucket
     * @return a bucket found using the user id and bucket id
     * @throws BadRequestException if a user with the given owner id cannot be found
     * */
    @RequestMapping(value = "/users/{ownerId}/buckets/{bucketId}", method = RequestMethod.GET)
    public Bucket findBucketById(@PathVariable(name = "ownerId") final Long ownerId,
                                       @PathVariable(name = "bucketId") final Long bucketId) {
        userDAO.findById(ownerId)
                .orElseThrow(() -> new BadRequestException("Unable to find a record with id " + ownerId));

        return bucketDAO.findById(bucketId)
                .orElseThrow(() -> new BadRequestException("Unable to find bucket with id " + bucketId));
    }

    /**
     * Update fields in a {@link Bucket} that is currently persisted in the database. Only non-null bucket fields are updated.
     *
     * @param ownerId id of the user that owns the bucket
     * @param bucketId id of the bucket that will be patched
     * @param bucket A bucket to patch
     * @return the patched bucket
     * @throws BadRequestException if the owner of the bucket with the given id does not match the owner in the url path variable.
     * */
    @RequestMapping(value = "/users/{ownerId}/buckets/{bucketId}", method = RequestMethod.PATCH, consumes = "application/json")
    public Bucket patchBucket(@PathVariable(value = "ownerId") final Long ownerId,
                              @PathVariable(value = "bucketId") final Long bucketId,
                              @RequestBody Bucket bucket) {
        Bucket persistedBucket = bucketDAO.findById(bucketId)
                .orElseThrow(() -> new BadRequestException("Unable to find bucket with id " + bucketId));

        if(!persistedBucket.getOwner().getId().equals(ownerId)) {
            throw new BadRequestException("Owner of bucket with id " + bucketId + " does not match url path variable for user id " + ownerId + ".");
        }

        if(bucket.getName() != null) {
            persistedBucket.setName(bucket.getName());
        }

        if(bucket.getIsPublic() != null) {
            persistedBucket.setIsPublic(bucket.getIsPublic());
        }

        if(bucket.getDescription() != null) {
            persistedBucket.setDescription(bucket.getDescription());
        }

        return bucketDAO.save(persistedBucket);
    }

    /**
     * Completely update a {@link Bucket} that is currently persisted in the database. All bucket fields are updated.
     *
     * @param ownerId id of the user that owns the bucket
     * @param bucketId id of the bucket that will be updated
     * @param bucket A bucket to update
     * @return the updated bucket
     * @throws BadRequestException if the owner of the bucket with the given id does not match the owner in the url path variable.
     * */
    @RequestMapping(value = "/users/{ownerId}/buckets/{bucketId}", method = RequestMethod.PUT, consumes = "application/json")
    public Bucket updateBucket(@PathVariable(value = "ownerId") final Long ownerId,
                               @PathVariable(value = "bucketId") final Long bucketId,
                               @RequestBody Bucket bucket) {
        Bucket persistedBucket = bucketDAO.findById(bucketId)
                .orElseThrow(() -> new BadRequestException("Unable to find bucket with id " + bucketId));

        if(!persistedBucket.getOwner().getId().equals(ownerId)) {
            throw new BadRequestException("Owner of bucket with id " + bucketId + " does not match url path variable for user id " + ownerId + ".");
        }

        bucket.setId(persistedBucket.getId());
        bucket.setOwner(persistedBucket.getOwner());

        return bucketDAO.save(bucket);
    }

    /**
     * Delete a bucket.
     *
     * @param ownerId id of the user that owns the bucket
     * @param bucketId id of the bucket that will be patched
     * @throws BadRequestException if the owner of the bucket with the given id does not match the owner in the url path variable.
     * */
    @RequestMapping(value = "/users/{ownerId}/buckets/{bucketId}", method = RequestMethod.DELETE)
    public void deleteBucket(@PathVariable(value = "ownerId") final Long ownerId,
                             @PathVariable(value = "bucketId") final Long bucketId) {
        Bucket persistedBucket = bucketDAO.findById(bucketId)
                .orElseThrow(() -> new BadRequestException("Unable to find bucket with id " + bucketId));

        if(!persistedBucket.getOwner().getId().equals(ownerId)) {
            throw new BadRequestException("Owner of bucket with id " + bucketId + " does not match url path variable for user id " + ownerId + ".");
        }

        bucketDAO.delete(persistedBucket);
    }
}
