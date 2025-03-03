package info.mackiewicz.bankapp.notification.email.template;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.notification.email.EmailContent;
import info.mackiewicz.bankapp.notification.email.template.templates.PasswordResetConfirmationTemplate;
import info.mackiewicz.bankapp.notification.email.template.templates.PasswordResetEmailTemplate;
import info.mackiewicz.bankapp.notification.email.template.templates.WelcomeEmailTemplate;

/**
 * Default implementation of EmailTemplateProvider that manages all email templates
 * used in the application. This implementation uses specific template classes for
 * different types of emails, providing a clean separation of concerns and maintainable
 * email template management.
 *
 * @see EmailTemplateProvider
 * @see WelcomeEmailTemplate
 * @see PasswordResetEmailTemplate
 * @see PasswordResetConfirmationTemplate
 */
@Component
class DefaultEmailTemplateProvider implements EmailTemplateProvider {
    
    private final WelcomeEmailTemplate welcomeTemplate;
    private final PasswordResetEmailTemplate resetTemplate;
    private final PasswordResetConfirmationTemplate resetConfirmationTemplate;

    /**
     * Creates a new instance of DefaultEmailTemplateProvider.
     * Initializes all required email templates.
     */
    DefaultEmailTemplateProvider() {
        this.welcomeTemplate = new WelcomeEmailTemplate();
        this.resetTemplate = new PasswordResetEmailTemplate();
        this.resetConfirmationTemplate = new PasswordResetConfirmationTemplate();
    }

    @Override
    public EmailContent getWelcomeEmail(String userName) {
        TemplateVariables variables = TemplateVariables.builder()
            .withUserName(userName)
            .build();

        return new EmailContent(
            welcomeTemplate.getSubject(),
            welcomeTemplate.generateEmail(variables)
        );
    }

    @Override
    public EmailContent getPasswordResetEmail(String userName, String resetLink) {
        TemplateVariables variables = TemplateVariables.builder()
            .withUserName(userName)
            .withResetLink(resetLink)
            .build();

        return new EmailContent(
            resetTemplate.getSubject(),
            resetTemplate.generateEmail(variables)
        );
    }

    @Override
    public EmailContent getPasswordResetConfirmationEmail(String userName, String loginLink) {
        TemplateVariables variables = TemplateVariables.builder()
            .withUserName(userName)
            .withLoginLink(loginLink)
            .build();

        return new EmailContent(
            resetConfirmationTemplate.getSubject(),
            resetConfirmationTemplate.generateEmail(variables)
        );
    }
}