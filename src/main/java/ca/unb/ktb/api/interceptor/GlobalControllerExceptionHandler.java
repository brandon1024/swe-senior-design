package ca.unb.ktb.api.interceptor;

import ca.unb.ktb.api.exception.client.BadRequestException;
import ca.unb.ktb.api.exception.client.ConflictException;
import ca.unb.ktb.api.exception.client.ForbiddenException;
import ca.unb.ktb.api.exception.client.GoneException;
import ca.unb.ktb.api.exception.client.MethodNotAllowedException;
import ca.unb.ktb.api.exception.client.NotFoundException;
import ca.unb.ktb.api.exception.client.TeapotException;
import ca.unb.ktb.api.exception.client.UnauthorizedException;
import ca.unb.ktb.api.exception.server.InternalServerErrorException;
import ca.unb.ktb.api.exception.server.NotImplementedException;
import ca.unb.ktb.api.exception.server.ServiceUnavailableException;
import ca.unb.ktb.core.svc.exception.MissingS3BucketConfigurationException;
import ca.unb.ktb.infrastructure.security.exception.MalformedAuthTokenException;
import ca.unb.ktb.infrastructure.security.exception.MissingAuthTokenException;
import ca.unb.ktb.infrastructure.security.exception.SignatureGenerationException;
import ca.unb.ktb.infrastructure.security.exception.UserNotFoundException;
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

    /*
     * API Layer Exception Handlers
     * */
    @ExceptionHandler(value = InternalServerErrorException.class)
    public ResponseEntity<String> handleInternalServerErrorException(final InternalServerErrorException e) {
        LOG.error("Intercepted InternalServerErrorException. {}", e.getMessage());
        LOG.debug("Intercepted InternalServerErrorException: {}", e);

        final String message = "Unable to process request due to an internal server error.";
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = NotImplementedException.class)
    public ResponseEntity<String> handleNotImplementedException(final NotImplementedException e) {
        LOG.warn("Intercepted NotImplementedException. {}", e.getMessage());
        LOG.debug("Intercepted NotImplementedException: {}", e);

        final String message = "Unable to process request because the requested resource is not implemented.";
        return new ResponseEntity<>(message, HttpStatus.NOT_IMPLEMENTED);
    }

    @ExceptionHandler(value = ServiceUnavailableException.class)
    public ResponseEntity<String> handleServiceUnavailableException(final ServiceUnavailableException e) {
        LOG.warn("Intercepted ServiceUnavailableException. {}", e.getMessage());
        LOG.debug("Intercepted ServiceUnavailableException: {}", e);

        final String message = "Unable to process request because the requested server is unavailable.";
        return new ResponseEntity<>(message, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(final BadRequestException e) {
        LOG.warn("Intercepted BadRequestException. {}", e.getMessage());
        LOG.debug("Intercepted BadRequestException: {}", e);

        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = ConflictException.class)
    public ResponseEntity<String> handleConflictException(final ConflictException e) {
        LOG.warn("Intercepted ConflictException. {}", e.getMessage());
        LOG.debug("Intercepted ConflictException: {}", e);

        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = ForbiddenException.class)
    public ResponseEntity<String> handleForbiddenException(final ForbiddenException e) {
        LOG.warn("Intercepted ForbiddenException. {}", e.getMessage());
        LOG.debug("Intercepted ForbiddenException: {}", e);

        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = GoneException.class)
    public ResponseEntity<String> handleGoneException(final GoneException e) {
        LOG.warn("Intercepted GoneException. {}", e.getMessage());
        LOG.debug("Intercepted GoneException: {}", e);

        return new ResponseEntity<>(e.getMessage(), HttpStatus.GONE);
    }

    @ExceptionHandler(value = MethodNotAllowedException.class)
    public ResponseEntity<String> handleMethodNotAllowedException(final MethodNotAllowedException e) {
        LOG.warn("Intercepted MethodNotAllowedException. {}", e.getMessage());
        LOG.debug("Intercepted MethodNotAllowedException: {}", e);

        return new ResponseEntity<>(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(final NotFoundException e) {
        LOG.warn("Intercepted NotFoundException. {}", e.getMessage());
        LOG.debug("Intercepted NotFoundException: {}", e);

        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = TeapotException.class)
    public ResponseEntity<String> handleTeapotException(final TeapotException e) {
        LOG.warn("Intercepted TeapotException. {}", e.getMessage());
        LOG.debug("Intercepted TeapotException: {}", e);

        return new ResponseEntity<>(e.getMessage(), HttpStatus.I_AM_A_TEAPOT);
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedException(final UnauthorizedException e) {
        LOG.warn("Intercepted UnauthorizedException. {}", e.getMessage());
        LOG.debug("Intercepted UnauthorizedException: {}", e);

        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }



    /*
     * Service Layer Exception Handlers
     * */
    @ExceptionHandler(value = MissingS3BucketConfigurationException.class)
    public ResponseEntity<String> handleMissingS3BucketConfigurationException(final MissingS3BucketConfigurationException e) {
        LOG.error("Misconfiguration of Spring resulted in failure to communicate with AWS S3: {}", e.getMessage());
        LOG.debug("Intercepted UnauthorizedException: {}", e);

        final String message = "Unable to process request due to an internal server error.";
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    /*
     * Infrastructure Layer Exception Handlers
     * */
    @ExceptionHandler(value = MalformedAuthTokenException.class)
    public ResponseEntity<String> handleMalformedAuthTokenException(final HibernateException e) {
        LOG.warn("User attempted to authenticate with a malformed authentication token: {}", e.getMessage());
        LOG.debug("Intercepted MalformedAuthTokenException: {}", e);

        final String message = "Malformed token resulted in failure to authenticate.";
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = MissingAuthTokenException.class)
    public ResponseEntity<String> handleMissingAuthTokenException(final HibernateException e) {
        LOG.warn("User attempted to authenticate without providing an auth token: {}", e.getMessage());
        LOG.debug("Intercepted MalformedAuthTokenException: {}", e);

        final String message = "Unable to authenticate user without auth token.";
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = SignatureGenerationException.class)
    public ResponseEntity<String> handleSignatureGenerationException(final HibernateException e) {
        LOG.error("Failure to sign JWT: {}", e.getMessage());
        LOG.debug("Intercepted MalformedAuthTokenException: {}", e);

        final String message = "Unable to authenticate user at this time. Please try again later.";
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(final HibernateException e) {
        LOG.info("User could not be found: {}", e.getMessage());
        LOG.debug("Intercepted MalformedAuthTokenException: {}", e);

        final String message = "User could not be found with the credentials provided.";
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }



    /*
     * Spring Framework Exception Handlers
     * */
    @ExceptionHandler(value = HibernateException.class)
    public ResponseEntity<String> handleHibernateException(final HibernateException e) {
        LOG.error("Intercepted HibernateException: {}", e.getMessage());
        LOG.debug("Intercepted MalformedAuthTokenException: {}", e);

        final String message = "Unable to process request due to malformed request.";
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = AuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationException(final AuthenticationException e) {
        LOG.info("User authentication failed: {}", e.getMessage());

        final String message = "Failed to authenticate user. Please try again.";
        return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
    }
}
