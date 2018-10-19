package com.unb.beforeigo.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.unb.beforeigo.api.dto.request.AuthenticationRequest;
import com.unb.beforeigo.api.dto.request.UserRegistrationRequest;
import com.unb.beforeigo.api.dto.response.AuthenticationResponse;
import com.unb.beforeigo.core.model.User;
import com.unb.beforeigo.infrastructure.security.UserPrincipal;
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
class UserControllerIntegrationTest extends APIIntegrationTestSuite {

    private static int id = 0;

    private User user;

    private UserPrincipal authenticatedUser;

    private String authToken;

    @BeforeEach void testSetup() throws JsonProcessingException {
        user = new User();
        user.setEmail("test" + id + "@test.ca");
        user.setUsername("username" + id);
        user.setPassword("password" + id);
        user.setFirstName("first" + id);
        user.setLastName("last" + id);
        user.setBio("bio" + id);

        UserRegistrationRequest registrationRequest = new UserRegistrationRequest(user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getPassword(),
                user.getFirstName(),
                user.getMiddleName(),
                user.getLastName());

        var requestJSON = APITestUtils.marshallToJSONLiteral(registrationRequest);
        var entity = APITestUtils.buildHTTPRequest(requestJSON);
        var response = restTemplate.exchange("/auth/signup", HttpMethod.POST, entity, AuthenticationResponse.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());

        authenticatedUser = response.getBody().getUser();
        authToken = response.getBody().getToken();
    }

    @AfterEach void testTeardown() {
        id++;
    }

    @Nested
    class RetrieveUserTest {

        @Test void retrieveUserByIdTest() {
            //Retrieve user by id using the retrieve API
            var entity = APITestUtils.buildAuthenticatedHTTPRequest(authToken);
            var retrieveResponse = restTemplate.exchange("/users?id={id}", HttpMethod.GET, entity, User[].class, Map.of("id", user.getId()));
            Assertions.assertEquals(HttpStatus.OK, retrieveResponse.getStatusCode());
            Assertions.assertNotNull(retrieveResponse.getBody());
            Assertions.assertEquals(1, retrieveResponse.getBody().length);
            Assertions.assertEquals(user.getId(), retrieveResponse.getBody()[0].getId());
        }

        @Test void retrieveUserByUsernameTest() {
            //Retrieve user by username using the retrieve API
            var retrieveResponse = restTemplate.exchange("/users?username={username}", HttpMethod.GET, HttpEntity.EMPTY, User[].class, Map.of("username", user.getUsername()));
            Assertions.assertEquals(HttpStatus.OK, retrieveResponse.getStatusCode());
            Assertions.assertNotNull(retrieveResponse.getBody());
            Assertions.assertEquals(1, retrieveResponse.getBody().length);
            Assertions.assertEquals(user.getId(), retrieveResponse.getBody()[0].getId());
        }

        @Test void retrieveUserByEmailAddressTest() {
            //Retrieve user by email using the retrieve API
            var retrieveResponse = restTemplate.exchange("/users?email={email}", HttpMethod.GET, HttpEntity.EMPTY, User[].class, Map.of("email", user.getEmail()));
            Assertions.assertEquals(HttpStatus.OK, retrieveResponse.getStatusCode());
            Assertions.assertNotNull(retrieveResponse.getBody());
            Assertions.assertEquals(1, retrieveResponse.getBody().length);
            Assertions.assertEquals(user.getId(), retrieveResponse.getBody()[0].getId());
        }
    }

    @Nested
    class UpdateUserTest {
        @BeforeEach void setup() throws JsonProcessingException {
            //Create user using the create API
            var requestJSON = APITestUtils.marshallToJSONLiteral(user);
            var entity = APITestUtils.buildHTTPRequest(requestJSON);
            var createResponse = restTemplate.exchange("/users", HttpMethod.POST, entity, User.class);
            Assertions.assertEquals(HttpStatus.OK, createResponse.getStatusCode());
            Assertions.assertNotNull(createResponse.getBody());
            Assertions.assertNotNull(createResponse.getBody().getId());

            user = createResponse.getBody();
        }

        @Test void updateUserFieldsInvalidTest() throws JsonProcessingException {
            //Update user with valid fields
            user.setFirstName("firstTest");
            user.setMiddleName("middleTest");
            user.setLastName("lastTest");
            user.setPassword("passTest");
            user.setBio("bioTest");
            user.setUsername("usernameTest");
            user.setEmail("emailTest");

            //Update user using the update API
            var requestJSON = APITestUtils.marshallToJSONLiteral(user);
            var entity = APITestUtils.buildHTTPRequest(requestJSON);
            var updateResponse = restTemplate.exchange("/users/{id}", HttpMethod.PUT, entity, User.class, Map.of("id", user.getId()));
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, updateResponse.getStatusCode());
        }

        @Test void updateEntireUserTest() throws JsonProcessingException {
            //Update user with valid fields
            user.setFirstName("firstTest1");
            user.setMiddleName("middleTest1");
            user.setLastName("lastTest1");
            user.setPassword("passTest1");
            user.setBio("bioTest1");
            user.setUsername("usernameTest1");
            user.setEmail("emailTest1@test.ca");

            //Update user using the update API
            var requestJSON = APITestUtils.marshallToJSONLiteral(user);
            var entity = APITestUtils.buildHTTPRequest(requestJSON);
            var updateResponse = restTemplate.exchange("/users/{id}", HttpMethod.PUT, entity, User.class, Map.of("id", user.getId()));
            var updatedUser = updateResponse.getBody();
            Assertions.assertNotNull(updatedUser);
            Assertions.assertEquals(user.getId(), updatedUser.getId());
            Assertions.assertEquals(user.getFirstName(), updatedUser.getFirstName());
            Assertions.assertEquals(user.getMiddleName(), updatedUser.getMiddleName());
            Assertions.assertEquals(user.getLastName(), updatedUser.getLastName());
            Assertions.assertEquals(user.getBio(), updatedUser.getBio());
            Assertions.assertEquals(user.getUsername(), updatedUser.getUsername());
            Assertions.assertEquals(user.getEmail(), updatedUser.getEmail());
        }
    }

    @Nested
    class DeleteUserTest {
        @Test void deleteUserByIdTest() throws JsonProcessingException {
            //Create user using the create API
            var requestJSON = APITestUtils.marshallToJSONLiteral(user);
            var entity = APITestUtils.buildHTTPRequest(requestJSON);
            var createResponse = restTemplate.exchange("/users", HttpMethod.POST, entity, User.class);
            Assertions.assertEquals(HttpStatus.OK, createResponse.getStatusCode());
            Assertions.assertNotNull(createResponse.getBody());
            Assertions.assertNotNull(createResponse.getBody().getId());

            //Delete user using delete API
            var deleteResponse = restTemplate.exchange("/users/{id}", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, Map.of("id", createResponse.getBody().getId()));
            Assertions.assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());

            //Check if user exists using retrieve API
            var retrieveResponse = restTemplate.exchange("/users?email={email}", HttpMethod.GET, HttpEntity.EMPTY, User[].class, Map.of("email", user.getEmail()));
            Assertions.assertEquals(HttpStatus.OK, retrieveResponse.getStatusCode());
            Assertions.assertNotNull(retrieveResponse.getBody());
        }

        @Test void deleteUnknownUserTest() {
            //Delete user using delete API
            var deleteResponse = restTemplate.exchange("/users/{id}", HttpMethod.DELETE, HttpEntity.EMPTY, Void.class, Map.of("id", id));
            Assertions.assertEquals(HttpStatus.BAD_REQUEST, deleteResponse.getStatusCode());
        }
    }
}
