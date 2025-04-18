package info.mackiewicz.bankapp.integration;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.integration.utils.IntegrationTestAccountService;
import info.mackiewicz.bankapp.integration.utils.IntegrationTestConfig;
import info.mackiewicz.bankapp.integration.utils.IntegrationTestUserService;
import info.mackiewicz.bankapp.system.banking.history.service.TransactionHistoryService;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.service.UserService;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Import(IntegrationTestConfig.class)
@AutoConfigureMockMvc
@DisplayName("Transaction History System Integration Test")
class TransactionHistoryRestControllerIntegrationTest {

    private static final Integer DEFAULT_ACCOUNT_ID = 1;
    private static final Integer OTHER_ACCOUNT_ID = 42;
    private static final BigDecimal DEFAULT_BALANCE = new BigDecimal("1000.00");
    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal("100.00");
    private static final String DEFAULT_TRANSACTION_TITLE = "TEST";

    private static int index = 1;

    @Autowired
    private IntegrationTestAccountService testAccountService;

    @Autowired
    private IntegrationTestUserService testUserService;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionHistoryService transactionHistoryService;


    private User getTestUser() {
        User testUser = testUserService.createTestUser(index++);
        testAccountService.createTestAccountWithBalance(testUser.getId(), DEFAULT_BALANCE);

        return userService.getUserById(testUser.getId());
    }

    private Transaction registerDefaultTransferTransaction(Account testAccount, Account destinationAccount) {
        Transaction transaction = Transaction.buildTransfer()
                .from(testAccount)
                .to(destinationAccount)
                .withAmount(DEFAULT_AMOUNT)
                .withTitle(DEFAULT_TRANSACTION_TITLE)
                .build();
        return transactionService.registerTransaction(transaction);
    }

    private static int getAccountId(User testUser) throws NoSuchElementException {
        return testUser.getAccounts().stream()
                .findFirst()
                .orElseThrow()
                .getId();
    }

    private static Account getAccount(User destinationUser) throws NoSuchElementException {
        return destinationUser.getAccounts().stream()
                .findFirst()
                .orElseThrow();
    }

    @BeforeEach
    void setUp() {
        User testUser = getTestUser();
        Account testAccount = getAccount(testUser);

        User destinationUser = getTestUser();
        Account destinationAccount = getAccount(destinationUser);
        //this transaction should not be returned in the result
        registerDefaultTransferTransaction(testAccount, destinationAccount);
    }

    @Test
    void getTransactions_ShouldReturnTransactionsFromService() throws Exception {
        // Given
        User testUser = getTestUser();
        Account testAccount = getAccount(testUser);

        User destinationUser = getTestUser();
        Account destinationAccount = getAccount(destinationUser);

        registerDefaultTransferTransaction(testAccount, destinationAccount);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/banking/history")
                        .param("accountId", String.valueOf(getAccountId(testUser)))
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].sourceAccount.formattedIban").value(testAccount.getFormattedIban()))
                .andExpect(jsonPath("$.content[0].targetAccount.formattedIban").value(destinationAccount.getFormattedIban()))
                .andExpect(jsonPath("$.content[0].transactionInfo.amount").value(DEFAULT_AMOUNT.doubleValue()))
                .andExpect(jsonPath("$.content[0].transactionInfo.title").value(DEFAULT_TRANSACTION_TITLE));
    }

    @Test
    void getTransactions_ShouldReturnOnlyOneTransactionFromService() throws Exception {
        // Given
        User testUser = getTestUser();
        Account testAccount = getAccount(testUser);

        User destinationUser = getTestUser();
        Account destinationAccount = getAccount(destinationUser);

        registerDefaultTransferTransaction(testAccount, destinationAccount);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/banking/history")
                        .param("accountId", String.valueOf(getAccountId(testUser)))
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                // we check for the absence of the second transaction, one that was made in the setup() method
                .andExpect(jsonPath("$.content[1]").doesNotExist());

    }

    @Test
    void exportTransactions_ShouldReturnExportedDataFromService() throws Exception {
        // Given
        User testUser = getTestUser();
        Account testAccount = getAccount(testUser);

        User destinationUser = getTestUser();
        Account destinationAccount = getAccount(destinationUser);

        registerDefaultTransferTransaction(testAccount, destinationAccount);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/banking/history/export")
                        .param("accountId", String.valueOf(getAccountId(testUser)))
                        .param("format", "csv")
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testPreAuthorize_WithInvalidAccountOwnership_ShouldThrowAccessDeniedException() throws Exception {
        // Given
        User testUser = getTestUser();

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/banking/history")
                        .param("accountId", OTHER_ACCOUNT_ID.toString())
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}