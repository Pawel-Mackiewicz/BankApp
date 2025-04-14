package info.mackiewicz.bankapp.integration.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.repository.AccountRepository;
import info.mackiewicz.bankapp.system.notification.email.EmailService;
import info.mackiewicz.bankapp.system.registration.dto.RegistrationRequest;
import info.mackiewicz.bankapp.system.registration.dto.RegistrationResponse;
import info.mackiewicz.bankapp.system.registration.service.BonusGrantingService;
import info.mackiewicz.bankapp.testutils.TestRequestFactory;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end tests validating the complete registration process, from controller to database,
 * considering interactions between all components.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RegistrationEndToEndTest {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationEndToEndTest.class);

    @Value("${bankapp.registration.WelcomeBonusAmount:1000}")
    private BigDecimal welcomeBonusAmount;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private BonusGrantingService bonusGrantingService;

    private RegistrationRequest validRequest;

    @BeforeEach
    void setUp() {
        logger.info("Setting up end-to-end test for registration process");
        
        // Prepare valid registration request using TestRequestFactory
        validRequest = TestRequestFactory.createValidRegistrationRequest();
        // Set constant email for duplicate checking tests
        validRequest.setEmail("jan.kowalski@example.com");
    }

    @Test
    @DisplayName("Should complete the entire registration process with database state verification")
    void fullRegistrationProcess_ShouldCreateUserWithAccountAndSendNotifications() throws Exception {
        // Act - Call registration endpoint
        MvcResult result = mockMvc.perform(post("/api/registration/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andReturn();
    
        // Parse response
        String responseJson = result.getResponse().getContentAsString();
        logger.debug("Registration response: {}", responseJson);
        RegistrationResponse response = objectMapper.readValue(responseJson, RegistrationResponse.class);
    
        // Assert - Verify response
        assertNotNull(response);
        assertEquals("Jan", response.firstname());
        assertEquals("Kowalski", response.lastname());
        assertEquals("jan.kowalski@example.com", response.email());
        assertNotNull(response.username());
    
        // Verify user in database
        User savedUser = userRepository.findByEmail(validRequest.getEmail()).orElse(null);
        assertNotNull(savedUser);
        assertEquals("Jan", savedUser.getFirstname());
        assertEquals("Kowalski", savedUser.getLastname());
        assertEquals("jan.kowalski@example.com", savedUser.getEmail().getValue());
        assertNotNull(savedUser.getId());
    
        // Verify bank account created for the user
        Optional<List<Account>> userAccounts = accountRepository.findAccountsByOwner_id(savedUser.getId());

        assertFalse(userAccounts.isEmpty(), "User should have a bank account created");
        Account userAccount = userAccounts.get().getFirst();
        
        // Verify bonus and email service calls
        verify(bonusGrantingService).grantWelcomeBonus(eq(userAccount.getIban()), eq(welcomeBonusAmount));
        verify(emailService).sendWelcomeEmail(
                eq("jan.kowalski@example.com"),
                eq("Jan Kowalski"),
                eq(savedUser.getUsername())
        );
    }
    
    @Test
    @DisplayName("Registration attempt with existing email should be rejected")
    void duplicateEmailRegistration_ShouldBeRejected() throws Exception {
        // Arrange - First register a user
        mockMvc.perform(post("/api/registration/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated());
    
        // Check user count after first registration
        long userCountAfterFirstRegistration = userRepository.count();
    
        // Act - Attempt to register again with the same email
        mockMvc.perform(post("/api/registration/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict());
    
        // Assert - Check that the user count hasn't changed
        long userCountAfterSecondRegistration = userRepository.count();
        assertEquals(userCountAfterFirstRegistration, userCountAfterSecondRegistration,
                "User count should not change after rejected registration");
    }
}