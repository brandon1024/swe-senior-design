package com.unb.beforeigo.core.svc;

import com.unb.beforeigo.api.dto.request.UserRegistrationRequest;
import com.unb.beforeigo.api.exception.client.BadRequestException;
import com.unb.beforeigo.application.dao.PhysicalAddressDAO;
import com.unb.beforeigo.application.dao.UserDAO;
import com.unb.beforeigo.application.dao.UserRelationshipDAO;
import com.unb.beforeigo.core.model.PhysicalAddress;
import com.unb.beforeigo.core.model.User;
import com.unb.beforeigo.core.model.UserRelationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

@Service
public class UserService {

    @Autowired private UserDAO userDAO;

    @Autowired private UserRelationshipDAO userRelationshipDAO;

    @Autowired private PhysicalAddressDAO physicalAddressDAO;

    @Autowired private PasswordEncoder passwordEncoder;

    /**
     * Save a user.
     *
     * Encrypts the user's password, sets the user's role to USER, performs validation, and saves the user to the
     * database. The user's physically address is also saved.
     *
     * @param user the user to save
     * @return the persisted user
     * @throws BadRequestException if the user cannot be saved because it does not meet validation constraints
     * */
    public User saveUser(final User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.USER);

        validateUser(user, () -> new BadRequestException("cannot save user that does not meet validation constraints"));

        physicalAddressDAO.save(user.getUserAddress());
        return userDAO.save(user);
    }

    /**
     * Patch a user.
     *
     * Applies only non-null fields present in the partialUser to the persistent user, and then attempts to save the
     * user to the database.
     *
     * @param partialUser a partial user
     * @param persistentUser the user that exists in the database that will be patched
     * @return the persisted user
     * @throws BadRequestException if the user cannot be saved because it does not meet validation constraints
     * */
    public User patchUser(final User partialUser, final User persistentUser) {
        if(Objects.nonNull(partialUser.getEmail())) {
            persistentUser.setEmail(partialUser.getEmail());
        }

        if(Objects.nonNull(partialUser.getUsername())) {
            persistentUser.setUsername(partialUser.getUsername());
        }

        if(Objects.nonNull(partialUser.getFirstName())) {
            persistentUser.setFirstName(partialUser.getFirstName());
        }

        if(Objects.nonNull(partialUser.getMiddleName())) {
            persistentUser.setMiddleName(partialUser.getMiddleName());
        }

        if(Objects.nonNull(partialUser.getLastName())) {
            persistentUser.setLastName(partialUser.getLastName());
        }

        if(Objects.nonNull(partialUser.getBio())) {
            persistentUser.setBio(partialUser.getBio());
        }

        if(Objects.nonNull(partialUser.getPassword())) {
            persistentUser.setPassword(partialUser.getPassword());
        }

        if(Objects.nonNull(partialUser.getUserAddress())) {
            PhysicalAddress userAddress = partialUser.getUserAddress();
            PhysicalAddress persistentUserAddress = persistentUser.getUserAddress();

            if(Objects.isNull(persistentUserAddress)) {
                persistentUserAddress = new PhysicalAddress();
                persistentUser.setUserAddress(persistentUserAddress);
            }

            if(Objects.nonNull(userAddress.getPrimaryStreetAddress())) {
                persistentUserAddress.setPrimaryStreetAddress(userAddress.getPrimaryStreetAddress());
            }

            if(Objects.nonNull(userAddress.getSecondaryStreetAddress())) {
                persistentUserAddress.setSecondaryStreetAddress(userAddress.getSecondaryStreetAddress());
            }

            if(Objects.nonNull(userAddress.getCity())) {
                persistentUserAddress.setCity(userAddress.getCity());
            }

            if(Objects.nonNull(userAddress.getProvince())) {
                persistentUserAddress.setProvince(userAddress.getProvince());
            }

            if(Objects.nonNull(userAddress.getCountry())) {
                persistentUserAddress.setCountry(userAddress.getCountry());
            }

            if(Objects.nonNull(userAddress.getPostalCode())) {
                persistentUserAddress.setPostalCode(userAddress.getPostalCode());
            }
        }

        return saveUser(partialUser);
    }

    /**
     * Update (overwrite) a user.
     *
     * Applies all fields present in the partialUser to the persistent user, and then attempts to save the user to the
     * database.
     *
     * @param partialUser a user
     * @param persistentUser the user that exists in the database that will be updated
     * @return the persisted user
     * @throws BadRequestException if the user cannot be saved because it does not meet validation constraints
     * */
    public User updateUser(final User partialUser, final User persistentUser) {
        partialUser.setId(persistentUser.getId());

        return saveUser(partialUser);
    }

    /**
     * Delete a user, along with its user relationships.
     *
     * @param persistentUser the persisted user that is to be deleted.
     * */
    public void deleteUser(final User persistentUser) {
        List<UserRelationship> relationships = userRelationshipDAO.findByFollower(persistentUser);

        userRelationshipDAO.deleteAll(relationships);
        userDAO.delete(persistentUser);
    }

    /**
     * Adapt a registration request DTO to a user entity. Fields in the registration request are copied to the user
     * object and returned.
     *
     * @param registrationRequest the registration request DTO
     * @return the new user object.
     * */
    public User buildUserFromRegistrationRequest(final UserRegistrationRequest registrationRequest) {
        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(registrationRequest.getPassword());
        user.setFirstName(registrationRequest.getFirstName());
        user.setMiddleName(registrationRequest.getMiddleName());
        user.setLastName(registrationRequest.getLastName());
        user.setRole(User.Role.USER);

        PhysicalAddress userAddress = new PhysicalAddress();
        userAddress.setPrimaryStreetAddress(registrationRequest.getPrimaryStreetAddress());
        userAddress.setSecondaryStreetAddress(registrationRequest.getSecondaryStreetAddress());
        userAddress.setCity(registrationRequest.getCity());
        userAddress.setProvince(registrationRequest.getProvince());
        userAddress.setCountry(registrationRequest.getCountry());
        userAddress.setPostalCode(registrationRequest.getPostalCode());

        user.setUserAddress(userAddress);

        return user;
    }

    /**
     * Validate a user.
     *
     * @param user the user to be validated
     * @return true if the the user is valid, false otherwise.
     * */
    public boolean validateUser(final User user) {
        return getUserConstraintViolations(user).isEmpty();
    }

    /**
     * Verify that a user is valid, returning the user if so, otherwise throw an exception produced by the exception
     * supplying function.
     *
     * @param <T> Type of the exception to be thrown
     * @param user the user to verify
     * @param exceptionSupplier the supplying function that produces an exception to be thrown
     * @throws T if the user is invalid.
     * @return the supplied user, if valid.
     * */
    public <T extends Throwable> User validateUser(final User user, Supplier<? extends T> exceptionSupplier) throws T {
        if(validateUser(user)) {
            return user;
        }

        throw exceptionSupplier.get();
    }

    /**
     * Get a set of validation constraint violations for a user.
     *
     * @param user the user to be validated
     * @return the set of constraint violations for a given user.
     * */
    public Set<ConstraintViolation<User>> getUserConstraintViolations(final User user) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

        return factory.getValidator().validate(user);
    }
}
