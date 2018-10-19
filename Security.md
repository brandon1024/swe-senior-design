# Spring Security and Authentication with JSON Web Tokens
## Introduction to JWT
JSON Web Token (JWT) is an open standard for creating tokens that assert some number of claims. It is typically used to provide a secure method for communicating JSON securely between a client and server. Requests made to the server are digitally signed with a secret key on the server side using the HMAC algorithm. This digital signature is used to verify the integrity of the request made against the server. JWTs may also be encrypted, but this project relies only on signed JWTs.

For example, a server could generate a token that has the claim "logged in as admin" and provide that to a client. The client could then use that token to prove to the server that they are logged in as admin. The tokens are signed by the server's secret key, so the server is able to verify that the token is legitimate.

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
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1NDAwNzA2OTIsImlhdCI6MTUzOTk4NDI5Mn0.9xaYrVuWI9Y7VI9xnU1TNqdEkB22rS3xct73l5dzs4M",
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

## Using a JWT Token
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