package ca.unb.ktb.application.dao;

import ca.unb.ktb.core.model.Bucket;
import ca.unb.ktb.core.model.User;
import org.springframework.data.domain.Pageable;
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
public interface BucketDAO extends JpaRepository<Bucket, Long> {

    /**
     * Find all {@link Bucket} objects with a given name.
     *
     * @param name Bucket name.
     * @return A {@link List} of buckets with the given name.
     * */
    List<Bucket> findAllByName(final String name);

    /**
     * Find all {@link Bucket} objects with a given owner.
     *
     * @param owner The creator and owner of the buckets to be found.
     * @return A {@link List} of buckets created by a given user.
     * */
    List<Bucket> findAllByOwner(final User owner);

    /**
     * Find a specific bucket with a given name and owner. If more than one bucket exists with a given name and owner,
     * the first bucket is returned.
     *
     * @param name The name of the bucket.
     * @param owner The owner of the bucket.
     * @return An {@link Optional} containing the first bucket record found, or {@code Optional.empty()} if not found.
     * */
    Optional<Bucket> findFirstByNameAndOwner(final String name, final User owner);

    /**
     * Find all {@link Bucket} objects with a given owner that are public.
     *
     * @param owner The creator and owner of the buckets to be found.
     * @return A {@link List} of public buckets created by a given user.
     * */
    List<Bucket> findAllByOwnerAndIsPublicTrue(final User owner);

    /**
     * Count the number of buckets owned by the given user.
     *
     * @param owner The creator and owner of the buckets to be found.
     * @return the number of buckets owned by the user.
     * */
    Long countAllByOwner(final User owner);

    /**
     * Count the number of public buckets owned by the given user.
     *
     * @param owner The creator and owner of the buckets to be found.
     * @return the number of public buckets owned by the user.
     * */
    Long countAllByOwnerAndIsPublicIsTrue(final User owner);

    /**
     * Find all buckets that contain the partial bucket name. The search is case-insensitive.
     *
     * All public buckets that partially match the query string will be returned. If the bucket is private and partially
     * matches the query string, the bucket will only be returned if owner_id matches initiatorId.
     *
     * @param partialBucketName The partial bucket name to search for.
     * @param initiatorId The user that initiated the query.
     * @param pageable Specify how the results should be paged.
     * @return Buckets that contain with the given partial bucket name.
     * */
    @Query(value = "SELECT * FROM buckets " +
            "WHERE (buckets.name ILIKE %:partialBucketName%) " +
            "AND (buckets.is_public OR buckets.owner_id = :initiatorId)",
            countQuery = "SELECT COUNT(*) FROM buckets " +
                    "WHERE (buckets.name ILIKE %:partialBucketName%) " +
                    "AND (buckets.is_public OR buckets.owner_id = :initiatorId)",
            nativeQuery = true)
    List<Bucket> findAllByNameLike(@Param("partialBucketName") final String partialBucketName,
                                   @Param("initiatorId") final Long initiatorId,
                                   final Pageable pageable);

    /**
     * Retrieve a list of buckets which were recently created by users who are followed by a given user.
     *
     * Results are sorted by the bucket created_at field. As such, the pageable should be unsorted.
     *
     * @param followerId the id of the current user.
     * @param pageable pagination details.
     * @return list of buckets, sorted by created_at, recently created by the followers of a given user.
     * */
    @Query(value = "SELECT buckets.* FROM users_relationships " +
            "INNER JOIN buckets ON (users_relationships.following_id = buckets.owner_id AND buckets.is_public) " +
            "WHERE users_relationships.follower_id = :followerId " +
            "ORDER BY buckets.created_at DESC",
            countQuery = "SELECT COUNT(buckets.*) FROM users_relationships " +
                    "INNER JOIN buckets ON (users_relationships.following_id = buckets.owner_id AND buckets.is_public) " +
                    "WHERE users_relationships.follower_id = :followerId " +
                    "ORDER BY buckets.created_at DESC",
            nativeQuery = true)
    List<Bucket> retrieveBucketsRecentlyCreatedByFollowedUsers(@Param("followerId") final Long followerId,
                                                               final Pageable pageable);

    /**
     * Retrieve a list of buckets which were recently created by a given user.
     *
     * This method is similar to {@link BucketDAO#findAllByOwner(User)}, except that bucket's are sorted in reverse
     * chronological order by created_at date.
     *
     * Results are sorted by the bucket created_at field. As such, the pageable should be unsorted.
     *
     * @param userId the id of the current user.
     * @param pageable pagination details.
     * @return list of buckets, sorted by created_at, recently created by the given user.
     * */
    @Query(value = "SELECT buckets.* FROM buckets " +
            "WHERE buckets.owner_id = :userId " +
            "ORDER BY buckets.created_at DESC",
            countQuery = "SELECT COUNT(buckets.*) FROM buckets " +
                    "WHERE buckets.owner_id = :userId " +
                    "ORDER BY buckets.created_at DESC",
            nativeQuery = true)
    List<Bucket> retrieveBucketsCreatedByUser(@Param("userId") final Long userId, final Pageable pageable);
}
