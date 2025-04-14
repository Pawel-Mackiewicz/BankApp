package info.mackiewicz.bankapp.integration.registration;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.model.TestAccountBuilder;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.system.notification.email.EmailService;
import info.mackiewicz.bankapp.system.registration.dto.RegistrationRequest;
import info.mackiewicz.bankapp.system.registration.dto.RegistrationResponse;
import info.mackiewicz.bankapp.system.registration.service.BonusGrantingService;
import info.mackiewicz.bankapp.system.registration.service.RegistrationService;
import info.mackiewicz.bankapp.testutils.TestRequestFactory;
import info.mackiewicz.bankapp.user.exception.DuplicatedUserException;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.repository.UserRepository;
import info.mackiewicz.bankapp.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RegistrationServiceIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationServiceIntegrationTest.class);

    @Value("${bankapp.registration.WelcomeBonusAmount:1000}")
    private BigDecimal welcomeBonusAmount;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private BonusGrantingService bonusGrantingService;

    @MockitoBean
    private AccountService accountService;

    private RegistrationRequest validRequest;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        logger.info("Setting up integration test for registration service");
        
        // Prepare test account
        testAccount = TestAccountBuilder.createTestAccount();

        when(accountService.createAccount(any(Integer.class))).thenReturn(testAccount);

        // Prepare valid registration request using TestRequestFactory
        validRequest = TestRequestFactory.createValidRegistrationRequest();
        // Set constant email for duplicate checking tests
        validRequest.setEmail("jan.kowalski@example.com");
    }

    @Test
    @DisplayName("Should register a new user with valid request")
    void registerUser_WhenValidRequest_ThenSuccess() {
        // Act
        RegistrationResponse response = registrationService.registerUser(validRequest);
    
        // Assert
        assertNotNull(response);
        assertEquals("Jan", response.firstname());
        assertEquals("Kowalski", response.lastname());
        assertEquals("jan.kowalski@example.com", response.email());
        assertNotNull(response.username());
    
        // Verify service calls
        verify(accountService).createAccount(any(Integer.class));
        verify(bonusGrantingService).grantWelcomeBonus(eq(testAccount.getIban()), eq(welcomeBonusAmount));
        verify(emailService).sendWelcomeEmail(
                eq("jan.kowalski@example.com"),
                eq("Jan Kowalski"),
                any(String.class)
        );
    
        // Verify that user was saved in the database
        User savedUser = userRepository.findByEmail(validRequest.getEmail()).orElse(null);
        assertNotNull(savedUser);
        assertEquals("Jan", savedUser.getFirstname());
        assertEquals("Kowalski", savedUser.getLastname());
    }
    
    @Test
    @DisplayName("Should reject registration when email already exists")
    void registerUser_WhenEmailAlreadyExists_ThenThrowException() {
        // Arrange - First register a user
        registrationService.registerUser(validRequest);
    
        // Prepare a second request with the same email but different data
        RegistrationRequest duplicateEmailRequest = TestRequestFactory.createValidRegistrationRequest();
        duplicateEmailRequest.setFirstname("Anna");
        duplicateEmailRequest.setLastname("Nowak");
        duplicateEmailRequest.setEmail("jan.kowalski@example.com"); // Same email
        duplicateEmailRequest.setPesel("98765432101");
        duplicateEmailRequest.setPhoneNumber("+48987654321");
        duplicateEmailRequest.setDateOfBirth(LocalDate.parse("1995-05-05"));
    
        // Act & Assert
        Exception exception = assertThrows(DuplicatedUserException.class, () -> {
            registrationService.registerUser(duplicateEmailRequest);
        });
        assertTrue(exception.getMessage().contains("already in use"));
    }
    
    @Test
    @DisplayName("Should perform complete end-to-end registration process")
    void registerUser_ShouldPerformCompleteEndToEndRegistration() {
        // Act
        RegistrationResponse response = registrationService.registerUser(validRequest);
    
        // Assert
        assertNotNull(response);
        
        // Verify that user was saved in the database
        User savedUser = userRepository.findByEmail(validRequest.getEmail()).orElse(null);
        assertNotNull(savedUser);
        
        // Verify individual process steps
        verify(accountService).createAccount(savedUser.getId());
        verify(bonusGrantingService).grantWelcomeBonus(testAccount.getIban(), welcomeBonusAmount);
        verify(emailService).sendWelcomeEmail(
                savedUser.getEmail().getValue(),
                savedUser.getFullName(),
                savedUser.getUsername()
        );
    }
}
