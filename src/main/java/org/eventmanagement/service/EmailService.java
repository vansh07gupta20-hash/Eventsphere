
package org.eventmanagement.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${app.frontend.reset-url}")
    private String resetUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(String recipientEmail, String token) {

        try {
            String resetLink = resetUrl + "?token=" + token;

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(recipientEmail);
            helper.setSubject("EventSphere - Password Reset");

            String emailContent =
                    "<h2>Password Reset Request</h2>" +
                            "<p>We received a request to reset your password.</p>" +
                            "<p>Click the link below to reset your password:</p>" +
                            "<a href=\"" + resetLink + "\">Reset My Password</a>" +
                            "<p>This link will expire in 30 minutes.</p>" +
                            "<p>If you did not request this, please ignore this email.</p>";

            helper.setText(emailContent, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Unable to send password reset email", e);
        }
    }
}
