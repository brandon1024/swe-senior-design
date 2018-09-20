package com.unb.beforeigo.api;

import com.unb.beforeigo.api.dto.response.ItemSummaryResponse;
import com.unb.beforeigo.api.exception.client.BadRequestException;
import com.unb.beforeigo.api.exception.client.UnauthorizedException;
import com.unb.beforeigo.core.model.Bucket;
import com.unb.beforeigo.core.model.Item;
import com.unb.beforeigo.core.svc.ItemService;
import com.unb.beforeigo.infrastructure.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
     * @param ownerId The id of the user that owns the bucket
     * @param bucketId The id of the bucket that owns the item
     * @param item A valid item with all necessary fields
     * @return a new item once persisted in the database
     * @throws UnauthorizedException if the authenticated principal user does not match the user in the path variable.
     * */
    @RequestMapping(value = "/users/{ownerId}/buckets/{bucketId}/items", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<ItemSummaryResponse> createItem(@PathVariable(name = "ownerId") final Long ownerId,
                                                          @PathVariable(name = "bucketId") final Long bucketId,
                                                          @RequestBody final Item item,
                                                          @AuthenticationPrincipal final UserPrincipal currentUser) {
        if(!Objects.equals(currentUser.getId(), ownerId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        ItemSummaryResponse response = itemService.createItem(bucketId, item);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieve a list of {@link Item}'s associated to a specific bucket.
     *
     * @param ownerId id of the user that owns the buckets.
     * @return a list of buckets associated to a given user
     * */
    @RequestMapping(value = "/users/{userId}/buckets/{bucketId}/items", method = RequestMethod.GET)
    public ResponseEntity<List<ItemSummaryResponse>> findItems(@PathVariable(name = "userId") final Long ownerId,
                                                               @PathVariable(name = "bucketId") final Long bucketId,
                                                               @AuthenticationPrincipal final UserPrincipal currentUser) {
        List<ItemSummaryResponse> response = itemService.findItems(bucketId, !Objects.equals(currentUser.getId(), ownerId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieve a specific {@link Item} associated to a specific bucket and user.
     *
     * If the owner id does not match the id of the principal user, the item is only returned if public.
     *
     * @param ownerId id of the user that owns the buckets.
     * @param bucketId id of the bucket
     * @param itemId id of the item
     * @return item associated to a given user. If the user and principal have matching ids, public or private bucket
     * may be returned, otherwise only returns a public bucket
     * @see ItemService#findItemById(Long, Long, boolean)
     * */
    @RequestMapping(value = "/users/{ownerId}/buckets/{bucketId}/items/{itemId}", method = RequestMethod.GET)
    public ResponseEntity<ItemSummaryResponse> findItemById(@PathVariable(name = "ownerId") final Long ownerId,
                                                            @PathVariable(name = "bucketId") final Long bucketId,
                                                            @PathVariable(name = "itemId") final Long itemId,
                                                            @AuthenticationPrincipal final UserPrincipal currentUser) {
        ItemSummaryResponse response = itemService.findItemById(bucketId, itemId, !Objects.equals(currentUser.getId(), ownerId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Update fields in a {@link Bucket} that is currently persisted in the database. Only non-null bucket fields are updated.
     *
     * @param ownerId id of the user that owns the bucket
     * @param bucketId id of the bucket that will be patched
     * @param item An item to patch
     * @return the patched item
     * @throws UnauthorizedException if the authenticated principal user does not match the user in the path variable.
     * */
    @RequestMapping(value = "/users/{ownerId}/buckets/{bucketId}/items/{itemId}", method = RequestMethod.PATCH, consumes = "application/json")
    public ResponseEntity<ItemSummaryResponse> patchItem(@PathVariable(name = "ownerId") final Long ownerId,
                                                         @PathVariable(name = "bucketId") final Long bucketId,
                                                         @PathVariable(name = "itemId") final Long itemId,
                                                         @RequestBody final Item item,
                                                         @AuthenticationPrincipal final UserPrincipal currentUser) {
        if(!Objects.equals(currentUser.getId(), ownerId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        ItemSummaryResponse response = itemService.patchItem(item, itemId, bucketId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Completely update a {@link Item} that is currently persisted in the database. All item fields are updated.
     *
     * @param ownerId id of the user that owns the bucket
     * @param bucketId id of the bucket that will be updated
     * @param itemId id of the item that will be updated
     * @param item An item to update
     * @return the updated item
     * @throws UnauthorizedException if the authenticated principal user does not match the user in the path variable.
     * */
    @RequestMapping(value = "/users/{ownerId}/buckets/{bucketId}/items/{itemId}", method = RequestMethod.PUT, consumes = "application/json")
    public ResponseEntity<ItemSummaryResponse> updateItem(@PathVariable(name = "ownerId") final Long ownerId,
                                                          @PathVariable(name = "bucketId") final Long bucketId,
                                                          @PathVariable(name = "itemId") final Long itemId,
                                                          @RequestBody final Item item,
                                                          @AuthenticationPrincipal final UserPrincipal currentUser) {
        if(!Objects.equals(currentUser.getId(), ownerId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        ItemSummaryResponse response = itemService.updateItem(item, itemId, bucketId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Delete an item.
     *
     * @param ownerId id of the user that owns the bucket
     * @param bucketId id of the bucket that owns the item
     * @param itemId id of the item that will be deleted
     * @throws UnauthorizedException if the authenticated principal user does not match the user in the path variable.
     * */
    @RequestMapping(value = "/users/{ownerId}/buckets/{bucketId}/items/{itemId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteItem(@PathVariable(name = "ownerId") final Long ownerId,
                                        @PathVariable(name = "bucketId") final Long bucketId,
                                        @PathVariable(name = "itemId") final Long itemId,
                                        @AuthenticationPrincipal final UserPrincipal currentUser) {
        if(!Objects.equals(currentUser.getId(), ownerId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        itemService.deleteItem(ownerId, bucketId, itemId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Create a new {@link Item} from an existing item.
     *
     * @param ownerId The id of the user that owns the bucket
     * @param toBucket The id of the bucket that will own the newly created item.
     * @param itemId The id of the item to be duplicated
     * @param fromBucket The id of the bucket that currently owns the bucket
     * @return a new item once persisted in the database
     * @throws UnauthorizedException if the id of the currently authenticated user does not match the path variable id
     * @see ItemService#duplicateItem(Long, Long, Long, Long)
     * */
    @RequestMapping(value = "/users/{id}/buckets/{bucketId}/items/{itemId}", method = RequestMethod.POST, consumes = "application/json", params = {"from"})
    public ResponseEntity<ItemSummaryResponse> duplicateItem(@PathVariable(name = "id") final Long ownerId,
                                                             @PathVariable(name = "bucketId") final Long toBucket,
                                                             @PathVariable(name = "itemId") final Long itemId,
                                                             @RequestParam(name = "from") final Long fromBucket,
                                                             @AuthenticationPrincipal final UserPrincipal currentUser) {
        if(!Objects.equals(currentUser.getId(), ownerId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        ItemSummaryResponse response = itemService.duplicateItem(ownerId, itemId, fromBucket, toBucket);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
