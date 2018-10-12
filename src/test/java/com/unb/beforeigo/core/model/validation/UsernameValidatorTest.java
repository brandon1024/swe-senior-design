package com.unb.beforeigo.core.model.validation;

import com.unb.beforeigo.core.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Arrays;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class UsernameValidatorTest {

    private static Validator validator;
    private User user;

    @BeforeAll static void setup() {
        var factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach void setupEach() {
        user = new User();
        user.setEmail("test@test.ca");
        user.setFirstName("first");
        user.setLastName("last");
        user.setPassword("test");
    }

    @Test void isValidWithNullUsernameTest() {
        user.setUsername(null);
        var violations = validator.validate(user);
        Assertions.assertFalse(violations.isEmpty(), "isValid must return false given null");
    }

    @Test void isValidUsernameLengthValidationTest() {
        //Test length less than 6
        var strBytes = new char[5];
        Arrays.fill(strBytes, 'c');
        user.setUsername(new String(strBytes));
        var violations = validator.validate(user);
        Assertions.assertFalse(violations.isEmpty(), "isValid must return false given username of length less than 6");

        //Test length greater than 64
        strBytes = new char[65];
        Arrays.fill(strBytes, 'c');
        user.setUsername(new String(strBytes));
        violations = validator.validate(user);
        Assertions.assertFalse(violations.isEmpty(), "isValid must return false given username of length greater than 64");

        //Test valid length
        strBytes = new char[32];
        Arrays.fill(strBytes, 'c');
        user.setUsername(new String(strBytes));
        violations = validator.validate(user);
        Assertions.assertTrue(violations.isEmpty(), "isValid must return true given valid username");
    }

    @Test void isValidUsernameEncodingValidationTest() {
        user.setUsername("testUsernameΣ");
        var violations = validator.validate(user);
        Assertions.assertFalse(violations.isEmpty(), "isValid must return false given username with invalid character encoding");
    }

    @Test void isValidUsernameCharacterValidationTest() {
        user.setUsername("test..Username");
        var violations = validator.validate(user);
        Assertions.assertFalse(violations.isEmpty(), "isValid must return false given username with two consecutive periods (..)");

        user.setUsername("test-U.s_e'rname90");
        violations = validator.validate(user);
        Assertions.assertTrue(violations.isEmpty(), "isValid must return true given valid username");

        user.setUsername("-testUsername");
        violations = validator.validate(user);
        Assertions.assertFalse(violations.isEmpty(), "isValid must return false given username prefixed by a non-alphanumeric character");

        user.setUsername("0TestUsername");
        violations = validator.validate(user);
        Assertions.assertTrue(violations.isEmpty(), "isValid must return true given valid username");

    }
}
