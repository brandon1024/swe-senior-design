package com.unb.beforeigo.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class UserBucketRelationshipSummaryResponse implements Serializable {

    public final Long initiatorId;

    public final Long bucketId;
}
