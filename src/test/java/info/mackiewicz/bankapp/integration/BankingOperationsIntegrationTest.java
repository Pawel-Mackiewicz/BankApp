package info.mackiewicz.bankapp.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.repository.AccountRepository;
import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import info.mackiewicz.bankapp.core.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.core.transaction.model.TransactionType;
import info.mackiewicz.bankapp.core.transaction.repository.TransactionRepository;
import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.core.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.core.user.repository.UserRepository;
import info.mackiewicz.bankapp.integration.utils.IntegrationTestAccountService;
import info.mackiewicz.bankapp.integration.utils.IntegrationTestConfig;
import info.mackiewicz.bankapp.integration.utils.IntegrationTestUserService;
import info.mackiewicz.bankapp.system.banking.operations.controller.dto.EmailTransferRequest;
import info.mackiewicz.bankapp.system.banking.operations.controller.dto.IbanTransferRequest;
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
import java.math.RoundingMode;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

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

    private Account sourceAccount;
    private Account sameOwnerDestinationAccount;
    private Account destinationAccount;
    private User testUser;
    private User recipientUser;

    @NotNull
    private BigDecimal getUpdatedAccountBalance(Account account) {
        Account updatedAccount = accountRepository.findById(account.getId()).orElseThrow();
        return updatedAccount.getBalance();
    }

    private void awaitTransactionCompletion(Integer transactionId) throws NoSuchElementException {
        long startTime = System.currentTimeMillis();

        await().atMost(2, TimeUnit.SECONDS)
                .pollInterval(20, TimeUnit.MILLISECONDS)
                .until(() -> transactionRepository.findById(transactionId)
                        .orElseThrow()
                        .getStatus()
                        .equals(TransactionStatus.DONE));

        long endTime = System.currentTimeMillis();
        log.info("Time to process transaction: " + (endTime - startTime) + "ms");
    }

    private void checkBalancesPostTransfer(Account sourceAccount, BigDecimal expectedSourceBalance, Account destinationAccount, BigDecimal expectedDestinationBalance) {
        BigDecimal sourceBalanceAfterTransfer = getUpdatedAccountBalance(sourceAccount);
        BigDecimal destinationBalanceAfterTransfer = getUpdatedAccountBalance(destinationAccount);

        validateExpectedBalance("source", sourceBalanceAfterTransfer, expectedSourceBalance);
        validateExpectedBalance("destination", destinationBalanceAfterTransfer, expectedDestinationBalance);
    }

    private void validateExpectedBalance(String accountType, BigDecimal actual, BigDecimal expected) {
        String message = String.format("After transfer: %s account: Expected balance: %s but was: %s",
                accountType, expected, actual);
        // not using assertEquals because BigDecimal.equals() differ same numbers with different scale
//        assertEquals(expected, actual, message);
        assertEquals(0, actual.compareTo(expected), message);
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
        testUser = testUserService.createRandomTestUser();

        recipientUser = testUserService.createRandomTestUser();

        // Creating test accounts
        sourceAccount = testAccountService.createTestAccountWithBalance(testUser.getId(), DEFAULT_BALANCE);
        sameOwnerDestinationAccount = testAccountService.createTestAccountWithBalance(testUser.getId(), DEFAULT_BALANCE);
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
        Transaction savedTransaction = transactionRepository.findById(transactionId).orElseThrow();

        assertEquals(TransactionStatus.NEW, savedTransaction.getStatus());
        assertEquals(TransactionType.TRANSFER_INTERNAL, savedTransaction.getType());
        assertEquals(sourceAccount, savedTransaction.getSourceAccount());
        assertEquals(destinationAccount, savedTransaction.getDestinationAccount());
        assertEquals(DEFAULT_TRANSFER_VALUE.setScale(2, RoundingMode.HALF_UP), savedTransaction.getAmount().setScale(2, RoundingMode.HALF_UP));
        assertEquals(DEFAULT_TRANSFER_TITLE, savedTransaction.getTitle());

        //accounts balance doesn't change, because it's not same owner of account
        checkBalancesPostTransfer(
                sourceAccount,
                DEFAULT_BALANCE,
                destinationAccount,
                DEFAULT_BALANCE
        );
    }

    @Test
    @DisplayName("Should immediately transfer funds in OWN_TRANSFER using IBAN")
    void iban_OWN_TRANSFER_shouldImmediatelyTransferFunds() throws Exception {
        // Preparing IBAN transfer request
        IbanTransferRequest request = new IbanTransferRequest();
        request.setSourceIban(sourceAccount.getIban());
        request.setRecipientIban(sameOwnerDestinationAccount.getIban());
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
                .andExpect(jsonPath("$.targetAccount.formattedIban", is(sameOwnerDestinationAccount.getFormattedIban())))
                .andExpect(jsonPath("$.transactionInfo.amount").value(DEFAULT_TRANSFER_VALUE.doubleValue()))
                .andExpect(jsonPath("$.transactionInfo.title").value(DEFAULT_TRANSFER_TITLE))
                .andExpect(jsonPath("$.transactionInfo.id").isNumber())
                .andReturn()
                .getResponse()
                .getContentAsString();

        //recover transaction id from response
        Integer transactionId = JsonPath.read(responseBody, "$.transactionInfo.id");
        awaitTransactionCompletion(transactionId);

        Transaction savedTransaction = transactionRepository.findById(transactionId).orElseThrow();

        assertEquals(TransactionStatus.DONE, savedTransaction.getStatus());
        assertEquals(TransactionType.TRANSFER_OWN, savedTransaction.getType());
        assertEquals(sourceAccount.getId(), savedTransaction.getSourceAccount().getId());
        assertEquals(sameOwnerDestinationAccount.getId(), savedTransaction.getDestinationAccount().getId());
        assertEquals(DEFAULT_TRANSFER_VALUE.setScale(2, RoundingMode.HALF_UP), savedTransaction.getAmount().setScale(2, RoundingMode.HALF_UP));
        assertEquals(DEFAULT_TRANSFER_TITLE, savedTransaction.getTitle());

        //accounts balance has changed, because it's same owner of account
        checkBalancesPostTransfer(
                sourceAccount,
                DEFAULT_BALANCE.subtract(DEFAULT_TRANSFER_VALUE),
                sameOwnerDestinationAccount,
                DEFAULT_BALANCE.add(DEFAULT_TRANSFER_VALUE)
        );
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
        Transaction savedTransaction = transactionRepository.findById(transactionId).orElseThrow();

        assertEquals(TransactionStatus.NEW, savedTransaction.getStatus());
        assertEquals(TransactionType.TRANSFER_INTERNAL, savedTransaction.getType());
        assertEquals(sourceAccount, savedTransaction.getSourceAccount());
        assertEquals(destinationAccount, savedTransaction.getDestinationAccount());
        assertEquals(DEFAULT_TRANSFER_VALUE.setScale(2, RoundingMode.HALF_UP), savedTransaction.getAmount().setScale(2, RoundingMode.HALF_UP));
        assertEquals(DEFAULT_TRANSFER_TITLE, savedTransaction.getTitle());


        //accounts balance don't change, because it's not same owner of account
        checkBalancesPostTransfer(
                sourceAccount,
                DEFAULT_BALANCE,
                destinationAccount,
                DEFAULT_BALANCE
        );
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

    @Test
    @DisplayName("Should return BAD_REQUEST when transaction title with invalid characters")
    void ibanTransfer_withInvalidTransactionTitle_shouldReturnBadRequest() throws Exception {
        // Preparing transfer request with invalid title
        IbanTransferRequest request = new IbanTransferRequest();
        request.setSourceIban(sourceAccount.getIban());
        request.setRecipientIban(destinationAccount.getIban());
        request.setAmount(DEFAULT_TRANSFER_VALUE);
        request.setTitle("Invalid$Title^<>"); // Invalid characters in title

        // Executing the test
        mockMvc.perform(post(BANKING_TRANSFER_IBAN_ENDPOINT)
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // Checking that account balances have not changed
        checkBalancesNotChanged();

        // Checking that the transaction was not saved in the database
        checkTransactionNotSavedInDB();
    }

    @Test
    @DisplayName("Should successfully transfer funds with valid special characters in title")
    void ibanTransfer_withValidSpecialCharactersInTitle_shouldSucceed() throws Exception {
        IbanTransferRequest request = new IbanTransferRequest();
        request.setSourceIban(sourceAccount.getIban());
        request.setRecipientIban(destinationAccount.getIban());
        request.setAmount(DEFAULT_TRANSFER_VALUE);
        request.setTitle("Payment for Order #123 - 50% discount!"); // Valid special chars

        mockMvc.perform(post(BANKING_TRANSFER_IBAN_ENDPOINT)
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());


    }
}