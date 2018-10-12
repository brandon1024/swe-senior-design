package com.unb.beforeigo.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.unb.beforeigo.core.model.Bucket;
import com.unb.beforeigo.core.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Map;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class BucketControllerIntegrationTest extends APIIntegrationTestSuite {

    private static long id = 0;

    private User user;

    @BeforeEach void setup() throws JsonProcessingException {
        user = new User();
        user.setEmail("test" + id + "@test.ca");
        user.setUsername("username" + id);
        user.setPassword("password" + id);
        user.setFirstName("first" + id);
        user.setLastName("last" + id);
        user.setBio("bio" + id);

        //Create user using the create API
        var userJSON = APITestUtils.marshallToJSONLiteral(user);
        var userEntity = APITestUtils.buildHTTPRequest(userJSON);
        var userCreateResponse = restTemplate.exchange("/users", HttpMethod.POST, userEntity, User.class);
        Assertions.assertEquals(HttpStatus.OK, userCreateResponse.getStatusCode());
        Assertions.assertNotNull(userCreateResponse.getBody());
        Assertions.assertNotNull(userCreateResponse.getBody().getId());

        user = userCreateResponse.getBody();
    }

    @AfterEach void testTeardown() {
        id++;
    }

    @Nested
    class CreateBucketTest {

        @Test void createBucketWithUnknownOwnerTest() throws JsonProcessingException {
            Bucket bucket = new Bucket();
            bucket.setName("ExampleName" + id);
            bucket.setIsPublic(true);
            bucket.setDescription("ExampleDescription" + id);

            //Create bucket using create API
            var bucketJSON = APITestUtils.marshallToJSONLiteral(bucket);
            var bucketEntity = APITestUtils.buildHTTPRequest(bucketJSON);
            var bucketCreateResponse = restTemplate.exchange("/users/{id}/buckets", HttpMethod.POST, bucketEntity, Void.class, Map.of("id", user.getId()+1));
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, bucketCreateResponse.getStatusCode());
        }

        @Test void createBucketWithNonNullIdTest() throws JsonProcessingException {
            Bucket bucket = new Bucket();
            bucket.setName("ExampleName" + id);
            bucket.setIsPublic(true);
            bucket.setDescription("ExampleDescription" + id);
            bucket.setId(id);

            //Create bucket using create API
            var bucketJSON = APITestUtils.marshallToJSONLiteral(bucket);
            var bucketEntity = APITestUtils.buildHTTPRequest(bucketJSON);
            var bucketCreateResponse = restTemplate.exchange("/users/{id}/buckets", HttpMethod.POST, bucketEntity, Void.class, Map.of("id", user.getId()));
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, bucketCreateResponse.getStatusCode());
        }

        @Test void createBucketSuccessTest() throws JsonProcessingException {
            Bucket bucket = new Bucket();
            bucket.setName("ExampleName" + id);
            bucket.setIsPublic(true);
            bucket.setDescription("ExampleDescription" + id);

            //Create bucket using create API
            var bucketJSON = APITestUtils.marshallToJSONLiteral(bucket);
            var bucketEntity = APITestUtils.buildHTTPRequest(bucketJSON);
            var bucketCreateResponse = restTemplate.exchange("/users/{id}/buckets", HttpMethod.POST, bucketEntity, Bucket.class, Map.of("id", user.getId()));
            Assertions.assertEquals(HttpStatus.OK, bucketCreateResponse.getStatusCode());
            Assertions.assertNotNull(bucketCreateResponse.getBody());
            Assertions.assertNotNull(bucketCreateResponse.getBody().getId());
        }
    }

    @Nested
    class RetrieveBucketTest {

        private Bucket bucket;

        @BeforeEach void setup() throws JsonProcessingException {
            bucket = new Bucket();
            bucket.setName("ExampleName" + id);
            bucket.setIsPublic(true);
            bucket.setDescription("ExampleDescription" + id);

            //Create bucket using create API
            var bucketJSON = APITestUtils.marshallToJSONLiteral(bucket);
            var bucketEntity = APITestUtils.buildHTTPRequest(bucketJSON);
            var bucketCreateResponse = restTemplate.exchange("/users/{id}/buckets", HttpMethod.POST, bucketEntity, Bucket.class, Map.of("id", user.getId()));
            Assertions.assertEquals(HttpStatus.OK, bucketCreateResponse.getStatusCode());
            Assertions.assertNotNull(bucketCreateResponse.getBody());
            Assertions.assertNotNull(bucketCreateResponse.getBody().getId());

            bucket = bucketCreateResponse.getBody();
        }

        @Test void retrieveBucketsByUserTest() {
            var bucketRetrieveResponse = restTemplate.exchange("/users/{id}/buckets", HttpMethod.GET, HttpEntity.EMPTY, Bucket[].class, Map.of("id", user.getId()));
            Assertions.assertEquals(HttpStatus.OK, bucketRetrieveResponse.getStatusCode());
            Assertions.assertNotNull(bucketRetrieveResponse.getBody());
            Assertions.assertEquals(1, bucketRetrieveResponse.getBody().length);
            Assertions.assertEquals(bucket.getId(), bucketRetrieveResponse.getBody()[0].getId());
        }

        @Test void retrieveUnknownBucketTest() {
            var bucketRetrieveResponse = restTemplate.exchange("/users/{id}/buckets", HttpMethod.GET, HttpEntity.EMPTY, Void.class, Map.of("id", user.getId()+1));
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, bucketRetrieveResponse.getStatusCode());
        }

        @Test void retrieveBucketByIdTest() {
            var bucketRetrieveResponse = restTemplate.exchange("/users/{userId}/buckets/{bucketId}", HttpMethod.GET, HttpEntity.EMPTY, Bucket.class, Map.of("userId", user.getId(), "bucketId", bucket.getId()));
            Assertions.assertEquals(HttpStatus.OK, bucketRetrieveResponse.getStatusCode());
            Assertions.assertNotNull(bucketRetrieveResponse.getBody());
            Assertions.assertEquals(bucket.getId(), bucketRetrieveResponse.getBody().getId());
        }
    }

    @Nested
    class UpdateBucketTest {

        private Bucket bucket;

        @BeforeEach void setup() throws JsonProcessingException {
            bucket = new Bucket();
            bucket.setName("ExampleName" + id);
            bucket.setIsPublic(true);
            bucket.setDescription("ExampleDescription" + id);

            //Create bucket using create API
            var bucketJSON = APITestUtils.marshallToJSONLiteral(bucket);
            var bucketEntity = APITestUtils.buildHTTPRequest(bucketJSON);
            var bucketCreateResponse = restTemplate.exchange("/users/{id}/buckets", HttpMethod.POST, bucketEntity, Bucket.class, Map.of("id", user.getId()));
            Assertions.assertEquals(HttpStatus.OK, bucketCreateResponse.getStatusCode());
            Assertions.assertNotNull(bucketCreateResponse.getBody());
            Assertions.assertNotNull(bucketCreateResponse.getBody().getId());

            bucket = bucketCreateResponse.getBody();
        }

        @Test void updateBucketTest() throws JsonProcessingException {
            bucket.setName("New Bucket Name");
            bucket.setIsPublic(false);

            //Create bucket using create API
            var bucketJSON = APITestUtils.marshallToJSONLiteral(bucket);
            var bucketEntity = APITestUtils.buildHTTPRequest(bucketJSON);
            var bucketUpdateResponse = restTemplate.exchange("/users/{userId}/buckets/{bucketId}", HttpMethod.PUT, bucketEntity, Bucket.class, Map.of("userId", user.getId(), "bucketId", bucket.getId()));
            Assertions.assertEquals(HttpStatus.OK, bucketUpdateResponse.getStatusCode());
            Assertions.assertNotNull(bucketUpdateResponse.getBody());
            Assertions.assertEquals(bucket.getId(), bucketUpdateResponse.getBody().getId());
            Assertions.assertEquals(bucket.getName(), bucketUpdateResponse.getBody().getName());
            Assertions.assertEquals(bucket.getIsPublic(), bucketUpdateResponse.getBody().getIsPublic());
        }
    }

    @Nested
    class DeleteBucketTest {

        private Bucket bucket;

        @BeforeEach void setup() throws JsonProcessingException {
            bucket = new Bucket();
            bucket.setName("ExampleName" + id);
            bucket.setIsPublic(true);
            bucket.setDescription("ExampleDescription" + id);

            //Create user relationship using create API
            var bucketJSON = APITestUtils.marshallToJSONLiteral(bucket);
            var bucketEntity = APITestUtils.buildHTTPRequest(bucketJSON);
            var bucketCreateResponse = restTemplate.exchange("/users/{id}/buckets", HttpMethod.POST, bucketEntity, Bucket.class, Map.of("id", user.getId()));
            Assertions.assertEquals(HttpStatus.OK, bucketCreateResponse.getStatusCode());
            Assertions.assertNotNull(bucketCreateResponse.getBody());
            Assertions.assertNotNull(bucketCreateResponse.getBody().getId());

            bucket = bucketCreateResponse.getBody();
        }

        @Test void deleteBucketTest() {
            var bucketDeleteResponse = restTemplate.exchange("/users/{userId}/buckets/{bucketId}", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, Map.of("userId", user.getId(), "bucketId", bucket.getId()));
            Assertions.assertEquals(HttpStatus.OK, bucketDeleteResponse.getStatusCode());

            var bucketRetrieveResponse = restTemplate.exchange("/users/{userId}/buckets/{bucketId}", HttpMethod.GET, HttpEntity.EMPTY, Void.class, Map.of("userId", user.getId(), "bucketId", bucket.getId()));
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, bucketRetrieveResponse.getStatusCode());
        }

        @Test void deleteUnknownBucketTest() {
            var bucketDeleteResponse = restTemplate.exchange("/users/{userId}/buckets/{bucketId}", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, Map.of("userId", user.getId(), "bucketId", bucket.getId()+1));
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, bucketDeleteResponse.getStatusCode());
        }
    }
}
