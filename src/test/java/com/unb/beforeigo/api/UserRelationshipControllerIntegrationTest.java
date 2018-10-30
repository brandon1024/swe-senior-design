package com.unb.beforeigo.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserRelationshipControllerIntegrationTest extends APIIntegrationTestSuite {

    @BeforeEach void setup() {

    }

    @Nested
    class CreateUserRelationshipTest {

        @Test void createUserRelationshipTest() {

        }

        @Test void createUserRelationshipAgainstOtherUserPreventionTest() {

        }

        @Test void createUserRelationshipWithSelfPreventionTest() {

        }
    }

    @Nested
    class RetrieveUserRelationshipTest {


        @Test void retrieveUsersFollowingUserTest() {

        }

        @Test void retrieveUsersFollowingUnknownUserTest() {

        }

        @Test void retrieveUsersFollowedByUserTest() {

        }

        @Test void retrieveUsersFollowedByUnknownUserTest() {

        }
    }

    @Nested
    class DeleteUserRelationshipTest {
        @Test void deleteUserRelationshipTest() {

        }

        @Test void deleteUserRelationshipAgainstOtherUserPreventionTest() {

        }
    }
}
