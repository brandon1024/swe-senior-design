package ca.unb.ktb.api;

import ca.unb.ktb.api.dto.response.ItemSummaryResponse;
import ca.unb.ktb.api.exception.client.UnauthorizedException;
import ca.unb.ktb.core.model.Item;
import ca.unb.ktb.core.svc.ItemService;
import ca.unb.ktb.infrastructure.security.UserPrincipal;
import io.swagger.annotations.ApiOperation;
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
public class ItemController {

    @Autowired private ItemService itemService;

    /**
     * Create a new {@link Item}.
     *
     * @param ownerId The id of the {@link ca.unb.ktb.core.model.User} that owns the {@link ca.unb.ktb.core.model.Bucket}.
     * @param bucketId The id of the {@link ca.unb.ktb.core.model.Bucket} that owns the {@link Item}.
     * @param item A valid {@link Item} with all necessary fields.
     * @param auth The authentication token.
     * @return A new {@link Item} once persisted in the database. HTTP CREATED.
     * @throws UnauthorizedException If the authenticated principal {@link ca.unb.ktb.core.model.User} does not match the user in the path variable.
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
     * Retrieve a list of {@link Item}'s associated to a specific {@link ca.unb.ktb.core.model.Bucket}.
     *
     * @param ownerId Id of the {@link ca.unb.ktb.core.model.User} that owns the {@link ca.unb.ktb.core.model.Bucket}s.
     * @param bucketId Id of the {@link ca.unb.ktb.core.model.Bucket} that owns the {@link Item}.
     * @param auth The authentication token.
     * @return A list of {@link Item}s associated to a given user. HTTP OK.
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
     * Retrieve a specific {@link Item} associated to a specific {@link ca.unb.ktb.core.model.Bucket} and user.
     *
     * If the owner id does not match the id of the principal user, the item is only returned if public.
     *
     * @param ownerId Id of the {@link ca.unb.ktb.core.model.User} that owns the {@link ca.unb.ktb.core.model.Bucket}s.
     * @param bucketId Id of the {@link ca.unb.ktb.core.model.Bucket} that owns the {@link Item}.
     * @param itemId Id of the {@link Item}.
     * @param auth The authentication token.
     * @return {@link Item} associated to a given {@link ca.unb.ktb.core.model.User}. If the user and principal have
     * matching ids, public or private {@link ca.unb.ktb.core.model.Bucket} may be returned, otherwise only returns a
     * public bucket. HTTP OK.
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
     * Update fields in a {@link Item} that is currently persisted in the database. Only non-null item fields are updated.
     *
     * @param ownerId Id of the {@link ca.unb.ktb.core.model.User} that owns the {@link ca.unb.ktb.core.model.Bucket}.
     * @param bucketId Id of the {@link ca.unb.ktb.core.model.Bucket} that owns the {@link Item}.
     * @param itemId Id of the {@link Item} to patch.
     * @param item An {@link Item} to patch.
     * @param auth The authentication token.
     * @return The patched {@link Item}. HTTP OK.
     * @throws UnauthorizedException If the authenticated principal {@link ca.unb.ktb.core.model.User} does not match
     * the user in the path variable.
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
     * @param ownerId Id of the {@link ca.unb.ktb.core.model.User} that owns the {@link ca.unb.ktb.core.model.Bucket}.
     * @param bucketId Id of the {@link ca.unb.ktb.core.model.Bucket} that owns the {@link Item}.
     * @param itemId Id of the {@link Item} that will be updated.
     * @param item An {@link Item} to update.
     * @param auth The authentication token.
     * @return The updated {@link Item}. HTTP OK.
     * @throws UnauthorizedException If the authenticated principal {@link ca.unb.ktb.core.model.User} does not match
     * the user in the path variable.
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
     * Delete an {@link Item}.
     *
     * @param ownerId Id of the {@link ca.unb.ktb.core.model.User} that owns the {@link ca.unb.ktb.core.model.Bucket}.
     * @param bucketId Id of the {@link ca.unb.ktb.core.model.Bucket} that owns the {@link Item}.
     * @param itemId Id of the {@link Item} that will be deleted.
     * @param auth The authentication token.
     * @return HTTP OK.
     * @throws UnauthorizedException If the authenticated principal {@link ca.unb.ktb.core.model.User} does not match
     * the user in the path variable.
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
     * @param ownerId The id of the {@link ca.unb.ktb.core.model.User} that owns the {@link ca.unb.ktb.core.model.Bucket}.
     * @param toBucket The id of the {@link ca.unb.ktb.core.model.Bucket} that will own the newly created {@link Item}.
     * @param itemId The id of the {@link Item} to be duplicated.
     * @param fromBucket The id of the {@link ca.unb.ktb.core.model.Bucket} that currently owns the {@link Item}.
     * @param auth The authentication token.
     * @return A new {@link Item} once persisted in the database. HTTP OK.
     * @throws UnauthorizedException If the authenticated principal {@link ca.unb.ktb.core.model.User} does not match
     * the user in the path variable.
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
