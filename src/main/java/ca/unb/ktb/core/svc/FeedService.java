package ca.unb.ktb.core.svc;

import ca.unb.ktb.api.dto.response.BucketSummaryResponse;
import ca.unb.ktb.api.dto.response.ItemSummaryResponse;
import ca.unb.ktb.api.dto.response.UserFeedResponse;
import ca.unb.ktb.api.dto.response.UserSummaryResponse;
import ca.unb.ktb.core.model.Bucket;
import ca.unb.ktb.core.model.Item;
import ca.unb.ktb.core.model.User;
import ca.unb.ktb.core.model.UserBucketRelationship;
import ca.unb.ktb.core.model.UserRelationship;
import ca.unb.ktb.infrastructure.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FeedService {

    @Autowired private UserService userService;

    @Autowired private BucketService bucketService;

    @Autowired private ItemService itemService;

    @Autowired private UserBucketRelationshipService userBucketRelationshipService;

    /**
     * Retrieve a summary of {@link User}s who are followed by the principal user and have recently created
     * {@link Bucket}s.
     *
     * @param pageable Pagination details.
     * @return A {@link UserFeedResponse}.
     * */
    public UserFeedResponse retrieveBucketsRecentlyCreatedByFollowedUsers(final Pageable pageable) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Bucket> buckets = bucketService.findBucketsRecentlyCreatedByFollowedUsers(currentUser.getId(), pageable);
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

        List<UserFeedResponse.UserBucketPair> response = adaptToUserBucketPairList(followedUserBuckets);
        return new UserFeedResponse(null, null, null, null, response, null, null, null);
    }

    /**
     * Retrieve a summary of {@link User}s who are followed by the principal user and have recently created
     * {@link Item}s.
     *
     * @param pageable pagination details.
     * @return A {@link UserFeedResponse}.
     * */
    public UserFeedResponse retrieveItemsRecentlyCreatedByFollowedUsers(final Pageable pageable) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Item> items = itemService.findItemsRecentlyCreatedByFollowedUsers(currentUser.getId(), pageable);
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
                    .map(itemService::adaptItemToItemSummary)
                    .collect(Collectors.toList());

            response.add(new UserFeedResponse.UserItemPair(userSummary, itemSummaryResponses));
        }

        return new UserFeedResponse(null, null, null, null, null, response, null, null);
    }

    /**
     * Retrieve a summary of {@link User}s who are followed by the principal user and have recently followed other users.
     *
     * @param pageable pagination details.
     * @return A {@link UserFeedResponse}.
     * */
    public UserFeedResponse retrieveUsersRecentlyFollowedByFollowedUsers(final Pageable pageable) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<UserRelationship> relationships = userService.findUsersRecentlyFollowedByFollowedUsers(currentUser.getId(), pageable);
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
     * Retrieve a summary of {@link User}s who are followed by the principal user and have recently followed
     * other {@link Bucket}s.
     *
     * @param pageable pagination details.
     * @return A {@link UserFeedResponse}.
     * */
    public UserFeedResponse retrieveBucketsRecentlyFollowedByFollowedUsers(final Pageable pageable) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<UserBucketRelationship> relationships =
                userBucketRelationshipService.findBucketsRecentlyFollowedByFollowedUsers(currentUser.getId(), pageable);
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

        List<UserFeedResponse.UserBucketPair> response = adaptToUserBucketPairList(followedUserNewBucketRelationships);
        return new UserFeedResponse(null, null, null, null, null, null, null, response);
    }

    /**
     * Retrieve a summary of {@link Bucket}s recently created by the principal user.
     *
     * @param pageable pagination details.
     * @return A {@link UserFeedResponse}.
     * */
    public UserFeedResponse retrieveBucketsRecentlyCreatedByUser(final Pageable pageable) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Bucket> buckets = bucketService.findBucketsRecentlyCreatedByUser(currentUser.getId(), pageable);
        List<BucketSummaryResponse> bucketSummaries = buckets.stream()
                .map(bucketService::adaptBucketToBucketSummary)
                .collect(Collectors.toList());

        return new UserFeedResponse(bucketSummaries, null, null, null, null, null, null, null);
    }

    /**
     * Retrieve a summary of {@link Item}s recently created by the principal user.
     *
     * @param pageable pagination details.
     * @return A {@link UserFeedResponse}.
     * */
    public UserFeedResponse retrieveItemsRecentlyCreatedByUser(final Pageable pageable) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Item> items = itemService.findItemsRecentlyCreatedByUser(currentUser.getId(), pageable);
        List<ItemSummaryResponse> itemSummaryResponses = items.stream()
                .map(itemService::adaptItemToItemSummary)
                .collect(Collectors.toList());

        return new UserFeedResponse(null, itemSummaryResponses, null, null, null, null, null, null);
    }

    /**
     * Retrieve a summary of {@link User}s recently followed by the principal user.
     *
     * @param pageable pagination details.
     * @return A {@link UserFeedResponse}.
     * */
    public UserFeedResponse retrieveUsersRecentlyFollowedByUser(final Pageable pageable) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<UserRelationship> relationships = userService.findUsersRecentlyFollowedByUser(currentUser.getId(), pageable);
        List<UserSummaryResponse> userSummaryResponses = relationships.stream()
                .map(r -> userService.adaptUserToSummary(r.getFollowing()))
                .collect(Collectors.toList());

        return new UserFeedResponse(null, null, userSummaryResponses, null, null, null, null, null);
    }

    /**
     * Retrieve a summary of {@link Bucket}s recently followed by the principal user.
     *
     * @param pageable pagination details.
     * @return A {@link UserFeedResponse}.
     * */
    public UserFeedResponse retrieveBucketsRecentlyFollowedByUser(final Pageable pageable) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<UserBucketRelationship> relationships = userBucketRelationshipService.findBucketsRecentlyFollowedByUser(currentUser.getId(), pageable);
        List<BucketSummaryResponse> bucketSummaryResponses = relationships.stream()
                .map(r -> bucketService.adaptBucketToBucketSummary(r.getFollowing()))
                .collect(Collectors.toList());

        return new UserFeedResponse(null, null, null, bucketSummaryResponses, null, null, null, null);
    }

    /**
     * Adapt the map of {@link User}-{@link List<Bucket>} entries into a list of {@link UserFeedResponse.UserBucketPair}s.
     *
     * @param userBucketsMap a {@link Map} of {@link User}-{@link List<Bucket>} entries
     * @return a list of {@link UserFeedResponse.UserBucketPair}s.
     * */
    private List<UserFeedResponse.UserBucketPair> adaptToUserBucketPairList(final Map<User, List<Bucket>> userBucketsMap) {
        List<UserFeedResponse.UserBucketPair> response = new ArrayList<>();
        for(Map.Entry<User, List<Bucket>> entry : userBucketsMap.entrySet()) {
            UserSummaryResponse followerSummary = userService.adaptUserToSummary(entry.getKey());
            List<BucketSummaryResponse> followingSummaries = entry.getValue().stream()
                    .map(bucketService::adaptBucketToBucketSummary)
                    .collect(Collectors.toList());

            response.add(new UserFeedResponse.UserBucketPair(followerSummary, followingSummaries));
        }

        return response;
    }
}
