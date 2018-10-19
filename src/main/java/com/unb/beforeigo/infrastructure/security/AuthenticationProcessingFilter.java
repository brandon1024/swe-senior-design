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
     * */
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
            throws ServletException, IOException {
        LOG.debug("processing authentication for '{}'", request.getRequestURL());

        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(isTokenPresent(authorizationHeader)) {
            final String authToken = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();
            final Long userId = JSONWebTokenUtil.parseUserIdFromToken(authToken);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            //If principal is authenticated, verify token validity
            if(authentication != null) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

                JSONWebTokenUtil.validateToken(authToken, userPrincipal, () ->
                    new MalformedAuthTokenException("Invalid token; either token is not formatted correctly or token-principal mismatch."));
            } else {
                UserPrincipal userPrincipal = this.userPrincipalService.loadUserById(userId);

                JSONWebTokenUtil.validateToken(authToken, userPrincipal, () ->
                        new MalformedAuthTokenException("Invalid token; either token is not formatted correctly or token-principal mismatch."));

                //if token is valid, add principal to security context
                UsernamePasswordAuthenticationToken newAuthentication =
                        new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
                newAuthentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(newAuthentication);
            }
        }

        chain.doFilter(request, response);
    }

    /**
     * Determine whether the authorization header contains a bearer type token.
     *
     * @param authorizationHeader the authorization header value
     * @return true if the authorization header value is not null and begins with "bearer ", returns false otherwise.
     * */
    private static boolean isTokenPresent(final String authorizationHeader) {
        return authorizationHeader != null && authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }
}
