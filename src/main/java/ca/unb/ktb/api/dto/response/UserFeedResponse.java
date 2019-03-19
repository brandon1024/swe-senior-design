package ca.unb.ktb.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserFeedResponse implements Serializable {

    private final List<BucketSummaryResponse> userRecentlyCreatedBuckets;

    private final List<ItemSummaryResponse> userRecentlyCreatedItems;

    private final List<UserSummaryResponse> userRecentlyFollowedUsers;

    private final List<BucketSummaryResponse> userRecentlyFollowedBuckets;

    private final List<UserBucketPair> followedUsersRecentlyCreatedBuckets;

    private final List<UserItemPair> followedUsersRecentlyCreatedItems;

    private final List<UserUserPair> followedUsersRecentlyFollowedUsers;

    private final List<UserBucketPair> followedUsersRecentlyFollowedBuckets;

    @Data
    @AllArgsConstructor
    public static class UserBucketPair implements Serializable {

        private final UserSummaryResponse user;

        private final List<BucketSummaryResponse> buckets;
    }

    @Data
    @AllArgsConstructor
    public static class UserItemPair implements Serializable {

        private final UserSummaryResponse user;

        private final List<ItemSummaryResponse> items;
    }

    @Data
    @AllArgsConstructor
    public static class UserUserPair implements Serializable {

        private final UserSummaryResponse user;

        private final List<UserSummaryResponse> users;
    }
}
