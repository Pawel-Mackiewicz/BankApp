package info.mackiewicz.bankapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

@Service
public class EmailService {

    private final Resend resend;

    public EmailService(@Value("${app.resend.api-key}") String apiKey) {
        this.resend = createResendClient(apiKey);
    }
    
    // Protected method to allow overriding in tests
    protected Resend createResendClient(String apiKey) {
        return new Resend(apiKey);
    }

    public String sendEmail(String to, String subject, String htmlContent) {
        try {
            CreateEmailOptions request = CreateEmailOptions.builder()
                .from("info@bankapp.mackiewicz.info")
                .to(to)
                .subject(subject)
                .html(htmlContent)
                .build();

            CreateEmailResponse response = resend.emails().send(request);
            return response.getId();
        } catch (ResendException e) {
            throw new RuntimeException("Błąd wysyłania e-maila: " + e.getMessage(), e);
        }
    }

    public void sendPasswordResetEmail(String email, String token) {

        String link = "http://localhost:8080/password-reset/token/" + token;
        String subject = "BankApp: Password Reset Request";
        String content = "To reset your password, click the link: " + link;
        sendEmail(email, subject, content);
    }

    public void sendPasswordResetConfirmation(String email) {

        String subject = "BankApp: Password Reset Confirmation";
        String content = "Your password has been successfully reset.";
        sendEmail(email, subject, content);
    }

    public void sendWelcomeEmail(String email) {

        String subject = "Welcome to BankApp!";
        String content = "Welcome to BankApp! We are glad you are here.";
        sendEmail(email, subject, content);
    }
}
