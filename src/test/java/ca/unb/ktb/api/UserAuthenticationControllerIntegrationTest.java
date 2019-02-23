package ca.unb.ktb.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class UserAuthenticationControllerIntegrationTest extends APIIntegrationTestSuite {

    @BeforeEach void setup() {

    }

    @Nested
    class RegistrationTest {

        @Test void registerUserTest() {

        }

        @Test void registerUserWithPasswordMismatchPreventionTest() {

        }

        @Test void registerUserWithInvalidUserPreventionTest() {

        }
    }

    @Nested
    class AuthenticationTest {

        @Test void authenticateUserByUsernameTest() {

        }

        @Test void authenticateUserByEmailTest() {

        }

        @Test void authenticateUserWithInvalidPasswordPreventionTest() {

        }
    }

    @Nested
    class TokenRefreshTest {
        @Test void authenticateUserWithInvalidPasswordPreventionTest() {

        }
    }

    @Nested
    class IdentityAvailabilityTest {

    }
}
