package com.unb.beforeigo.api.dto.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRelationshipSummaryResponse implements Serializable {

    final Long initiatorId;

    final Long subjectId;
}
