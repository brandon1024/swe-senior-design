package com.unb.beforeigo.api;

import com.unb.beforeigo.api.exception.client.BadRequestException;
import com.unb.beforeigo.application.dao.UserDAO;
import com.unb.beforeigo.application.dao.UserRelationshipDAO;
import com.unb.beforeigo.core.model.User;
import com.unb.beforeigo.core.model.UserRelationship;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserRelationshipController {

    @Autowired private UserRelationshipDAO userRelationshipDAO;

    @Autowired private UserDAO userDAO;

    /**
     * Create a new {@link UserRelationship}.
     *
     * Usage example:
     * {@code http://api.before-i-go.com/users/12/followers?id=26 }
     * will result in user with id 26 following user with id 12
     *
     * @param subjectId the id of the user that is to be followed by the initiator
     * @param initiatorId the id of the user that wishes to follow the subject
     * @return a new user relationship once persisted in the database
     * @throws BadRequestException if the subjectId or initiatorId do not map to valid users.
     * */
    @RequestMapping(value = "/{id}/followers", method = RequestMethod.POST)
    public UserRelationship createUserRelationship(@PathVariable(name = "id") final Long subjectId,
                                                   @RequestParam(name = "id") final Long initiatorId) {
        UserRelationship relationship = new UserRelationship();
        User follower = userDAO.findById(initiatorId)
                .orElseThrow(() -> new BadRequestException("Unable to find user with id " + initiatorId));
        User following = userDAO.findById(subjectId)
                .orElseThrow(() -> new BadRequestException("Unable to find user with id " + subjectId));

        relationship.setFollower(follower);
        relationship.setFollowing(following);

        return userRelationshipDAO.save(relationship);
    }

    /**
     * Retrieve a list of {@link User}'s that are following a given user.
     *
     * @param subjectId the id of the user to be used in the query
     * @return a list of users that are following the user with the given id
     * */
    @RequestMapping(value = "/{id}/followers", method = RequestMethod.GET)
    public List<User> findFollowers(@PathVariable(name = "id") final Long subjectId) {
        User following = new User();
        following.setId(subjectId);

        UserRelationship relationship = new UserRelationship();
        relationship.setFollowing(following);

        List<UserRelationship> relationships = userRelationshipDAO.findAll(Example.of(relationship));

        return relationships.stream().map(UserRelationship::getFollower).collect(Collectors.toList());
    }

    /**
     * Retrieve a list of {@link User}'s that are followed by a given user.
     *
     * @param subjectId the id of the user to be used in the query
     * @return a list of users that are followed by the user with the given id
     * */
    @RequestMapping(value = "/{id}/following", method = RequestMethod.GET)
    public List<User> findFollowing(@PathVariable(name = "id") final Long subjectId) {
        User follower = new User();
        follower.setId(subjectId);

        UserRelationship relationship = new UserRelationship();
        relationship.setFollower(follower);

        List<UserRelationship> relationships = userRelationshipDAO.findAll(Example.of(relationship));

        return relationships.stream().map(UserRelationship::getFollowing).collect(Collectors.toList());
    }

    /**
     * Delete a user relationship. The user with the id provided as a path variable will no longer have the
     * follower with the id specified as a request parameter.
     *
     * Example usage:
     * {@code DELETE /users/12/followers?id=26 }
     * will result in user with id 26 unfollow user with id 12.
     *
     * @param subjectId the id of the user
     * @param initiatorId the id of the user
     * @throws BadRequestException if the relationship does not exist
     * */
    @RequestMapping(value = "/{id}/followers", method = RequestMethod.DELETE)
    public void deleteUserRelationship(@PathVariable(value = "id") final Long subjectId,
                                       @RequestParam(value = "id") final Long initiatorId) {
        User follower = new User();
        follower.setId(initiatorId);
        User following = new User();
        following.setId(subjectId);

        UserRelationship relationship = new UserRelationship();
        relationship.setFollower(follower);
        relationship.setFollowing(following);

        relationship = userRelationshipDAO.findOne(Example.of(relationship)).
                orElseThrow(() -> new BadRequestException("Unable to find relationship"));

        userRelationshipDAO.delete(relationship);
    }
}
