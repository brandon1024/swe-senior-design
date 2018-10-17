package com.unb.beforeigo.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    public AuthenticationProcessingFilter(UserPrincipalService userPrincipalService) {
        this.userPrincipalService = userPrincipalService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        LOG.debug("processing authentication for '{}'", request.getRequestURL());

        final String requestHeader = request.getHeader("Authorization");
        String authToken = null;
        Long userId = null;

        if(requestHeader != null && requestHeader.startsWith("Bearer ")) {
            authToken = requestHeader.substring(7);
            userId = JSONWebTokenUtil.parseUserIdFromToken(authToken);
        }

        if(userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
           UserPrincipal userDetails = this.userPrincipalService.loadUserById(userId);

            //For simple validation it is completely sufficient to just check the token integrity.
            if(JSONWebTokenUtil.validateToken(authToken, userDetails)) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }
}
