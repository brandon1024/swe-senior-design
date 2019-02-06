package com.unb.beforeigo.application.dao;

import com.unb.beforeigo.core.model.Bucket;
import com.unb.beforeigo.core.model.User;
import com.unb.beforeigo.core.model.UserBucketRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository interface for defining specific DAO methods not already generated by JPA.
 *
 * @author Brandon Richardson
 * */
public interface UserBucketRelationshipDAO extends JpaRepository<UserBucketRelationship, Long> {

    /**
     * Retrieve a list of relationships associated to a given user. Used to retrieve a list of buckets that a user
     * is following.
     *
     * @param follower The user that is following the buckets.
     * @return A {@link List} of relationships associated to the given user.
     * */
    List<UserBucketRelationship> findAllByFollower(final User follower);

    /**
     * Retrieve a list of relationships associated to a given bucket. Used to retrieve a list of users that are following
     * a given bucket.
     *
     * @param following The bucket that is being followed.
     * @return A {@link List} of relationships associated to the given bucket.
     * */
    List<UserBucketRelationship> findAllByFollowing(final Bucket following);

    /**
     * Retrieve a specific user-bucket relationship by user and bucket.
     *
     * @param follower The user that is following the bucket.
     * @param following The bucket that is being followed.
     * @return An {@link Optional} UserBucketRelationship.
     * */
    Optional<UserBucketRelationship> findByFollowerAndFollowing(final User follower, final Bucket following);

    /**
     * Retrieve bucket count for a given user.
     *
     * @param owner The user who's bucket count will be retrieved.
     * @return Bucket count of given user.
     * */
    @Query("SELECT COUNT(owner) FROM Bucket b WHERE b.owner = :owner")
    int findBucketCount(@Param("owner") User owner);


}