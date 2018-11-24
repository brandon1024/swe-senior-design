package com.unb.beforeigo.infrastructure.security;

import com.unb.beforeigo.infrastructure.security.exception.MalformedAuthTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
class AuthenticationProcessingFilter extends OncePerRequestFilter {

    private final UserPrincipalService userPrincipalService;

    private static final String AUTHENTICATION_SCHEME = "Bearer";

    public AuthenticationProcessingFilter(UserPrincipalService userPrincipalService) {
        this.userPrincipalService = userPrincipalService;
    }

    /**
     * Intercept the request to verify the validity of the auth token, if present.
     *
     * If the Authorization header is present on the request, and the authorization type is "bearer", checks whether
     * the current security context holds the authorized principal and verifies that the principal matches the token.
     *
     * If the security context does not hold an authorized principal, uses the userId extracted from the token to
     * load the user details and sets the user as the security context authenticated principal.
     *
     * Requests without the Authorization header are processed normally.
     *
     * @param request The request.
     * @param response The response.
     * @param chain The filter chain.
     * */
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain chain) throws ServletException, IOException {
        LOG.debug("Processing authentication for '{}'", request.getRequestURL());

        final Optional<String> authToken = getTokenFromAuthorizationHeader(request.getHeader(HttpHeaders.AUTHORIZATION));
        if(authToken.isPresent()) {
            final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if(Objects.nonNull(authentication)) {
                //If principal is authenticated, verify token validity
                validateTokenAgainstAuthentication(authToken.get(), authentication);
            } else {
                //if token is valid, add principal to security context
                authenticateTokenHolder(authToken.get(), request);
            }
        }

        chain.doFilter(request, response);
    }

    /**
     * Extract, if present, the bearer token from the authorization header value.
     *
     * @param authorizationHeader The authorization header value.
     * @return An Optional holding a String if the token is present, and returns empty optional otherwise.
     * */
    private static Optional<String> getTokenFromAuthorizationHeader(final String authorizationHeader) {
        if(Objects.isNull(authorizationHeader)) {
            return Optional.empty();
        }

        if(!authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ")) {
            return Optional.empty();
        }

        final String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();
        return Optional.of(token);
    }

    /**
     * Extract the UserPrincipal from the authentication, and validate the given auth token against it.
     *
     * @param token The signed JWT token.
     * @param authentication The current authentication.
     * @throws MalformedAuthTokenException If the token is malformed (does not meet validation).
     * */
    private void validateTokenAgainstAuthentication(final String token, final Authentication authentication) {
        final UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        JSONWebTokenUtil.validateToken(token, userPrincipal, () ->
                new MalformedAuthTokenException("Invalid token; either token is not formatted correctly or token-principal mismatch."));
    }

    /**
     * Authenticate the token holder by extracting the user ID from the token, loading the user details and
     * authenticating the user.
     *
     * @param token The signed JWT token.
     * @param request The request.
     * @throws MalformedAuthTokenException If the token is malformed (does not meet validation).
     * */
    private void authenticateTokenHolder(final String token, HttpServletRequest request) {
        final Long userId = JSONWebTokenUtil.parseUserIdFromToken(token);
        final UserPrincipal userPrincipal = this.userPrincipalService.loadUserById(userId);

        JSONWebTokenUtil.validateToken(token, userPrincipal, () ->
                new MalformedAuthTokenException("Invalid token; either token is not formatted correctly or token-principal mismatch."));

        //if token is valid, add principal to security context
        final UsernamePasswordAuthenticationToken newAuthentication =
                new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
        newAuthentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
    }
}
