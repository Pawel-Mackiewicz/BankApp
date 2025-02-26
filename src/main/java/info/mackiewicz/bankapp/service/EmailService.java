package info.mackiewicz.bankapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

import lombok.RequiredArgsConstructor;

@Service
public class EmailService {

    private final Resend resend;

    public EmailService(@Value("${app.resend.api-key}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

 public String sendEmail(String to, String subject, String htmlContent) {
        try {
            CreateEmailOptions request = CreateEmailOptions.builder()
                .from("noreply@twojadomena.com")
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
        //TODO: implement email sending with password reset token;
        String subject = "Password Reset Request";
        String content = "To reset your password, click the link: " + token;
        sendEmail(email, subject, content);
    }

    public void sendPasswordResetConfirmation(String email) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sendPasswordResetConfirmation'");
    }
}
