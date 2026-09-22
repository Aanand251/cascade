package com.cascade.file_service.service;

import com.cascade.file_service.dto.LoginRequest;
import com.cascade.file_service.dto.LoginResponse;
import com.cascade.file_service.dto.RegisterRequest;
import com.cascade.file_service.entity.EmailVerificationToken;
import com.cascade.file_service.entity.Role;
import com.cascade.file_service.entity.User;
import com.cascade.file_service.repository.EmailVerificationTokenRepository;
import com.cascade.file_service.repository.UserRepository;
import com.cascade.file_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.email-verification.expiration-minutes:30}")
    private long expirationMinutes;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public void register(RegisterRequest request) {

        String email = normalizeEmail(request.email());

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Unable to register account");
        }

        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .enabled(true)
                .emailVerified(false)
                .build();

        userRepository.save(user);

        String rawToken = generateToken();

        EmailVerificationToken verificationToken =
                EmailVerificationToken.builder()
                        .user(user)
                        .tokenHash(hashToken(rawToken))
                        .expiresAt(
                                OffsetDateTime.now()
                                        .plusMinutes(expirationMinutes)
                        )
                        .build();

        tokenRepository.save(verificationToken);

        String verificationUrl =
                frontendUrl + "/verify-email?token=" + rawToken;

        emailService.sendVerificationEmail(
                email,
                verificationUrl
        );
    }

    @Transactional
    public void verifyEmail(String rawToken) {

        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException(
                    "Invalid verification token"
            );
        }

        String tokenHash = hashToken(rawToken);

        EmailVerificationToken verificationToken =
                tokenRepository.findByTokenHash(tokenHash)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Invalid verification token"
                                ));

        if (verificationToken.getUsedAt() != null) {
            throw new IllegalArgumentException(
                    "Invalid verification token"
            );
        }

        if (verificationToken.getExpiresAt()
                .isBefore(OffsetDateTime.now())) {

            throw new IllegalArgumentException(
                    "Invalid verification token"
            );
        }

        User user = verificationToken.getUser();

        user.setEmailVerified(true);
        user.setEmailVerifiedAt(OffsetDateTime.now());

        verificationToken.setUsedAt(OffsetDateTime.now());

        userRepository.save(user);
        tokenRepository.save(verificationToken);
    }

    public LoginResponse login(LoginRequest request) {

        String email = normalizeEmail(request.email());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        request.password()
                )
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid credentials"
                        ));

        if (!user.isEnabled()) {
            throw new IllegalArgumentException(
                    "Account is disabled"
            );
        }

        if (!user.isEmailVerified()) {
            throw new IllegalArgumentException(
                    "Email is not verified"
            );
        }

        String accessToken =
                jwtService.generateAccessToken(user);

        return new LoginResponse(
                accessToken,
                "Bearer",
                jwtService.getAccessTokenExpirationSeconds()
        );
    }

    private String generateToken() {

        byte[] bytes = new byte[32];

        secureRandom.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }

    private String hashToken(String token) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder result = new StringBuilder(64);

            for (byte b : hash) {
                result.append(
                        String.format("%02x", b)
                );
            }

            return result.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "SHA-256 algorithm unavailable",
                    e
            );
        }
    }

    private String normalizeEmail(String email) {

        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}