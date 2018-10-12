package com.unb.beforeigo.api;

import com.unb.beforeigo.api.exception.client.BadRequestException;
import com.unb.beforeigo.application.dao.PhysicalAddressDAO;
import com.unb.beforeigo.application.dao.UserDAO;
import com.unb.beforeigo.application.dao.UserRelationshipDAO;
import com.unb.beforeigo.core.model.PhysicalAddress;
import com.unb.beforeigo.core.model.User;
import com.unb.beforeigo.core.model.UserRelationship;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/")
@Slf4j
public class UserController {

    @Autowired private UserDAO userDAO;

    @Autowired private PhysicalAddressDAO addressDAO;

    @Autowired private UserRelationshipDAO userRelationshipDAO;

    /**
     * Create a new {@link User}. The user role is set to USER, regardless of whether a role is set in the request.
     *
     * @param user A valid user with all necessary fields
     * @return a new user once persisted in the database
     * @throws BadRequestException if the user provided has a non-null id field
     * */
    @RequestMapping(value = "/users", method = RequestMethod.POST, consumes = "application/json")
    public User createUser(@Valid @RequestBody final User user) {
        if(user.getId() != null) {
            throw new BadRequestException("Cannot create a user with a specific user id " + user.getId());
        }

        user.setRole(User.Role.USER);

        return userDAO.save(user);
    }

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
    public List<User> findUsers(@RequestParam(name = "id", required = false) final Long userId,
                                @RequestParam(name = "username", required = false) final String username,
                                @RequestParam(name = "email", required = false) final String email,
                                @RequestParam(name = "firstname", required = false) final String firstName,
                                @RequestParam(name = "middlename", required = false) final String middleName,
                                @RequestParam(name = "lastname", required = false) final String lastName) {
        User queryUser = new User();
        queryUser.setId(userId);
        queryUser.setUsername(username);
        queryUser.setEmail(email);
        queryUser.setFirstName(firstName);
        queryUser.setMiddleName(middleName);
        queryUser.setLastName(lastName);

        return userDAO.findAll(Example.of(queryUser, ExampleMatcher.matchingAny()));
    }

    /**
     * Retrieve a specific {@link User} by id.
     *
     * @param userId the id of the user
     * @return the user with the given id.
     * @throws BadRequestException if a user with the given id cannot be found.
     * */
    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public User findUserById(@PathVariable(name = "id") final Long userId) {
        return userDAO.findById(userId)
                .orElseThrow(() -> new BadRequestException("Unable to find user with id " + userId));
    }

    /**
     * Update fields in a {@link User} that is currently persisted in the database. Only non-null user fields are updated.
     *
     * @param userId the id of the user to update
     * @param user the user to update
     * @return the updated user
     * @throws BadRequestException if a user cannot be found with the provided id, or the new user does not meet User validation constraints.
     * */
    @RequestMapping(value = "/users/{id}", method = RequestMethod.PATCH, consumes = "application/json")
    public User patchUser(@PathVariable(name = "id") final Long userId,
                          @RequestBody final User user) {
        User persistentUser = userDAO.findById(userId)
                .orElseThrow(() -> new BadRequestException("Unable to find user with id " + userId));

        if(user.getEmail() != null) {
            persistentUser.setEmail(user.getEmail());
        }

        if(user.getUsername() != null) {
            persistentUser.setUsername(user.getUsername());
        }

        if(user.getFirstName() != null) {
            persistentUser.setFirstName(user.getFirstName());
        }

        if(user.getMiddleName() != null) {
            persistentUser.setMiddleName(user.getMiddleName());
        }

        if(user.getLastName() != null) {
            persistentUser.setLastName(user.getLastName());
        }

        if(user.getBio() != null) {
            persistentUser.setBio(user.getBio());
        }

        if(user.getUserAddress() != null) {
            PhysicalAddress userAddress = user.getUserAddress();
            PhysicalAddress persistentUserAddress = persistentUser.getUserAddress();
            if(persistentUserAddress == null) {
                persistentUserAddress = new PhysicalAddress();
                persistentUser.setUserAddress(persistentUserAddress);
            }

            if(userAddress.getPrimaryStreetAddress() != null) {
                persistentUserAddress.setPrimaryStreetAddress(userAddress.getPrimaryStreetAddress());
            }

            if(userAddress.getSecondaryStreetAddress() != null) {
                persistentUserAddress.setSecondaryStreetAddress(userAddress.getSecondaryStreetAddress());
            }

            if(userAddress.getCity() != null) {
                persistentUserAddress.setCity(userAddress.getCity());
            }

            if(userAddress.getProvince() != null) {
                persistentUserAddress.setProvince(userAddress.getProvince());
            }

            if(userAddress.getCountry() != null) {
                persistentUserAddress.setCountry(userAddress.getCountry());
            }

            if(userAddress.getPostalCode() != null) {
                persistentUserAddress.setPostalCode(userAddress.getPostalCode());
            }

            addressDAO.save(persistentUserAddress);
        }

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Set<ConstraintViolation<User>> validationViolations = factory.getValidator().validate(persistentUser);
        if(validationViolations.size() > 0) {
            throw new BadRequestException("updated user does not meet validation constraints");
        }

        return userDAO.save(persistentUser);
    }

    /**
     * Completely update a {@link User} that is currently persisted in the database. All user fields are updated.
     *
     * @param userId the id of the user to update
     * @param user the user to update
     * @return the updated user
     * @throws BadRequestException if a user cannot be found with the provided id, or the new user does not meet User validation constraints.
     * */
    @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT, consumes = "application/json")
    public User updateUser(@PathVariable(name = "id") final Long userId, @Valid @RequestBody final User user) {
        User persistentUser = userDAO.findById(userId)
                .orElseThrow(() -> new BadRequestException("Unable to find user with id " + userId));

        user.setId(persistentUser.getId());
        user.setRole(User.Role.USER);
        return userDAO.save(user);
    }

    /**
     * Delete a {@link User}.
     *
     * @param userId the id of the user to be deleted
     * @throws BadRequestException if a user cannot be found with the provided id
     * */
    @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable(name = "id") final Long userId) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new BadRequestException("Unable to find user with id " + userId));
        List<UserRelationship> relationships = userRelationshipDAO.findByFollower(user);

        userRelationshipDAO.deleteAll(relationships);
        userDAO.delete(user);
    }
}
