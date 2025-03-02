package info.mackiewicz.bankapp.notification.email;

/**
 * Interface for email sending functionality.
 * Abstracts the actual email sending mechanism.
 */
public interface EmailSender {
    /**
     * Sends an email with given parameters.
     * @param to recipient email address
     * @param subject email subject
     * @param htmlContent email content in HTML format
     * @return email ID from the sending service
     */
    String send(String to, String subject, String htmlContent);
}