package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.service.email.EmailSender;
import info.mackiewicz.bankapp.service.template.api.EmailContent;
import info.mackiewicz.bankapp.service.template.EmailTemplateProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    private static final String TEST_BASE_URL = "http://test.com";
    
    @Mock
    private EmailSender emailSender;
    
    @Mock
    private EmailTemplateProvider templateProvider;
    
    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "baseUrl", TEST_BASE_URL);
    }

    @Test
    void sendWelcomeEmail_UsesCorrectTemplate() {
        // Arrange
        String email = "test@example.com";
        String userName = "test";
        EmailContent mockContent = new EmailContent("Test Subject", "<p>Test content</p>");
        when(templateProvider.getWelcomeEmail(userName))
            .thenReturn(mockContent);

        // Act
        emailService.sendWelcomeEmail(email);

        // Assert
        verify(templateProvider).getWelcomeEmail(userName);
        verify(emailSender).send(email, mockContent.subject(), mockContent.htmlContent());
    }

    @Test
    void sendPasswordResetEmail_UsesCorrectTemplate() {
        // Arrange
        String email = "test@example.com";
        String userName = "test";
        String token = "reset-token";
        String expectedResetLink = TEST_BASE_URL + "/password-reset/token/" + token;
        EmailContent mockContent = new EmailContent("Reset Subject", "<p>Reset content</p>");
        
        when(templateProvider.getPasswordResetEmail(userName, expectedResetLink))
            .thenReturn(mockContent);

        // Act
        emailService.sendPasswordResetEmail(email, token);

        // Assert
        verify(templateProvider).getPasswordResetEmail(userName, expectedResetLink);
        verify(emailSender).send(email, mockContent.subject(), mockContent.htmlContent());
    }

    @Test
    void sendPasswordResetConfirmation_UsesCorrectTemplate() {
        // Arrange
        String email = "test@example.com";
        String userName = "test";
        String expectedLoginLink = TEST_BASE_URL + "/login";
        EmailContent mockContent = new EmailContent("Confirmation Subject", "<p>Confirmation content</p>");
        
        when(templateProvider.getPasswordResetConfirmationEmail(userName, expectedLoginLink))
            .thenReturn(mockContent);

        // Act
        emailService.sendPasswordResetConfirmation(email);

        // Assert
        verify(templateProvider).getPasswordResetConfirmationEmail(userName, expectedLoginLink);
        verify(emailSender).send(email, mockContent.subject(), mockContent.htmlContent());
    }

    @Test
    void sendEmail_ExtractsUsernameCorrectly() {
        // Arrange
        String email = "user.name@example.com";
        String expectedUsername = "user.name";
        EmailContent mockContent = new EmailContent("Test Subject", "<p>Test content</p>");
        when(templateProvider.getWelcomeEmail(expectedUsername))
            .thenReturn(mockContent);

        // Act
        emailService.sendWelcomeEmail(email);

        // Assert
        verify(templateProvider).getWelcomeEmail(expectedUsername);
    }
}