package com.unb.beforeigo.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class CORSFilter extends OncePerRequestFilter {

    private static String allowedOrigin;

    private static String maxAge;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", CORSFilter.allowedOrigin);
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,PATCH,DELETE,PUT,OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", CORSFilter.maxAge);
        response.setHeader("Vary", "Accept-Encoding, Origin");
        filterChain.doFilter(request, response);
    }

    @Value("${ui.origin:http://localhost:3000}")
    public void setOrigin(final String origin) {
        CORSFilter.allowedOrigin = origin;
    }

    @Value("${ui.cors.maxAge:86400}")
    public void setMaxAge(final String maxAge) {
        CORSFilter.maxAge = maxAge;
    }
}