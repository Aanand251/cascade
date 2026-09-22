package com.cascade.file_service.service;

public interface EmailService {

    void sendVerificationEmail(String email, String verificationUrl);
}