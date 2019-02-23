package ca.unb.ktb.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class UserRelationshipSummaryResponse implements Serializable {

    final Long initiatorId;

    final Long subjectId;
}
