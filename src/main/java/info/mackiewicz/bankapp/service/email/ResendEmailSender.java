package info.mackiewicz.bankapp.service.email;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Implementation of EmailSender using Resend API.
 */
@Component
public class ResendEmailSender implements EmailSender {

    private final Resend resend;
    private final String fromAddress;

    public ResendEmailSender(
            @Value("${app.resend.api-key}") String apiKey,
            @Value("${app.email.from-address:info@bankapp.mackiewicz.info}") String fromAddress) {
        this.resend = createResendClient(apiKey);
        this.fromAddress = fromAddress;
    }

    // Protected method to allow overriding in tests
    protected Resend createResendClient(String apiKey) {
        return new Resend(apiKey);
    }

    @Override
    public String send(String to, String subject, String htmlContent) {
        validateEmailParameters(to);
        
        try {
            CreateEmailOptions request = CreateEmailOptions.builder()
                .from("BankApp <" + fromAddress + ">")
                .to(to)
                .subject(subject)
                .html(htmlContent)
                .build();

            return resend.emails().send(request).getId();
        } catch (ResendException e) {
            throw new RuntimeException("Error sending email: " + e.getMessage(), e);
        }
    }

    private void validateEmailParameters(String to) {
        if (to == null || to.trim().isEmpty()) {
            throw new IllegalArgumentException("Email recipient (to) cannot be null or empty");
        }
        if (!to.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format: " + to);
        }
    }
}