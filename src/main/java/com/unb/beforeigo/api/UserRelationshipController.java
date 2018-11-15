package com.unb.beforeigo.api;

import com.unb.beforeigo.api.dto.response.UserRelationshipSummaryResponse;
import com.unb.beforeigo.api.dto.response.UserSummaryResponse;
import com.unb.beforeigo.api.exception.client.BadRequestException;
import com.unb.beforeigo.api.exception.client.UnauthorizedException;
import com.unb.beforeigo.core.model.User;
import com.unb.beforeigo.core.model.UserRelationship;
import com.unb.beforeigo.core.svc.UserService;
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
@RequestMapping("/users")
@Slf4j
public class UserRelationshipController {

    @Autowired private UserService userService;

    /**
     * Create a new {@link UserRelationship}.
     *
     * Usage example:
     * {@code POST /users/12/following?id=26 }
     * will result in user with id 12 following user with id 26
     *
     * @param initiatorId the id of the user that wishes to follow the subject
     * @param subjectId the id of the user that is to be followed by the initiator
     * @param currentUser The principal user.
     * @return a user relationship summary once persisted in the database
     * @throws UnauthorizedException if the id of the currently authenticated user does not match the path variable id
     * @throws BadRequestException if the subjectId and initiatorId are equal.
     * */
    @RequestMapping(value = "/{id}/following", method = RequestMethod.POST)
    public ResponseEntity<UserRelationshipSummaryResponse> createUserRelationship(@PathVariable(name = "id") final Long initiatorId,
                                                                                  @RequestParam(name = "id") final Long subjectId,
                                                                                  @AuthenticationPrincipal final UserPrincipal currentUser) {
        if(!Objects.equals(currentUser.getId(), initiatorId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        if(Objects.equals(subjectId, initiatorId)) {
            throw new BadRequestException("Initiator cannot follow themselves.");
        }

        UserRelationshipSummaryResponse response = userService.createUserRelationship(initiatorId, subjectId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieve a list of {@link User}'s that are following a given user.
     *
     * @param subjectId the id of the user to be used in the query
     * @return a list of users that are following the user with the given id
     * */
    @RequestMapping(value = "/{id}/followers", method = RequestMethod.GET)
    public ResponseEntity<List<UserSummaryResponse>> findFollowers(@PathVariable(name = "id") final Long subjectId) {
        List<UserSummaryResponse> response = userService.findFollowers(subjectId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieve a list of {@link User}'s that are followed by a given user.
     *
     * @param subjectId the id of the user to be used in the query
     * @return a list of users that are followed by the user with the given id
     * */
    @RequestMapping(value = "/{id}/following", method = RequestMethod.GET)
    public ResponseEntity<List<UserSummaryResponse>> findFollowing(@PathVariable(name = "id") final Long subjectId) {
        List<UserSummaryResponse> response = userService.findFollowing(subjectId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Delete a {@link UserRelationship} The user with the id provided as a path variable will no longer be following the
     * user with id specified as a request parameter.
     *
     * Example usage:
     * {@code DELETE /users/12/following?id=26 }
     * will result in user with id 12 unfollow user with id 26.
     *
     * @param subjectId the id of the user
     * @param initiatorId the id of the user
     * @param currentUser The principal user.
     * @return Http OK
     * @throws UnauthorizedException if the id of the currently authenticated user does not match the path variable id
     * */
    @RequestMapping(value = "/{id}/following", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUserRelationship(@PathVariable(value = "id") final Long initiatorId,
                                                    @RequestParam(value = "id") final Long subjectId,
                                                    @AuthenticationPrincipal final UserPrincipal currentUser) {
        if(!Objects.equals(currentUser.getId(), initiatorId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        userService.deleteUserRelationship(initiatorId, subjectId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
