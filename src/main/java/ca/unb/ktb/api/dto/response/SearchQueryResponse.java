package ca.unb.ktb.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SearchQueryResponse {

    private final List<UserSummaryResponse> users;

    private final List<BucketSummaryResponse> buckets;

    private final List<ItemSummaryResponse> items;
}
