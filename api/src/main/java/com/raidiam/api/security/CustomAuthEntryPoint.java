package com.raidiam.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.raidiam.api.dtos.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.server.resource.introspection.BadOpaqueTokenException;
import org.springframework.security.authentication.InsufficientAuthenticationException;

import java.io.IOException;
import java.time.Instant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuthEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        ErrorResponse error;
        int status;

        if (hasCause(authException, AuthServerUnavailableException.class)) {
            status = HttpServletResponse.SC_SERVICE_UNAVAILABLE;
            error = new ErrorResponse(
                    "auth_server_error",
                    "Auth server may be unavailable",
                    status,
                    Instant.now());
        } else if (hasCause(authException, BadOpaqueTokenException.class)) {
            status = HttpServletResponse.SC_UNAUTHORIZED;
            error = new ErrorResponse(
                    "invalid_token",
                    "Invalid or expired access token",
                    status,
                    Instant.now());
        } else if (hasCause(authException, InsufficientAuthenticationException.class)) {
            status = HttpServletResponse.SC_UNAUTHORIZED;
            error = new ErrorResponse(
                    "test",
                    "Invalid or expired access token",
                    status,
                    Instant.now());
        } else {
            status = HttpServletResponse.SC_UNAUTHORIZED;
            error = new ErrorResponse(
                    "unauthorized",
                    "Authentication failed",
                    status,
                    Instant.now());
        }

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), error);
    }

    private boolean hasCause(Throwable ex, Class<? extends Throwable> type) {
        while (ex != null) {
            if (type.isInstance(ex)) {
                return true;
            }
            ex = ex.getCause();
        }
        return false;
    }
}