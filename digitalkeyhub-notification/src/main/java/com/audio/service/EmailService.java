package com.audio.service;


import com.audio.exception.EmailSendingException;

import java.util.Map;

public interface EmailService {
    void sendPaymentReceipt(String to, String subject, Map<String, Object> templateData)
            throws EmailSendingException;

    void sendWelcomeEmail(String to, String username) throws EmailSendingException;

    void sendPasswordResetEmail(String to, String resetLink) throws EmailSendingException;
}
