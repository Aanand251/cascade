package com.cascade.file_service.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

public final class AuthenticatedUser {

    private AuthenticatedUser() {
    }

    public static Long getId(Authentication authentication) {

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof Jwt jwt)) {

            throw new IllegalStateException(
                    "Authenticated JWT is required"
            );
        }

        return Long.valueOf(jwt.getSubject());
    }
}