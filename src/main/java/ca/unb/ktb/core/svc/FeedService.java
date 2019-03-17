package ca.unb.ktb.core.svc;

import ca.unb.ktb.api.dto.response.BucketSummaryResponse;
import ca.unb.ktb.api.dto.response.ItemSummaryResponse;
import ca.unb.ktb.api.dto.response.UserFeedResponse;
import ca.unb.ktb.api.dto.response.UserSummaryResponse;
import ca.unb.ktb.api.exception.client.BadRequestException;
import ca.unb.ktb.application.dao.BucketDAO;
import ca.unb.ktb.application.dao.ItemDAO;
import ca.unb.ktb.application.dao.UserBucketRelationshipDAO;
import ca.unb.ktb.application.dao.UserDAO;
import ca.unb.ktb.application.dao.UserRelationshipDAO;
import ca.unb.ktb.core.model.Bucket;
import ca.unb.ktb.core.model.Item;
import ca.unb.ktb.core.model.User;
import ca.unb.ktb.core.model.UserBucketRelationship;
import ca.unb.ktb.core.model.UserRelationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FeedService {

    @Autowired private UserDAO userDAO;

    @Autowired private BucketDAO bucketDAO;

    @Autowired private ItemDAO itemDAO;

    @Autowired private UserRelationshipDAO userRelationshipDAO;

    @Autowired private UserBucketRelationshipDAO userBucketRelationshipDAO;

    @Autowired private UserService userService;

    /**
     * Retrieve a map of users who are followed by the user with the given user id and have recently created buckets.
     *
     * @param userId the id of the current user.
     * @param pageable pagination details.
     * */
    public UserFeedResponse retrieveBucketsCreatedByFollowedUsers(final Long userId,
                                                                  final Pageable pageable) {
        userDAO.findById(userId)
                .orElseThrow(() -> new BadRequestException("Unable to find user with id " + userId));

        List<Bucket> buckets = bucketDAO.retrieveBucketsRecentlyCreatedByFollowedUsers(userId, pageable);
        Map<User, List<Bucket>> followedUserBuckets = new HashMap<>();
        for(Bucket bucket : buckets) {
            User bucketOwner = bucket.getOwner();

            if(followedUserBuckets.containsKey(bucketOwner)) {
                followedUserBuckets.get(bucketOwner).add(bucket);
            } else {
                List<Bucket> userBuckets = new ArrayList<>();
                userBuckets.add(bucket);
                followedUserBuckets.put(bucketOwner, userBuckets);
            }
        }

        List<UserFeedResponse.UserBucketPair> response = new ArrayList<>();
        for(Map.Entry<User, List<Bucket>> entry : followedUserBuckets.entrySet()) {
            UserSummaryResponse userSummary = userService.adaptUserToSummary(entry.getKey());
            List<BucketSummaryResponse> bucketSummaryResponses = entry.getValue().stream()
                    .map(BucketService::adaptBucketToBucketSummary)
                    .collect(Collectors.toList());

            response.add(new UserFeedResponse.UserBucketPair(userSummary, bucketSummaryResponses));
        }

        return new UserFeedResponse(null, null, null, null, response, null, null, null);
    }

    /**
     * Retrieve a map of users who are followed by the user with the given user id and have recently created items.
     *
     * @param userId the id of the current user.
     * @param pageable pagination details.
     * */
    public UserFeedResponse retrieveItemsCreatedByFollowedUsers(final Long userId, final Pageable pageable) {
        userDAO.findById(userId)
                .orElseThrow(() -> new BadRequestException("Unable to find user with id " + userId));

        List<Item> items = itemDAO.retrieveItemsCreatedByFollowedUsers(userId, pageable);
        Map<User, List<Item>> followedUserItems = new HashMap<>();
        for(Item item : items) {
            User itemOwner = item.getParent().getOwner();

            if(followedUserItems.containsKey(itemOwner)) {
                followedUserItems.get(itemOwner).add(item);
            } else {
                List<Item> userItems = new ArrayList<>();
                userItems.add(item);
                followedUserItems.put(itemOwner, userItems);
            }
        }

        List<UserFeedResponse.UserItemPair> response = new ArrayList<>();
        for(Map.Entry<User, List<Item>> entry : followedUserItems.entrySet()) {
            UserSummaryResponse userSummary = userService.adaptUserToSummary(entry.getKey());
            List<ItemSummaryResponse> itemSummaryResponses = entry.getValue().stream()
                    .map(ItemService::adaptItemToItemSummary)
                    .collect(Collectors.toList());

            response.add(new UserFeedResponse.UserItemPair(userSummary, itemSummaryResponses));
        }

        return new UserFeedResponse(null, null, null, null, null, response, null, null);
    }

    /**
     * Retrieve a map of users who are followed by the user with the given user id and have recently followed other users.
     *
     * @param userId the id of the current user.
     * @param pageable pagination details.
     * */
    public UserFeedResponse retrieveUsersFollowedByFollowedUsers(final Long userId, final Pageable pageable) {
        userDAO.findById(userId)
                .orElseThrow(() -> new BadRequestException("Unable to find user with id " + userId));

        List<UserRelationship> relationships = userRelationshipDAO.retrieveUsersFollowedByFollowedUsers(userId, pageable);
        Map<User, List<User>> followedUserNewRelationships = new HashMap<>();
        for(UserRelationship relationship : relationships) {
            User follower = relationship.getFollower();
            User following = relationship.getFollowing();

            if(followedUserNewRelationships.containsKey(follower)) {
                followedUserNewRelationships.get(follower).add(following);
            } else {
                List<User> users = new ArrayList<>();
                users.add(following);
                followedUserNewRelationships.put(follower, users);
            }
        }

        List<UserFeedResponse.UserUserPair> response = new ArrayList<>();
        for(Map.Entry<User, List<User>> entry : followedUserNewRelationships.entrySet()) {
            UserSummaryResponse followerSummary = userService.adaptUserToSummary(entry.getKey());
            List<UserSummaryResponse> followingSummaries = entry.getValue().stream()
                    .map(userService::adaptUserToSummary)
                    .collect(Collectors.toList());

            response.add(new UserFeedResponse.UserUserPair(followerSummary, followingSummaries));
        }

        return new UserFeedResponse(null, null, null, null, null, null, response, null);
    }

    /**
     * Retrieve a map of users who are followed by the user with the given user id and have recently followed other buckets.
     *
     * @param userId the id of the current user.
     * @param pageable pagination details.
     * */
    public UserFeedResponse retrieveBucketsFollowedByFollowedUsers(final Long userId, final Pageable pageable) {
        userDAO.findById(userId)
                .orElseThrow(() -> new BadRequestException("Unable to find user with id " + userId));

        List<UserBucketRelationship> relationships =
                userBucketRelationshipDAO.retrieveBucketsFollowedByFollowedUsers(userId, pageable);
        Map<User, List<Bucket>> followedUserNewBucketRelationships = new HashMap<>();
        for(UserBucketRelationship relationship : relationships) {
            User follower = relationship.getFollower();
            Bucket following = relationship.getFollowing();

            if(followedUserNewBucketRelationships.containsKey(follower)) {
                followedUserNewBucketRelationships.get(follower).add(following);
            } else {
                List<Bucket> buckets = new ArrayList<>();
                buckets.add(following);
                followedUserNewBucketRelationships.put(follower, buckets);
            }
        }

        List<UserFeedResponse.UserBucketPair> response = new ArrayList<>();
        for(Map.Entry<User, List<Bucket>> entry : followedUserNewBucketRelationships.entrySet()) {
            UserSummaryResponse followerSummary = userService.adaptUserToSummary(entry.getKey());
            List<BucketSummaryResponse> followingSummaries = entry.getValue().stream()
                    .map(BucketService::adaptBucketToBucketSummary)
                    .collect(Collectors.toList());

            response.add(new UserFeedResponse.UserBucketPair(followerSummary, followingSummaries));
        }

        return new UserFeedResponse(null, null, null, null, null, null, null, response);
    }

    /**
     * Retrieve a list of buckets recently created by the user with the given user id.
     *
     * @param userId the id of the current user.
     * @param pageable pagination details.
     * */
    public UserFeedResponse retrieveBucketsCreatedByUser(final Long userId, final Pageable pageable) {
        userDAO.findById(userId)
                .orElseThrow(() -> new BadRequestException("Unable to find user with id " + userId));

        List<Bucket> buckets = bucketDAO.retrieveBucketsCreatedByUser(userId, pageable);
        List<BucketSummaryResponse> bucketSummaries = buckets.stream()
                .map(BucketService::adaptBucketToBucketSummary)
                .collect(Collectors.toList());

        return new UserFeedResponse(bucketSummaries, null, null, null, null, null, null, null);
    }

    /**
     * Retrieve a list of items recently created by the user with the given user id.
     *
     * @param userId the id of the current user.
     * @param pageable pagination details.
     * */
    public UserFeedResponse retrieveItemsCreatedByUser(final Long userId, final Pageable pageable) {
        userDAO.findById(userId)
                .orElseThrow(() -> new BadRequestException("Unable to find user with id " + userId));

        List<Item> items = itemDAO.retrieveItemsCreatedByUser(userId, pageable);
        List<ItemSummaryResponse> itemSummaryResponses = items.stream()
                .map(ItemService::adaptItemToItemSummary)
                .collect(Collectors.toList());

        return new UserFeedResponse(null, itemSummaryResponses, null, null, null, null, null, null);
    }

    /**
     * Retrieve a list of users recently followed by the user with the given user id.
     *
     * @param userId the id of the current user.
     * @param pageable pagination details.
     * */
    public UserFeedResponse retrieveUsersFollowedByUser(final Long userId, final Pageable pageable) {
        userDAO.findById(userId)
                .orElseThrow(() -> new BadRequestException("Unable to find user with id " + userId));

        List<UserRelationship> relationships = userRelationshipDAO.retrieveUsersFollowedByUser(userId, pageable);
        List<UserSummaryResponse> userSummaryResponses = relationships.stream()
                .map(r -> userService.adaptUserToSummary(r.getFollowing()))
                .collect(Collectors.toList());

        return new UserFeedResponse(null, null, userSummaryResponses, null, null, null, null, null);
    }

    /**
     * Retrieve a list of buckets recently followed by the user with the given user id.
     *
     * @param userId the id of the current user.
     * @param pageable pagination details.
     * */
    public UserFeedResponse retrieveBucketsFollowedByUser(final Long userId, final Pageable pageable) {
        userDAO.findById(userId)
                .orElseThrow(() -> new BadRequestException("Unable to find user with id " + userId));

        List<UserBucketRelationship> relationships = userBucketRelationshipDAO.retrieveBucketsFollowedByUser(userId, pageable);
        List<BucketSummaryResponse> bucketSummaryResponses = relationships.stream()
                .map(r -> BucketService.adaptBucketToBucketSummary(r.getFollowing()))
                .collect(Collectors.toList());

        return new UserFeedResponse(null, null, null, bucketSummaryResponses, null, null, null, null);
    }
}
