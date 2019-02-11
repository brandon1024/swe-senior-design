package com.unb.beforeigo.core.svc;

import com.unb.beforeigo.api.dto.request.UserRegistrationRequest;
import com.unb.beforeigo.api.dto.response.UserProfileSummaryResponse;
import com.unb.beforeigo.api.dto.response.UserRelationshipSummaryResponse;
import com.unb.beforeigo.api.dto.response.UserSummaryResponse;
import com.unb.beforeigo.api.exception.client.BadRequestException;
import com.unb.beforeigo.api.exception.client.NotFoundException;
import com.unb.beforeigo.application.dao.PhysicalAddressDAO;
import com.unb.beforeigo.application.dao.UserBucketRelationshipDAO;
import com.unb.beforeigo.application.dao.UserDAO;
import com.unb.beforeigo.application.dao.UserRelationshipDAO;
import com.unb.beforeigo.core.model.PhysicalAddress;
import com.unb.beforeigo.core.model.User;
import com.unb.beforeigo.core.model.UserRelationship;
import com.unb.beforeigo.core.model.validation.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired private UserDAO userDAO;

    @Autowired private UserRelationshipDAO userRelationshipDAO;

    @Autowired private UserBucketRelationshipDAO userBucketRelationshipDAO;

    @Autowired private PhysicalAddressDAO physicalAddressDAO;

    @Autowired private PasswordEncoder passwordEncoder;

    /**
     * Create a new {@link User}.
     *
     * The user provided must be valid. The {@link User#role} field will be overwritten with the default role
     * {@link User.Role#USER}. The {@link User#id} field is set to null to prevent this method from being used to
     * overwrite a user already persisted.
     *
     * @param user The user to create.
     * @return A summary of the user once persisted in the database.
     * */
    public UserSummaryResponse createUser(final User user) {
        user.setId(null);

        User response = saveUser(user);
        return adaptUserToSummary(response);
    }

    /**
     * Create a {@link UserRelationship}.
     *
     * @param initiatorId The id of the user that is the 'follower'.
     * @param subjectId The id of the user that is being 'followed'.
     * @return A summary of the user relationship, once persisted in the database.
     * @throws BadRequestException If a user with id initiatorId or subjectId cannot be found.
     * */
    public UserRelationshipSummaryResponse createUserRelationship(final Long initiatorId, final Long subjectId) {
        User follower = userDAO.findById(initiatorId)
                .orElseThrow(() -> new BadRequestException("Unable to find user with id " + initiatorId));
        User following = userDAO.findById(subjectId)
                .orElseThrow(() -> new BadRequestException("Unable to find user with id " + subjectId));

        UserRelationship response = userRelationshipDAO.save(new UserRelationship(follower, following));
        return adaptUserRelationshipToSummary(response);
    }

    /**
     * Retrieve a list of {@link User}'s that match any of the given id, username or email address, first, middle or
     * last name.
     *
     * Only non-null parameters are used in the query.
     *
     * @param userId The id of the user to use in the query.
     * @param username An optional username to use in the query.
     * @param email An optional email to use in the query.
     * @param firstName An optional first name to use in the query.
     * @param middleName An optional middle name to use in the query.
     * @param lastName An optional last name to use in the query.
     * @return A list of users found matching any of the request parameters.
     * */
    public List<UserSummaryResponse> findUsers(@Nullable final Long userId,
                                               @Nullable final String username,
                                               @Nullable final String email,
                                               @Nullable final String firstName,
                                               @Nullable final String middleName,
                                               @Nullable final String lastName) {
        User queryUser = new User();
        queryUser.setId(userId);
        queryUser.setUsername(username);
        queryUser.setEmail(email);
        queryUser.setFirstName(firstName);
        queryUser.setMiddleName(middleName);
        queryUser.setLastName(lastName);

        List<User> queriedUsers = userDAO.findAll(Example.of(queryUser, ExampleMatcher.matchingAny()));

        return queriedUsers.stream().map(UserService::adaptUserToSummary).collect(Collectors.toList());
    }

    /**
     * Retrieve a specific {@link User} by id.
     *
     * @param userId The id of the user.
     * @return The user with the given id.
     * @throws BadRequestException If a user with the given id cannot be found.
     * */
    public UserSummaryResponse findUserById(final Long userId) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new BadRequestException("Unable to find user with id " + userId));

        return adaptUserToSummary(user);
    }

    /**
     * Retrieve a summary of users that are following the user with the given subject id.
     *
     * @param subjectId The id of the user to use in the query.
     * @return A list of user summaries for users that are following a given user.
     * */
    public List<UserSummaryResponse> findFollowers(final Long subjectId) {
        UserRelationship relationship = new UserRelationship(null, new User(subjectId));

        List<UserRelationship> relationships = userRelationshipDAO.findAll(Example.of(relationship));

        return relationships.stream()
                .map(UserRelationship::getFollower)
                .map(UserService::adaptUserToSummary)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve a summary of users that are followed by the user with the given subject id.
     *
     * @param subjectId The id of the user to use in the query.
     * @return A list of user summaries for users that are followed by a given user.
     * */
    public List<UserSummaryResponse> findFollowing(final Long subjectId) {
        UserRelationship relationship = new UserRelationship(new User(subjectId), null);

        List<UserRelationship> relationships = userRelationshipDAO.findAll(Example.of(relationship));

        return relationships.stream()
                .map(UserRelationship::getFollowing)
                .map(UserService::adaptUserToSummary)
                .collect(Collectors.toList());
    }

    /**
     * Patch a {@link User}.
     *
     * Applies only non-null fields present in the partialUser to the persistent user, and then attempts to save the
     * user to the database.
     *
     * @param partialUser A partial user.
     * @param userId The id of the user that exists in the database that will be patched.
     * @return The persisted user.
     * @throws BadRequestException If a user with the given id cannot be found.
     * @throws BadRequestException If the user cannot be saved because it does not meet validation constraints.
     * */
    public UserSummaryResponse patchUser(final User partialUser, final Long userId) {
        User persistentUser = userDAO.findById(userId)
                .orElseThrow(() -> new BadRequestException("Unable to find user with id " + userId));

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

        User response = userDAO.save(persistentUser);
        return adaptUserToSummary(response);
    }

    /**
     * Update (overwrite) a {@link User}.
     *
     * Applies all fields present in the partialUser to the persistent user, and then attempts to save the user to the
     * database.
     *
     * @param partialUser A user.
     * @param userId The id of the user that exists in the database that will be updated.
     * @return The persisted user.
     * */
    public UserSummaryResponse updateUser(final User partialUser, final Long userId) {
        partialUser.setId(userId);

        User response = saveUser(partialUser);
        return adaptUserToSummary(response);
    }

    /**
     * Delete a {@link User}, along with its user relationships.
     *
     * @param userId The id of the persisted user that is to be deleted.
     * */
    public void deleteUser(final Long userId) {
        User persistentUser = userDAO.findById(userId)
                .orElseThrow(() -> new BadRequestException("Unable to find user with id " + userId));

        List<UserRelationship> relationships = userRelationshipDAO.findByFollower(persistentUser);

        userRelationshipDAO.deleteAll(relationships);
        userDAO.delete(persistentUser);
    }

    /**
     * Delete a user relationship.
     *
     * @param initiatorId The user being followed.
     * @param subjectId The user that is following a user.
     * */
    public void deleteUserRelationship(final Long initiatorId, final long subjectId) {
        UserRelationship relationship = userRelationshipDAO.findOne(Example.of(new UserRelationship(new User(initiatorId), new User(subjectId)))).
                orElseThrow(() -> new BadRequestException("Unable to find relationship"));

        userRelationshipDAO.delete(relationship);
    }

    /**
     * Save a {@link User}.
     *
     * Encrypts the user's password, sets the user's role to USER, performs validation, and saves the user to the
     * database. The user's physically address is also saved.
     *
     * @param user the user to save.
     * @return The persisted user.
     * @throws BadRequestException If the user cannot be saved because it does not meet validation constraints.
     * */
    private User saveUser(final User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.USER);

        EntityValidator.validateEntity(user, () ->
                new BadRequestException("cannot save user that does not meet validation constraints"));

        physicalAddressDAO.save(user.getUserAddress());
        return userDAO.save(user);
    }

    /**
     * Adapt a registration request DTO to a {@link User}. Fields in the registration request are copied to the user
     * object and returned.
     *
     * @param registrationRequest The registration request DTO.
     * @return The new user object.
     * */
    public static User buildUserFromRegistrationRequest(final UserRegistrationRequest registrationRequest) {
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
     * Build a UserSummaryResponse DTO of a {@link User}.
     *
     * @param user The user to be used to build a UserSummaryResponse.
     * @return A summary of the user.
     * */
    public static UserSummaryResponse adaptUserToSummary(final User user) {
        return new UserSummaryResponse(user.getId(), user.getUsername(), user.getEmail(), user.getBio(),
                user.getFirstName(), user.getMiddleName(), user.getLastName());
    }

    /**
     * Build a UserRelationshipSummaryResponse DTO of a {@link UserRelationship}.
     *
     * @param relationship The relationship to be used to build a UserRelationshipSummaryResponse.
     * @return A summary of the user relationship.
     * */
    public static UserRelationshipSummaryResponse adaptUserRelationshipToSummary(final UserRelationship relationship) {
        return new UserRelationshipSummaryResponse(relationship.getFollower().getId(), relationship.getFollowing().getId());
    }

    /**
     * Build a UserProfileSummaryResponse DTO of a {@link User}.
     *
     * @param userId The user id to be used to build a UserProfileSummaryResponse.
     * @return The profile summary of the user.
     * @throws BadRequestException if the user cannot be found with the specific id.
     * */
    public UserProfileSummaryResponse constructProfileSummary(final Long userId, final Long initiatorId) {

        User user = userDAO.findById(userId).orElseThrow(() -> new BadRequestException("Unable to find user with id " +
                userId));
        UserSummaryResponse userSummary = adaptUserToSummary(user);

        int bucketCount;
        if(Objects.equals(userId, initiatorId)){
            bucketCount = userBucketRelationshipDAO.findPublicBucketCount(user) +
                    userBucketRelationshipDAO.findPrivateBucketCount(user);
        }else{
            bucketCount = userBucketRelationshipDAO.findPublicBucketCount(user);
        }

        return new UserProfileSummaryResponse(userSummary, userRelationshipDAO.findFollowerCount(user),
                userRelationshipDAO.findFollowingCount(user), bucketCount, user.getCreatedAt());
    }

}
