package com.unb.beforeigo.api;

import com.unb.beforeigo.api.dto.response.UserSummaryResponse;
import com.unb.beforeigo.api.exception.client.BadRequestException;
import com.unb.beforeigo.api.exception.client.UnauthorizedException;
import com.unb.beforeigo.core.model.User;
import com.unb.beforeigo.core.svc.UserService;
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

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/")
@Slf4j
public class UserController {

    @Autowired private UserService userService;

    /**
     * Retrieve a list of {@link User}'s by with a given id, username or email address, first, middle or last name.
     *
     * If no request parameters are provided, all users are returned. If more than one request parameter is specified,
     * users are queried and selected if they match any of the parameters.
     *
     * Usage example:
     * {@code http://api.before-i-go.com/users?id=133&username=example&email=example@example.com}
     * will retrieve all users that:
     * <ul>
     *     <li>have an 133, or</li>
     *     <li>have username example, or</li>
     *     <li>have email address example@example.com</li>
     * </ul>
     *
     * @param userId an optional id to be used in the query
     * @param username an optional username to use in the query
     * @param email an optional email to use in the query
     * @param firstName an optional first name to use in the query
     * @param middleName an optional middle name to use in the query
     * @param lastName an optional last name to use in the query
     * @return a list of users found matching any of the request parameters.
     * */
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<List<UserSummaryResponse>> findUsers(@RequestParam(name = "id", required = false) final Long userId,
                                                               @RequestParam(name = "username", required = false) final String username,
                                                               @RequestParam(name = "email", required = false) final String email,
                                                               @RequestParam(name = "firstname", required = false) final String firstName,
                                                               @RequestParam(name = "middlename", required = false) final String middleName,
                                                               @RequestParam(name = "lastname", required = false) final String lastName) {
        List<UserSummaryResponse> users = userService.findUsers(userId, username, email, firstName, middleName, lastName);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Retrieve a specific {@link User} by id.
     *
     * @param userId the id of the user
     * @return the user with the given id.
     * @throws BadRequestException if a user with the given id cannot be found.
     * */
    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public ResponseEntity<UserSummaryResponse> findUserById(@PathVariable(name = "id") final Long userId) {
        UserSummaryResponse user = userService.findUserById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Update fields in a {@link User} that is currently persisted in the database.
     *
     * Only non-null user fields are updated. The id of the currently authenticated user must match the path variable id.
     *
     * @param userId the id of the user to update
     * @param user the user to update
     * @param currentUser the currently authenticated user
     * @return the updated user
     * @throws UnauthorizedException if the id of the currently authenticated user does not match the path variable id
     * @throws BadRequestException if a user cannot be found with the provided id, or the new user does not meet User
     * validation constraints.
     * */
    @RequestMapping(value = "/users/{id}", method = RequestMethod.PATCH, consumes = "application/json")
    public ResponseEntity<UserSummaryResponse> patchUser(@PathVariable(name = "id") final Long userId,
                                                         @RequestBody final User user,
                                                         @AuthenticationPrincipal final UserPrincipal currentUser) {
        if(!Objects.equals(currentUser.getId(), userId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        UserSummaryResponse patchedUser = userService.patchUser(user, userId);
        return new ResponseEntity<>(patchedUser, HttpStatus.OK);
    }

    /**
     * Completely update a {@link User} that is currently persisted in the database. All user fields are updated.
     *
     * The id of the currently authenticated user must match the path variable id.
     *
     * @param userId the id of the user to update
     * @param user the user to update
     * @param currentUser the currently authenticated user
     * @return the updated user
     * @throws UnauthorizedException if the id of the currently authenticated user does not match the path variable id
     * @throws BadRequestException if a user cannot be found with the provided id, or the new user does not meet User
     * validation constraints.
     * */
    @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT, consumes = "application/json")
    public ResponseEntity<UserSummaryResponse> updateUser(@PathVariable(name = "id") final Long userId,
                                                          @Valid @RequestBody final User user,
                                                          @AuthenticationPrincipal final UserPrincipal currentUser) {
        if(!Objects.equals(currentUser.getId(), userId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        UserSummaryResponse updatedUser = userService.updateUser(user, userId);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    /**
     * Delete a {@link User}.
     *
     * The id of the currently authenticated user must match the path variable id.
     *
     * @param userId the id of the user to be deleted
     * @param currentUser the currently authenticated user
     * @throws UnauthorizedException if the id of the currently authenticated user does not match the path variable id
     * @throws BadRequestException if a user cannot be found with the provided id
     * */
    @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable(name = "id") final Long userId,
                                        @AuthenticationPrincipal final UserPrincipal currentUser) {
        if(!Objects.equals(currentUser.getId(), userId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
