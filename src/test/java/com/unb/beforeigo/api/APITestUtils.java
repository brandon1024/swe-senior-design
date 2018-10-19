package com.unb.beforeigo.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;

final class APITestUtils {

    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(MapperFeature.USE_ANNOTATIONS, false);
    }

    /**
     * Convenience method used to marshall a given object to a JSON string literal. Equivalent to:
     *
     * <pre>
     *  ObjectMapper mapper = new ObjectMapper();
     *  mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
     *  mapper.configure(MapperFeature.USE_ANNOTATIONS, false);
     *  String json = mapper.writeValueAsString(object);
     * </pre>
     *
     * Note that null fields are ignored.
     *
     * @param object The object to marshall to a JSON literal
     * @return a JSON literal of the object
     * */
    static <T> String marshallToJSONLiteral(T object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }

    /**
     * Build an HTTP request entity for testing purposes, with a request body that is a JSON literal.
     *
     * Adds the following headers:
     * Content-Type: application/json
     * Accept: application/json
     *
     * @param requestJSON a JSON literal
     * @return an HTTP Entity
     * */
    static HttpEntity<String> buildHTTPRequest(String requestJSON) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        return new HttpEntity<>(requestJSON, headers);
    }

    /**
     * Build an HTTP request entity for testing purposes, with a request body that is a JSON literal.
     *
     * The bearer token is added to the request Authentication header.
     *
     * Adds the following headers:
     * Content-Type: application/json
     * Accept: application/json
     * Authorization: Bearer token
     *
     * @param requestJSON a JSON literal
     * @param bearerToken the JSON Web Token
     * @return an HTTP Entity
     * */
    static HttpEntity<String> buildAuthenticatedHTTPRequest(String requestJSON, String bearerToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);

        return new HttpEntity<>(requestJSON, headers);
    }

    /**
     * Build an HTTP request entity for testing purposes, with a request body that is a JSON literal.
     *
     * The bearer token is added to the request Authentication header.
     *
     * Adds the following headers:
     * Authorization: Bearer token
     *
     * @param bearerToken the JSON Web Token
     * @return an HTTP Entity
     * */
    static HttpEntity<String> buildAuthenticatedHTTPRequest(String bearerToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);

        return new HttpEntity<>(headers);
    }
}
