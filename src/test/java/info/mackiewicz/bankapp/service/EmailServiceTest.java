package info.mackiewicz.bankapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.EmailsService;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceTest.class);
    
    private static final String TEST_API_KEY = "re_test_key";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_TOKEN = "test-token";
    private static final String TEST_RESPONSE_ID = "email_123456789";

    @Mock
    private Resend resend;
    
    @Mock
    private EmailsService emailsService;
    
    private EmailService emailService;

    @BeforeEach
    void setUp() throws Exception {
        // Configure mocked Resend to return emailsService
        when(resend.emails()).thenReturn(emailsService);
        
        // Create EmailService with mocked Resend
        emailService = new EmailService(TEST_API_KEY) {
            @Override
            protected Resend createResendClient(String apiKey) {
                // Override to return our mock instead of creating a real client
                assertEquals(TEST_API_KEY, apiKey, "API key should match");
                return resend;
            }
        };
    }

    @Test
    void sendEmail_Success_ReturnsResponseId() throws ResendException {
        // Arrange
        String subject = "Test Subject";
        String content = "Test Content";
        
        // Mock response from Resend
        CreateEmailResponse mockResponse = new CreateEmailResponse();
        mockResponse.setId(TEST_RESPONSE_ID);
        when(emailsService.send(any(CreateEmailOptions.class))).thenReturn(mockResponse);
        
        // Act
        String result = emailService.sendEmail(TEST_EMAIL, subject, content);
        
        assertEquals(TEST_RESPONSE_ID, result, "Should return response ID");
        
        // Verify the email options were correctly built
        ArgumentCaptor<CreateEmailOptions> optionsCaptor = ArgumentCaptor.forClass(CreateEmailOptions.class);
        verify(emailsService).send(optionsCaptor.capture());
        
        CreateEmailOptions capturedOptions = optionsCaptor.getValue();
        assertEquals("info@bankapp.mackiewicz.info", capturedOptions.getFrom(), "From address should match");
        assertEquals(TEST_EMAIL, capturedOptions.getTo().get(0), "To address should match");
        assertEquals(subject, capturedOptions.getSubject(), "Subject should match");
        assertEquals(content, capturedOptions.getHtml(), "Content should match");
        
        logger.info("sendEmail_Success_ReturnsResponseId: Test passed");
    }

    @Test
    void sendEmail_WhenResendThrowsException_ShouldWrapInRuntimeException() throws ResendException {
        // Arrange
        String subject = "Test Subject";
        String content = "Test Content";
        ResendException resendException = new ResendException("API error");
        
        when(emailsService.send(any(CreateEmailOptions.class))).thenThrow(resendException);
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> emailService.sendEmail(TEST_EMAIL, subject, content),
            "Should throw RuntimeException when Resend throws an exception");
        
        assertTrue(exception.getMessage().contains("Błąd wysyłania e-maila"), 
            "Exception message should indicate an email sending error");
        assertEquals(resendException, exception.getCause(), 
            "ResendException should be the cause of the RuntimeException");
        
        logger.info("sendEmail_WhenResendThrowsException_ShouldWrapInRuntimeException: Test passed");
    }

    @Test
    void sendPasswordResetEmail_ShouldCallSendEmailWithCorrectParams() {
        // Arrange
        // Create a spy of the emailService to verify sendEmail is called correctly
        EmailService spy = spy(emailService);
        doReturn(TEST_RESPONSE_ID).when(spy).sendEmail(anyString(), anyString(), anyString());
        
        // Act
        spy.sendPasswordResetEmail(TEST_EMAIL, TEST_TOKEN);
        
        // Assert
        // Verify sendEmail was called with the correct parameters
        verify(spy).sendEmail(
            eq(TEST_EMAIL),
            eq("BankApp: Password Reset Request"),
            contains("http://localhost:8080/password-reset/token/" + TEST_TOKEN)
        );
        
        logger.info("sendPasswordResetEmail_ShouldCallSendEmailWithCorrectParams: Test passed");
    }

    @Test
    void sendPasswordResetConfirmation_ShouldCallSendEmailWithCorrectParams() {
        // Arrange
        EmailService spy = spy(emailService);
        doReturn(TEST_RESPONSE_ID).when(spy).sendEmail(anyString(), anyString(), anyString());
        
        // Act
        spy.sendPasswordResetConfirmation(TEST_EMAIL);
        
        // Assert
        verify(spy).sendEmail(
            eq(TEST_EMAIL),
            eq("BankApp: Password Reset Confirmation"),
            contains("Your password has been successfully reset")
        );
        
        logger.info("sendPasswordResetConfirmation_ShouldCallSendEmailWithCorrectParams: Test passed");
    }

    @Test
    void sendWelcomeEmail_ShouldCallSendEmailWithCorrectParams() {
        // Arrange
        EmailService spy = spy(emailService);
        doReturn(TEST_RESPONSE_ID).when(spy).sendEmail(anyString(), anyString(), anyString());
        
        // Act
        spy.sendWelcomeEmail(TEST_EMAIL);
        
        // Assert
        verify(spy).sendEmail(
            eq(TEST_EMAIL),
            eq("Welcome to BankApp!"),
            contains("Welcome to BankApp")
        );
        
        logger.info("sendWelcomeEmail_ShouldCallSendEmailWithCorrectParams: Test passed");
    }
}