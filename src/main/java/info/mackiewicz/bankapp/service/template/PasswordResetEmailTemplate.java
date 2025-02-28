package info.mackiewicz.bankapp.service.template;

/**
 * Password reset email template.
 * Contains reset link and security information.
 */
public class PasswordResetEmailTemplate extends EmailTemplate {

    @Override
    public String getSubject() {
        return "BankApp: Password Reset Request";
    }

    @Override
    protected String getContent(TemplateVariables variables) {
        return String.format("""
            <h1 class="header">Password Reset Request</h1>
            
            <p>Dear %s,</p>
            
            <p>We received a request to reset your BankApp password. If you didn't request this change, please ignore this email.</p>
            
            <div style="margin: 30px 0; text-align: center;">
                <a href="%s" class="cta-button">
                    Reset Password
                </a>
            </div>
            
            <div class="info-box">
                <p style="margin: 0;"><strong>Important:</strong></p>
                <ul style="margin-bottom: 0;">
                    <li>This link is valid for 30 minutes</li>
                    <li>The link can only be used once</li>
                </ul>
            </div>
            
            <p style="margin-top: 30px;">Best regards,<br>The BankApp Team</p>
            """,
            variables.get("userName"),
            variables.get("resetLink")
        );
    }
}