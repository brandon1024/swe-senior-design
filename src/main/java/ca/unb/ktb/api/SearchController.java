package ca.unb.ktb.api;

import ca.unb.ktb.api.dto.response.BucketSummaryResponse;
import ca.unb.ktb.api.dto.response.ItemSummaryResponse;
import ca.unb.ktb.api.dto.response.SearchQueryResponse;
import ca.unb.ktb.api.dto.response.UserSummaryResponse;
import ca.unb.ktb.core.model.Bucket;
import ca.unb.ktb.core.model.Item;
import ca.unb.ktb.core.model.User;
import ca.unb.ktb.core.svc.BucketService;
import ca.unb.ktb.core.svc.ItemService;
import ca.unb.ktb.core.svc.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction;

@RestController
@RequestMapping("/")
public class SearchController {

    @Autowired private BucketService bucketService;

    @Autowired private UserService userService;

    @Autowired private ItemService itemService;

    /**
     * Search for {@link ca.unb.ktb.core.model.User}s, {@link ca.unb.ktb.core.model.Bucket}s and
     * {@link ca.unb.ktb.core.model.Item}s by search query, returning at most 10 results of each type.
     *
     * @param query The search query string.
     * @param page The page number of the search.
     * @param size How many {@link ca.unb.ktb.core.model.Item}s to be displayed per page.
     * @param userSort What value to sort the {@link ca.unb.ktb.core.model.User} results by.
     * @param bucketSort What value to sort the {@link ca.unb.ktb.core.model.Bucket} results by.
     * @param itemSort What value to sort the {@link ca.unb.ktb.core.model.Item} results by.
     * @return A {@link SearchQueryResponse} containing the search results.
     * */
    @ApiOperation(
            value = "Search for users, buckets, and items by query string.",
            response = SearchQueryResponse.class
    )
    @RequestMapping(
            value = "/search",
            method = RequestMethod.GET
    )
    public ResponseEntity<SearchQueryResponse> search(
            @Size(min = 3, message = "Search query must be at least 3 characters in length")
            @RequestParam(name = "query") final String query,
            @RequestParam(name = "page", defaultValue = "0", required = false) final Integer page,
            @RequestParam(name = "size", defaultValue = "20", required = false) final Integer size,
            @RequestParam(name = "userSort", defaultValue = "username", required = false) final String userSort,
            @RequestParam(name = "bucketSort", defaultValue = "name", required = false) final String bucketSort,
            @RequestParam(name = "itemSort", defaultValue = "name", required = false) final String itemSort) {
        List<User> users =
                userService.findUsersByUsernameOrRealName(query, PageRequest.of(page, size, Direction.ASC, userSort));
        List<Bucket> buckets =
                bucketService.findBucketsByName(query, PageRequest.of(page, size, Direction.ASC, bucketSort));
        List<Item> items =
                itemService.findItemsByName(query, PageRequest.of(page, size, Direction.ASC, itemSort));

        /* Adapt to DTOs */
        List<UserSummaryResponse> usersResponse = users.parallelStream()
                .map(userService::adaptUserToSummary)
                .collect(Collectors.toList());

        List<BucketSummaryResponse> bucketsResponse = buckets.parallelStream()
                .map(bucketService::adaptBucketToBucketSummary)
                .collect(Collectors.toList());

        List<ItemSummaryResponse> itemsResponse = items.parallelStream()
                .map(itemService::adaptItemToItemSummary)
                .collect(Collectors.toList());

        return new ResponseEntity<>(new SearchQueryResponse(usersResponse, bucketsResponse, itemsResponse), HttpStatus.OK);
    }
}
