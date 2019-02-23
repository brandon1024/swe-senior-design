package ca.unb.ktb.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ItemSummaryResponse implements Serializable {

    private final Long id;

    private final Long parentId;

    private final String name;

    private final String link;

    private final String description;

    private final boolean isComplete;
}
