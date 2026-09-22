package com.cascade.file_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("default")
@Slf4j
public class DevEmailService implements EmailService {

    @Override
    public void sendVerificationEmail(String email, String verificationUrl) {
        log.info("DEV EMAIL VERIFICATION: email={}, verificationUrl={}",
                email,
                verificationUrl);
    }
}