# Spring Security and Authentication with JSON Web Tokens
## Introduction to JWT
JSON Web Token (JWT) is an open standard for creating tokens that assert some number of claims. It is used to provide a secure method for communicating JSON securely between a client and server, and allows for stateless user session management. Requests made to the server are digitally signed with a secret key on the server side using the HMAC algorithm, which can be later used to verify the integrity of the request made against the server. JWTs may also be encrypted, but this project relies only on signed JWTs.

As an example, consider that a server could generate a token that has the claim "logged in as admin" and provide that to a client. The client could use that token to prove to the server that they are logged in as admin. Because the tokens are signed by the server's secret key, the server is able to verify that the token is legitimate.

Now, consider the situation in which a user attempts to forge a token to make them appear to have admin privileges. They modify the payload to assert the claim that they are an administrator. After making a request, the server verifies the integrity of the message using the token signature. Because they do not match, the server can reject the request. The user would have to forge the token signature as well, but this is not possible without the sever's secret key, because the signature is generated with a one-way hash function (HMAC, in this case).

The signed JWT has three parts, and takes the form `xxxxx.yyyyy.zzzzz`. The first part is the header, which contains the type of token (JWT) and the algorithm used (in our case, HMAC using SHA-256 hash algorithm). The second part is the payload, which contains various statements about the token, known as claims, such as subject (username), expiration time of the token, and other custom fields. The last part is the signature, which is calculated using the HMACSHA256 algorithm, as described below:
```
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret)
```

Here is an example token:
```
Header:
{
  "alg": "HS256",
  "typ": "JWT"
}

Payload:
{
  "sub": "username",
  "uid": "12",
  "exp": 1516325422,
  "iat": 1516239022
}

Signature:
HMAC-SHA256("your-256-bit-secret", base64UrlEncode(header) + "." + base64UrlEncode(payload))
  
Token:
base64UrlEncode(header) + "." + base64UrlEncode(payload) + "." + encodeBase64(signature)

Encoded:
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VybmFtZSIsInVpZCI6IjEyIiwiZXhwIjoxNTE2MzI1NDIyLCJpYXQiOjE1MTYyMzkwMjJ9.bbbxGrj1f9f5dMmWqVcKk4Pc0wpOuAnwhc7uHJ6eYOM
```

This project relies on JWTs for Single Sign On user authentication. Once a token is granted to a user, it can be used until it expires. As such, the token can be saved in local storage on the client side and used whenever the user visits the page. This token can be used to access various secured APIs.

For more details on JSON Web Tokens, [check out this resource](https://jwt.io/introduction/). You can also view the [RFC Standard](https://tools.ietf.org/html/rfc7519).

## Authentication API Overview
This project places all authentication-based services, such as logging in and creating a new account, under the `/auth/` namespace. Below is a description of each endpoint in the authentication namespace.

### `/auth/signin`
Used for logging in. Login information is sent in the request body as JSON, and if authentication succeeds, the user principal and token is returned to the client.

#### Request
```
POST /auth/signin HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Cache-Control: no-cache
Postman-Token: 4a7ae31d-7c75-28b8-e18f-640b7abd4451

{
	"username": "TestUser",
	"password": "password"
}
```

#### Response
```
HTTP/1.1 200 OK
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Content-Type: application/json;charset=UTF-8
Date: Fri, 19 Oct 2018 21:10:38 GMT
Expires: 0
Pragma: no-cache
Transfer-Encoding: chunked
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block

{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJUZXN0VXNlciIsInVpZCI6MSwiZXhwIjoxNTQwMDY5ODM4LCJpYXQiOjE1Mzk5ODM0Mzh9.QlKIMghftzvbtGcb2zTppB5vkyAzTxEN1POUqApmq7Y",
    "user": {
        "id": 1,
        "username": "TestUser",
        "email": "test@test.com",
        "authorities": [{
            "authority": "USER"
        }],
        "accountNonExpired": true,
        "accountNonLocked": true,
        "credentialsNonExpired": true,
        "enabled": true
    }
}
```

### `/auth/signup`
Used to create a new user, and receive an auth token. The user account information is sent as JSON in the request, and if account creation succeeds , the user principal and token is returned to the client.

#### Request
```
POST /auth/signup HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Cache-Control: no-cache

{
	"username": "TestUser1",
	"email": "test1@test.com",
	"password": "password",
	"passwordConfirm": "password",
	"firstName": "first",
	"lastName": "last"
}
```

#### Response
```
HTTP/1.1 200 OK
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Content-Type: application/json;charset=UTF-8
Date: Fri, 19 Oct 2018 21:10:38 GMT
Expires: 0
Pragma: no-cache
Transfer-Encoding: chunked
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block

{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJUZXN0VXNlcjEiLCJ1aWQiOjIsImV4cCI6MTU0MDA3MDUwMSwiaWF0IjoxNTM5OTg0MTAxfQ.aWes_OybvSVZ9o6AVGvs3XAAT9y8gLkEJBoJrFavTgg",
    "user": {
        "id": 2,
        "username": "TestUser1",
        "email": "test1@test.com",
        "authorities": [{
            "authority": "USER"
        }],
        "accountNonExpired": true,
        "accountNonLocked": true,
        "credentialsNonExpired": true,
        "enabled": true
    }
}
```

### `/auth/token_refresh`
Used to refresh a token. The token must be included in the `Authorization` HTTP header with type `bearer`, as shown below.

#### Request
```
POST /auth/token_refresh HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJUZXN0VXNlcjEiLCJ1aWQiOjIsImV4cCI6MTU0MDA3MDUwMSwiaWF0IjoxNTM5OTg0MTAxfQ.aWes_OybvSVZ9o6AVGvs3XAAT9y8gLkEJBoJrFavTgg
Cache-Control: no-cache
```

#### Response
```
HTTP/1.1 200 OK
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Content-Type: application/json;charset=UTF-8
Date: Fri, 19 Oct 2018 21:10:38 GMT
Expires: 0
Pragma: no-cache
Transfer-Encoding: chunked
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block

{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJUZXN0VXNlcjEiLCJ1aWQiOjIsImV4cCI6MTU0MDA3MDUwMSwiaWF0IjoxNTM5OTg0MTAxfQ.aWes_OybvSVZ9o6AVGvs3XAAT9y8gLkEJBoJrFavTgg",
    "user": {
        "id": 2,
        "username": "TestUser1",
        "email": "test1@test.com",
        "authorities": [{
            "authority": "USER"
        }],
        "accountNonExpired": true,
        "accountNonLocked": true,
        "credentialsNonExpired": true,
        "enabled": true
    }
}
```

### `/auth/identity_available`
Used to determine if a given username or email address is available.

#### Request
```
GET /auth/identity_available?email=test@test.com&username=TestUser8 HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Cache-Control: no-cache
```

### Response
```
HTTP/1.1 200 OK
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Content-Type: application/json;charset=UTF-8
Date: Fri, 19 Oct 2018 21:10:38 GMT
Expires: 0
Pragma: no-cache
Transfer-Encoding: chunked
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-XSS-Protection: 1; mode=block

{
    "usernameAvailable": true,
    "emailAddressAvailable": false
}
```

## Using a JWT Token (Client)
Once a token is generated by the server and returned to the client, it must be included in the `Authorization` header of any subsequent HTTP request. The token must be specified with the `Bearer` authentication type (bearer tokens to access OAuth 2.0-protected resources, used by JWT).

```
POST /auth/token_refresh HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJUZXN0VXNlcjEiLCJ1aWQiOjIsImV4cCI6MTU0MDA3MDUwMSwiaWF0IjoxNTM5OTg0MTAxfQ.aWes_OybvSVZ9o6AVGvs3XAAT9y8gLkEJBoJrFavTgg
Cache-Control: no-cache
```

Because JWT allows for stateless session management and does not rely on cookies for storing the session information on the client side, the token can be stored in the browser local storage for later use.

For more details about the Authorization header, read about it in [the MDN web docs](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Authorization).

For more details about OAuth 2 bearer token usage, consult [the RFC standard](https://tools.ietf.org/html/rfc6750).

## Configuration
JWT properties are loaded via the [Spring Boot Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html), thus they may be configured in any way that is supported by Spring externalized configuration. Be careful, however, because these properties are the backbone of the application security, and they should be secured when used in a production environment.

### `jwt.secret`
Secret key used to digitally sign the JWT header and payload. The property value must be a String with a length no greater than 32 characters. Secret keys with lengths less than or greater than 32 characters are padded or truncated.

Recommended: a highly entropic string of at least 256 bits (32 characters).
Default: secretkey

### `jwt.expiration`
The number of seconds until the token expires.

Recommended: greater than 1 day, and less than 30 days
Default: 86400 (1 day)

## Implementation Details
JWT tokens are manipulated using the `JSONWebTokenUtil` utility class. Example usage of this class can be found below.

### Example Usage
```
//Generate Token
final UserPrincipal user = new UserPrincipal(12L, "testUser", "test@test.ca", "password", Collections.singletonList(new SimpleGrantedAuthority(User.Role.USER.name())));
final String token = JSONWebTokenUtil.generateToken(userPrincipal);

...

//Validate Token
JSONWebTokenUtil.validateToken(token, user, () ->
                new MalformedAuthTokenException("Invalid token; either token is not formatted correctly or token-principal mismatch."));

...

//Use Token
final Long userId = JSONWebTokenUtil.parseUserIdFromToken(token);
final Long username = JSONWebTokenUtil.parseUsernameFromToken(token);
final Long email = JSONWebTokenUtil.parseEmailAddressFromToken(token);
final Date tokenIssuedAt = JSONWebTokenUtil.parseIssueTimeFromToken(token);
final Date tokenExpiresAt = JSONWebTokenUtil.parseExpirationTimeFromToken(token);
```

### Javadoc

#### `public static String parseUsernameFromToken(String token)`

Attempt to extract a username from the JWT token.

 * **Parameters:** `token` — The JWT token
 * **Returns:** the username of the user that made the request
 * **Exceptions:** `MalformedAuthTokenException` — if the token cannot be parsed

#### `public static String parseEmailAddressFromToken(String token)`

Attempt to extract an email address from the JWT token.

 * **Parameters:** `token` — The JWT token
 * **Returns:** the email address of the user that made the request
 * **Exceptions:** `MalformedAuthTokenException` — if the token cannot be parsed

#### `public static Long parseUserIdFromToken(String token)`

Attempt to extract a user id from the JWT token.

 * **Parameters:** `token` — The JWT token
 * **Returns:** the id of the user that made the request
 * **Exceptions:** `MalformedAuthTokenException` — if the token cannot be parsed

#### `public static Date parseIssueTimeFromToken(String token)`

Attempt to extract the issue time from the JWT token.

 * **Parameters:** `token` — The JWT token
 * **Returns:** the date representing when this token was issued
 * **Exceptions:** `MalformedAuthTokenException` — if the token cannot be parsed

#### `public static Date parseExpirationTimeFromToken(String token)`

Attempt to extract the expiration time from the JWT token.

 * **Parameters:** `token` — The JWT token
 * **Returns:** the date representing when this token will expire
 * **Exceptions:** `MalformedAuthTokenException` — if the token cannot be parsed

#### `public static String generateToken(UserPrincipal user)`

Generate a signed JWT token from the user using HS256.

 * **Parameters:** `user` — the user for which the token will be generated
 * **Returns:** the JWT token
 * **Exceptions:** `SignatureGenerationException` — if the new token cannot be signed due to an unexpected exception

#### `public static String refreshToken(String token)`

Refresh a token by updating the expirationTime claim and issueTime claim.

 * **Parameters:** `token` — the serialized SignedJWT token
 * **Returns:** a serialized refreshed SignedJWT toke
 * **Exceptions:**
   * `MalformedAuthTokenException` — if the token cannot be parsed
   * `SignatureGenerationException` — if the new token cannot be signed due to an unexpected exception

#### `public static boolean validateToken(final String token, final UserPrincipal user)`

Verify that a token:
- has a valid signature
- the token is not expired
- the token subject matches the user provided
- the token UID_CLAIM_NAME claim matches the user id provided
- the token EMAIL_ADDR_CLAIM_NAME claim matches the user email address provided

 * **Parameters:**
   * `token` — the serialized SignedJWT token
   * `user` — the user to verify the token against
 * **Returns:** whether the token is valid.

#### `public static <T extends Throwable> String validateToken(final String token, final UserPrincipal user, final Supplier<? extends T> exceptionSupplier) throws T`

Verify that a token is valid, returning the token if so, otherwise throw an exception produced by the exception supplying function.

 * **Parameters:**
   * `<T>` — Type of the exception to be thrown
   * `token` — the serialized SignedJWT token
   * `user` — the user to verify the token against
   * `exceptionSupplier` — the supplying function that produces an exception to be thrown
 * **Exceptions:** `T` — if the token is invalid.
 * **Returns:** the token, if valid.

#### `private static Date generateExpirationDate(final Date createdDate)`

Generate expiration date from a given date. The expiration date will be determined based on the `jwt.expiration` property value.

 * **Parameters:** `createdDate` — basis date
 * **Returns:** a new date that is `jwt.expiration` seconds later than the basis date.