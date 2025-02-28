package info.mackiewicz.bankapp.service.email.template;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.service.email.template.api.EmailContent;

/**
 * Default implementation of EmailTemplateProvider using specific template classes.
 */
@Component
public class DefaultEmailTemplateProvider implements EmailTemplateProvider {
    
    private final WelcomeEmailTemplate welcomeTemplate;
    private final PasswordResetEmailTemplate resetTemplate;
    private final PasswordResetConfirmationTemplate resetConfirmationTemplate;

    public DefaultEmailTemplateProvider() {
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