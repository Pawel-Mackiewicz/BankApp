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
    private static final String TEST_EMAIL = "jan.kowalski@example.com";
    private static final String TEST_FIRSTNAME = "Jan";
    private static final String TEST_LASTNAME = "Kowalski";
    private static final String TEST_FULLNAME = "Jan Kowalski";
    private static final String TEST_DUPLICATE_FIRSTNAME = "Anna";
    private static final String TEST_DUPLICATE_LASTNAME = "Nowak";
    private static final String TEST_DUPLICATE_PESEL = "98765432101";
    private static final String TEST_DUPLICATE_PHONE = "+48987654321";
    private static final String TEST_DUPLICATE_BIRTHDATE_STR = "1995-05-05";
    private static final String ERROR_MESSAGE_ALREADY_IN_USE = "already in use";

    @Value("${bankapp.registration.WelcomeBonusAmount:1000}")
    private BigDecimal welcomeBonusAmount;

    @Autowired
    private RegistrationService registrationService;

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

    private static final String LOG_SETUP_MESSAGE = "Setting up integration test for registration service";
    
    @BeforeEach
    void setUp() {
        logger.info(LOG_SETUP_MESSAGE);
        
        // Prepare test account
        testAccount = TestAccountBuilder.createTestAccount();

        when(accountService.createAccount(any(Integer.class))).thenReturn(testAccount);

        // Prepare valid registration request using TestRequestFactory
        validRequest = TestRequestFactory.createValidRegistrationRequest();
        // Set constant email for duplicate checking tests
        validRequest.setEmail(TEST_EMAIL);
    }

    @Test
    @DisplayName("Should register a new user with valid request")
    void registerUser_WhenValidRequest_ThenSuccess() {
        // Act
        RegistrationResponse response = registrationService.registerUser(validRequest);
    
        // Assert
        assertNotNull(response);
        assertEquals(TEST_FIRSTNAME, response.firstname());
        assertEquals(TEST_LASTNAME, response.lastname());
        assertEquals(TEST_EMAIL, response.email());
        assertNotNull(response.username());
    
        // Verify service calls
        verify(accountService).createAccount(any(Integer.class));
        verify(bonusGrantingService).grantWelcomeBonus(eq(testAccount.getIban()), eq(welcomeBonusAmount));
        verify(emailService).sendWelcomeEmail(
                eq(TEST_EMAIL),
                eq(TEST_FULLNAME),
                any(String.class)
        );
    
        // Verify that user was saved in the database
        User savedUser = userRepository.findByEmail(validRequest.getEmail()).orElse(null);
        assertNotNull(savedUser);
        assertEquals(TEST_FIRSTNAME, savedUser.getFirstname());
        assertEquals(TEST_LASTNAME, savedUser.getLastname());
    }
    
    @Test
    @DisplayName("Should reject registration when email already exists")
    void registerUser_WhenEmailAlreadyExists_ThenThrowException() {
        // Arrange - First register a user
        registrationService.registerUser(validRequest);
    
        // Prepare a second request with the same email but different data
        RegistrationRequest duplicateEmailRequest = TestRequestFactory.createValidRegistrationRequest();
        duplicateEmailRequest.setFirstname(TEST_DUPLICATE_FIRSTNAME);
        duplicateEmailRequest.setLastname(TEST_DUPLICATE_LASTNAME);
        duplicateEmailRequest.setEmail(TEST_EMAIL); // Same email
        duplicateEmailRequest.setPesel(TEST_DUPLICATE_PESEL);
        duplicateEmailRequest.setPhoneNumber(TEST_DUPLICATE_PHONE);
        duplicateEmailRequest.setDateOfBirth(LocalDate.parse(TEST_DUPLICATE_BIRTHDATE_STR));
    
        // Act & Assert
        Exception exception = assertThrows(DuplicatedUserException.class,
                () -> registrationService.registerUser(duplicateEmailRequest));
        assertTrue(exception.getMessage().contains(ERROR_MESSAGE_ALREADY_IN_USE));
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
