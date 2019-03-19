package ca.unb.ktb.api;

import ca.unb.ktb.api.dto.response.UserRelationshipSummaryResponse;
import ca.unb.ktb.api.dto.response.UserSummaryResponse;
import ca.unb.ktb.api.exception.client.BadRequestException;
import ca.unb.ktb.api.exception.client.UnauthorizedException;
import ca.unb.ktb.core.model.User;
import ca.unb.ktb.core.model.UserRelationship;
import ca.unb.ktb.core.svc.UserService;
import ca.unb.ktb.infrastructure.security.UserPrincipal;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
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
     * @param initiatorId The id of the user that wishes to follow the subject.
     * @param subjectId The id of the user that is to be followed by the initiator.
     * @param auth The authentication token.
     * @return A user relationship summary once persisted in the database. HTTP CREATED.
     * @throws UnauthorizedException If the id of the currently authenticated user does not match the path variable id.
     * @throws BadRequestException If the subjectId and initiatorId are equal.
     * */
    @ApiOperation(value = "Create a new user relationship.", response = UserRelationshipSummaryResponse.class)
    @RequestMapping(value = "/{id}/following", method = RequestMethod.POST)
    public ResponseEntity<UserRelationshipSummaryResponse> createUserRelationship(
            @PathVariable(name = "id") final Long initiatorId,
            @RequestParam(name = "id") final Long subjectId,
            @AuthenticationPrincipal final Authentication auth) {
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
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
     * @param subjectId The id of the user to be used in the query.
     * @return A list of users that are following the user with the given id. HTTP OK.
     * */
    @ApiOperation(value = "Retrieve a list of users that are following a given user.",
            response = UserSummaryResponse.class,
            responseContainer = "List")
    @RequestMapping(value = "/{id}/followers", method = RequestMethod.GET)
    public ResponseEntity<List<UserSummaryResponse>> findFollowers(@PathVariable(name = "id") final Long subjectId) {
        List<UserSummaryResponse> response = userService.findFollowers(subjectId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieve a list of {@link User}'s that are followed by a given user.
     *
     * @param subjectId The id of the user to be used in the query.
     * @return A list of users that are followed by the user with the given id. HTTP OK.
     * */
    @ApiOperation(value = "Retrieve a list of users that are followed by a given user.",
            response = UserSummaryResponse.class,
            responseContainer = "List")
    @RequestMapping(value = "/{id}/following", method = RequestMethod.GET)
    public ResponseEntity<List<UserSummaryResponse>> findFollowing(@PathVariable(name = "id") final Long subjectId) {
        List<UserSummaryResponse> response = userService.findFollowing(subjectId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Delete a {@link UserRelationship}. The user with the id provided as a path variable will no longer be following the
     * user with id specified as a request parameter.
     *
     * Example usage:
     * {@code DELETE /users/12/following?id=26 }
     * will result in user with id 12 unfollow user with id 26.
     *
     * @param subjectId The id of the user.
     * @param initiatorId The id of the user.
     * @param auth The authentication token.
     * @return HTTP OK.
     * @throws UnauthorizedException If the id of the currently authenticated user does not match the path variable id.
     * */
    @ApiOperation(value = "Delete a user relationship.")
    @RequestMapping(value = "/{id}/following", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUserRelationship(@PathVariable(value = "id") final Long initiatorId,
                                                    @RequestParam(value = "id") final Long subjectId,
                                                    @AuthenticationPrincipal final Authentication auth) {
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
        if(!Objects.equals(currentUser.getId(), initiatorId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        userService.deleteUserRelationship(initiatorId, subjectId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
