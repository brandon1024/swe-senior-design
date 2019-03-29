package ca.unb.ktb.core.svc;

import ca.unb.ktb.api.dto.request.UserRegistrationRequest;
import ca.unb.ktb.api.dto.response.UserProfileSummaryResponse;
import ca.unb.ktb.api.dto.response.UserRelationshipSummaryResponse;
import ca.unb.ktb.api.dto.response.UserSummaryResponse;
import ca.unb.ktb.api.exception.client.BadRequestException;
import ca.unb.ktb.api.exception.client.UnauthorizedException;
import ca.unb.ktb.api.exception.server.InternalServerErrorException;
import ca.unb.ktb.application.dao.PhysicalAddressDAO;
import ca.unb.ktb.application.dao.UserDAO;
import ca.unb.ktb.application.dao.UserRelationshipDAO;
import ca.unb.ktb.core.model.Bucket;
import ca.unb.ktb.core.model.PhysicalAddress;
import ca.unb.ktb.core.model.User;
import ca.unb.ktb.core.model.UserRelationship;
import ca.unb.ktb.core.model.validation.EntityValidator;
import ca.unb.ktb.infrastructure.AmazonS3Bucket;
import ca.unb.ktb.infrastructure.AmazonS3BucketConfiguration;
import ca.unb.ktb.infrastructure.security.UserPrincipal;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    @Autowired private UserDAO userDAO;

    @Autowired private PhysicalAddressDAO physicalAddressDAO;

    @Autowired private UserRelationshipDAO userRelationshipDAO;

    @Autowired private BucketService bucketService;

    @Autowired private AmazonS3ClientService s3ClientService;

    @Autowired private AmazonS3BucketConfiguration awsBucketConfiguration;

    @Autowired private PasswordEncoder passwordEncoder;

    /**
     * Create a new {@link User}.
     *
     * @param user The {@link User} to create.
     * @return The {@link User}, once persisted in the database.
     * @see UserService#saveUser(User)
     * */
    public User createUser(final User user) {
        user.setId(null);

        LOG.info("Creating new user", user.getUsername());

        return saveUser(user);
    }

    /**
     * Create a {@link UserRelationship}. If successful, the principal user will now be following the {@link User} with
     * the given id.
     *
     * @param userId The id of the {@link User} that is being 'followed'.
     * @return The {@link UserRelationship}, once persisted in the database.
     * @throws BadRequestException If the {@link User} does not exist.
     * @see UserService#findPrincipalUser(Long)
     * @see UserService#findUserById(Long)
     * */
    public UserRelationship createUserRelationship(final Long userId) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User follower = findPrincipalUser(currentUser.getId());
        User following = findUserById(userId);

        LOG.info("User {} following user {}", currentUser.getId(), following.getId());

        return userRelationshipDAO.save(new UserRelationship(follower, following));
    }

    /**
     * Retrieve a list of {@link User}s that match any of the given id, username or email address, first, middle or
     * last name. Null parameters are ignored.
     *
     * @param userId The id of the {@link User} to use in the query.
     * @param username An optional username to use in the query.
     * @param email An optional email to use in the query.
     * @param firstName An optional first name to use in the query.
     * @param middleName An optional middle name to use in the query.
     * @param lastName An optional last name to use in the query.
     * @return A list of {@link User}s matching any of the parameters..
     * */
    public List<User> findUsers(@Nullable final Long userId,
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

        return userDAO.findAll(Example.of(queryUser, ExampleMatcher.matchingAny()));
    }

    /**
     * Retrieve a list of {@link User}s with a username or real name that partially matches the query string.
     *
     * @param queryString The username query string.
     * @param pageable Specify how the results should be paged.
     * @return a list of {@link User}s whose username or real name fully or partially matches the query string.
     * @see UserDAO#findAllByUsernameOrRealNameLike(String, Pageable)
     * */
    public List<User> findUsersByUsernameOrRealName(final String queryString, final Pageable pageable) {
        return userDAO.findAllByUsernameOrRealNameLike(queryString, pageable);
    }

    /**
     * Retrieve a specific {@link User} by id.
     *
     * @param userId The id of the {@link User}.
     * @return The {@link User} with the given id.
     * @throws BadRequestException If a {@link User} with the given id cannot be found.
     * */
    public User findUserById(final Long userId) {
        return userDAO.findById(userId).orElseThrow(() ->
                new BadRequestException("Unable to find user with id " + userId));
    }

    /**
     * Retrieve a list of {@link User}s that are following a given user.
     *
     * @param userId The id of the {@link User} to use in the query.
     * @return A list of {@link User}s that are following a given user.
     * */
    public List<User> findFollowers(final Long userId) {
        UserRelationship relationship = new UserRelationship(null, new User(userId));
        List<UserRelationship> relationships = userRelationshipDAO.findAll(Example.of(relationship));

        return relationships.stream()
                .map(UserRelationship::getFollower)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve a list of {@link User}s that are followed by a given user.
     *
     * @param userId The id of the {@link User} being followed.
     * @return A list of {@link User}s that are followed by a given user.
     * */
    public List<User> findFollowing(final Long userId) {
        UserRelationship relationship = new UserRelationship(new User(userId), null);
        List<UserRelationship> relationships = userRelationshipDAO.findAll(Example.of(relationship));

        return relationships.stream()
                .map(UserRelationship::getFollowing)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve a list of {@link UserRelationship} between followers of a given {@link User} and other users.
     *
     * @param pageable Specify how the results should be paged.
     * @return A list of {@link UserRelationship} between followers of a given {@link User} and other users.
     * @see UserRelationshipDAO#retrieveUsersFollowedByFollowedUsers(Long, Pageable)
     * */
    public List<UserRelationship> findUsersRecentlyFollowedByFollowedUsers(final Long userId, final Pageable pageable) {
        return userRelationshipDAO.retrieveUsersFollowedByFollowedUsers(userId, pageable);
    }

    /**
     * Retrieve a list of {@link UserRelationship} between a given {@link User} and other users.
     *
     * @param pageable Specify how the results should be paged.
     * @return A list of {@link UserRelationship} between a given {@link User} and other users.
     * @see UserRelationshipDAO#retrieveUsersFollowedByUser(Long, Pageable)
     * */
    public List<UserRelationship> findUsersRecentlyFollowedByUser(final Long userId, final Pageable pageable) {
        return userRelationshipDAO.retrieveUsersFollowedByUser(userId, pageable);
    }

    /**
     * Patch the fields in the principal {@link User}.
     *
     * Applies only non-null fields present in the partialUser to the principal user.
     *
     * User role is ignored. If the password field is non-null, it is encrypted before updating the persisted user.
     *
     * @param partialUser A partial {@link User}.
     * @return The persisted {@link User}.
     * @see UserService#findPrincipalUser(Long)
     * */
    public User patchUser(final User partialUser) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User persistentUser = findPrincipalUser(currentUser.getId());

        LOG.info("User {} patching their user details", currentUser.getId());

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
            persistentUser.setPassword(passwordEncoder.encode(partialUser.getPassword()));
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

            physicalAddressDAO.save(persistentUserAddress);
        }

        return userDAO.save(persistentUser);
    }

    /**
     * Completely overwrite fields in the principal {@link User}.
     *
     * The password field is encrypted before updating the persisted user.
     *
     * @param partialUser A {@link User}.
     * @return The persisted {@link User}.
     * @see UserService#findPrincipalUser(Long)
     * */
    public User updateUser(final User partialUser) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = findPrincipalUser(currentUser.getId());
        partialUser.setId(user.getId());

        LOG.info("User {} updating their user details", currentUser.getId());

        return saveUser(partialUser);
    }


    /**
     * Update the principal {@link User}'s profile image with a new image, return the updated user.
     *
     * Images are stored Amazon S3 using the {@link AmazonS3ClientService} in the bucket under the following path:
     * s3://{bucket name}/{user id}/{file md5 hash}.{original filename}
     *
     * Images are uploaded to the bucket configured through the Spring environment. The key to the new object is stored
     * under the User's model.
     *
     * The following image metadata is attached to the object before upload:
     * - username: the name of the user
     * - original-filename: the original name of the file
     *
     * @param file The new profile picture.
     * @return The updated {@link User}, once persisted in the database.
     * @throws InternalServerErrorException If an unexpected exception occurred.
     * @see AmazonS3ClientService#multipartFileUpload(MultipartFile, ObjectMetadata, AmazonS3Bucket, String)
     * @see AmazonS3BucketConfiguration
     * */
    public User updateProfilePicture(final MultipartFile file) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User persistentUser = findPrincipalUser(currentUser.getId());

        LOG.info("User {} updating their profile picture with file {}", currentUser.getId(), file.getOriginalFilename());

        AmazonS3Bucket bucket = awsBucketConfiguration.getBuckets().get(AmazonS3BucketConfiguration.userProfileImageBucket);
        LOG.debug("Uploading file to AWS S3 bucket {} in region {}", bucket.getName(), bucket.getRegion());

        ObjectMetadata imageMetadata = new ObjectMetadata();
        imageMetadata.addUserMetadata("username", persistentUser.getUsername());
        imageMetadata.addUserMetadata("original-filename", file.getOriginalFilename());

        try {
            String objectId = s3ClientService.multipartFileUpload(file, imageMetadata, bucket, currentUser.getId().toString());
            persistentUser.setProfilePictureObjectKey(objectId);
        } catch (InterruptedException e) {
            throw new InternalServerErrorException("Image upload interrupted unexpectedly.", e);
        } catch (IOException e) {
            throw new InternalServerErrorException("Image upload failed due to unexpected exception.", e);
        } catch (NoSuchAlgorithmException e) {
            throw new InternalServerErrorException("Could not compute MD5 checksum of uploaded file.", e);
        }

        return userDAO.save(persistentUser);
    }

    /**
     * Delete principal {@link User}, along with its user relationships.
     *
     * @see BucketService#findBucketsByOwner(Long)
     * */
    public void deleteUser() {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User persistentUser = findPrincipalUser(currentUser.getId());

        LOG.info("User {} deleting their user account", currentUser.getId());

        List<Bucket> buckets = bucketService.findBucketsByOwner(currentUser.getId());
        LOG.debug("User {} deleting {} buckets", buckets.size());

        for(Bucket bucket : buckets) {
            bucketService.deleteBucket(bucket);
        }

        List<UserRelationship> relationships = userRelationshipDAO.findByFollower(persistentUser);
        LOG.debug("User {} deleting {} user relationships", relationships.size());

        userRelationshipDAO.deleteAll(relationships);
        userDAO.delete(persistentUser);
    }

    /**
     * Delete a {@link User}-{@link User} relationship.
     *
     * @param subjectId The user being followed.
     * */
    public void deleteUserRelationship(final Long subjectId) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User persistentUser = findPrincipalUser(currentUser.getId());
        User subject = findUserById(subjectId);

        LOG.debug("User {} deleting user relationship with user {}", currentUser.getId(), subject);

        UserRelationship example = new UserRelationship(persistentUser, subject);
        UserRelationship relationship = userRelationshipDAO.findOne(Example.of(example)).orElseThrow(() ->
                new BadRequestException("Unable to find relationship"));

        userRelationshipDAO.delete(relationship);
    }

    /**
     * Save a {@link User}.
     *
     * Encrypts the user's password, sets the user's role to ROLE_USER, performs validation, and saves the user to the
     * database. The user's {@link PhysicalAddress} is also saved.
     *
     * @param user The {@link User} to save.
     * @return The persisted {@link User}.
     * @throws BadRequestException If the user cannot be saved because it does not meet validation constraints.
     * */
    private User saveUser(final User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.ROLE_USER);

        EntityValidator.validateEntity(user, () ->
                new BadRequestException("cannot save user that does not meet validation constraints"));

        physicalAddressDAO.save(user.getUserAddress());
        return userDAO.save(user);
    }

    /**
     * Adapt a {@link UserRegistrationRequest} DTO to a {@link User}. Fields in the registration request are copied to the user
     * object and returned.
     *
     * The returned user will have the role ROLE_USER.
     *
     * @param registrationRequest The {@link UserRegistrationRequest} DTO.
     * @return The {@link User} object.
     * */
    public static User buildUserFromRegistrationRequest(final UserRegistrationRequest registrationRequest) {
        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setEmail(registrationRequest.getEmail());
        user.setPassword(registrationRequest.getPassword());
        user.setFirstName(registrationRequest.getFirstName());
        user.setMiddleName(registrationRequest.getMiddleName());
        user.setLastName(registrationRequest.getLastName());
        user.setRole(User.Role.ROLE_USER);

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
     * Build a {@link UserSummaryResponse} DTO of a {@link User}.
     *
     * Generates a pre-signed URL for the {@link User}'s profile picture using the {@link AmazonS3ClientService}.
     *
     * @param user The {@link User} to be used to build a {@link UserSummaryResponse}.
     * @return A summary of the {@link User}.
     * @see AmazonS3ClientService#generatePreSignedObjectURL(AmazonS3Bucket, String)
     * @see AmazonS3BucketConfiguration
     * */
    public UserSummaryResponse adaptUserToSummary(final User user) {
        String url = null;

        if(Objects.nonNull(user.getProfilePictureObjectKey())) {
            AmazonS3Bucket bucket = awsBucketConfiguration.getBucket(AmazonS3BucketConfiguration.userProfileImageBucket);
            Optional<URL> presignedUrl = s3ClientService.generatePreSignedObjectURL(bucket, user.getProfilePictureObjectKey());
            url = presignedUrl.map(URL::toString).orElse(null);

            if(Objects.isNull(url)) {
                LOG.warn("Could not generate pre-signed url for profile picture with user id {} and object key {}; no such object exists in bucket {}.",
                        user.getId(), user.getProfilePictureObjectKey(), bucket.getName());
            }
        }

        return new UserSummaryResponse(user.getId(), user.getUsername(), user.getEmail(), user.getBio(),
                user.getFirstName(), user.getMiddleName(), user.getLastName(), url);
    }

    /**
     * Build a {@link UserRelationshipSummaryResponse} DTO of a {@link UserRelationship}.
     *
     * @param relationship The {@link UserRelationship} to be used to build a {@link UserRelationshipSummaryResponse}.
     * @return A summary of the {@link UserRelationship}.
     * */
    public UserRelationshipSummaryResponse adaptUserRelationshipToSummary(final UserRelationship relationship) {
        return new UserRelationshipSummaryResponse(relationship.getFollower().getId(), relationship.getFollowing().getId());
    }

    /**
     * Build a {@link UserProfileSummaryResponse} DTO of a {@link User}.
     *
     * @param userId The {@link User} id to be used to build a {@link UserProfileSummaryResponse}.
     * @return The {@link UserProfileSummaryResponse} of the {@link User}.
     * @throws BadRequestException If the {@link User} cannot be found with the specific id.
     * @see UserService#findUserById(Long)
     * */
    public UserProfileSummaryResponse constructProfileSummary(final Long userId) {
        User user = findUserById(userId);
        int bucketCount = bucketService.getBucketCount(userId).intValue();
        return new UserProfileSummaryResponse(adaptUserToSummary(user), userRelationshipDAO.findFollowerCount(user),
                userRelationshipDAO.findFollowingCount(user), bucketCount, user.getCreatedAt());
    }

    /**
     * Get the principal user, and throw an {@link UnauthorizedException} if the {@link User} does not match the given id.
     *
     * @param userId The {@link User} id to verify.
     * @return The principal user, if the {@link User} id matches the given id.
     * @throws UnauthorizedException If the {@link User} does not match the given id.
     * @see UserService#findUserById(Long)
     * */
    private User findPrincipalUser(final Long userId) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User persistentUser = findUserById(userId);

        if(!Objects.equals(currentUser.getId(), persistentUser.getId())) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        return persistentUser;
    }
}
