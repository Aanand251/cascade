package com.cascade.file_service.service;

import com.cascade.file_service.entity.Role;
import com.cascade.file_service.entity.User;
import com.cascade.file_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminBootstrapService implements CommandLineRunner {

    private final UserRepository userRepository;

    @Value("${app.bootstrap-admin.enabled:false}")
    private boolean enabled;

    @Value("${app.bootstrap-admin.email:}")
    private String adminEmail;

    @Override
    @Transactional
    public void run(String... args) {

        if (!enabled) {
            return;
        }

        if (adminEmail == null || adminEmail.isBlank()) {
            throw new IllegalStateException(
                    "Bootstrap admin email is required"
            );
        }

        String email = adminEmail.trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Bootstrap admin user does not exist: " + email
                        ));

        user.setRole(Role.ADMIN);

        userRepository.save(user);
    }
}