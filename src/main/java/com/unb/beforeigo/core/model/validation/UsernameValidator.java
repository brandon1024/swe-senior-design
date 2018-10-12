package com.unb.beforeigo.core.model.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.nio.charset.Charset;

/**
 * Validator for usernames. Ensures usernames satisfy the following constraints:
 * <ul>
 *     <li>cannot be null</li>
 *     <li>must have a length no less than 6 and no greater than 64</li>
 *     <li>contains only the following ASCII characters:</li>
 *     <ul>
 *         <li>Usernames can contain letters (a-z), numbers (0-9), dashes (-), underscores (_), apostrophes ('), and periods (.).</li>
 *         <li>Usernames can't contain an ampersand (&), equal sign (=), brackets (<,>), plus sign (+), comma (,), or more than one period (.) in a row.</li>
 *         <li>Usernames can't begin with non-alphanumeric characters, with a maximum of 64 characters.</li>
 *     </ul>
 * </ul>
 *
 * @author Brandon Richardson
 * */
public class UsernameValidator implements ConstraintValidator<Username, String> {

    @Override
    public void initialize(Username constraintAnnotation) {}

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(value == null) {
            return false;
        }

        if(value.length() < 6 || value.length() > 64) {
            return false;
        }

        var asciiCharset = Charset.forName("US-ASCII").newEncoder();
        if(!asciiCharset.canEncode(value)) {
            return false;
        }

        //verify begins with alpha-numeric, followed by a valid character
        if(!value.matches("\\A\\p{Alnum}[a-zA-Z0-9\\-_'.]+\\z")) {
            return false;
        }

        //verify no more than one period (.) in a row
        if(value.matches("\\A.*\\.\\..*\\z")) {
            return false;
        }

        return true;
    }
}
