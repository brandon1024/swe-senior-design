package com.unb.beforeigo.api.interceptor;

import com.unb.beforeigo.api.exception.client.BadRequestException;
import com.unb.beforeigo.api.exception.client.ConflictException;
import com.unb.beforeigo.api.exception.client.ForbiddenException;
import com.unb.beforeigo.api.exception.client.GoneException;
import com.unb.beforeigo.api.exception.client.MethodNotAllowedException;
import com.unb.beforeigo.api.exception.client.NotFoundException;
import com.unb.beforeigo.api.exception.client.TeapotException;
import com.unb.beforeigo.api.exception.client.UnauthorizedException;
import com.unb.beforeigo.api.exception.server.InternalServerErrorException;
import com.unb.beforeigo.api.exception.server.NotImplementedException;
import com.unb.beforeigo.api.exception.server.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {

    /* === Server Errors === */
    @ExceptionHandler(value = InternalServerErrorException.class)
    public ResponseEntity<String> handleInternalServerErrorException(final InternalServerErrorException e) {
        LOG.info("Exception intercepted by GlobalControllerExceptionHandler", e);

        final String message = "Unable to process request due to an internal server error.";
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = NotImplementedException.class)
    public ResponseEntity<String> handleNotImplementedException(final NotImplementedException e) {
        LOG.info("Exception intercepted by GlobalControllerExceptionHandler", e);

        final String message = "Unable to process request because the requested resource is not implemented.";
        return new ResponseEntity<>(message, HttpStatus.NOT_IMPLEMENTED);
    }

    @ExceptionHandler(value = ServiceUnavailableException.class)
    public ResponseEntity<String> handleServiceUnavailableException(final ServiceUnavailableException e) {
        LOG.info("Exception intercepted by GlobalControllerExceptionHandler", e);

        final String message = "Unable to process request because the requested server is unavailable.";
        return new ResponseEntity<>(message, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(value = HibernateException.class)
    public ResponseEntity<String> handleHibernateException(final HibernateException e) {
        LOG.info("Exception intercepted by GlobalControllerExceptionHandler", e);

        final String message = "Unable to process request due to malformed request.";
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = AuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationException(final AuthenticationException e) {
        LOG.info("Exception intercepted by GlobalControllerExceptionHandler", e);

        final String message = "Unable to process the request due to an unexpected authentication error.";
        return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
    }


    /* === Client Errors === */
    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(final BadRequestException e) {
        LOG.info("Exception intercepted by GlobalControllerExceptionHandler", e);

        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ConflictException.class)
    public ResponseEntity<String> handleConflictException(final ConflictException e) {
        LOG.info("Exception intercepted by GlobalControllerExceptionHandler", e);

        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = ForbiddenException.class)
    public ResponseEntity<String> handleForbiddenException(final ForbiddenException e) {
        LOG.info("Exception intercepted by GlobalControllerExceptionHandler", e);

        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = GoneException.class)
    public ResponseEntity<String> handleGoneException(final GoneException e) {
        LOG.info("Exception intercepted by GlobalControllerExceptionHandler", e);

        return new ResponseEntity<>(e.getMessage(), HttpStatus.GONE);
    }

    @ExceptionHandler(value = MethodNotAllowedException.class)
    public ResponseEntity<String> handleMethodNotAllowedException(final MethodNotAllowedException e) {
        LOG.info("Exception intercepted by GlobalControllerExceptionHandler", e);

        return new ResponseEntity<>(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(final NotFoundException e) {
        LOG.info("Exception intercepted by GlobalControllerExceptionHandler", e);

        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = TeapotException.class)
    public ResponseEntity<String> handleTeapotException(final TeapotException e) {
        LOG.info("Exception intercepted by GlobalControllerExceptionHandler", e);

        return new ResponseEntity<>(e.getMessage(), HttpStatus.I_AM_A_TEAPOT);
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedException(final UnauthorizedException e) {
        LOG.info("Exception intercepted by GlobalControllerExceptionHandler", e);

        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }
}
