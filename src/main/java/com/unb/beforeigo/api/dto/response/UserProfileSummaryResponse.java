package com.unb.beforeigo.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
public class UserProfileSummaryResponse implements Serializable {

    private final UserSummaryResponse user;

    private final int followerCount;

    private final int followingCount;

    private final int publicBucketCount;

    private final int privateBucketCount;

    private final Date joined;
}
