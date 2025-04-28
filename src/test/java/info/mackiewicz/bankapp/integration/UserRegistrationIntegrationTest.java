package info.mackiewicz.bankapp.integration;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.service.AccountService;
import info.mackiewicz.bankapp.presentation.auth.dto.UserRegistrationRequest;
import info.mackiewicz.bankapp.system.notification.email.EmailService;
import info.mackiewicz.bankapp.system.transaction.processing.TransactionProcessingService;
import info.mackiewicz.bankapp.testutils.TestAccountBuilder;
import info.mackiewicz.bankapp.testutils.TestUserBuilder;
import info.mackiewicz.bankapp.testutils.TestUserRegistrationDtoBuilder;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import info.mackiewicz.bankapp.user.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UserRegistrationIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationIntegrationTest.class);

    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private TransactionProcessingService transactionProcessingService;

    @BeforeAll
    static void beforeAll() {
        logger.info("Initializing test suite for UserRegistrationIntegrationTest");
    }

    @BeforeEach
    void setUp() {
        logger.info("Setting up test context for UserRegistrationIntegrationTest");
        logger.info("MockMvc status: {}", mockMvc != null ? "initialized" : "null");
        logger.info("EmailService mock status: {}", emailService != null ? "initialized" : "null");
        
        // Setup mock bank account
        Account bankAccount = TestAccountBuilder.createBankAccount();
        when(accountService.getAccountById(-1)).thenReturn(bankAccount);
        
        // Setup mock for new account creation
        when(accountService.createAccount(any())).thenAnswer(invocation -> {
            User owner = TestUserBuilder.createRandomTestUser();
            return TestAccountBuilder.createTestAccount(1, BigDecimal.ZERO, owner);
        });

        // Setup mock for transaction registration
        when(transactionService.registerTransaction(any())).thenAnswer(invocation -> {
            Transaction transaction = invocation.getArgument(0);
            transaction.setId(100); // Set a known ID for verification
            return transaction;
        });
    }

    @Test
    void shouldSuccessfullyRegisterNewUser() throws Exception {
        UserRegistrationRequest dto = TestUserRegistrationDtoBuilder.createValid();
        
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstname", dto.getFirstname())
                .param("lastname", dto.getLastname())
                .param("email", dto.getEmail())
                .param("password", dto.getPassword())
                .param("confirmPassword", dto.getConfirmPassword())
                .param("pesel", dto.getPesel())
                .param("phoneNumber", dto.getPhoneNumber())
                .param("dateOfBirth", dto.getDateOfBirth().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    
        verify(emailService).sendWelcomeEmail(
            eq(dto.getEmail()),
            eq(dto.getFirstname() + " " + dto.getLastname()),
            anyString()
        );
        
        // Verify welcome bonus transaction registration
        verify(transactionService).registerTransaction(argThat(transaction ->
            transaction.getAmount().equals(new BigDecimal("1000")) &&
            transaction.getTitle().equals("Welcome bonus") &&
            transaction.getType() == TransactionType.TRANSFER_INTERNAL &&
            transaction.getSourceAccount().getId() == -1
        ));

        // Verify transaction processing was called with the correct ID
        verify(transactionProcessingService).processTransactionById(100);
    }

    @Test
    void shouldRejectRegistrationWithDuplicateEmail() throws Exception {
        UserRegistrationRequest dto = TestUserRegistrationDtoBuilder.createValid();
        
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstname", dto.getFirstname())
                .param("lastname", dto.getLastname())
                .param("email", dto.getEmail())
                .param("password", dto.getPassword())
                .param("confirmPassword", dto.getConfirmPassword())
                .param("pesel", dto.getPesel())
                .param("phoneNumber", dto.getPhoneNumber())
                .param("dateOfBirth", dto.getDateOfBirth().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstname", dto.getFirstname())
                .param("lastname", dto.getLastname())
                .param("email", dto.getEmail())
                .param("password", dto.getPassword())
                .param("confirmPassword", dto.getConfirmPassword())
                .param("pesel", dto.getPesel())
                .param("phoneNumber", dto.getPhoneNumber())
                .param("dateOfBirth", dto.getDateOfBirth().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attribute("status", HttpStatus.BAD_REQUEST));
                
        verify(emailService).sendWelcomeEmail(anyString(), anyString(), anyString());
    }

    @Test
    void shouldRejectRegistrationWithInvalidFirstname() throws Exception {
        UserRegistrationRequest dto = TestUserRegistrationDtoBuilder.createWithInvalidFirstName();
        
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstname", dto.getFirstname())
                .param("lastname", dto.getLastname())
                .param("email", dto.getEmail())
                .param("password", dto.getPassword())
                .param("confirmPassword", dto.getConfirmPassword())
                .param("pesel", dto.getPesel())
                .param("phoneNumber", dto.getPhoneNumber())
                .param("dateOfBirth", dto.getDateOfBirth().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attribute("status", HttpStatus.BAD_REQUEST));
        
        verify(emailService, never()).sendWelcomeEmail(anyString(), anyString(), anyString());
    }

    @Test
    void shouldRejectRegistrationWithInvalidPesel() throws Exception {
        UserRegistrationRequest dto = TestUserRegistrationDtoBuilder.createWithInvalidPesel();
        
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstname", dto.getFirstname())
                .param("lastname", dto.getLastname())
                .param("email", dto.getEmail())
                .param("password", dto.getPassword())
                .param("confirmPassword", dto.getConfirmPassword())
                .param("pesel", dto.getPesel())
                .param("phoneNumber", dto.getPhoneNumber())
                .param("dateOfBirth", dto.getDateOfBirth().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attribute("status", HttpStatus.BAD_REQUEST));
        
        verify(emailService, never()).sendWelcomeEmail(anyString(), anyString(), anyString());
    }
    
    @Test
    void shouldRejectRegistrationForMinor() throws Exception {
        UserRegistrationRequest dto = TestUserRegistrationDtoBuilder.createWithInvalidAge();
        
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstname", dto.getFirstname())
                .param("lastname", dto.getLastname())
                .param("email", dto.getEmail())
                .param("password", dto.getPassword())
                .param("confirmPassword", dto.getConfirmPassword())
                .param("pesel", dto.getPesel())
                .param("phoneNumber", dto.getPhoneNumber())
                .param("dateOfBirth", dto.getDateOfBirth().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attribute("status", HttpStatus.BAD_REQUEST));
        
        verify(emailService, never()).sendWelcomeEmail(anyString(), anyString(), anyString());
    }
}