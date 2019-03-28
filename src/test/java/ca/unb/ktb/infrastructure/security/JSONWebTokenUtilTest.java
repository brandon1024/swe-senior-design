package ca.unb.ktb.infrastructure.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class JSONWebTokenUtilTest {

    private UserPrincipal userPrincipal;

    @BeforeEach void setup() {
        userPrincipal = new UserPrincipal(1L, "testUsername", "test@email.com",
                "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Nested
    class ParseTokenTest {

        private String token;

        @BeforeEach void setup() {
            token = JSONWebTokenUtil.generateToken(userPrincipal);
        }

        @Test
        void parseUsernameFromTokenTest() {
            Assertions.assertEquals(userPrincipal.getUsername(), JSONWebTokenUtil.parseUsernameFromToken(token));
        }

        @Test
        void parseUserIdFromTokenTest() {
            Assertions.assertEquals(userPrincipal.getId(), JSONWebTokenUtil.parseUserIdFromToken(token));
        }
    }

    @Test void generateTokenTest() {
        String token = JSONWebTokenUtil.generateToken(userPrincipal);
        Assertions.assertTrue(JSONWebTokenUtil.validateToken(token, userPrincipal));
    }

    @Test void validateTokenTest() {
        String token = JSONWebTokenUtil.generateToken(userPrincipal);
        userPrincipal = new UserPrincipal(2L, "testUser", "test1@email.com",
                "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        Assertions.assertFalse(JSONWebTokenUtil.validateToken(token, userPrincipal));
        Assertions.assertThrows(RuntimeException.class, () ->
            JSONWebTokenUtil.validateToken(token, userPrincipal, RuntimeException::new));
    }
}
