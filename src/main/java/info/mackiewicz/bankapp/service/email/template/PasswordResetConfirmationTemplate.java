package info.mackiewicz.bankapp.service.email.template;

/**
 * Password reset confirmation email template.
 * Contains login button and security tips.
 */
public class PasswordResetConfirmationTemplate extends EmailTemplate {

    @Override
    public String getSubject() {
        return "BankApp: Password Reset Confirmation";
    }

    @Override
    protected String getContent(TemplateVariables variables) {
        return String.format("""
            <h1 class="header">Password Reset Successful</h1>
            
            <p>Dear %s,</p>
            
            <p>Your password has been successfully changed. You can now log in to your account with your new password.</p>
            
            <div style="margin: 30px 0; text-align: center;">
                <a href="%s" class="cta-button">
                    Log In Now
                </a>
            </div>
            
            <div class="info-box">
                <p style="margin: 0;"><strong>Security Tips:</strong></p>
                <ul style="margin-bottom: 0;">
                    <li>Change your password regularly</li>
                    <li>Use unique passwords for different accounts</li>
                    <li>Never share your login credentials</li>
                </ul>
            </div>
            
            <p class="warning">
                If you did not make this change, please contact our security team immediately.
            </p>
            
            <p style="margin-top: 30px;">Best regards,<br>The BankApp Team</p>
            """,
            variables.get("userName"),
            variables.get("loginLink")
        );
    }
}