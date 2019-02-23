package ca.unb.ktb.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserControllerIntegrationTest extends APIIntegrationTestSuite {

    @BeforeEach void setup() {

    }

    @Nested
    class RetrieveUserTest {

        @Test void retrieveUsersTest() {

        }

        @Test void retrieveUsersWithNoParameters() {

        }

        @Test void retrieveUserByIdTest() {

        }

        @Test void retrieveUserByNonexistentIdTest() {

        }
    }

    @Nested
    class UpdateUserTest {

        @Test void patchUserTest() {

        }

        @Test void patchAgainstOtherUserPreventionTest() {

        }

        @Test void updateUserTest() {

        }

        @Test void updateAgainstOtherUserPreventionTest() {

        }
    }

    @Nested
    class DeleteUserTest {
        @Test void deleteUserByIdTest() {

        }

        @Test void deleteUnknownUserTest() {

        }

        @Test void deleteAgainstOtherUserPreventionTest() {

        }
    }
}
