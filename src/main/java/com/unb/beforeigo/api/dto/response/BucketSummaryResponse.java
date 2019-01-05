package com.unb.beforeigo.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class BucketSummaryResponse implements Serializable {

    private final Long id;

    private final Long ownerId;

    private final String name;

    private final Boolean isPublic;

    private final String description;
}
