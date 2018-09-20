package com.unb.beforeigo.api.dto.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class ItemSummaryResponse implements Serializable {

    private final Long id;

    private final Long parentId;

    private final String name;

    private final String link;

    private final String description;
}