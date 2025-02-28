package info.mackiewicz.bankapp.service.email.template;

/**
 * Data class for email content.
 * Contains both subject and HTML content.
 */
public record EmailContent(String subject, String htmlContent) {
    /**
     * Creates a new EmailContent instance.
     * @param subject email subject
     * @param htmlContent email HTML content
     */
    public EmailContent {
        if (subject == null || subject.trim().isEmpty()) {
            throw new IllegalArgumentException("Subject cannot be null or empty");
        }
        if (htmlContent == null || htmlContent.trim().isEmpty()) {
            throw new IllegalArgumentException("HTML content cannot be null or empty");
        }
    }
}