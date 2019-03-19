package ca.unb.ktb.api;

import ca.unb.ktb.api.dto.response.ItemSummaryResponse;
import ca.unb.ktb.api.exception.client.UnauthorizedException;
import ca.unb.ktb.core.model.Item;
import ca.unb.ktb.core.svc.ItemService;
import ca.unb.ktb.infrastructure.security.UserPrincipal;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/")
@Slf4j
public class ItemController {

    @Autowired private ItemService itemService;

    /**
     * Create a new {@link Item}.
     *
     * @param ownerId The id of the user that owns the bucket.
     * @param bucketId The id of the bucket that owns the item.
     * @param item A valid item with all necessary fields.
     * @param auth The authentication token.
     * @return A new item once persisted in the database. HTTP CREATED.
     * @throws UnauthorizedException If the authenticated principal user does not match the user in the path variable.
     * */
    @ApiOperation(value = "Create a new item.", response = ItemSummaryResponse.class)
    @RequestMapping(value = "/users/{ownerId}/buckets/{bucketId}/items",
            method = RequestMethod.POST,
            consumes = "application/json")
    public ResponseEntity<ItemSummaryResponse> createItem(@PathVariable(name = "ownerId") final Long ownerId,
                                                          @PathVariable(name = "bucketId") final Long bucketId,
                                                          @RequestBody final Item item,
                                                          @AuthenticationPrincipal final Authentication auth) {
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
        if(!Objects.equals(currentUser.getId(), ownerId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        ItemSummaryResponse response = itemService.createItem(bucketId, item);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieve a list of {@link Item}'s associated to a specific bucket.
     *
     * @param ownerId Id of the user that owns the buckets.
     * @param bucketId Id of the bucket.
     * @param auth The authentication token.
     * @return A list of buckets associated to a given user. HTTP OK.
     * */
    @ApiOperation(value = "Retrieve a list of items associated to a specific bucket.",
            response = ItemSummaryResponse.class,
            responseContainer = "List")
    @RequestMapping(value = "/users/{userId}/buckets/{bucketId}/items", method = RequestMethod.GET)
    public ResponseEntity<List<ItemSummaryResponse>> findItems(@PathVariable(name = "userId") final Long ownerId,
                                                               @PathVariable(name = "bucketId") final Long bucketId,
                                                               @AuthenticationPrincipal final Authentication auth) {
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
        List<ItemSummaryResponse> response = itemService.findItems(bucketId, !Objects.equals(currentUser.getId(), ownerId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieve a specific {@link Item} associated to a specific bucket and user.
     *
     * If the owner id does not match the id of the principal user, the item is only returned if public.
     *
     * @param ownerId Id of the user that owns the buckets.
     * @param bucketId Id of the bucket.
     * @param itemId Id of the item.
     * @param auth The authentication token.
     * @return Item associated to a given user. If the user and principal have matching ids, public or private bucket
     * may be returned, otherwise only returns a public bucket. HTTP OK.
     * @see ItemService#findItemById(Long, Long, boolean)
     * */
    @ApiOperation(value = "Retrieve a specific item associated to a specific bucket and user.",
            response = ItemSummaryResponse.class)
    @RequestMapping(value = "/users/{ownerId}/buckets/{bucketId}/items/{itemId}", method = RequestMethod.GET)
    public ResponseEntity<ItemSummaryResponse> findItemById(@PathVariable(name = "ownerId") final Long ownerId,
                                                            @PathVariable(name = "bucketId") final Long bucketId,
                                                            @PathVariable(name = "itemId") final Long itemId,
                                                            @AuthenticationPrincipal final Authentication auth) {
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
        ItemSummaryResponse response = itemService.findItemById(bucketId, itemId, !Objects.equals(currentUser.getId(), ownerId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Update fields in a {@link Item} that is currently persisted in the database. Only non-null bucket fields are updated.
     *
     * @param ownerId Id of the user that owns the bucket.
     * @param bucketId Id of the bucket that owns the item.
     * @param itemId Id of the item to patch.
     * @param item An item to patch.
     * @param auth The authentication token.
     * @return The patched item. HTTP OK.
     * @throws UnauthorizedException If the authenticated principal user does not match the user in the path variable.
     * */
    @ApiOperation(value = "Update fields in an item that is currently persisted in the database.",
            response = ItemSummaryResponse.class)
    @RequestMapping(value = "/users/{ownerId}/buckets/{bucketId}/items/{itemId}",
            method = RequestMethod.PATCH,
            consumes = "application/json")
    public ResponseEntity<ItemSummaryResponse> patchItem(@PathVariable(name = "ownerId") final Long ownerId,
                                                         @PathVariable(name = "bucketId") final Long bucketId,
                                                         @PathVariable(name = "itemId") final Long itemId,
                                                         @RequestBody final Item item,
                                                         @AuthenticationPrincipal final Authentication auth) {
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
        if(!Objects.equals(currentUser.getId(), ownerId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        ItemSummaryResponse response = itemService.patchItem(item, itemId, bucketId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Completely update a {@link Item} that is currently persisted in the database. All item fields are updated.
     *
     * @param ownerId Id of the user that owns the bucket.
     * @param bucketId Id of the bucket that will be updated.
     * @param itemId Id of the item that will be updated.
     * @param item An item to update.
     * @param auth The authentication token.
     * @return The updated item. HTTP OK.
     * @throws UnauthorizedException If the authenticated principal user does not match the user in the path variable.
     * */
    @ApiOperation(value = "Completely update an item that is currently persisted in the database.",
            response = ItemSummaryResponse.class)
    @RequestMapping(value = "/users/{ownerId}/buckets/{bucketId}/items/{itemId}",
            method = RequestMethod.PUT,
            consumes = "application/json")
    public ResponseEntity<ItemSummaryResponse> updateItem(@PathVariable(name = "ownerId") final Long ownerId,
                                                          @PathVariable(name = "bucketId") final Long bucketId,
                                                          @PathVariable(name = "itemId") final Long itemId,
                                                          @RequestBody final Item item,
                                                          @AuthenticationPrincipal final Authentication auth) {
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
        if(!Objects.equals(currentUser.getId(), ownerId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        ItemSummaryResponse response = itemService.updateItem(item, itemId, bucketId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Delete an item.
     *
     * @param ownerId Id of the user that owns the bucket.
     * @param bucketId Id of the bucket that owns the item.
     * @param itemId Id of the item that will be deleted.
     * @param auth The authentication token.
     * @return HTTP OK.
     * @throws UnauthorizedException If the authenticated principal user does not match the user in the path variable.
     * */
    @ApiOperation(value = "Delete an item.")
    @RequestMapping(value = "/users/{ownerId}/buckets/{bucketId}/items/{itemId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteItem(@PathVariable(name = "ownerId") final Long ownerId,
                                        @PathVariable(name = "bucketId") final Long bucketId,
                                        @PathVariable(name = "itemId") final Long itemId,
                                        @AuthenticationPrincipal final Authentication auth) {
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
        if(!Objects.equals(currentUser.getId(), ownerId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        itemService.deleteItem(ownerId, bucketId, itemId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Create a new {@link Item} from an existing item.
     *
     * @param ownerId The id of the user that owns the bucket.
     * @param toBucket The id of the bucket that will own the newly created item.
     * @param itemId The id of the item to be duplicated.
     * @param fromBucket The id of the bucket that currently owns the bucket.
     * @param auth The authentication token.
     * @return A new item once persisted in the database. HTTP OK.
     * @throws UnauthorizedException If the id of the currently authenticated user does not match the path variable id.
     * @see ItemService#duplicateItem(Long, Long, Long, Long)
     * */
    @ApiOperation(value = "Create a new item from an existing item.", response = ItemSummaryResponse.class)
    @RequestMapping(value = "/users/{id}/buckets/{bucketId}/items/{itemId}",
            method = RequestMethod.POST,
            consumes = "application/json",
            params = {"from"})
    public ResponseEntity<ItemSummaryResponse> duplicateItem(@PathVariable(name = "id") final Long ownerId,
                                                             @PathVariable(name = "bucketId") final Long toBucket,
                                                             @PathVariable(name = "itemId") final Long itemId,
                                                             @RequestParam(name = "from") final Long fromBucket,
                                                             @AuthenticationPrincipal final Authentication auth) {
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
        if(!Objects.equals(currentUser.getId(), ownerId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        ItemSummaryResponse response = itemService.duplicateItem(ownerId, itemId, fromBucket, toBucket);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
