package com.unb.beforeigo.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class AuthenticationEntryPoint implements org.springframework.security.web.AuthenticationEntryPoint {

    /**
     * Invoked when user tries to access a secured REST resource without supplying any credentials. Responds with a
     * 401 Unauthorized response.
     *
     * @param request That resulted in an <code>AuthenticationException</code>.
     * @param response So that the user agent can begin authentication.
     * @param authException That caused the invocation.
     */
    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response,
                         final AuthenticationException authException) throws IOException {
        LOG.error("User attempted to access secured REST resource without supplying credentials.", authException);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
