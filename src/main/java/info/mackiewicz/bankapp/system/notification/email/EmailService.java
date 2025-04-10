package info.mackiewicz.bankapp.system.notification.email;

import info.mackiewicz.bankapp.system.notification.email.exception.EmailSendingException;
import info.mackiewicz.bankapp.system.notification.email.template.EmailTemplateProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for sending various types of emails.
 * Uses EmailSender for delivery and EmailTemplateProvider for content generation.
 */
@Slf4j
@Service
public class EmailService {

    private final EmailSender emailSender;
    private final EmailTemplateProvider templateProvider;
    private final String baseUrl;

    public EmailService(
            EmailSender emailSender,
            EmailTemplateProvider templateProvider,
            @Value("${app.base-url:http://localhost:8080}") String baseUrl) {
        this.emailSender = emailSender;
        this.templateProvider = templateProvider;
        this.baseUrl = baseUrl;
    }

    /**
     * Sends welcome email to new user.
     * @param email recipient's email address
     * @param fullNameOfUser full name of user for personalization
     * @param username username of the user for personalization
     */
    public void sendWelcomeEmail(String email, String fullNameOfUser, String username) {
        try {
            EmailContent content = templateProvider.getWelcomeEmail(fullNameOfUser, username);
            emailSender.send(email, content.subject(), content.htmlContent());
        } catch (RuntimeException e) {
            log.error("Error sending welcome email to {}", email, e);
            throw new EmailSendingException("Error sending welcome email", e);
        }
    }

    /**
     * Sends password reset email with reset link.
     * @param email recipient's email address
     * @param token password reset token
     * @param fullNameOfUser full name of user for personalization
     */
    public void sendPasswordResetEmail(String email, String token, String fullNameOfUser) {
        try {
            String resetLink = baseUrl + "/password-reset/token/" + token;
            EmailContent content = templateProvider.getPasswordResetEmail(fullNameOfUser, resetLink);
            emailSender.send(email, content.subject(), content.htmlContent());
        } catch (RuntimeException e) {
            log.error("Error sending password reset email to {}", email, e);
            throw new EmailSendingException("Error sending password reset email to: " + email, e);
        }
    }

    /**
     * Sends password reset confirmation email.
     * @param email recipient's email address
     */
    public void sendPasswordResetConfirmation(String email, String fullNameOfUser) {
        try {
            String loginLink = baseUrl + "/login";
            EmailContent content = templateProvider.getPasswordResetConfirmationEmail(fullNameOfUser, loginLink);
            emailSender.send(email, content.subject(), content.htmlContent());
        } catch (RuntimeException e) {
            throw new EmailSendingException("Error sending password reset confirmation email to: " + email, e);
        }
    }
}