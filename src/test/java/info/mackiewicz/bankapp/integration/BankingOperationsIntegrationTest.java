package info.mackiewicz.bankapp.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.repository.AccountRepository;
import info.mackiewicz.bankapp.integration.utils.IntegrationTestAccountService;
import info.mackiewicz.bankapp.integration.utils.IntegrationTestConfig;
import info.mackiewicz.bankapp.integration.utils.IntegrationTestUserService;
import info.mackiewicz.bankapp.system.banking.operations.api.dto.EmailTransferRequest;
import info.mackiewicz.bankapp.system.banking.operations.api.dto.IbanTransferRequest;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.transaction.repository.TransactionRepository;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.iban4j.Iban;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(IntegrationTestConfig.class)
@DisplayName("Banking Operations System integration tests")
public class BankingOperationsIntegrationTest {

    public static final String BANKING_TRANSFER_IBAN_ENDPOINT = "/api/banking/transfer/iban";
    public static final String BANKING_TRANSFER_EMAIL_ENDPOINT = "/api/banking/transfer/email";

    @Autowired
    private IntegrationTestUserService testUserService;

    @Autowired
    private IntegrationTestAccountService testAccountService;

    private static final BigDecimal DEFAULT_BALANCE = new BigDecimal("1000.00");
    private static final BigDecimal DEFAULT_TRANSFER_VALUE = new BigDecimal("100");
    private static final String DEFAULT_TRANSFER_TITLE = "Test transfer";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    private Account sourceAccount;
    private Account destinationAccount;
    private User testUser;
    private User recipientUser;

    private static final AtomicInteger userCounter = new AtomicInteger(1);

    @NotNull
    private BigDecimal getUpdatedAccountBalance(Account account) {
        Account updatedAccount = accountRepository.findById(account.getId()).orElseThrow();
        return updatedAccount.getBalance();
    }

    private void awaitTransactionCompletion(Integer transactionId) throws NoSuchElementException {
        long startTime = System.currentTimeMillis();

        await().atMost(500, TimeUnit.MILLISECONDS)
                .pollInterval(10, TimeUnit.MILLISECONDS)
                .until(() -> transactionRepository.findById(transactionId)
                        .orElseThrow()
                        .getStatus()
                        .equals(TransactionStatus.DONE));

        long endTime = System.currentTimeMillis();
        log.info("Time to process transaction: " + (endTime - startTime) + "ms");
    }

    private void checkBalancesChangedCorrectly() {
        BigDecimal sourceBalanceAfterTransfer = getUpdatedAccountBalance(sourceAccount);
        BigDecimal expectedSourceBalance = DEFAULT_BALANCE.subtract(DEFAULT_TRANSFER_VALUE);
        BigDecimal destinationBalanceAfterTransfer = getUpdatedAccountBalance(destinationAccount);
        BigDecimal expectedDestinationBalance = DEFAULT_BALANCE.add(DEFAULT_TRANSFER_VALUE);

        validateExpectedBalance("source", sourceBalanceAfterTransfer, expectedSourceBalance);
        validateExpectedBalance("destination", destinationBalanceAfterTransfer, expectedDestinationBalance);
    }

    private void validateExpectedBalance(String accountType, BigDecimal actual, BigDecimal expected) {
        String message = String.format("After transfer: %s account: Expected balance: %s but was: %s",
                accountType, expected, actual);
        assertEquals(expected, actual, message);
    }

    private void checkBalancesNotChanged() {
        BigDecimal sourceBalanceAfterTransfer = getUpdatedAccountBalance(sourceAccount);
        validateExpectedBalance("source", sourceBalanceAfterTransfer, DEFAULT_BALANCE);
        BigDecimal destinationBalanceAfterTransfer = getUpdatedAccountBalance(destinationAccount);
        validateExpectedBalance("destination", destinationBalanceAfterTransfer, DEFAULT_BALANCE);
    }

    private void checkTransactionNotSavedInDB() {
        assertTrue(transactionRepository.findByAccountId(sourceAccount.getId())
                .map(List::isEmpty)
                .orElse(true));
    }

    @BeforeEach
    void setUp() {
        // Cleaning repositories before each test
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        userRepository.deleteAll();

        // Creating test users
        testUser = testUserService.createTestUser(userCounter.getAndIncrement());

        recipientUser = testUserService.createTestUser(userCounter.getAndIncrement());

        // Creating test accounts
        sourceAccount = testAccountService.createTestAccountWithBalance(testUser.getId(), DEFAULT_BALANCE);
        destinationAccount = testAccountService.createTestAccountWithBalance(recipientUser.getId(), DEFAULT_BALANCE);

        //making sure that we get user with an account
        testUser = userRepository
                .getUserById(testUser.getId())
                .orElseThrow();
    }

    @Test
    @DisplayName("Should successfully transfer funds between accounts using IBAN")
    void ibanTransfer_shouldSuccessfullyTransferFunds() throws Exception {
        // Preparing IBAN transfer request
        IbanTransferRequest request = new IbanTransferRequest();
        request.setSourceIban(sourceAccount.getIban());
        request.setRecipientIban(destinationAccount.getIban());
        request.setAmount(DEFAULT_TRANSFER_VALUE);
        request.setTitle(DEFAULT_TRANSFER_TITLE);

        // Executing the test and saving the response as a String
        String responseBody = mockMvc.perform(post(BANKING_TRANSFER_IBAN_ENDPOINT)
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sourceAccount.formattedIban", is(sourceAccount.getFormattedIban())))
                .andExpect(jsonPath("$.targetAccount.formattedIban", is(destinationAccount.getFormattedIban())))
                .andExpect(jsonPath("$.transactionInfo.amount").value(DEFAULT_TRANSFER_VALUE.doubleValue()))
                .andExpect(jsonPath("$.transactionInfo.title").value(DEFAULT_TRANSFER_TITLE))
                .andExpect(jsonPath("$.transactionInfo.id").isNumber())
                .andReturn()
                .getResponse()
                .getContentAsString();

        //recover transaction id from response
        Integer transactionId = JsonPath.read(responseBody, "$.transactionInfo.id");
        //process transaction by recovered id
        transactionService.processTransactionById(transactionId);
        awaitTransactionCompletion(transactionId);

        checkBalancesChangedCorrectly();
    }

    @Test
    @DisplayName("Should successfully transfer funds between accounts using email")
    void emailTransfer_shouldSuccessfullyTransferFunds() throws Exception {
        // Preparing an email transfer request
        EmailTransferRequest request = new EmailTransferRequest();
        request.setSourceIban(sourceAccount.getIban());
        request.setDestinationEmail(recipientUser.getEmail());
        request.setAmount(DEFAULT_TRANSFER_VALUE);
        request.setTitle(DEFAULT_TRANSFER_TITLE);

        // Executing the test and saving the response as a String
        String responseBody = mockMvc.perform(post(BANKING_TRANSFER_EMAIL_ENDPOINT)
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sourceAccount.formattedIban", is(sourceAccount.getFormattedIban())))
                .andExpect(jsonPath("$.targetAccount.formattedIban", is(destinationAccount.getFormattedIban())))
                .andExpect(jsonPath("$.transactionInfo.amount").value(DEFAULT_TRANSFER_VALUE.doubleValue()))
                .andExpect(jsonPath("$.transactionInfo.title").value(DEFAULT_TRANSFER_TITLE))
                .andExpect(jsonPath("$.transactionInfo.id").isNumber())
                .andReturn()
                .getResponse()
                .getContentAsString();

        //recover transaction id from response
        Integer transactionId = JsonPath.read(responseBody, "$.transactionInfo.id");
        //process transaction by recovered id
        transactionService.processTransactionById(transactionId);
        awaitTransactionCompletion(transactionId);

        checkBalancesChangedCorrectly();
    }

    @Test
    @DisplayName("Should return BAD_REQUEST when amount greater than balance")
    void ibanTransfer_withInsufficientFunds_shouldReturnBadRequest() throws Exception {
        // Preparing transfer request with amount exceeding balance
        IbanTransferRequest request = new IbanTransferRequest();
        request.setSourceIban(sourceAccount.getIban());
        request.setRecipientIban(destinationAccount.getIban());
        request.setAmount(DEFAULT_BALANCE.add(BigDecimal.ONE)); // Amount greater than balance
        request.setTitle(DEFAULT_TRANSFER_TITLE);

        // Executing the test
        mockMvc.perform(post(BANKING_TRANSFER_IBAN_ENDPOINT)
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("INSUFFICIENT_FUNDS")));

        // Checking that account balances have not changed
        checkBalancesNotChanged();

        // Checking that the transaction was not saved in the database
        checkTransactionNotSavedInDB();
    }

    @Test
    @DisplayName("Should return BAD_REQUEST when IBAN not registered")
    void ibanTransfer_toNonExistentAccount_shouldReturnNotFound() throws Exception {
        // Preparing transfer request to non-existent account
        IbanTransferRequest request = new IbanTransferRequest();
        request.setSourceIban(sourceAccount.getIban());
        request.setRecipientIban(Iban.random());
        request.setAmount(DEFAULT_BALANCE);
        request.setTitle(DEFAULT_TRANSFER_TITLE);

        // Executing the test
        mockMvc.perform(post(BANKING_TRANSFER_IBAN_ENDPOINT)
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("ACCOUNT_NOT_FOUND")));

        // Checking that account balances have not changed
        checkBalancesNotChanged();

        // Checking that the transaction was not saved in the database
        checkTransactionNotSavedInDB();
    }

    @Test
    @DisplayName("Should return BAD_REQUEST when EmailAddress not registered")
    void emailTransfer_toNonExistentEmail_shouldReturnNotFound() throws Exception {
        // Preparing transfer request to non-existent email
        EmailTransferRequest request = new EmailTransferRequest();
        request.setSourceIban(sourceAccount.getIban());
        request.setDestinationEmail(new EmailAddress("nonexistent@example.com"));
        request.setAmount(DEFAULT_TRANSFER_VALUE);
        request.setTitle(DEFAULT_TRANSFER_TITLE);

        // Executing the test
        mockMvc.perform(post(BANKING_TRANSFER_EMAIL_ENDPOINT)
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("ACCOUNT_OWNER_NOT_FOUND")));

        // Checking that account balances have not changed
        checkBalancesNotChanged();

        // Checking that the transaction was not saved in the database
        checkTransactionNotSavedInDB();
    }

    @Test
    @DisplayName("Should return FORBIDDEN when source account not owned by the user")
    void ibanTransfer_fromAccountNotOwnedByUser_shouldReturnForbidden() throws Exception {
        // Attempt to transfer from an account not owned by the user
        IbanTransferRequest request = new IbanTransferRequest();
        request.setSourceIban(destinationAccount.getIban()); // swapping destination with source
        request.setRecipientIban(sourceAccount.getIban());
        request.setAmount(DEFAULT_TRANSFER_VALUE);
        request.setTitle(DEFAULT_TRANSFER_TITLE);

        // Executing the test
        mockMvc.perform(post(BANKING_TRANSFER_IBAN_ENDPOINT)
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title", is("ACCOUNT_OWNERSHIP_ERROR")));

        // Checking that account balances have not changed
        checkBalancesNotChanged();

        // Checking that the transaction was not saved in the database
        checkTransactionNotSavedInDB();
    }

    @Test
    @DisplayName("Should return UNAUTHORIZED without full authentication")
    void ibanTransfer_whenUnauthenticated_shouldReturnUnauthorized() throws Exception {
        // Preparing transfer request
        IbanTransferRequest request = new IbanTransferRequest();
        request.setSourceIban(sourceAccount.getIban());
        request.setRecipientIban(destinationAccount.getIban());
        request.setAmount(DEFAULT_TRANSFER_VALUE);
        request.setTitle(DEFAULT_TRANSFER_TITLE);

        // Executing the test without authentication
        mockMvc.perform(post(BANKING_TRANSFER_IBAN_ENDPOINT)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        // Checking that account balances have not changed
        checkBalancesNotChanged();

        // Checking that the transaction was not saved in the database
        checkTransactionNotSavedInDB();
    }
}