package com.unb.beforeigo.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.unb.beforeigo.core.model.User;
import com.unb.beforeigo.core.model.UserRelationship;
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
class UserRelationshipControllerIntegrationTest extends APIIntegrationTestSuite {

    private static long id = 0;

    private User user1;
    private User user2;

    @BeforeEach void setup() {
        user1 = new User();
        user1.setEmail("test" + id + "@test.ca");
        user1.setUsername("username" + id);
        user1.setPassword("password" + id);
        user1.setFirstName("first" + id);
        user1.setLastName("last" + id);
        user1.setBio("bio" + id);

        id++;

        user2 = new User();
        user2.setEmail("test" + id + "@test.ca");
        user2.setUsername("username" + id);
        user2.setPassword("password" + id);
        user2.setFirstName("first" + id);
        user2.setLastName("last" + id);
        user2.setBio("bio" + id);

        id++;
    }

    @Nested
    class CreateUserRelationshipTest {

        @BeforeEach void setup() throws JsonProcessingException {
            //Create user using the create API
            var userJSON = APITestUtils.marshallToJSONLiteral(user1);
            var userEntity = APITestUtils.buildHTTPRequest(userJSON);
            var userCreateResponse = restTemplate.exchange("/users", HttpMethod.POST, userEntity, User.class);
            Assertions.assertEquals(HttpStatus.OK, userCreateResponse.getStatusCode());
            Assertions.assertNotNull(userCreateResponse.getBody());
            Assertions.assertNotNull(userCreateResponse.getBody().getId());

            user1 = userCreateResponse.getBody();

            //Create user using the create API
            userJSON = APITestUtils.marshallToJSONLiteral(user2);
            userEntity = APITestUtils.buildHTTPRequest(userJSON);
            userCreateResponse = restTemplate.exchange("/users", HttpMethod.POST, userEntity, User.class);
            Assertions.assertEquals(HttpStatus.OK, userCreateResponse.getStatusCode());
            Assertions.assertNotNull(userCreateResponse.getBody());
            Assertions.assertNotNull(userCreateResponse.getBody().getId());

            user2 = userCreateResponse.getBody();
        }

        @Test void createRelationshipWithUnknownUsersTest() {
            //Attempt to create user relationship using create API
            var dummyId = user1.getId() + user2.getId();
            var relationshipCreateResponse = restTemplate.exchange("/users/{id1}/followers?id={id2}", HttpMethod.POST, HttpEntity.EMPTY, Void.class, Map.of("id1", user1.getId(), "id2", dummyId));
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, relationshipCreateResponse.getStatusCode());
        }

        @Test void createDuplicateRelationshipTest() throws JsonProcessingException {
            //Create user relationship using create API
            var relationshipCreateResponse = restTemplate.exchange("/users/{id1}/followers?id={id2}", HttpMethod.POST, HttpEntity.EMPTY, Void.class, Map.of("id1", user1.getId(), "id2", user2.getId()));
            Assertions.assertEquals(HttpStatus.OK, relationshipCreateResponse.getStatusCode());

            //Attempt to create user relationship using create API
            relationshipCreateResponse = restTemplate.exchange("/users/{id1}/followers?id={id2}", HttpMethod.POST, HttpEntity.EMPTY, Void.class, Map.of("id1", user1.getId(), "id2", user2.getId()));
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, relationshipCreateResponse.getStatusCode());
        }

        @Test void createRelationshipSuccessTest() {
            //Create user relationship using create API
            var relationshipCreateResponse = restTemplate.exchange("/users/{id1}/followers?id={id2}", HttpMethod.POST, HttpEntity.EMPTY, UserRelationship.class, Map.of("id1", user1.getId(), "id2", user2.getId()));
            Assertions.assertEquals(HttpStatus.OK, relationshipCreateResponse.getStatusCode());
            Assertions.assertNotNull(relationshipCreateResponse.getBody());
            Assertions.assertNotNull(relationshipCreateResponse.getBody().getId());
            Assertions.assertEquals(user2.getId(), relationshipCreateResponse.getBody().getFollower().getId());
            Assertions.assertEquals(user1.getId(), relationshipCreateResponse.getBody().getFollowing().getId());
        }
    }

    @Nested
    class RetrieveUserRelationshipTest {

        private UserRelationship relationship;

        @BeforeEach void setup() throws JsonProcessingException {
            //Create user using the create API
            var userJSON = APITestUtils.marshallToJSONLiteral(user1);
            var userEntity = APITestUtils.buildHTTPRequest(userJSON);
            var userCreateResponse = restTemplate.exchange("/users", HttpMethod.POST, userEntity, User.class);
            Assertions.assertEquals(HttpStatus.OK, userCreateResponse.getStatusCode());
            Assertions.assertNotNull(userCreateResponse.getBody());
            Assertions.assertNotNull(userCreateResponse.getBody().getId());

            user1 = userCreateResponse.getBody();

            //Create user using the create API
            userJSON = APITestUtils.marshallToJSONLiteral(user2);
            userEntity = APITestUtils.buildHTTPRequest(userJSON);
            userCreateResponse = restTemplate.exchange("/users", HttpMethod.POST, userEntity, User.class);
            Assertions.assertEquals(HttpStatus.OK, userCreateResponse.getStatusCode());
            Assertions.assertNotNull(userCreateResponse.getBody());
            Assertions.assertNotNull(userCreateResponse.getBody().getId());

            user2 = userCreateResponse.getBody();

            //Create user relationship using the create API
            var relationshipCreateResponse = restTemplate.exchange("/users/{id1}/followers?id={id2}", HttpMethod.POST, HttpEntity.EMPTY, UserRelationship.class, Map.of("id1", user1.getId(), "id2", user2.getId()));
            Assertions.assertEquals(HttpStatus.OK, relationshipCreateResponse.getStatusCode());
            Assertions.assertNotNull(relationshipCreateResponse.getBody());
            Assertions.assertNotNull(relationshipCreateResponse.getBody().getId());
            Assertions.assertEquals(user2.getId(), relationshipCreateResponse.getBody().getFollower().getId());
            Assertions.assertEquals(user1.getId(), relationshipCreateResponse.getBody().getFollowing().getId());

            relationship = relationshipCreateResponse.getBody();
        }

        @Test void retrieveUsersFollowingUser() {
            //Create user relationship using the create API
            var followersRetrieveResponse = restTemplate.exchange("/users/{id}/followers", HttpMethod.GET, HttpEntity.EMPTY, User[].class, Map.of("id", user1.getId()));
            Assertions.assertEquals(HttpStatus.OK, followersRetrieveResponse.getStatusCode());
            Assertions.assertNotNull(followersRetrieveResponse.getBody());
            Assertions.assertEquals(1, followersRetrieveResponse.getBody().length);
            Assertions.assertEquals(user2.getId(), followersRetrieveResponse.getBody()[0].getId());
        }

        @Test void retrieveUsersFollowedByUser() {
            //Create user relationship using the create API
            var followersRetrieveResponse = restTemplate.exchange("/users/{id}/following", HttpMethod.GET, HttpEntity.EMPTY, User[].class, Map.of("id", user1.getId()));
            Assertions.assertEquals(HttpStatus.OK, followersRetrieveResponse.getStatusCode());
            Assertions.assertNotNull(followersRetrieveResponse.getBody());
            Assertions.assertEquals(0, followersRetrieveResponse.getBody().length);

            //Create user relationship using the create API
            var relationshipCreateResponse = restTemplate.exchange("/users/{id2}/followers?id={id1}", HttpMethod.POST, HttpEntity.EMPTY, UserRelationship.class, Map.of("id2", user2.getId(), "id1", user1.getId()));
            Assertions.assertEquals(HttpStatus.OK, relationshipCreateResponse.getStatusCode());
            Assertions.assertNotNull(relationshipCreateResponse.getBody());
            Assertions.assertNotNull(relationshipCreateResponse.getBody().getId());
            Assertions.assertEquals(user1.getId(), relationshipCreateResponse.getBody().getFollower().getId());
            Assertions.assertEquals(user2.getId(), relationshipCreateResponse.getBody().getFollowing().getId());

            //Create user relationship using the create API
            followersRetrieveResponse = restTemplate.exchange("/users/{id}/following", HttpMethod.GET, HttpEntity.EMPTY, User[].class, Map.of("id", user1.getId()));
            Assertions.assertEquals(HttpStatus.OK, followersRetrieveResponse.getStatusCode());
            Assertions.assertNotNull(followersRetrieveResponse.getBody());
            Assertions.assertEquals(1, followersRetrieveResponse.getBody().length);
            Assertions.assertEquals(user2.getId(), followersRetrieveResponse.getBody()[0].getId());
        }
    }

    @Nested
    class DeleteUserRelationshipTest {
        @Test void deleteRelationshipByIdTest() {

        }

        @Test void deleteUnknownRelationshipTest() {

        }
    }
}
