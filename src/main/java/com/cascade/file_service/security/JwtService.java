package com.cascade.file_service.security;

import com.cascade.file_service.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;

    @Value("${app.jwt.access-token-expiration-minutes:15}")
    private long accessTokenExpirationMinutes;

    public String generateAccessToken(User user) {

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("cascade-file-service")
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiresAt(
                        now.plus(
                                accessTokenExpirationMinutes,
                                ChronoUnit.MINUTES
                        )
                )
                .id(UUID.randomUUID().toString())
                .claim("email", user.getEmail())
                .claim("role", user.getRole().name())
                .build();

        Jwt jwt = jwtEncoder.encode(
                JwtEncoderParameters.from(claims)
        );

        return jwt.getTokenValue();
    }

    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpirationMinutes * 60;
    }
}