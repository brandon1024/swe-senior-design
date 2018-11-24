package com.unb.beforeigo.api.dto.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserBucketRelationshipSummaryResponse implements Serializable {

    public final Long initiatorId;

    public final Long bucketId;
}
