package ca.unb.ktb.api;

import ca.unb.ktb.api.dto.response.UserFeedResponse;
import ca.unb.ktb.api.exception.client.UnauthorizedException;
import ca.unb.ktb.core.svc.FeedService;
import ca.unb.ktb.infrastructure.security.UserPrincipal;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/feed")
@Slf4j
public class FeedController {

    @Autowired private FeedService feedService;

    /**
     * Retrieve feed data for a user. Retrieve a map of users who are followed by the user with the given user id and
     * have recently created buckets. Results are sorted in reverse chronological order.
     *
     * @param userId id of the current user.
     * @param page the page number of the search.
     * @param size how many items are displayed per page.
     * @param auth authentication principal
     * @return the feed results
     * */
    @ApiOperation(value = "Retrieve users who are followed by user with given user id and have recently created buckets.",
            response = UserFeedResponse.class)
    @RequestMapping(value = "/{id}/following/created_buckets", method = RequestMethod.GET)
    public ResponseEntity<UserFeedResponse> retrieveBucketsCreatedByFollowedUsers(
            @PathVariable(name = "id") final Long userId,
            @RequestParam(name = "page", defaultValue = "0", required = false) final Integer page,
            @RequestParam(name = "size", defaultValue = "20", required = false) final Integer size,
            @AuthenticationPrincipal final Authentication auth) {
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
        if(!Objects.equals(currentUser.getId(), userId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        UserFeedResponse response = feedService.retrieveBucketsCreatedByFollowedUsers(userId, PageRequest.of(page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieve feed data for a user. Retrieve a map of users who are followed by the user with the given user id and
     * have recently created items. Results are sorted in reverse chronological order.
     *
     * @param userId id of the current user.
     * @param page the page number of the search.
     * @param size how many items are displayed per page.
     * @param auth authentication principal
     * @return the feed results
     * */
    @ApiOperation(value = "Retrieve users who are followed by user with given user id and have recently created items.",
            response = UserFeedResponse.class)
    @RequestMapping(value = "/{id}/following/created_items", method = RequestMethod.GET)
    public ResponseEntity<UserFeedResponse> retrieveItemsCreatedByFollowedUsers(
            @PathVariable(name = "id") final Long userId,
            @RequestParam(name = "page", defaultValue = "0", required = false) final Integer page,
            @RequestParam(name = "size", defaultValue = "20", required = false) final Integer size,
            @AuthenticationPrincipal final Authentication auth) {
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
        if(!Objects.equals(currentUser.getId(), userId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        UserFeedResponse response = feedService.retrieveItemsCreatedByFollowedUsers(userId, PageRequest.of(page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieve feed data for a user. Retrieve a map of users who are followed by the user with the given user id and
     * have recently followed other users. Results are sorted in reverse chronological order.
     *
     * @param userId id of the current user.
     * @param page the page number of the search.
     * @param size how many items are displayed per page.
     * @param auth authentication principal
     * @return the feed results
     * */
    @ApiOperation(value = "Retrieve users who are followed by user with given user id and have recently followed other users.",
            response = UserFeedResponse.class)
    @RequestMapping(value = "/{id}/following/followed_users", method = RequestMethod.GET)
    public ResponseEntity<UserFeedResponse> retrieveUsersFollowedByFollowedUsers(
            @PathVariable(name = "id") final Long userId,
            @RequestParam(name = "page", defaultValue = "0", required = false) final Integer page,
            @RequestParam(name = "size", defaultValue = "20", required = false) final Integer size,
            @AuthenticationPrincipal final Authentication auth) {
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
        if(!Objects.equals(currentUser.getId(), userId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        UserFeedResponse response = feedService.retrieveUsersFollowedByFollowedUsers(userId, PageRequest.of(page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieve feed data for a user. Retrieve a map of users who are followed by the user with the given user id and
     * have recently followed other buckets. Results are sorted in reverse chronological order.
     *
     * @param userId id of the current user.
     * @param page the page number of the search.
     * @param size how many items are displayed per page.
     * @param auth authentication principal
     * @return the feed results
     * */
    @ApiOperation(value = "Retrieve users who are followed by user with given user id and have recently followed other buckets.",
            response = UserFeedResponse.class)
    @RequestMapping(value = "/{id}/following/followed_buckets", method = RequestMethod.GET)
    public ResponseEntity<UserFeedResponse> retrieveBucketsFollowedByFollowedUsers(
            @PathVariable(name = "id") final Long userId,
            @RequestParam(name = "page", defaultValue = "0", required = false) final Integer page,
            @RequestParam(name = "size", defaultValue = "20", required = false) final Integer size,
            @AuthenticationPrincipal final Authentication auth) {
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
        if(!Objects.equals(currentUser.getId(), userId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        UserFeedResponse response = feedService.retrieveBucketsFollowedByFollowedUsers(userId, PageRequest.of(page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieve feed data for a user. Retrieve a list of buckets recently created by the user with the given user id.
     * Results are sorted in reverse chronological order.
     *
     * @param userId id of the current user.
     * @param page the page number of the search.
     * @param size how many items are displayed per page.
     * @param auth authentication principal
     * @return the feed results
     * */
    @ApiOperation(value = "Retrieve a list of buckets recently created by the user with the given user id.",
            response = UserFeedResponse.class)
    @RequestMapping(value = "/{id}/created_buckets", method = RequestMethod.GET)
    public ResponseEntity<UserFeedResponse> retrieveBucketsCreatedByUser(
            @PathVariable(name = "id") final Long userId,
            @RequestParam(name = "page", defaultValue = "0", required = false) final Integer page,
            @RequestParam(name = "size", defaultValue = "20", required = false) final Integer size,
            @AuthenticationPrincipal final Authentication auth) {
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
        if(!Objects.equals(currentUser.getId(), userId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        UserFeedResponse response = feedService.retrieveBucketsCreatedByUser(userId, PageRequest.of(page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieve feed data for a user. Retrieve a list of items recently created by the user with the given user id.
     * Results are sorted in reverse chronological order.
     *
     * @param userId id of the current user.
     * @param page the page number of the search.
     * @param size how many items are displayed per page.
     * @param auth authentication principal
     * @return the feed results
     * */
    @ApiOperation(value = "Retrieve a list of items recently created by the user with the given user id.",
            response = UserFeedResponse.class)
    @RequestMapping(value = "/{id}/created_items", method = RequestMethod.GET)
    public ResponseEntity<UserFeedResponse> retrieveItemsCreatedByUser(
            @PathVariable(name = "id") final Long userId,
            @RequestParam(name = "page", defaultValue = "0", required = false) final Integer page,
            @RequestParam(name = "size", defaultValue = "20", required = false) final Integer size,
            @AuthenticationPrincipal final Authentication auth) {
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
        if(!Objects.equals(currentUser.getId(), userId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        UserFeedResponse response = feedService.retrieveItemsCreatedByUser(userId, PageRequest.of(page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieve feed data for a user. Retrieve a list of users recently followed by the user with the given user id.
     * Results are sorted in reverse chronological order.
     *
     * @param userId id of the current user.
     * @param page the page number of the search.
     * @param size how many items are displayed per page.
     * @param auth authentication principal
     * @return the feed results
     * */
    @ApiOperation(value = "Retrieve a list of users recently followed by the user with the given user id.",
            response = UserFeedResponse.class)
    @RequestMapping(value = "/{id}/followed_users", method = RequestMethod.GET)
    public ResponseEntity<UserFeedResponse> retrieveUsersFollowedUser(
            @PathVariable(name = "id") final Long userId,
            @RequestParam(name = "page", defaultValue = "0", required = false) final Integer page,
            @RequestParam(name = "size", defaultValue = "20", required = false) final Integer size,
            @AuthenticationPrincipal final Authentication auth) {
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
        if(!Objects.equals(currentUser.getId(), userId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        UserFeedResponse response = feedService.retrieveUsersFollowedByUser(userId, PageRequest.of(page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieve feed data for a user. Retrieve a list of buckets recently followed by the user with the given user id.
     * Results are sorted in reverse chronological order.
     *
     * @param userId id of the current user.
     * @param page the page number of the search.
     * @param size how many items are displayed per page.
     * @param auth authentication principal
     * @return the feed results
     * */
    @ApiOperation(value = "Retrieve a list of buckets recently followed by the user with the given user id.",
            response = UserFeedResponse.class)
    @RequestMapping(value = "/{id}/followed_buckets", method = RequestMethod.GET)
    public ResponseEntity<UserFeedResponse> retrieveBucketsFollowedByUser(
            @PathVariable(name = "id") final Long userId,
            @RequestParam(name = "page", defaultValue = "0", required = false) final Integer page,
            @RequestParam(name = "size", defaultValue = "20", required = false) final Integer size,
            @AuthenticationPrincipal final Authentication auth) {
        UserPrincipal currentUser = (UserPrincipal) auth.getPrincipal();
        if(!Objects.equals(currentUser.getId(), userId)) {
            throw new UnauthorizedException("Insufficient permissions.");
        }

        UserFeedResponse response = feedService.retrieveBucketsFollowedByUser(userId, PageRequest.of(page, size));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
