package info.mackiewicz.bankapp.notification.email.template;

import info.mackiewicz.bankapp.notification.email.EmailContent;

/**
 * Provider interface for email templates. This interface defines methods for obtaining
 * various types of email templates used in the application. Each method returns
 * a complete email content with subject and body.
 */
public interface EmailTemplateProvider {
    /**
     * Creates a welcome email template for new users.
     *
     * @param userName user's full name for personalization of the email content
     * @return EmailContent object containing the welcome email subject and HTML body
     */
    EmailContent getWelcomeEmail(String userName);

    /**
     * Creates a password reset email template containing a reset link.
     *
     * @param userName user's full name for personalization of the email content
     * @param resetLink full URL to the password reset page
     * @return EmailContent object containing the password reset email subject and HTML body
     */
    EmailContent getPasswordResetEmail(String userName, String resetLink);

    /**
     * Creates a password reset confirmation email template after successful password change.
     *
     * @param userName user's full name for personalization of the email content
     * @param loginLink full URL to the login page
     * @return EmailContent object containing the confirmation email subject and HTML body
     */
    EmailContent getPasswordResetConfirmationEmail(String userName, String loginLink);
}