package com.unb.beforeigo.infrastructure.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.util.DateUtils;
import com.unb.beforeigo.infrastructure.security.exception.MalformedAuthTokenException;
import com.unb.beforeigo.infrastructure.security.exception.SignatureGenerationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

@Component
public final class JSONWebTokenUtil {

    private static final String UID_CLAIM_NAME = "uid";

    private static final String EMAIL_ADDR_CLAIM_NAME = "ead";

    private static byte[] secret;

    private static Long expiration;

    /**
     * Attempt to extract a username from the JWT token.
     *
     * @param token The JWT token.
     * @return The username of the user that made the request.
     */
    public static String parseUsernameFromToken(String token) {
        JWTClaimsSet cs = JSONWebTokenUtil.parseTokenClaimSet(token);

        return cs.getSubject();
    }

    /**
     * Attempt to extract an email address from the JWT token.
     *
     * @param token The JWT token.
     * @return The email address of the user that made the request.
     * @throws MalformedAuthTokenException If the token cannot be parsed.
     */
    public static String parseEmailAddressFromToken(String token) {
        JWTClaimsSet cs = JSONWebTokenUtil.parseTokenClaimSet(token);

        try {
            return cs.getStringClaim(EMAIL_ADDR_CLAIM_NAME);
        } catch(ParseException e) {
            throw new MalformedAuthTokenException("Unable to parse Long from 'uid' claim value", e);
        }
    }

    /**
     * Attempt to extract a user id from the JWT token.
     *
     * @param token The JWT token.
     * @return The id of the user that made the request.
     */
    public static Long parseUserIdFromToken(String token) {
        JWTClaimsSet cs = JSONWebTokenUtil.parseTokenClaimSet(token);

        try {
            return cs.getLongClaim(UID_CLAIM_NAME);
        } catch(ParseException e) {
            throw new MalformedAuthTokenException("Unable to parse Long from 'uid' claim value", e);
        }
    }

    /**
     * Attempt to extract the issue time from the JWT token.
     *
     * @param token The JWT token.
     * @return The date representing when this token was issued.
     */
    public static Date parseIssueTimeFromToken(String token) {
        JWTClaimsSet cs = JSONWebTokenUtil.parseTokenClaimSet(token);

        return cs.getIssueTime();
    }

    /**
     * Attempt to extract the expiration time from the JWT token.
     *
     * @param token The JWT token.
     * @return The date representing when this token will expire.
     */
    public static Date parseExpirationTimeFromToken(String token) {
        JWTClaimsSet cs = JSONWebTokenUtil.parseTokenClaimSet(token);

        return cs.getExpirationTime();
    }

    /**
     * Parse the JWTClaimSet from the JWT token.
     *
     * @param token The JWT token.
     * @return The JWTClaimSet for the token.
     * @throws MalformedAuthTokenException If the token cannot be parsed.
     * */
    private static JWTClaimsSet parseTokenClaimSet(String token) {
        try {
            return JWTParser.parse(token).getJWTClaimsSet();
        } catch(ParseException e) {
            throw new MalformedAuthTokenException("Unable to parse token", e);
        }
    }

    /**
     * Generate a signed JWT token from the user using HS256.
     *
     * @param user The user for which the token will be generated.
     * @return The JWT token.
     * @throws SignatureGenerationException If the new token cannot be signed due to an unexpected exception.
     */
    public static String generateToken(UserPrincipal user) {
        Date currentTime = new Date();
        Date expirationTime = JSONWebTokenUtil.generateExpirationDate(currentTime);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .claim(UID_CLAIM_NAME, user.getId())
                .claim(EMAIL_ADDR_CLAIM_NAME, user.getEmail())
                .expirationTime(expirationTime)
                .issueTime(currentTime)
                .notBeforeTime(currentTime)
                .jwtID(UUID.randomUUID().toString())
                .build();

        SignedJWT token = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);

        try {
            JWSSigner signer = new MACSigner(secret);
            token.sign(signer);
        } catch(JOSEException e) {
            throw new SignatureGenerationException("Unable to sign token due to unexpected JOSEException.", e);
        }

        return token.serialize();
    }

    /**
     * Refresh a token by updating the expirationTime claim and issueTime claim.
     *
     * @param token The serialized SignedJWT token.
     * @return A serialized refreshed SignedJWT token.
     * @throws MalformedAuthTokenException If the token cannot be parsed.
     * @throws SignatureGenerationException If the new token cannot be signed due to an unexpected exception.
     * */
    public static String refreshToken(String token) {
        SignedJWT oldToken;
        JWTClaimsSet cs;

        try {
            oldToken = SignedJWT.parse(token);
            cs = oldToken.getJWTClaimsSet();
        } catch(ParseException e) {
            throw new MalformedAuthTokenException("Unable to parse token", e);
        }

        Date currentTime = new Date();
        Date expirationTime = JSONWebTokenUtil.generateExpirationDate(currentTime);
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(cs.getSubject())
                .claim(UID_CLAIM_NAME, cs.getClaim(UID_CLAIM_NAME))
                .claim(EMAIL_ADDR_CLAIM_NAME, cs.getClaim(EMAIL_ADDR_CLAIM_NAME))
                .expirationTime(expirationTime)
                .issueTime(currentTime)
                .notBeforeTime(currentTime)
                .jwtID(UUID.randomUUID().toString())
                .build();

        SignedJWT newToken = new SignedJWT(oldToken.getHeader(), claims);
        try {
            JWSSigner signer = new MACSigner(secret);
            newToken.sign(signer);
        } catch(JOSEException e) {
            throw new SignatureGenerationException("Unable to sign token due to unexpected JOSEException.", e);
        }

        return newToken.serialize();
    }

    /**
     * Verify that a token:
     * <ul>
     *  <li>has a valid signature</li>
     *  <li>the token is not expired</li>
     *  <li>the token subject matches the user provided</li>
     *  <li>the token UID_CLAIM_NAME claim matches the user id provided</li>
     *  <li>the token EMAIL_ADDR_CLAIM_NAME claim matches the user email address provided</li>
     * </ul>
     *
     * @param token The serialized SignedJWT token.
     * @param user The user to verify the token against.
     * @return Whether the token is valid.
     * */
    public static boolean validateToken(final String token, final UserPrincipal user) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(secret);

            if(!signedJWT.verify(verifier)) {
                return false;
            }

            JWTClaimsSet cs = signedJWT.getJWTClaimsSet();
            Date currentTime = new Date();
            if(DateUtils.isAfter(currentTime, cs.getExpirationTime(), 60L)) {
                return false;
            }

            if(!Objects.equals(user.getId(), cs.getClaim(UID_CLAIM_NAME))) {
                return false;
            }

            if(!Objects.equals(user.getEmail(), cs.getClaim(EMAIL_ADDR_CLAIM_NAME))) {
                return false;
            }

            if(!Objects.equals(user.getUsername(), cs.getSubject())) {
                return false;
            }
        } catch (JOSEException | ParseException e) {
            return false;
        }

        return true;
    }

    /**
     * Verify that a token is valid, returning the token if so, otherwise throw an exception produced by the exception
     * supplying function.
     *
     * @param <T> Type of the exception to be thrown.
     * @param token The serialized SignedJWT token.
     * @param user The user to verify the token against.
     * @param exceptionSupplier The supplying function that produces an exception to be thrown.
     * @throws T If the token is invalid.
     * @return The token, if valid.
     * */
    public static <T extends Throwable> String validateToken(final String token,
                                                             final UserPrincipal user,
                                                             final Supplier<? extends T> exceptionSupplier) throws T {
        if(validateToken(token, user)) {
            return token;
        }

        throw exceptionSupplier.get();
    }

    /**
     * Generate expiration date from a given date. The expiration date will be determined based on the
     * <pre>jwt.expiration</pre> property value.
     *
     * @param createdDate Basis date.
     * @return A new date that is <pre>jwt.expiration</pre> seconds later than the basis date.
     * */
    private static Date generateExpirationDate(final Date createdDate) {
        return new Date(createdDate.getTime() + expiration * 1000);
    }

    @Value("${jwt.secret:secretkey}")
    public void setSecret(final String secret) {
        JSONWebTokenUtil.secret = Arrays.copyOf(secret.getBytes(), 32);
    }

    @Value("${jwt.expiration:86400}")
    public void setTokenExpiration(final long expiration) {
        JSONWebTokenUtil.expiration = expiration;
    }
}
