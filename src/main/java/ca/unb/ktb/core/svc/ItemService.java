package ca.unb.ktb.core.svc;

import ca.unb.ktb.api.dto.response.ItemSummaryResponse;
import ca.unb.ktb.api.exception.client.BadRequestException;
import ca.unb.ktb.api.exception.client.UnauthorizedException;
import ca.unb.ktb.application.dao.ItemDAO;
import ca.unb.ktb.core.model.Bucket;
import ca.unb.ktb.core.model.Item;
import ca.unb.ktb.core.model.validation.EntityValidator;
import ca.unb.ktb.infrastructure.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ItemService {

    @Autowired private ItemDAO itemDAO;

    @Autowired private BucketService bucketService;

    /**
     * Create a new {@link Item} that is associated to a given {@link Bucket}.
     *
     * @param item The {@link Item} to create.
     * @param bucketId The id of the parent {@link Bucket}.
     * @return The {@link Item} once persisted in the database.
     * @throws UnauthorizedException if the principal user does not own the bucket with the given bucketId.
     * @see BucketService#findBucketById(Long)
     * */
    public Item createItem(final Item item, final Long bucketId) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Bucket itemParent = bucketService.findBucketById(bucketId);

        if(!Objects.equals(currentUser.getId(), itemParent.getOwner().getId())) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        item.setParent(itemParent);
        item.setId(null);

        return saveItem(item);
    }

    /**
     * Create a new {@link Item} that is duplicated from an existing item.
     *
     * The item must belong to a public {@link Bucket} if the principal does not own the bucket from which the item will be
     * duplicated. The principal user must own the destination bucket.
     *
     * @param itemId The id of the {@link Item} that will be duplicated.
     * @param fromBucket The id of the {@link Bucket} that currently owns the {@link Item}.
     * @param toBucket The id of the {@link Bucket} that will own the newly created {@link Item}.
     * @return The newly created {@link Item}, once persisted in the database.
     * @see BucketService#findBucketById(Long)
     * @see ItemService#findItemById(Long)
     * */
    public Item duplicateItem(final Long itemId, final Long fromBucket, final Long toBucket) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Bucket sourceBucket = bucketService.findBucketById(fromBucket);
        Bucket destinationBucket = bucketService.findBucketById(toBucket);
        Item duplicatedItem = findItemById(itemId);

        /* Verify that item belongs to bucket */
        if(!Objects.equals(duplicatedItem.getParent().getId(), sourceBucket.getId())) {
            throw new BadRequestException(String.format("Unable to find an item with id %d and parent bucket id %d.",
                    itemId, fromBucket));
        }

        /* Verify that principal user owns the destination bucket */
        if(!Objects.equals(currentUser.getId(), destinationBucket.getOwner().getId())) {
            throw new UnauthorizedException(String.format("Unable to duplicate item into a bucket not owned by the user with id %d.",
                    currentUser.getId()));
        }

        duplicatedItem.setId(null);
        duplicatedItem.setParent(destinationBucket);
        return itemDAO.save(duplicatedItem);
    }

    /**
     * Duplicate all {@link Item}s that are associated to a given {@link Bucket} into another existing bucket.
     *
     * @param fromBucket The id of the {@link Bucket} that currently owns the {@link Item}s.
     * @param toBucket The id of the {@link Bucket} that will own the newly created {@link Item}s.
     * @return A list of the new {@link Item}s.
     * @throws UnauthorizedException If the principal user does not own the destination bucket.
     * @see BucketService#findBucketById(Long)
     * */
    public List<Item> duplicateBucketItems(final Long fromBucket, final Long toBucket) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Bucket itemParent = bucketService.findBucketById(fromBucket);
        Bucket newItemParent = bucketService.findBucketById(toBucket);

        if(!Objects.equals(currentUser.getId(), newItemParent.getOwner().getId())) {
            throw new UnauthorizedException(String.format("Unable to duplicate item into a bucket not owned by the user with id %d.",
                    currentUser.getId()));
        }

        List<Item> items = itemDAO.findAllByParent(itemParent);
        for(Item item : items) {
            item.setId(null);
            item.setParent(newItemParent);
        }

        return itemDAO.saveAll(items);
    }

    /**
     * Retrieve a list of {@link Item}s belonging to a given {@link Bucket}.
     *
     * @param bucketId The id of the {@link Bucket} that owns the {@link Item}s.
     * @return A list of {@link Item}s belonging to the given {@link Bucket}.
     * @see BucketService#findBucketById(Long)
     * @see ItemDAO#findAllByParent(Bucket)
     * */
    public List<Item> findItems(final Long bucketId) {
        Bucket itemParent = bucketService.findBucketById(bucketId);
        return itemDAO.findAllByParent(itemParent);
    }

    /**
     * Retrieve a specific {@link Item} by id.
     *
     * @param itemId The id of the {@link Item} to retrieve.
     * @return The {@link Item}.
     * @throws BadRequestException If an {@link Item} with the given id cannot be found.
     * @throws UnauthorizedException If the item belongs to a {@link Bucket} that is private, and the principal user
     * does not own the bucket.
     * */
    public Item findItemById(final Long itemId) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Item item = itemDAO.findById(itemId).orElseThrow(() ->
                new BadRequestException(String.format("Unable to find a record with id %d.", itemId)));

        Bucket parentBucket = item.getParent();
        if(!parentBucket.getIsPublic() && !Objects.equals(parentBucket.getOwner().getId(), currentUser.getId())) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        return item;
    }

    /**
     * Retrieve a list of {@link Item}s whose names partially match a query string.
     *
     * {@link Item}s that belong to private {@link Bucket}s will only be returned if owned by the principal user.
     *
     * @param queryString The {@link Item} name query string.
     * @param pageable Specify how the results should be paged.
     * @return A list of {@link Item}s whose names partially match a query string.
     * @see ItemDAO#findAllByNameLike(String, Long, Pageable)
     * */
    public List<Item> findItemsByName(final String queryString, final Pageable pageable) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return itemDAO.findAllByNameLike(queryString, currentUser.getId(), pageable);
    }

    /**
     * Retrieve a list of {@link Item}s recently created by {@link ca.unb.ktb.core.model.User}s that are followed by a
     * given user.
     *
     * {@link Item}s that belong to private {@link Bucket}s will only be returned if owned by the user with the given id.
     *
     * @param userId The id of the {@link ca.unb.ktb.core.model.User}.
     * @param pageable Specify how the results should be paged.
     * @return A list of {@link Item}s recently created by {@link ca.unb.ktb.core.model.User}s that are followed by a
     * given user.
     * @see ItemDAO#retrieveItemsCreatedByFollowedUsers(Long, Pageable)
     * */
    public List<Item> findItemsRecentlyCreatedByFollowedUsers(final Long userId, final Pageable pageable) {
        return itemDAO.retrieveItemsCreatedByFollowedUsers(userId, pageable);
    }

    /**
     * Retrieve a list of {@link Item}s recently created by a {@link ca.unb.ktb.core.model.User} with the given user id.
     *
     * @param userId The id of the {@link ca.unb.ktb.core.model.User}.
     * @param pageable Specify how the results should be paged.
     * @return A list of {@link Item}s recently created by a {@link ca.unb.ktb.core.model.User} with the given user id.
     * @see ItemDAO#retrieveItemsCreatedByUser(Long, Pageable)
     * */
    public List<Item> findItemsRecentlyCreatedByUser(final Long userId, final Pageable pageable) {
        return itemDAO.retrieveItemsCreatedByUser(userId, pageable);
    }

    /**
     * Partially update an {@link Item} with a given id.
     *
     * All non-null fields in the partial item are used to overwrite the same fields in the item currently persisted
     * in the database.
     *
     * The item parent field is ignored, because a bucket cannot be transferred to another bucket without duplication.
     *
     * @param partialItem The partial {@link Item} used to update the item.
     * @param itemId The id of the {@link Item} to patch.
     * @return The patched {@link Item}, once persisted in the database.
     * @see ItemService#findItemByIdOwnedByPrincipal(Long)
     * */
    public Item patchItem(final Item partialItem, final Long itemId) {
        Item persistedItem = findItemByIdOwnedByPrincipal(itemId);

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

        return saveItem(persistedItem);
    }

    /**
     * Completely update a {@link Item} with a given id.
     *
     * All fields in the item are used to overwrite the fields in the {@link Bucket} currently persisted in the database.
     *
     * The item parent field is ignored, because a bucket cannot be transferred to another bucket without duplication.
     *
     * @param item The {@link Item} used to update the persisted item.
     * @param itemId The id of the {@link Item} to update.
     * @return The updated {@link Item}, once persisted in the database.
     * @see ItemService#findItemByIdOwnedByPrincipal(Long)
     * */
    public Item updateItem(final Item item, final Long itemId) {
        Item persistedItem = findItemByIdOwnedByPrincipal(itemId);
        item.setId(persistedItem.getId());
        item.setParent(persistedItem.getParent());

        return saveItem(persistedItem);
    }

    /**
     * Delete an {@link Item}.
     *
     * @param itemId The id of the {@link Item}.
     * @see ItemService#findItemByIdOwnedByPrincipal(Long)
     * */
    public void deleteItem(final Long itemId) {
        Item persistedItem = findItemByIdOwnedByPrincipal(itemId);

        itemDAO.delete(persistedItem);
    }

    /**
     * Delete all {@link Item}s within a given {@link Bucket}. The principal user must be the owner of the bucket, or an
     * {@link UnauthorizedException} is thrown.
     *
     * @param bucket The {@link Bucket} whose {@link Item}s are to be removed.
     * @throws UnauthorizedException If the principal user does not own the {@link Bucket}.
     * @see ItemDAO#findAllByParent(Bucket)
     * */
    public void deleteItems(final Bucket bucket) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!Objects.equals(currentUser.getId(), bucket.getOwner().getId())) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        List<Item> items = itemDAO.findAllByParent(bucket);
        itemDAO.deleteAll(items);
    }

    /**
     * Find an {@link Item} owned by a given {@link ca.unb.ktb.core.model.User}.
     *
     * This method ensures that the principal user owns the item before returning it. This is primarily used by
     * methods that modify items to ensure that a user is not modifying a item that does not belong to them.
     *
     * @param itemId The id of the {@link Item} to fetch.
     * @return The {@link Item}.
     * @throws BadRequestException If an {@link Item} with the given id does not exist, or the parent bucket is not
     * owned by the given owner.
     * @throws UnauthorizedException If the owner of the {@link Item} does not match the principal user.
     * @see ItemService#findItemById(Long)
     * */
    private Item findItemByIdOwnedByPrincipal(final Long itemId) {
        UserPrincipal currentUser = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Item persistedItem = findItemById(itemId);
        Bucket parent = persistedItem.getParent();

        if(!Objects.equals(parent.getOwner().getId(), currentUser.getId())) {
            throw new UnauthorizedException("Cannot modify an item owned by another user.");
        }

        return persistedItem;
    }

    /**
     * Save an {@link Item}.
     *
     * Performs constraint validation.
     *
     * @param item The {@link Item} to save.
     * @return The {@link Item} once persisted in the database.
     * @throws BadRequestException If the item does not meet validation constraints.
     * */
    private Item saveItem(final Item item) {
        EntityValidator.validateEntity(item, () ->
                new BadRequestException("cannot save item that does not meet validation constraints"));

        return itemDAO.save(item);
    }

    /**
     * Build a {@link ItemSummaryResponse} DTO of an {@link Item}.
     *
     * @param item The {@link Item} to be used to build a {@link ItemSummaryResponse}.
     * @return A summary of the {@link Item}.
     * */
    public ItemSummaryResponse adaptItemToItemSummary(final Item item) {
        Long parentId = Objects.nonNull(item.getParent()) ? item.getParent().getId() : null;
        return new ItemSummaryResponse(item.getId(),
                parentId,
                item.getName(),
                item.getLink(),
                item.getDescription(),
                item.getIsComplete());
    }
}
