package ca.unb.ktb.api;

import ca.unb.ktb.api.dto.response.ItemSummaryResponse;
import ca.unb.ktb.api.exception.client.BadRequestException;
import ca.unb.ktb.api.exception.client.UnauthorizedException;
import ca.unb.ktb.core.model.Bucket;
import ca.unb.ktb.core.model.Item;
import ca.unb.ktb.core.svc.BucketService;
import ca.unb.ktb.core.svc.ItemService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class ItemController {

    @Autowired private ItemService itemService;

    @Autowired private BucketService bucketService;

    /**
     * Create a new {@link Item} in a given {@link ca.unb.ktb.core.model.Bucket}.
     *
     * @param ownerId The id of the {@link ca.unb.ktb.core.model.User} that owns the {@link ca.unb.ktb.core.model.Bucket}.
     * @param bucketId The id of the {@link ca.unb.ktb.core.model.Bucket} that owns the {@link Item}.
     * @param item A valid {@link Item} with all necessary fields.
     * @return A summary of the new {@link Item} once persisted in the database.
     * @throws BadRequestException If unable to find a bucket with the given id and owner.
     * @see ItemService#createItem(Item, Long)
     * */
    @ApiOperation(
            value = "Create a new item.",
            response = ItemSummaryResponse.class
    )
    @RequestMapping(
            value = "/users/{ownerId}/buckets/{bucketId}/items",
            method = RequestMethod.POST,
            consumes = "application/json"
    )
    public ResponseEntity<ItemSummaryResponse> createItem(@PathVariable(name = "ownerId") final Long ownerId,
                                                          @PathVariable(name = "bucketId") final Long bucketId,
                                                          @RequestBody final Item item) {
        Bucket existingBucket = bucketService.findBucketById(ownerId);
        if(Objects.equals(ownerId, existingBucket.getOwner().getId())) {
            throw new BadRequestException(String.format("Unable to find bucket with id %d and owner %d.", bucketId, ownerId));
        }

        Item newItem = itemService.createItem(item, bucketId);
        ItemSummaryResponse response = itemService.adaptItemToItemSummary(newItem);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Create a new {@link Item} from an existing item.
     *
     * @param ownerId The id of the {@link ca.unb.ktb.core.model.User} that owns the {@link ca.unb.ktb.core.model.Bucket}.
     * @param toBucket The id of the {@link ca.unb.ktb.core.model.Bucket} that will own the newly created {@link Item}.
     * @param itemId The id of the {@link Item} to be duplicated.
     * @param fromBucket The id of the {@link ca.unb.ktb.core.model.Bucket} that currently owns the {@link Item}.
     * @return A new {@link Item} once persisted in the database.
     * @see ItemService#duplicateItem(Long, Long, Long)
     * */
    @ApiOperation(
            value = "Create a new item from an existing item.",
            response = ItemSummaryResponse.class
    )
    @RequestMapping(
            value = "/users/{id}/buckets/{bucketId}/items/{itemId}",
            method = RequestMethod.POST,
            consumes = "application/json",
            params = {"from"}
    )
    public ResponseEntity<ItemSummaryResponse> duplicateItem(@PathVariable(name = "id") final Long ownerId,
                                                             @PathVariable(name = "bucketId") final Long fromBucket,
                                                             @PathVariable(name = "itemId") final Long itemId,
                                                             @RequestParam(name = "to") final Long toBucket) {
        validateItemURIPath(ownerId, fromBucket, itemId);
        Item newItem = itemService.duplicateItem(itemId, fromBucket, toBucket);
        ItemSummaryResponse response = itemService.adaptItemToItemSummary(newItem);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieve a list of {@link Item}'s associated to a specific {@link ca.unb.ktb.core.model.Bucket}.
     *
     * @param ownerId Id of the {@link ca.unb.ktb.core.model.User} that owns the {@link ca.unb.ktb.core.model.Bucket}s.
     * @param bucketId Id of the {@link ca.unb.ktb.core.model.Bucket} that owns the {@link Item}.
     * @return A list of {@link Item}s associated to a given user.
     * */
    @ApiOperation(
            value = "Retrieve a list of items associated to a specific bucket.",
            response = ItemSummaryResponse.class,
            responseContainer = "List"
    )
    @RequestMapping(
            value = "/users/{userId}/buckets/{bucketId}/items",
            method = RequestMethod.GET
    )
    public ResponseEntity<List<ItemSummaryResponse>> findItems(@PathVariable(name = "userId") final Long ownerId,
                                                               @PathVariable(name = "bucketId") final Long bucketId) {
        Bucket existingBucket = bucketService.findBucketById(ownerId);
        if(Objects.equals(ownerId, existingBucket.getOwner().getId())) {
            throw new BadRequestException(String.format("Unable to find bucket with id %d and owner %d.", bucketId, ownerId));
        }

        List<Item> items = itemService.findItems(bucketId);
        List<ItemSummaryResponse> response = items.parallelStream()
                .map(itemService::adaptItemToItemSummary)
                .collect(Collectors.toList());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieve a specific {@link Item} associated to a specific {@link ca.unb.ktb.core.model.Bucket} and
     * {@link ca.unb.ktb.core.model.User}.
     *
     * If the owner id does not match the id of the principal user, the item is only returned if the parent bucket is
     * public.
     *
     * @param ownerId Id of the {@link ca.unb.ktb.core.model.User} that owns the {@link ca.unb.ktb.core.model.Bucket}s.
     * @param bucketId Id of the {@link ca.unb.ktb.core.model.Bucket} that owns the {@link Item}.
     * @param itemId Id of the {@link Item}.
     * @return {@link Item} associated to a given {@link ca.unb.ktb.core.model.User}. If the user and principal have
     * matching ids, public or private {@link ca.unb.ktb.core.model.Bucket} may be returned, otherwise only returns a
     * public bucket.
     * @see ItemService#findItemById(Long)
     * */
    @ApiOperation(
            value = "Retrieve a specific item associated to a specific bucket and user.",
            response = ItemSummaryResponse.class
    )
    @RequestMapping(
            value = "/users/{ownerId}/buckets/{bucketId}/items/{itemId}",
            method = RequestMethod.GET
    )
    public ResponseEntity<ItemSummaryResponse> findItemById(@PathVariable(name = "ownerId") final Long ownerId,
                                                            @PathVariable(name = "bucketId") final Long bucketId,
                                                            @PathVariable(name = "itemId") final Long itemId) {
        Item item = validateItemURIPath(ownerId, bucketId, itemId);
        ItemSummaryResponse response = itemService.adaptItemToItemSummary(item);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Update fields in a {@link Item} that is currently persisted in the database. Only non-null item fields are updated.
     *
     * @param ownerId Id of the {@link ca.unb.ktb.core.model.User} that owns the {@link ca.unb.ktb.core.model.Bucket}.
     * @param bucketId Id of the {@link ca.unb.ktb.core.model.Bucket} that owns the {@link Item}.
     * @param itemId Id of the {@link Item} to patch.
     * @param item An {@link Item} to patch.
     * @return The patched {@link Item}.
     * @see ItemService#patchItem(Item, Long)
     * */
    @ApiOperation(
            value = "Update fields in an item that is currently persisted in the database.",
            response = ItemSummaryResponse.class
    )
    @RequestMapping(
            value = "/users/{ownerId}/buckets/{bucketId}/items/{itemId}",
            method = RequestMethod.PATCH,
            consumes = "application/json"
    )
    public ResponseEntity<ItemSummaryResponse> patchItem(@PathVariable(name = "ownerId") final Long ownerId,
                                                         @PathVariable(name = "bucketId") final Long bucketId,
                                                         @PathVariable(name = "itemId") final Long itemId,
                                                         @RequestBody final Item item) {
        validateItemURIPath(ownerId, bucketId, itemId);

        Item newItem = itemService.patchItem(item, itemId);
        ItemSummaryResponse response = itemService.adaptItemToItemSummary(newItem);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Completely update a {@link Item} that is currently persisted in the database. All item fields are updated.
     *
     * @param ownerId Id of the {@link ca.unb.ktb.core.model.User} that owns the {@link ca.unb.ktb.core.model.Bucket}.
     * @param bucketId Id of the {@link ca.unb.ktb.core.model.Bucket} that owns the {@link Item}.
     * @param itemId Id of the {@link Item} that will be updated.
     * @param item An {@link Item} to update.
     * @return The updated {@link Item}.
     * */
    @ApiOperation(
            value = "Completely update an item that is currently persisted in the database.",
            response = ItemSummaryResponse.class
    )
    @RequestMapping(
            value = "/users/{ownerId}/buckets/{bucketId}/items/{itemId}",
            method = RequestMethod.PUT,
            consumes = "application/json"
    )
    public ResponseEntity<ItemSummaryResponse> updateItem(@PathVariable(name = "ownerId") final Long ownerId,
                                                          @PathVariable(name = "bucketId") final Long bucketId,
                                                          @PathVariable(name = "itemId") final Long itemId,
                                                          @RequestBody final Item item) {
        validateItemURIPath(ownerId, bucketId, itemId);

        Item newItem = itemService.updateItem(item, itemId);
        ItemSummaryResponse response = itemService.adaptItemToItemSummary(newItem);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Delete an {@link Item}.
     *
     * @param ownerId Id of the {@link ca.unb.ktb.core.model.User} that owns the {@link ca.unb.ktb.core.model.Bucket}.
     * @param bucketId Id of the {@link ca.unb.ktb.core.model.Bucket} that owns the {@link Item}.
     * @param itemId Id of the {@link Item} that will be deleted.
     * @return Empty response.
     * */
    @ApiOperation(value = "Delete an item.")
    @RequestMapping(
            value = "/users/{ownerId}/buckets/{bucketId}/items/{itemId}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<?> deleteItem(@PathVariable(name = "ownerId") final Long ownerId,
                                        @PathVariable(name = "bucketId") final Long bucketId,
                                        @PathVariable(name = "itemId") final Long itemId) {
        validateItemURIPath(ownerId, bucketId, itemId);

        itemService.deleteItem(ownerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     *  Validate an {@link Item} URI path. Ensure that the item with the given id belongs to the given {@link Bucket}
     *  and {@link ca.unb.ktb.core.model.User}.
     *
     * @param ownerId Id of the owner of the {@link Item}.
     * @param bucketId Id of the parent {@link Bucket} of the {@link Item}.
     * @param itemId Id of the {@link Item}.
     * @return The {@link Item}.
     * @throws BadRequestException If no such {@link Item} could be found.
     * */
    private Item validateItemURIPath(final Long ownerId, final Long bucketId, final Long itemId) {
        Bucket existingBucket = bucketService.findBucketById(ownerId);
        if(Objects.equals(ownerId, existingBucket.getOwner().getId())) {
            throw new BadRequestException(String.format("Unable to find bucket with id %d and owner %d.", bucketId, ownerId));
        }

        Item item = itemService.findItemById(itemId);
        if(Objects.equals(item.getParent().getId(), existingBucket.getId())) {
            throw new BadRequestException(String.format("Unable to find item with id %d and parent %d.", itemId, bucketId));
        }

        return item;
    }
}
