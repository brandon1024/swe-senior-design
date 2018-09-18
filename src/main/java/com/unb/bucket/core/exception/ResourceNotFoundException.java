package com.unb.bucket.core.exception;

import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception used to signal that a specific resource could not be found.
 * */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
@Value
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;
}
