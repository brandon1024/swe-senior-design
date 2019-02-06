package com.unb.beforeigo.application.dao;

import com.unb.beforeigo.core.model.User;
import com.unb.beforeigo.core.model.UserRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * JPA Repository interface for defining specific DAO methods not already generated by JPA.
 *
 * @author Brandon Richardson
 * */
public interface UserRelationshipDAO extends JpaRepository<UserRelationship, Long> {

    /**
     * Retrieve a list of relationships from a given user, i.e. users they are following.
     *
     * @param follower The user that is following another user.
     * @return List of relationships that the user initiated.
     * */
    List<UserRelationship> findByFollower(final User follower);

    /**
     * Retrieve a list of relationships with a given user, i.e. users that follow them.
     *
     * @param user The user that is being followed by another user.
     * @return List of relationships that users have initiated with a given user.
     * */
    List<UserRelationship> findByFollowing(final User user);

    /**
     * Retrieve follower count for a given user.
     *
     * @param user The user who's follower count will be retrieved.
     * @return Follower count of given user.
     * */
    @Query("SELECT COUNT(following) FROM UserRelationship u WHERE u.following = :user")
    int findFollowerCount(@Param("user") User user);

    /**
     * Retrieve following count for a given user.
     *
     * @param user The user who's following count will be retrieved.
     * @return Following count of given user.
     * */
    @Query("SELECT COUNT(follower) FROM UserRelationship u WHERE u.follower = :user")
    int findFollowingCount(@Param("user") User user);

}
