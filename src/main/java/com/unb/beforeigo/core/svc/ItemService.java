package com.unb.beforeigo.core.svc;

import com.unb.beforeigo.api.dto.response.ItemSummaryResponse;
import com.unb.beforeigo.api.exception.client.BadRequestException;
import com.unb.beforeigo.api.exception.client.UnauthorizedException;
import com.unb.beforeigo.application.dao.BucketDAO;
import com.unb.beforeigo.application.dao.ItemDAO;
import com.unb.beforeigo.core.model.Bucket;
import com.unb.beforeigo.core.model.Item;
import com.unb.beforeigo.core.model.validation.EntityValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ItemService {

    @Autowired private BucketDAO bucketDAO;

    @Autowired private ItemDAO itemDAO;

    /**
     * Create a new {@link Item} that is associated to a given bucket.
     *
     * The item provided must be valid. The {@link Item} parent field will be overwritten with the bucket found using the
     * bucketId parameter. The {@link Item} id field is set to null to prevent this method from being used to
     * overwrite an item already persisted.
     *
     * @param bucketId The id of the bucket that the item is in.
     * @param item The item to create.
     * @return A summary of the item once persisted in the database.
     * @throws BadRequestException If a bucket with the given bucketId cannot be found.
     * */
    public ItemSummaryResponse createItem(final Long bucketId, final Item item) {
        Bucket itemParent = bucketDAO.findById(bucketId)
                .orElseThrow(() ->
                        new BadRequestException("Unable to find a bucket with id " + bucketId));

        item.setParent(itemParent);
        item.setId(null);

        Item savedItem = saveItem(item);
        return adaptItemToItemSummary(savedItem);
    }

    /**
     * Create a new {@link Item} that is associated to a given bucket, that is duplicated from an existing item.
     *
     * Duplicated items will inherit all fields from the parent item (except {@link Item#} parent).
     *
     * If the item parent with the id provided is private, the item will only be duplicated if the owner of the item
     * matches the id of the childBucketOwnerId param. Conversely, if the childBucketOwnerId param matches the owner of
     * the bucket, the bucket may be duplicated regardless of whether it is private or public.
     *
     * @param userId The id of the user that will own the duplicated item.
     * @param itemId The id of the item that will be duplicated.
     * @param fromBucket The id of the bucket that currently owns the bucket.
     * @param toBucket The id of the bucket that will own the newly created items.
     * @return A summary of the duplicated item once persisted in the database.
     * @throws BadRequestException If a bucket or item cannot be found with the given ids.
     * @throws UnauthorizedException If the user is not permitted to duplicate the item.
     * */
    public ItemSummaryResponse duplicateItem(final Long userId, final Long itemId, final Long fromBucket, final Long toBucket) {
        Item item = itemDAO.findById(itemId)
                .orElseThrow(() ->
                        new BadRequestException("Unable to find a item with id " + itemId));

        Bucket itemParent = item.getParent();
        if(!Objects.equals(itemParent.getId(), fromBucket)) {
            throw new BadRequestException("Item parent id mismatch.");
        }

        if(!itemParent.getIsPublic() && !Objects.equals(itemParent.getOwner().getId(), userId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        Bucket newItemParent = bucketDAO.findById(toBucket)
                .orElseThrow(() ->
                        new BadRequestException("Unable to find a bucket with id " + toBucket));

        item.setId(null);
        item.setParent(newItemParent);

        Item savedItem = saveItem(item);
        return adaptItemToItemSummary(savedItem);
    }

    /**
     * Duplicate all {@link Item}'s that is associated to a given bucket into another existing bucket.
     *
     * @param userId The id of the user that will own the duplicated items.
     * @param fromBucket The id of the bucket that currently owns the bucket.
     * @param toBucket The id of the bucket that will own the newly created items.
     * @return A summary of the duplicated item once persisted in the database.
     * @throws BadRequestException If a bucket or item cannot be found with the given ids.
     * @throws UnauthorizedException if the user is not permitted to duplicate the item.
     * */
    public List<ItemSummaryResponse> duplicateBucketItems(final Long userId, final Long fromBucket, final Long toBucket) {
        Bucket itemParent = bucketDAO.findById(fromBucket)
                .orElseThrow(() ->
                        new BadRequestException("Unable to find a bucket with id " + fromBucket));

        if(!itemParent.getIsPublic() && !Objects.equals(itemParent.getOwner().getId(), userId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        Bucket newItemParent = bucketDAO.findById(toBucket)
                .orElseThrow(() ->
                        new BadRequestException("Unable to find a bucket with id " + toBucket));

        List<Item> items = itemDAO.findAllByParent(itemParent);
        for(Item item : items) {
            item.setParent(newItemParent);
            item.setId(null);
        }

        itemDAO.saveAll(items);
        return items.stream()
                .map(ItemService::adaptItemToItemSummary)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve a list of {@link Item}'s associated to a given bucket.
     *
     * @param bucketId The id of the bucket that owns the items.
     * @param publicOnly Returns only items from buckets that are public.
     * @return A list of item summaries.
     * @throws BadRequestException If a bucket with the given id cannot be found.
     * */
    public List<ItemSummaryResponse> findItems(final Long bucketId, final boolean publicOnly) {
        Bucket parent = bucketDAO.findById(bucketId)
                .orElseThrow(() ->
                        new BadRequestException("Unable to find a bucket with id " + bucketId));

        if(publicOnly && !parent.getIsPublic()) {
            return Collections.emptyList();
        }

        List<Item> items = itemDAO.findAllByParent(parent);

        return items.stream()
                .map(ItemService::adaptItemToItemSummary)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve a specific {@link Item} by item id.
     *
     * If the bucket is private and the publicOnly parameter is true (i.e. the bucket with the given id is private),
     * then an UnauthorizedException is thrown.
     *
     * @param bucketId The id of the bucket that the item belongs to.
     * @param itemId The id of the item to retrieve.
     * @param publicOnly Specify whether bucket is retrieved only if it is public.
     * @return A summary of a bucket, if found.
     * @throws BadRequestException If a bucket with the given id cannot be found.
     * @throws UnauthorizedException If the bucket with the given id is private, but the publicOnly param is true.
     * */
    public ItemSummaryResponse findItemById(final Long bucketId, final Long itemId, final boolean publicOnly) {
        Bucket parent = bucketDAO.findById(bucketId)
                .orElseThrow(() ->
                        new BadRequestException("Unable to find a bucket with id " + bucketId));

        Item item = itemDAO.findById(itemId)
                .orElseThrow(() ->
                        new BadRequestException("Unable to find a item with id " + itemId));

        if(!Objects.equals(parent.getId(), item.getParent().getId())){
            throw new BadRequestException("Item parent with id " + item.getParent().getId() + " doesn't match bucket id");
        }

        if(publicOnly && !item.getParent().getIsPublic()) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        return adaptItemToItemSummary(item);
    }

    /**
     * Partially update a {@link Bucket} with a given id.
     *
     * All non-null fields in the partialBucket are used to overwrite the same fields in the bucket currently persisted
     * in the database.
     *
     * Partial bucket owner field is ignored, because the bucket ownership cannot be transferred to a new user.
     *
     * The partial bucket provided must have the owner field specified. Although it is not used to update
     * the persisted bucket, it is used to verify the user that owns the partial bucket matches the user that owns the
     * bucket with the given id. Specifically, this is used by the {@link com.unb.beforeigo.api.BucketController} to
     * verify that the user id provided as a path variable matches the owner of the bucket with the id provided as a path
     * variable.
     *
     * @param partialItem The partial item used to update the item.
     * @param itemId The id of the item to patch.
     * @param bucketId The id of the bucket that owns the item.
     * @return A summary of the patched bucket, once persisted in the database.
     * @throws BadRequestException If a bucket with the given bucketId cannot be found.
     * @throws BadRequestException If the owner of the partial bucket does not match the owner of the bucket with the
     * given id.
     * */
    public ItemSummaryResponse patchItem(final Item partialItem, final Long itemId, final Long bucketId) {
        Item persistedItem = itemDAO.findById(itemId)
                .orElseThrow(() -> new BadRequestException("Unable to find item with id " + itemId));

        if(!Objects.equals(persistedItem.getParent().getId(), bucketId)) {
            throw new BadRequestException("Parent of item with id " + itemId +
                    " does not match partial item parent with bucket id " + bucketId + ".");
        }

        if(Objects.nonNull(partialItem.getName())) {
            persistedItem.setName(partialItem.getName());
        }

        if(Objects.nonNull(partialItem.getLink())) {
            persistedItem.setLink(partialItem.getLink());
        }

        if(Objects.nonNull(partialItem.getDescription())) {
            persistedItem.setDescription(partialItem.getDescription());
        }

        if(Objects.nonNull(partialItem.getIsComplete())) {
            persistedItem.setIsComplete(partialItem.getIsComplete());
        }

        Item childItem = saveItem(persistedItem);
        return adaptItemToItemSummary(childItem);
    }

    /**
     * Completely update a {@link Item} with a given id.
     *
     * All fields in the bucket parameter are used to overwrite the fields in the bucket currently persisted
     * in the database.
     *
     * The item provided must have the parent field specified, and it must match the item with the provided
     * itemId. This is used to prevent the transfer of ownership of an item.
     *
     * @param item The item used to update the persisted item.
     * @param itemId The id of the item to update.
     * @param bucketId The id of the bucket that owns the item.
     * @return A summary of the updated item, once persisted in the database.
     * @throws BadRequestException If an item with the given itemId cannot be found.
     * @throws BadRequestException If the parent of the item does not match the parent of the item with the given id.
     * */
    public ItemSummaryResponse updateItem(final Item item, final Long itemId, final Long bucketId) {
        Item persistedItem = itemDAO.findById(itemId)
                .orElseThrow(() -> new BadRequestException("Unable to find item with id " + itemId));

        if(!Objects.equals(persistedItem.getParent().getId(), bucketId)) {
            throw new BadRequestException("Parent of item with id " + itemId +
                    " does not match url path variable for bucket id " + bucketId + ".");
        }

        item.setId(persistedItem.getId());
        item.setParent(persistedItem.getParent());

        Item childItem = saveItem(persistedItem);
        return adaptItemToItemSummary(childItem);
    }

    /**
     * Delete a given item. The {@link Item} id field must be non-null.
     *
     * The item provided must have the parent field specified. It is used to verify the bucket that has the
     * item provided matches the bucket that has the item with the given id. Specifically, this is used by the
     * {@link com.unb.beforeigo.api.ItemController} to verify that the bucket id provided as a path variable matches the
     * parent of the item with the id provided as a path variable.
     *
     * @param ownerId The owner of the item.
     * @param bucketId The parent bucket id.
     * @param itemId The id of the item.
     * @throws BadRequestException If the parent of the item does not match the parent of the item with the given id.
     * */
    public void deleteItem(final Long ownerId, final Long bucketId, final Long itemId) {
        Item persistedItem = itemDAO.findById(itemId)
                .orElseThrow(() -> new BadRequestException("Unable to find item with id " + itemId));

        Bucket parent = bucketDAO.findById(persistedItem.getParent().getId())
                .orElseThrow(() -> new BadRequestException("Unable to find bucket with id " + persistedItem.getParent().getId()));

        if(!Objects.equals(parent.getId(), bucketId)) {
            throw new BadRequestException("Item does not belong to the given bucket.");
        }

        if(!Objects.equals(parent.getOwner().getId(), ownerId)) {
            throw new BadRequestException("Owner id does not match buckets owner id");
        }

        itemDAO.delete(persistedItem);
    }

    /**
     * Save an item.
     * Performs constraint validation.
     *
     * @param item The item to save.
     * @return The item once persisted in the database.
     * @throws BadRequestException If the item does not meet validation constraints.
     * @see org.springframework.data.jpa.repository.JpaRepository#save(Object)
     * */
    private Item saveItem(final Item item) {
        EntityValidator.validateEntity(item, () ->
                new BadRequestException("cannot save item that does not meet validation constraints"));

        return itemDAO.save(item);
    }

    /**
     * Build a ItemSummaryResponse DTO of an Item entity.
     *
     * @param item The item to be used to build a ItemSummaryResponse.
     * @return A summary of the item.
     * */
    public static ItemSummaryResponse adaptItemToItemSummary(final Item item) {
        Long parentId = Objects.nonNull(item.getParent()) ? item.getParent().getId() : null;
        return new ItemSummaryResponse(item.getId(),
                parentId,
                item.getName(),
                item.getLink(),
                item.getDescription());
    }
}
