package info.mackiewicz.bankapp.notification.email.template;

/**
 * Base abstract class for all email templates. Provides common HTML structure and styling
 * for all email templates in the application. This class implements a template method pattern
 * where concrete classes only need to provide the specific content and subject of the email.
 *
 * This class ensures consistent email styling and structure across all email communications
 * by providing a base HTML template with predefined CSS styles.
 *
 * The template method pattern is used to ensure that all email templates follow the same
 * structure and styling while allowing specific implementations to define their own content
 * and subject. Template classes should extend this class and implement the abstract methods.
 *
 * @see WelcomeEmailTemplate
 * @see PasswordResetEmailTemplate
 * @see PasswordResetConfirmationTemplate
 */
public abstract class EmailTemplate {
    
    // Base HTML template with styling - not meant to be modified by implementing classes
    private static final String BASE_TEMPLATE = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        line-height: 1.6;
                        color: #333;
                        margin: 0;
                        padding: 0;
                    }
                    .container {
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 20px;
                    }
                    .header {
                        color: #007bff;
                    }
                    .cta-button {
                        background-color: #007bff;
                        color: white !important ;
                        padding: 12px 25px;
                        text-decoration: none;
                        border-radius: 5px;
                        display: inline-block;
                    }
                    .info-box {
                        margin-top: 20px;
                        padding: 15px;
                        background-color: #f8f9fa;
                        border-radius: 5px;
                    }
                    .warning {
                        color: #dc3545;
                        margin-top: 20px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    %s
                </div>
            </body>
            </html>
            """;

    /**
     * Gets the subject of the email.
     * @return email subject
     */
    public abstract String getSubject();

    /**
     * Gets the content of the email.
     * Implement this method to provide the specific content for each template.
     * @param variables template variables to be replaced in the content
     * @return formatted email content
     */
    protected abstract String getContent(TemplateVariables variables);

    /**
     * Generates the complete HTML email.
     * @param variables template variables to be replaced in the content
     * @return complete HTML email
     */
    public final String generateEmail(TemplateVariables variables) {
        String content = getContent(variables);
        return String.format(BASE_TEMPLATE, content);
    }
}