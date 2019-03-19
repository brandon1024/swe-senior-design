# Feed API Documentation
The Feed API is comprised of eight endpoints, categorized into two groups. The groups and endpoints are described in detail below.

All results returned from endpoints in the Feed API are automatically sorted in reverse chronological order.

Pagination is enabled on the endpoints, and is controlled via request parameters.

## User Circle Activity Feed Endpoints
### Buckets Created By User's Friends
This endpoint is used to retrieve buckets that were recently created by a user's friends. Buckets are grouped by the user that created the bucket.

As an example, suppose you (the user) are following a user `@Bob`. If `@Bob` were to create a bucket titled `My Bucket`, then this bucket would be shown on your feed through this endpoint. A simplified response from this endpoint would look something like this:
```
{
    "followedUsersRecentlyCreatedBuckets": [{
        "user": {
            "username": "Bob"
        },
        "buckets": [{
            "name": "My Bucket"
        }]
    }]
}
```

- URI: `/feed/{id}/following/created_buckets`
- Request Parameters:
    - `page`: the page offset.
    - `size`: the number of results per page.
- Sample Response:
```
{
    "followedUsersRecentlyCreatedBuckets": [
        {
            "user": {
                "id": 4,
                "username": "testuser",
                "email": "testuser@ktb.com",
                "bio": null,
                "firstName": "test",
                "middleName": null,
                "lastName": "user"
            },
            "buckets": [
                {
                    "id": 10,
                    "ownerId": 4,
                    "name": "testBucket1",
                    "isPublic": true,
                    "description": "testBucket"
                }
            ]
        }
    ]
}
```

### Items Created By User's Friends
This endpoint is used to retrieve items that were recently created by a user's friends. Items are grouped by the user that created the item.

As an example, suppose you (the user) are following a user `@Bob`. If `@Bob` were to create an item titled `My Item`, then this item would be shown on your feed through this endpoint. A simplified response from this endpoint would look something like this:
```
{
    "followedUsersRecentlyCreatedItems": [{
        "user": {
            "username": "Bob"
        },
        "items": [{
            "name": "My Item"
        }]
    }]
}
```

- URI: `/feed/{id}/following/created_items`
- Request Parameters:
    - `page`: the page offset.
    - `size`: the number of results per page.
- Sample Response:
```
{
    "followedUsersRecentlyCreatedItems": [
        {
            "user": {
                "id": 4,
                "username": "testuser",
                "email": "testuser@ktb.com",
                "bio": null,
                "firstName": "test",
                "middleName": null,
                "lastName": "user"
            },
            "items": [
                {
                    "id": 11,
                    "parentId": 10,
                    "name": "testItem",
                    "link": "tmp",
                    "description": "testItem",
                    "complete": false
                }
            ]
        }
    ]
}
```

### Users Followed By User's Friends
This endpoint is used to retrieve users that were recently followed by a user's friends. Users are grouped by the user that followed the users.

The main purpose of the endpoint is to grow a user's friend circle by allowing them to meet other users on the platform.

As an example, suppose you (the user) are following a user `@Bob`. If `@Bob` were to follow a user named `@Alice`, then `@Alice` would be shown on your feed through this endpoint. A simplified response from this endpoint would look something like this:
```
{
    "followedUsersRecentlyFollowedUsers": [{
            "user": {
                "username": "Bob",
            },
            "users": [{
                "username": "Alice",
            }]
        }
    ]
}
```

- URI: `/feed/{id}/following/followed_users`
- Request Parameters:
    - `page`: the page offset.
    - `size`: the number of results per page.
- Sample Response:
```
{
    "followedUsersRecentlyFollowedUsers": [
        {
            "user": {
                "id": 4,
                "username": "testuser",
                "email": "testuser@ktb.com",
                "bio": null,
                "firstName": "test",
                "middleName": null,
                "lastName": "user"
            },
            "users": [
                {
                    "id": 8,
                    "username": "testuser2",
                    "email": "testuser2@ktb.com",
                    "bio": null,
                    "firstName": "test",
                    "middleName": null,
                    "lastName": "user"
                },
                {
                    "id": 6,
                    "username": "testuser1",
                    "email": "testuser1@ktb.com",
                    "bio": null,
                    "firstName": "test",
                    "middleName": null,
                    "lastName": "user"
                }
            ]
        }
    ]
}
```

### Buckets Followed By User's Friends
Similar to the previous endpoint, this endpoint is used to retrieve a list of buckets who were recently followed by a user's friends.

As an example, suppose you (the user) are following a user `@Bob`. If `@Bob` were to follow a bucket named `Alice's Bucket`, then `Alice's Bucket` would be shown on your feed through this endpoint. A simplified response from this endpoint would look something like this:
```
{
    "followedUsersRecentlyFollowedBuckets": [{
            "user": {
                "username": "Bob",
            },
            "buckets": [{
                "name": "Alice's Bucket",
            }]
        }
    ]
}
```


- URI: `/feed/{id}/following/followed_buckets`
- Request Parameters:
    - `page`: the page offset.
    - `size`: the number of results per page.
- Sample Response:
```
{
    "followedUsersRecentlyFollowedBuckets": [
        {
            "user": {
                "id": 4,
                "username": "testuser",
                "email": "testuser@ktb.com",
                "bio": null,
                "firstName": "test",
                "middleName": null,
                "lastName": "user"
            },
            "buckets": [
                {
                    "id": 14,
                    "ownerId": 6,
                    "name": "testBucket2",
                    "isPublic": true,
                    "description": "testBucket"
                }
            ]
        }
    ]
}
```

## User Activity Feed Endpoints
### Buckets Created By Me
This endpoint is used to retrieve a list of buckets which were created by the current user.

- URI: `/feed/{id}/created_buckets`
- Request Parameters:
    - `page`: the page offset.
    - `size`: the number of results per page.
- Sample Response:
```
{
    "userRecentlyCreatedBuckets": [
        {
            "id": 14,
            "ownerId": 6,
            "name": "testBucket2",
            "isPublic": true,
            "description": "testBucket"
        }
    ]
}
```

### Items Created By Me
This endpoint is used to retrieve a list of items which were created by the current user.

- URI: `/feed/{id}/created_items`
- Request Parameters:
    - `page`: the page offset.
    - `size`: the number of results per page.
- Sample Response:
```
{
    "userRecentlyCreatedItems": [
        {
            "id": 11,
            "parentId": 10,
            "name": "testItem",
            "link": "tmp",
            "description": "testItem",
            "complete": false
        }
    ]
}
```

### Users Followed By Me
This endpoint is used to retrieve a list of users which were recently followed by the current user.

- URI: `/feed/{id}/followed_users`
- Request Parameters:
    - `page`: the page offset.
    - `size`: the number of results per page.
- Sample Response:
```
{
    "userRecentlyFollowedUsers": [
        {
            "id": 4,
            "username": "testuser",
            "email": "testuser@ktb.com",
            "bio": null,
            "firstName": "test",
            "middleName": null,
            "lastName": "user"
        }
    ]
}
```

### Buckets Followed By Me
This endpoint is used to retrieve a list of buckets which were recently followed by the current user.

- URI: `/feed/{id}/followed_buckets`
- Request Parameters:
    - `page`: the page offset.
    - `size`: the number of results per page.
- Sample Response:
```
{
    "userRecentlyFollowedBuckets": [
        {
            "id": 14,
            "ownerId": 6,
            "name": "testBucket2",
            "isPublic": true,
            "description": "testBucket"
        }
    ]
}
```

## Future Work
There is an issue in the backlog for allowing the ability to like buckets and items. When this feature is realized, new endpoints will need to be added to retrieve new likes.