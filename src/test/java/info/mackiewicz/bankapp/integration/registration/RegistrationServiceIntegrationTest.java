package info.mackiewicz.bankapp.integration.registration;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.model.TestAccountBuilder;
import info.mackiewicz.bankapp.core.account.service.AccountService;
import info.mackiewicz.bankapp.core.user.exception.DuplicatedUserException;
import info.mackiewicz.bankapp.core.user.exception.UserBaseException;
import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.core.user.repository.UserRepository;
import info.mackiewicz.bankapp.system.notification.email.EmailService;
import info.mackiewicz.bankapp.system.registration.dto.RegistrationRequest;
import info.mackiewicz.bankapp.system.registration.dto.RegistrationResponse;
import info.mackiewicz.bankapp.system.registration.service.BonusGrantingService;
import info.mackiewicz.bankapp.system.registration.service.RegistrationService;
import info.mackiewicz.bankapp.testutils.TestRequestFactory;
import org.iban4j.Iban;
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
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RegistrationServiceIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationServiceIntegrationTest.class);
    private static final String TEST_EMAIL = "jan.kowalski@example.com";
    private static final String TEST_FIRSTNAME = "Jan";
    private static final String TEST_LASTNAME = "Kowalski";
    private static final String TEST_FULLNAME = TEST_FIRSTNAME + " " + TEST_LASTNAME;
    private static final String TEST_DUPLICATE_FIRSTNAME = "Anna";
    private static final String TEST_DUPLICATE_LASTNAME = "Nowak";
    private static final String TEST_DUPLICATE_PESEL = "98765432101";
    private static final String TEST_DUPLICATE_PHONE = "+48987654321";
    private static final String TEST_DUPLICATE_BIRTHDATE_STR = "1995-05-05";
    private static final String ERROR_MESSAGE_ALREADY_IN_USE = "already in use";
    private static final String MINIMUM_AGE_BIRTHDATE_STR = LocalDate.now().minusYears(18).toString();
    private static final BigDecimal CUSTOM_BONUS_AMOUNT = new BigDecimal("2000");

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
    
    @Test
    @DisplayName("Should reject registration when PESEL already exists")
    void registerUser_WhenPeselAlreadyExists_ThenThrowException() {
        // Arrange - First register a user
        registrationService.registerUser(validRequest);
    
        // Prepare a second request with the same PESEL but different data
        RegistrationRequest duplicatePeselRequest = TestRequestFactory.createValidRegistrationRequest();
        duplicatePeselRequest.setFirstname(TEST_DUPLICATE_FIRSTNAME);
        duplicatePeselRequest.setLastname(TEST_DUPLICATE_LASTNAME);
        duplicatePeselRequest.setEmail("another." + TEST_EMAIL); // Different email
        duplicatePeselRequest.setPesel(validRequest.getPesel().getValue()); // Same PESEL
        duplicatePeselRequest.setPhoneNumber(TEST_DUPLICATE_PHONE);
        duplicatePeselRequest.setDateOfBirth(LocalDate.parse(TEST_DUPLICATE_BIRTHDATE_STR));
    
        // Act & Assert
        Exception exception = assertThrows(DuplicatedUserException.class,
                () -> registrationService.registerUser(duplicatePeselRequest));
        assertTrue(exception.getMessage().contains(ERROR_MESSAGE_ALREADY_IN_USE));
    }
    
    @Test
    @DisplayName("Should reject registration when phone number already exists")
    void registerUser_WhenPhoneNumberAlreadyExists_ThenThrowException() {
        // Arrange - First register a user
        registrationService.registerUser(validRequest);
    
        // Prepare a second request with the same phone number but different data
        RegistrationRequest duplicatePhoneRequest = TestRequestFactory.createValidRegistrationRequest();
        duplicatePhoneRequest.setFirstname(TEST_DUPLICATE_FIRSTNAME);
        duplicatePhoneRequest.setLastname(TEST_DUPLICATE_LASTNAME);
        duplicatePhoneRequest.setEmail("another." + TEST_EMAIL); // Different email
        duplicatePhoneRequest.setPesel(TEST_DUPLICATE_PESEL);
        duplicatePhoneRequest.setPhoneNumber(validRequest.getPhoneNumber().getValue()); // Same phone number
        duplicatePhoneRequest.setDateOfBirth(LocalDate.parse(TEST_DUPLICATE_BIRTHDATE_STR));
    
        // Act & Assert
        Exception exception = assertThrows(DuplicatedUserException.class,
                () -> registrationService.registerUser(duplicatePhoneRequest));
        assertTrue(exception.getMessage().contains(ERROR_MESSAGE_ALREADY_IN_USE));
    }
    
    @Test
    @DisplayName("Should register user with minimum required age")
    void registerUser_WithMinimumRequiredAge_ThenSuccess() {
        // Arrange - Create request with minimum age (18 years today)
        RegistrationRequest minimumAgeRequest = TestRequestFactory.createValidRegistrationRequest();
        minimumAgeRequest.setEmail("minimum.age@example.com");
        minimumAgeRequest.setDateOfBirth(LocalDate.parse(MINIMUM_AGE_BIRTHDATE_STR));
    
        // Act
        RegistrationResponse response = registrationService.registerUser(minimumAgeRequest);
    
        // Assert
        assertNotNull(response);
        
        // Verify user was saved
        User savedUser = userRepository.findByEmail(minimumAgeRequest.getEmail()).orElse(null);
        assertNotNull(savedUser);
        assertEquals(minimumAgeRequest.getDateOfBirth(), savedUser.getDateOfBirth());
    }
    
    @Test
    @DisplayName("Should complete registration even when email service fails")
    void registerUser_WhenEmailServiceFails_ThenStillCompleteRegistration() {
        // Arrange
        doThrow(new RuntimeException("Email service unavailable"))
            .when(emailService).sendWelcomeEmail(anyString(), anyString(), anyString());
    
        // Act
        RegistrationResponse response = registrationService.registerUser(validRequest);
    
        // Assert
        assertNotNull(response);
        
        // Verify other services were still called
        verify(accountService).createAccount(any(Integer.class));
        verify(bonusGrantingService).grantWelcomeBonus(any(Iban.class), any(BigDecimal.class));
        
        // Verify user was saved despite email failure
        User savedUser = userRepository.findByEmail(validRequest.getEmail()).orElse(null);
        assertNotNull(savedUser);
    }
    
    @Test
    @DisplayName("Should handle custom welcome bonus amount")
    void registerUser_WithCustomWelcomeBonusAmount_ThenApplyCorrectBonus() {
        // Arrange - Create a ReflectionTestUtils to set a custom bonus amount
        ReflectionTestUtils.setField(registrationService, "defaultWelcomeBonusAmount", CUSTOM_BONUS_AMOUNT);
    
        // Act
        RegistrationResponse response = registrationService.registerUser(validRequest);
    
        // Assert
        assertNotNull(response);
        
        // Verify the custom bonus amount was used
        verify(bonusGrantingService).grantWelcomeBonus(eq(testAccount.getIban()), eq(CUSTOM_BONUS_AMOUNT));
    }
    
    @Test
    @DisplayName("Should handle registration validation errors for invalid PESEL")
    void registerUser_WithInvalidPesel_ThenThrowException() {
        // Arrange
        RegistrationRequest invalidPeselRequest = TestRequestFactory.createRegistrationRequestWithInvalidPesel();
    
        // Act & Assert
        Exception exception = assertThrows(UserBaseException.class,
                () -> registrationService.registerUser(invalidPeselRequest));
        assertTrue(exception.getMessage().toLowerCase().contains("pesel"));
    }
    
    @Test
    @DisplayName("Should handle registration validation errors for invalid firstname")
    void registerUser_WithInvalidFirstname_ThenThrowException() {
        // Arrange
        RegistrationRequest invalidFirstnameRequest = TestRequestFactory.createRegistrationRequestWithInvalidFirstname();
    
        // Act & Assert
        Exception exception = assertThrows(UserBaseException.class,
                () -> registrationService.registerUser(invalidFirstnameRequest));
        assertTrue(exception.getMessage().toLowerCase().contains("name"));
    }
    
    @Test
    @DisplayName("Should reject registration for minor")
    void registerUser_ForMinor_ThenThrowException() {
        // Arrange
        RegistrationRequest minorRequest = TestRequestFactory.createRegistrationRequestForMinor();
    
        // Act & Assert
        Exception exception = assertThrows(UserBaseException.class,
                () -> registrationService.registerUser(minorRequest));
        assertTrue(exception.getMessage().toLowerCase().contains("age") || exception.getMessage().toLowerCase().contains("years"));
    }
    
    @Test
    @DisplayName("Should generate unique username for users with same name")
    void registerUser_WithSameNames_ThenGenerateUniqueUsernames() {
        // Arrange - First register a user
        RegistrationResponse firstResponse = registrationService.registerUser(validRequest);
        
        // Create a second request with the same name but different email and other data
        RegistrationRequest sameNameRequest = TestRequestFactory.createValidRegistrationRequest();
        sameNameRequest.setFirstname(TEST_FIRSTNAME);  // Same firstname
        sameNameRequest.setLastname(TEST_LASTNAME);    // Same lastname
        sameNameRequest.setEmail("another." + TEST_EMAIL); // Different email
        sameNameRequest.setPesel(TEST_DUPLICATE_PESEL);
        sameNameRequest.setPhoneNumber(TEST_DUPLICATE_PHONE);
    
        // Act
        RegistrationResponse secondResponse = registrationService.registerUser(sameNameRequest);
    
        // Assert
        assertNotNull(firstResponse.username());
        assertNotNull(secondResponse.username());
        assertNotEquals(firstResponse.username(), secondResponse.username());
    }
    
    @Test
    @DisplayName("Should handle concurrent registration attempts")
    void registerUser_WithConcurrentRequests_ThenHandleCorrectly() throws InterruptedException, ExecutionException {
        // Arrange - Create two different but valid requests
        RegistrationRequest request1 = TestRequestFactory.createValidRegistrationRequest();
        request1.setEmail("user1@example.com");
        request1.setPesel("11111111111");
        request1.setPhoneNumber("+48111111111");
        
        RegistrationRequest request2 = TestRequestFactory.createValidRegistrationRequest();
        request2.setEmail("user2@example.com");
        request2.setPesel("22222222222");
        request2.setPhoneNumber("+48222222222");
        
        // Act - Submit both registrations concurrently
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<RegistrationResponse> future1 = executorService.submit(() -> registrationService.registerUser(request1));
        Future<RegistrationResponse> future2 = executorService.submit(() -> registrationService.registerUser(request2));
        
        // Get results
        RegistrationResponse response1 = future1.get();
        RegistrationResponse response2 = future2.get();
        
        // Shutdown executor
        executorService.shutdown();
        
        // Assert
        assertNotNull(response1);
        assertNotNull(response2);
        
        // Verify both users were saved
        User savedUser1 = userRepository.findByEmail(request1.getEmail()).orElse(null);
        User savedUser2 = userRepository.findByEmail(request2.getEmail()).orElse(null);
        
        assertNotNull(savedUser1);
        assertNotNull(savedUser2);
        assertNotEquals(savedUser1.getId(), savedUser2.getId());
    }
}
