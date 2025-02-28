package info.mackiewicz.bankapp.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.Emails;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private Resend resendMock;
    
    @Mock
    private Emails emailsMock;
    
    private EmailService emailService;
    
    @BeforeEach
    void setUp() {
        // Create a test instance that overrides client creation
        emailService = new EmailService("test-api-key") {
            @Override
            protected Resend createResendClient(String apiKey) {
                return resendMock;
            }
        };
    }

    @Test
    void sendEmail_Success() throws ResendException {
        // Arrange
        String expectedId = "test-email-id";
        CreateEmailResponse mockResponse = new CreateEmailResponse();
        mockResponse.setId(expectedId);
        when(resendMock.emails()).thenReturn(emailsMock);
        when(emailsMock.send(any(CreateEmailOptions.class)))
            .thenReturn(mockResponse);

        // Act
        String resultId = emailService.sendEmail(
            "test@example.com",
            "Test Subject",
            "<p>Test content</p>"
        );

        // Assert
        assertEquals(expectedId, resultId);
        verify(emailsMock).send(any(CreateEmailOptions.class));
    }

    @Test
    void sendEmail_ThrowsException_WhenResendFails() throws ResendException {
        // Arrange
        when(resendMock.emails()).thenReturn(emailsMock);
        when(emailsMock.send(any(CreateEmailOptions.class)))
            .thenThrow(new ResendException("API Error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
            emailService.sendEmail(
                "test@example.com",
                "Test Subject",
                "<p>Test content</p>"
            )
        );
    }

    @Test
    void sendPasswordResetEmail_ContainsCorrectContent() throws ResendException {
        // Arrange
        String email = "test@example.com";
        String token = "reset-token-123";
        CreateEmailResponse mockResponse = new CreateEmailResponse();
        mockResponse.setId("test-email-id");
        when(resendMock.emails()).thenReturn(emailsMock);
        when(emailsMock.send(any(CreateEmailOptions.class)))
            .thenReturn(mockResponse);
        
        // Act
        emailService.sendPasswordResetEmail(email, token);

        // Assert
        verify(emailsMock).send(argThat(request -> {
            String content = request.getHtml();
            return content != null &&
                   content.contains(token) &&
                   content.contains("http://localhost:8080/password-reset/token/") &&
                   request.getSubject() != null &&
                   request.getSubject().contains("Password Reset");
        }));
    }

    @Test
    void sendPasswordResetConfirmation_ContainsCorrectContent() throws ResendException {
        // Arrange
        CreateEmailResponse mockResponse = new CreateEmailResponse();
        mockResponse.setId("test-email-id");
        when(resendMock.emails()).thenReturn(emailsMock);
        when(emailsMock.send(any(CreateEmailOptions.class)))
            .thenReturn(mockResponse);
        
        String email = "test@example.com";
        
        // Act
        emailService.sendPasswordResetConfirmation(email);

        // Assert
        verify(emailsMock).send(argThat(request -> 
            request.getSubject() != null &&
            request.getSubject().contains("Password Reset Confirmation") &&
            request.getHtml() != null &&
            request.getHtml().contains("successfully reset")
        ));
    }

    @Test
    void sendWelcomeEmail_ContainsCorrectContent() throws ResendException {
        // Arrange
        CreateEmailResponse mockResponse = new CreateEmailResponse();
        mockResponse.setId("test-email-id");
        when(resendMock.emails()).thenReturn(emailsMock);
        when(emailsMock.send(any(CreateEmailOptions.class)))
            .thenReturn(mockResponse);
        
        String email = "test@example.com";
        
        // Act
        emailService.sendWelcomeEmail(email);

        // Assert
        verify(emailsMock).send(argThat(request -> 
            request.getSubject() != null &&
            request.getSubject().contains("Welcome to BankApp") &&
            request.getHtml() != null &&
            request.getHtml().contains("Welcome to BankApp")
        ));
    }

    @Test
    void allEmails_UseCorrectFromAddress() throws ResendException {
        // Arrange
        CreateEmailResponse mockResponse = new CreateEmailResponse();
        mockResponse.setId("test-email-id");
        when(resendMock.emails()).thenReturn(emailsMock);
        when(emailsMock.send(any(CreateEmailOptions.class)))
            .thenReturn(mockResponse);
        
        // Act
        emailService.sendWelcomeEmail("test@example.com");

        // Assert
        verify(emailsMock).send(argThat(request ->
            request.getFrom() != null &&
            "info@bankapp.mackiewicz.info".equals(request.getFrom())
        ));
    }

    @Test
    void sendEmail_ValidatesRequiredFields() {
        // Assert null/empty validation
        assertAll(
            () -> assertThrows(IllegalArgumentException.class, () ->
                emailService.sendEmail(null, "subject", "content")
            ),
            () -> assertThrows(IllegalArgumentException.class, () ->
                emailService.sendEmail("", "subject", "content")
            ),
            () -> assertThrows(IllegalArgumentException.class, () ->
                emailService.sendEmail("test@example.com", null, "content")
            ),
            () -> assertThrows(IllegalArgumentException.class, () ->
                emailService.sendEmail("test@example.com", "", "content")
            ),
            () -> assertThrows(IllegalArgumentException.class, () ->
                emailService.sendEmail("test@example.com", "subject", null)
            ),
            () -> assertThrows(IllegalArgumentException.class, () ->
                emailService.sendEmail("test@example.com", "subject", "")
            )
        );
    }
}