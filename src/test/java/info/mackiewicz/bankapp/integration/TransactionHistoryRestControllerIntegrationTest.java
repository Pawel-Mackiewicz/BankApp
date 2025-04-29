package info.mackiewicz.bankapp.integration;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import info.mackiewicz.bankapp.core.transaction.service.TransactionService;
import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.core.user.service.UserService;
import info.mackiewicz.bankapp.integration.utils.IntegrationTestAccountService;
import info.mackiewicz.bankapp.integration.utils.IntegrationTestConfig;
import info.mackiewicz.bankapp.integration.utils.IntegrationTestUserService;
import info.mackiewicz.bankapp.shared.util.Util;
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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@Import(IntegrationTestConfig.class)
@AutoConfigureMockMvc
@DisplayName("Transaction History System Integration Test")
class TransactionHistoryRestControllerIntegrationTest {

    private static final BigDecimal DEFAULT_BALANCE = new BigDecimal("1000.00");
    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal("100.00");
    private static final String DEFAULT_TRANSACTION_TITLE = "TEST";

    private static final String API_HISTORY_PATH = "/api/banking/history";
    private static final String API_HISTORY_EXPORT_PATH = API_HISTORY_PATH + "/export";

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

    private User createTestUserWithAccount() {
        User testUser = testUserService.createRandomTestUser();
        testAccountService.createTestAccountWithBalance(testUser.getId(), DEFAULT_BALANCE);

        return userService.getUserById(testUser.getId());
    }

    private Transaction registerTransaction(Account sourceAccount, Account destinationAccount, BigDecimal amount, String title) {
        Transaction transaction = Transaction.buildTransfer()
                .from(sourceAccount)
                .to(destinationAccount)
                .withAmount(amount)
                .withTitle(title)
                .build();
        return transactionService.registerTransaction(transaction);
    }

    private void registerDefaultTransferTransaction(Account testAccount, Account destinationAccount) {
        registerTransaction(testAccount, destinationAccount, DEFAULT_AMOUNT, DEFAULT_TRANSACTION_TITLE);
    }

    @BeforeEach
    void setUp() {
        User testUser = createTestUserWithAccount();
        Account testAccount = getAccount(testUser);

        User destinationUser = createTestUserWithAccount();
        Account destinationAccount = getAccount(destinationUser);
        //this transaction should not be returned in the result
        registerDefaultTransferTransaction(testAccount, destinationAccount);
    }

    @Test
    void getTransactions_ShouldReturnTransactionsFromService() throws Exception {
        // Given
        User testUser = createTestUserWithAccount();
        Account testAccount = getAccount(testUser);

        User destinationUser = createTestUserWithAccount();
        Account destinationAccount = getAccount(destinationUser);

        registerDefaultTransferTransaction(testAccount, destinationAccount);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_HISTORY_PATH)
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
        User testUser = createTestUserWithAccount();
        Account testAccount = getAccount(testUser);

        User destinationUser = createTestUserWithAccount();
        Account destinationAccount = getAccount(destinationUser);

        registerDefaultTransferTransaction(testAccount, destinationAccount);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_HISTORY_PATH)
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
        User testUser = createTestUserWithAccount();
        Account testAccount = getAccount(testUser);

        User destinationUser = createTestUserWithAccount();
        Account destinationAccount = getAccount(destinationUser);

        registerDefaultTransferTransaction(testAccount, destinationAccount);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_HISTORY_EXPORT_PATH)
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
        User testUser = createTestUserWithAccount();
        User otherUser = createTestUserWithAccount();
        int otherUserAccountId = getAccountId(otherUser);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_HISTORY_PATH)
                        .param("accountId", String.valueOf(otherUserAccountId))
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void getTransactions_ShouldFilterByAmountRange() throws Exception {
        // Given
        User testUser = createTestUserWithAccount();
        Account testAccount = getAccount(testUser);
        Integer accountId = getAccountId(testUser);

        User destinationUser = createTestUserWithAccount();
        Account destinationAccount = getAccount(destinationUser);

        // Create transactions with different amounts
        registerTransaction(testAccount, destinationAccount, new BigDecimal("50.00"), "Transaction 1");
        Transaction expectedTransaction = registerTransaction(testAccount, destinationAccount, new BigDecimal("150.00"), "Transaction 2"); // Should be returned
        registerTransaction(testAccount, destinationAccount, new BigDecimal("250.00"), "Transaction 3");

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_HISTORY_PATH)
                        .param("accountId", String.valueOf(accountId))
                        .param("amountFrom", "100.00") // Filter range
                        .param("amountTo", "200.00")   // Filter range
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1)) // Expect only one transaction
                .andExpect(jsonPath("$.content[0].transactionInfo.amount").value(expectedTransaction.getAmount().doubleValue()))
                .andExpect(jsonPath("$.content[0].transactionInfo.title").value(expectedTransaction.getTitle()));
    }

    @Test
    void getTransactions_ShouldFilterByQueryString() throws Exception {
        // Given
        User testUser = createTestUserWithAccount();
        Account testAccount = getAccount(testUser);
        Integer accountId = getAccountId(testUser);
        User destinationUser = createTestUserWithAccount();
        Account destinationAccount = getAccount(destinationUser);

        registerTransaction(testAccount, destinationAccount, new BigDecimal("10.00"), "Grocery Shopping");
        Transaction expected = registerTransaction(testAccount, destinationAccount, new BigDecimal("20.00"), "Online Store Purchase");
        registerTransaction(testAccount, destinationAccount, new BigDecimal("30.00"), "Gas Station");

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_HISTORY_PATH)
                        .param("accountId", String.valueOf(accountId))
                        .param("query", "Store") // Search for "Store" in title/description
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].transactionInfo.title").value(expected.getTitle()));
    }

    @Test
    void getTransactions_ShouldSortByDateDescending() throws Exception {
        // Given
        User testUser = createTestUserWithAccount();
        Account testAccount = getAccount(testUser);
        Integer accountId = getAccountId(testUser);
        User destinationUser = createTestUserWithAccount();
        Account destinationAccount = getAccount(destinationUser);

        // `Sleep` is used to make sure that the timestamps are different
        Transaction t1 = registerTransaction(testAccount, destinationAccount, new BigDecimal("10.00"), "First");
        Util.sleep(100);
        Transaction t2 = registerTransaction(testAccount, destinationAccount, new BigDecimal("20.00"), "Second");
        Util.sleep(100);
        Transaction t3 = registerTransaction(testAccount, destinationAccount, new BigDecimal("30.00"), "Third");


        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_HISTORY_PATH)
                        .param("accountId", String.valueOf(accountId))
                        .param("sortBy", "date")
                        .param("sortDirection", "DESCENDING")
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[0].transactionInfo.title").value(t3.getTitle())) // Most recent
                .andExpect(jsonPath("$.content[1].transactionInfo.title").value(t2.getTitle()))
                .andExpect(jsonPath("$.content[2].transactionInfo.title").value(t1.getTitle())); // Oldest
    }

    @Test
    void getTransactions_ShouldSortByAmountAscending() throws Exception {
        // Given
        User testUser = createTestUserWithAccount();
        Account testAccount = getAccount(testUser);
        Integer accountId = getAccountId(testUser);
        User destinationUser = createTestUserWithAccount();
        Account destinationAccount = getAccount(destinationUser);

        Transaction t1 = registerTransaction(testAccount, destinationAccount, new BigDecimal("30.00"), "Thirty");
        Transaction t2 = registerTransaction(testAccount, destinationAccount, new BigDecimal("10.00"), "Ten"); // Should be first
        Transaction t3 = registerTransaction(testAccount, destinationAccount, new BigDecimal("20.00"), "Twenty"); // Should be second

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_HISTORY_PATH)
                        .param("accountId", String.valueOf(accountId))
                        .param("sortBy", "amount")
                        .param("sortDirection", "ASCENDING")
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[0].transactionInfo.title").value(t2.getTitle())) // Smallest amount
                .andExpect(jsonPath("$.content[1].transactionInfo.title").value(t3.getTitle()))
                .andExpect(jsonPath("$.content[2].transactionInfo.title").value(t1.getTitle())); // Largest amount
    }


    @Test
    void getTransactions_ShouldHandlePagination() throws Exception {
        // Given
        User testUser = createTestUserWithAccount();
        Account testAccount = getAccount(testUser);
        Integer accountId = getAccountId(testUser);
        User destinationUser = createTestUserWithAccount();
        Account destinationAccount = getAccount(destinationUser);

        // Create 15 transactions
        for (int i = 1; i <= 15; i++) {
            registerTransaction(testAccount, destinationAccount, new BigDecimal(i * 10), "Trans " + i);
        }

        // When & Then - Request page 1 (second page, size 10)
        mockMvc.perform(MockMvcRequestBuilders.get(API_HISTORY_PATH)
                        .param("accountId", String.valueOf(accountId))
                        .param("page", "1") // 0-indexed, so this is the second page
                        .param("size", "10")
                        .param("sortBy", "amount") // Sort consistently for predictable results
                        .param("sortDirection", "ASCENDING")
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(5)) // 5 transactions on the second page
                .andExpect(jsonPath("$.pageable.pageNumber").value(1))
                .andExpect(jsonPath("$.pageable.pageSize").value(10))
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.totalPages").value(2))
                // Check the first element on page 1 (which is the 11th overall transaction by amount)
                .andExpect(jsonPath("$.content[0].transactionInfo.title").value("Trans 11"));
    }

    @Test
    void getTransactions_ShouldReturnBadRequestForInvalidFilterParams() throws Exception {
        // Given
        User testUser = createTestUserWithAccount();
        Integer accountId = getAccountId(testUser);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_HISTORY_PATH)
                        .param("accountId", String.valueOf(accountId))
                        .param("dateFrom", "invalid-date-format") // Invalid date
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest()); // Expect 400 due to validation failure
        // Potentially check error message details if the API returns structured errors
        // .andExpect(jsonPath("$.errors[0].field").value("dateFrom"))
    }

    @Test
    void exportTransactions_ShouldSupportPdfFormat() throws Exception {
        // Given
        User testUser = createTestUserWithAccount();
        Account testAccount = getAccount(testUser);
        Integer accountId = getAccountId(testUser);
        User destinationUser = createTestUserWithAccount();
        Account destinationAccount = getAccount(destinationUser);
        registerTransaction(testAccount, destinationAccount, new BigDecimal("100.00"), "PDF Export Test");

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_HISTORY_EXPORT_PATH)
                        .param("accountId", String.valueOf(accountId))
                        .param("format", "pdf") // Request PDF format
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                // Verify content type for PDF
                .andExpect(result -> {
                    String contentType = result.getResponse().getContentType();
                    assertNotNull(contentType);
                    // Allow for potential variations like "application/pdf;charset=UTF-8"
                    assertTrue(contentType.startsWith(MediaType.APPLICATION_PDF_VALUE));
                    assertTrue(result.getResponse().getContentAsByteArray().length > 0);
                });
    }

    @Test
    void exportTransactions_ShouldReturnErrorForUnsupportedFormat() throws Exception {
        // Given
        User testUser = createTestUserWithAccount();
        Account testAccount = getAccount(testUser);
        Integer accountId = getAccountId(testUser);
        User destinationUser = createTestUserWithAccount();
        Account destinationAccount = getAccount(destinationUser);

        // Create only 2 transactions
        registerTransaction(testAccount, destinationAccount, new BigDecimal("10.00"), "Trans 1");
        registerTransaction(testAccount, destinationAccount, new BigDecimal("20.00"), "Trans 2");

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_HISTORY_EXPORT_PATH)
                        .param("accountId", String.valueOf(accountId))
                        .param("format", "unsupported-format") // Request an invalid format
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                // According to the interface documentation,
                // this should return 500
                // don't really know, why it works as it supposed to (it's made to throw 415
                // but in prod it returns 500
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void getTransactions_ShouldReturnEmptyListForAccountWithNoHistory() throws Exception {
        // Given
        User testUser = createTestUserWithAccount(); // Creates a user and an account with balance, but no transactions yet
        Integer accountId = getAccountId(testUser);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_HISTORY_PATH)
                        .param("accountId", String.valueOf(accountId))
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0)) // Expect empty array
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void getTransactions_ShouldReturnEmptyListForPageOutOfBounds() throws Exception {
        // Given
        User testUser = createTestUserWithAccount();
        Account testAccount = getAccount(testUser);
        Integer accountId = getAccountId(testUser);
        User destinationUser = createTestUserWithAccount();
        Account destinationAccount = getAccount(destinationUser);

        // Create only 2 transactions
        registerTransaction(testAccount, destinationAccount, new BigDecimal("10.00"), "Trans 1");
        registerTransaction(testAccount, destinationAccount, new BigDecimal("20.00"), "Trans 2");


        // When & Then - Request page 10 (far beyond available data)
        mockMvc.perform(MockMvcRequestBuilders.get(API_HISTORY_PATH)
                        .param("accountId", String.valueOf(accountId))
                        .param("page", "10")
                        .param("size", "10")
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0)) // Expect empty content
                .andExpect(jsonPath("$.pageable.pageNumber").value(10))
                .andExpect(jsonPath("$.totalElements").value(2)) // Total elements should still be correct
                .andExpect(jsonPath("$.totalPages").value(1)); // Only 1 page exists
    }

    @Test
    void getTransactions_ShouldReturnUnauthorizedWithoutAuthentication() throws Exception {
        // Given
        // No user context provided

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_HISTORY_PATH)
                        .param("accountId", "1") // Any account ID
                        .with(SecurityMockMvcRequestPostProcessors.csrf()) // CSRF might still be needed depending on config
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized()); // Expect 401
    }
}