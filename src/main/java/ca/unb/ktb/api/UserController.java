package ca.unb.ktb.api;

import ca.unb.ktb.api.dto.response.UserProfileSummaryResponse;
import ca.unb.ktb.api.dto.response.UserSummaryResponse;
import ca.unb.ktb.api.exception.client.BadRequestException;
import ca.unb.ktb.api.exception.client.UnauthorizedException;
import ca.unb.ktb.core.model.User;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/")
@Slf4j
public class UserController {

    private static final List<String> contentTypes = Arrays.asList("image/png", "image/jpeg", "image/gif");

    @Autowired private UserService userService;

    /**
     * Retrieve a list of {@link User}'s with a given id, username or email address, first, middle or last name.
     *
     * If no request parameters are provided, all users are returned. If more than one request parameter is specified,
     * users are queried and selected if they match any of the parameters.
     *
     * Usage example:
     * {@code http://api.kick-the-bucket.com/users?id=133&username=example&email=example@example.com}
     * will retrieve all users that:
     * <ul>
     *     <li>have an 133, or</li>
     *     <li>have username example, or</li>
     *     <li>have email address example@example.com</li>
     * </ul>
     *
     * @param userId An optional id to be used in the query.
     * @param username An optional username to use in the query.
     * @param email An optional email to use in the query.
     * @param firstName An optional first name to use in the query.
     * @param middleName An optional middle name to use in the query.
     * @param lastName An optional last name to use in the query.
     * @return A list of users found matching any of the request parameters. HTTP OK.
     * */
    @ApiOperation(value = "Retrieve a list of users by various fields.",
            response = UserSummaryResponse.class,
            responseContainer = "List")
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<List<UserSummaryResponse>> findUsers(
            @RequestParam(name = "id", required = false) final Long userId,
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
     * @param userId The id of the user.
     * @return The user with the given id. HTTP OK.
     * */
    @ApiOperation(value = "Retrieve a specific user by id.", response = UserSummaryResponse.class)
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
     * @param userId The id of the user to update.
     * @param user The user to update.
     * @param auth The authentication token.
     * @return The updated user. HTTP OK.
     * @throws UnauthorizedException If the id of the currently authenticated user does not match the path variable id.
     * */
    @ApiOperation(value = "Update fields in a user that is currently persisted in the database.",
            response = UserSummaryResponse.class)
    @RequestMapping(value = "/users/{id}", method = RequestMethod.PATCH, consumes = "application/json")
    public ResponseEntity<UserSummaryResponse> patchUser(@PathVariable(name = "id") final Long userId,
                                                         @RequestBody final User user,
                                                         @AuthenticationPrincipal final Authentication auth) {
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
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
     * @param userId The id of the user to update.
     * @param user The user to update.
     * @param auth The authentication token.
     * @return The updated user. HTTP OK.
     * @throws UnauthorizedException If the id of the currently authenticated user does not match the path variable id.
     * */
    @ApiOperation(value = "Completely update a user that is currently persisted in the database.",
            response = UserSummaryResponse.class)
    @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT, consumes = "application/json")
    public ResponseEntity<UserSummaryResponse> updateUser(@PathVariable(name = "id") final Long userId,
                                                          @Valid @RequestBody final User user,
                                                          @AuthenticationPrincipal final Authentication auth) {
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
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
     * @param userId The id of the user to be deleted.
     * @param auth The authentication token.
     * @return HTTP OK.
     * @throws UnauthorizedException If the id of the currently authenticated user does not match the path variable id.
     * */
    @ApiOperation(value = "Delete a user.")
    @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable(name = "id") final Long userId,
                                        @AuthenticationPrincipal final Authentication auth) {
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
        if(!Objects.equals(currentUser.getId(), userId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Get a {@link User}'s profile information.
     *
     * {@link User}'s id must be provided.
     *
     * @param userId The id of the user who's profile you wish to obtain.
     * @return User profile response data. HTTP OK.
     * @throws UnauthorizedException If the id of the currently authenticated user does not match the path variable id.
     * */
    @ApiOperation(value = "Get a user's profile.")
    @RequestMapping(value = "/users/{id}/profile", method = RequestMethod.GET)
    public ResponseEntity<UserProfileSummaryResponse> getUserProfile(@PathVariable(name = "id") final Long userId,
                                                                     @AuthenticationPrincipal final Authentication auth) {

        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
        Long initiatorId = currentUser.getId();

        UserProfileSummaryResponse userProfile = userService.constructProfileSummary(userId, initiatorId);
        return new ResponseEntity<>(userProfile, HttpStatus.OK);
    }

    /**
     * Upload a profile picture for a given user.
     *
     * @param userId The id of the user who's profile picture will be changed.
     * @param file The name of the file uploaded
     * @return A summary of the user. HTTP OK.
     * @throws UnauthorizedException If the id of the currently authenticated user does not match the path variable id.
     * @throws BadRequestException If the uploaded file is empty, or the file type is unsupported.
     * */
    @ApiOperation(value = "Upload a profile picture.")
    @RequestMapping(value = "/users/{id}/profile/imageupload", method = RequestMethod.POST)
    public ResponseEntity<UserSummaryResponse> profilePictureUpload(@PathVariable(name = "id") final Long userId,
                                                                    @RequestParam(name = "file") final MultipartFile file,
                                                                    @AuthenticationPrincipal final Authentication auth) {
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
        if(!Objects.equals(currentUser.getId(), userId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        if(file.isEmpty()) {
            throw new BadRequestException("uploaded file cannot be empty");
        }

        if(!contentTypes.contains(file.getContentType())) {
            throw new BadRequestException(String.format("unsupported content type '%s", file.getContentType()));
        }

        UserSummaryResponse updatedUser = userService.updateProfilePicture(userId, file);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }
}
