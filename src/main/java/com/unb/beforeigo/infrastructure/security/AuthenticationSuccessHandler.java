package com.unb.beforeigo.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthenticationSuccessHandler
        implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {

    /**
     * Called when a user has been successfully authenticated.
     *
     * @param request The request which caused the successful authentication.
     * @param response The response.
     * @param authentication The Authentication object which was created during the authentication process.
     */
    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response,
                                        final Authentication authentication) {}
}
