package info.mackiewicz.bankapp.config;

import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.repository.UserRepository;
import info.mackiewicz.bankapp.service.EmailService;
import info.mackiewicz.bankapp.service.PasswordService;
import info.mackiewicz.bankapp.service.UserService;
import info.mackiewicz.bankapp.service.UsernameGeneratorService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.mockito.Mockito;

@TestConfiguration
public class TestConfig {

    /**
     * Provides a mock EmailService for tests to avoid requiring the Resend API key
     */
    @Bean
    @Primary
    public EmailService emailService() {
        return new EmailService("test-api-key") {
            @Override
            protected com.resend.Resend createResendClient(String apiKey) {
                // Return a no-op implementation for tests
                return new com.resend.Resend(apiKey);
            }
            
            @Override
            public String sendEmail(String to, String subject, String htmlContent) {
                // Log the email instead of sending it
                System.out.println("TEST EMAIL - To: " + to + ", Subject: " + subject);
                return "test-email-id";
            }
            
            @Override
            public void sendPasswordResetEmail(String email, String token) {
                System.out.println("TEST PASSWORD RESET EMAIL - To: " + email + ", Token: " + token);
            }
            
            @Override
            public void sendPasswordResetConfirmation(String email) {
                System.out.println("TEST PASSWORD RESET CONFIRMATION - To: " + email);
            }
        };
    }
    
    /**
     * Provides a mock UserService for tests
     */
    @Bean
    @Primary
    public UserService userService(UserRepository userRepository,
                                  PasswordService passwordService,
                                  UsernameGeneratorService usernameGeneratorService) {
        // Create a real UserService but override the userExistsByEmail method
        return new UserService(userRepository, passwordService, usernameGeneratorService) {
            @Override
            public boolean userExistsByEmail(String email) {
                // For test email, always return true
                if ("test@example.com".equals(email)) {
                    return true;
                }
                return super.userExistsByEmail(email);
            }
            
            @Override
            public void changeUsersPassword(String email, String newPassword) {
                // Just log for tests
                System.out.println("TEST CHANGE PASSWORD - Email: " + email);
            }
        };
    }
}