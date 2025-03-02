package info.mackiewicz.bankapp.notification.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.notification.email.template.EmailTemplateProvider;

/**
 * Service for sending various types of emails.
 * Uses EmailSender for delivery and EmailTemplateProvider for content generation.
 */
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
     */
    public void sendWelcomeEmail(String email, String fullNameOfUser) {
        EmailContent content = templateProvider.getWelcomeEmail(fullNameOfUser);
        emailSender.send(email, content.subject(), content.htmlContent());
    }

    /**
     * Sends password reset email with reset link.
     * @param email recipient's email address
     * @param token password reset token
     * @param fullNameOfUser full name of user for personalization
     */
    public void sendPasswordResetEmail(String email, String token, String fullNameOfUser) {
        String resetLink = baseUrl + "/password-reset/token/" + token;
        EmailContent content = templateProvider.getPasswordResetEmail(fullNameOfUser, resetLink);
        emailSender.send(email, content.subject(), content.htmlContent());
    }

    /**
     * Sends password reset confirmation email.
     * @param email recipient's email address
     */
    public void sendPasswordResetConfirmation(String email, String fullNameOfUser) {
        String loginLink = baseUrl + "/login";
        EmailContent content = templateProvider.getPasswordResetConfirmationEmail(fullNameOfUser, loginLink);
        emailSender.send(email, content.subject(), content.htmlContent());
    }
}
