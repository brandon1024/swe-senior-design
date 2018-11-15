package com.unb.beforeigo.application.dao;

import com.unb.beforeigo.core.model.Bucket;
import com.unb.beforeigo.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

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
     * @param name bucket name
     * @return list of buckets with the given name
     * */
    List<Bucket> findAllByName(final String name);

    /**
     * Find all {@link Bucket} objects with a given owner.
     *
     * @param owner the creator and owner of the buckets to be found
     * @return a list of buckets created by a given user
     * */
    List<Bucket> findAllByOwner(final User owner);

    /**
     * Find a specific bucket with a given name and owner. If more than one bucket exists
     * with a given name and owner, the first bucket is returned.
     *
     * @param name the name of the bucket
     * @param owner the owner of the bucket
     * @return an {@link Optional} containing the first bucket record found, or
     * {@code Optional.empty()} if not found.
     * */
    Optional<Bucket> findFirstByNameAndOwner(final String name, final User owner);

    /**
     * Find all {@link Bucket} objects with a given owner that are public.
     *
     * @param owner the creator and owner of the buckets to be found
     * @return a list of public buckets created by a given user
     * */
    List<Bucket> findAllByOwnerAndIsPublicTrue(final User owner);
}
