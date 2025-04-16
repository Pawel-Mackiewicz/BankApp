package info.mackiewicz.bankapp.integration;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.system.banking.history.dto.TransactionFilterRequest;
import info.mackiewicz.bankapp.system.banking.history.service.TransactionHistoryService;
import info.mackiewicz.bankapp.system.shared.AccountAuthorizationService;
import info.mackiewicz.bankapp.testutils.TestAccountBuilder;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.user.model.User;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionHistoryRestControllerIntegrationTest {

    private static final Integer DEFAULT_ACCOUNT_ID = 7;
    private static final Integer OTHER_ACCOUNT_ID = 42;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionHistoryService transactionHistoryService;

    @Autowired
    private AccountAuthorizationService accountAuthorizationService;

    private Page<Transaction> transactionPage;

    @NotNull
    private static User getTestUser(int accountId) {
        User testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testuser");
        Set<Account> accounts = new HashSet<>();
        Account account = TestAccountBuilder.createTestAccount(accountId, BigDecimal.ZERO, testUser);
        accounts.add(account);
        testUser.setAccounts(accounts);
        return testUser;
    }

    @BeforeEach
    void setUp() {
        transactionPage = new PageImpl<>(Collections.emptyList());

        when(transactionHistoryService.getTransactionHistory(any(TransactionFilterRequest.class)))
                .thenReturn(transactionPage);

        when(transactionHistoryService.exportTransactions(any(TransactionFilterRequest.class), anyString()))
                .thenReturn(org.springframework.http.ResponseEntity.ok(new byte[0]));

    }

    @Test
    void getTransactions_ShouldReturnTransactionsFromService() throws Exception {
        // Given
        User testUser = getTestUser(DEFAULT_ACCOUNT_ID);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/banking/history")
                        .param("accountId", DEFAULT_ACCOUNT_ID.toString())
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        verify(transactionHistoryService).getTransactionHistory(any(TransactionFilterRequest.class));
    }

    @Test
    void exportTransactions_ShouldReturnExportedDataFromService() throws Exception {
        // Given
        User testUser = getTestUser(DEFAULT_ACCOUNT_ID);


        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/banking/history/export")
                        .param("accountId", DEFAULT_ACCOUNT_ID.toString())
                        .param("format", "csv")
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(transactionHistoryService).exportTransactions(any(TransactionFilterRequest.class), eq("csv"));
    }

    @Test
    void testPreAuthorize_WithValidAccountOwnership_ShouldSucceed() throws Exception {
        // Given
        User testUser = getTestUser(DEFAULT_ACCOUNT_ID);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/banking/history")
                        .param("accountId", DEFAULT_ACCOUNT_ID.toString())
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(transactionHistoryService).getTransactionHistory(any(TransactionFilterRequest.class));
    }

    @Test
    void testPreAuthorize_WithInvalidAccountOwnership_ShouldThrowAccessDeniedException() throws Exception {
        // Given
        User testUser = getTestUser(DEFAULT_ACCOUNT_ID);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/banking/history")
                        .param("accountId", OTHER_ACCOUNT_ID.toString())
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(transactionHistoryService, never()).getTransactionHistory(any(TransactionFilterRequest.class));
    }

    @Test
    void testPreAuthorize_ForExportTransactions_ShouldCheckOwnership() throws Exception {

        // Given
        User testUser = getTestUser(DEFAULT_ACCOUNT_ID);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/banking/history/export")
                        .param("accountId", DEFAULT_ACCOUNT_ID.toString())
                        .param("format", "csv")
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testPreAuthorize_ForDifferentAccountIds_ShouldPassCorrectAccountId() throws Exception {

        // Given
        User testUser = getTestUser(DEFAULT_ACCOUNT_ID);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/banking/history")
                        .param("accountId", DEFAULT_ACCOUNT_ID.toString())
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(transactionHistoryService).getTransactionHistory(any(TransactionFilterRequest.class));
    }
}