package info.mackiewicz.bankapp.system.notification.email.template;

import info.mackiewicz.bankapp.system.notification.email.EmailContent;

/**
 * Interface for email template management.
 */
public interface EmailTemplateProvider {
    /**
     * Gets welcome email template.
     * @param userName user's name for personalization
     * @param username user's username for personalization
     * @return email content and subject
     */
    EmailContent getWelcomeEmail(String userName, String username);

    /**
     * Gets password reset email template.
     * @param userName user's name for personalization
     * @param resetLink password reset link
     * @return email content and subject
     */
    EmailContent getPasswordResetEmail(String userName, String resetLink);

    /**
     * Gets password reset confirmation email template.
     * @param userName user's name for personalization
     * @param loginLink login page link
     * @return email content and subject
     */
    EmailContent getPasswordResetConfirmationEmail(String userName, String loginLink);
}