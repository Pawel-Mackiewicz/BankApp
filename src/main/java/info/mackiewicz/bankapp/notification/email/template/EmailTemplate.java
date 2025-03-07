package info.mackiewicz.bankapp.notification.email.template;

/**
 * Base class for all email templates.
 * Provides common functionality and structure for email templates.
 */
public abstract class EmailTemplate {
    
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