package info.mackiewicz.bankapp.presentation.dashboard.service;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.presentation.dashboard.dto.TransactionFilterDTO;
import info.mackiewicz.bankapp.presentation.dashboard.service.export.TransactionExporter;
import info.mackiewicz.bankapp.testutils.TestAccountBuilder;
import info.mackiewicz.bankapp.testutils.TestUserBuilder;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import info.mackiewicz.bankapp.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionHistoryServiceTest {

    @Mock
    private TransactionService transactionService;
    
    @Mock
    private AccountService accountService;
    
    @Mock
    private TransactionFilterService filterService;
    
    @Mock
    private TransactionExporter csvExporter;

    @InjectMocks
    private TransactionHistoryService transactionHistoryService;

    private User testUser;
    private Integer testUserId;
    private Integer otherUserId;
    private Account testAccount;
    private Account destinationAccount;
    private TransactionFilterDTO filter;
    private List<Transaction> transactions;

    @BeforeEach
    void setUp() {
        // Create test user and accounts
        testUser = TestUserBuilder.createTestUser();
        testUserId = testUser.getId();
        otherUserId = TestUserBuilder.createRandomTestUser().getId();
        
        testAccount = TestAccountBuilder.createTestAccount(1, BigDecimal.valueOf(1000), testUser);
        destinationAccount = TestAccountBuilder.createTestAccount(2, BigDecimal.valueOf(2000), TestUserBuilder.createRandomTestUser());

        filter = TransactionFilterDTO.builder()
                .accountId(testAccount.getId())
                .page(0)
                .size(20)
                .build();

        // Create test transactions
        transactions = Arrays.asList(
            Transaction.buildTransfer()
                .asInternalTransfer()
                .from(testAccount)
                .to(destinationAccount)
                .withAmount(BigDecimal.valueOf(100))
                .withTitle("Test transaction 1")
                .build(),
            Transaction.buildTransfer()
                .asInternalTransfer()
                .from(destinationAccount)
                .to(testAccount)
                .withAmount(BigDecimal.valueOf(200))
                .withTitle("Test transaction 2")
                .build()
        );

        // Initialize exporters list
        List<TransactionExporter> exporters = Collections.singletonList(csvExporter);
        ReflectionTestUtils.setField(transactionHistoryService, "exporters", exporters);
    }

    @Test
    void getTransactionHistory_WhenUserOwnsAccount_ReturnsTransactions() {
        // Given
        when(accountService.getAccountById(testAccount.getId())).thenReturn(testAccount);
        when(transactionService.getRecentTransactions(eq(testAccount.getId()), anyInt())).thenReturn(transactions);
        when(filterService.filterTransactions(eq(transactions), any(), any(), any(), any(), any(), any()))
                .thenReturn(transactions);

        // When
        Page<Transaction> result = transactionHistoryService.getTransactionHistory(testUserId, filter);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(transactions, result.getContent());
    }

    @Test
    void getTransactionHistory_WhenUserDoesNotOwnAccount_ThrowsAccessDeniedException() {
        // Given
        when(accountService.getAccountById(testAccount.getId())).thenReturn(testAccount);

        // When/Then
        assertThrows(AccessDeniedException.class, 
            () -> transactionHistoryService.getTransactionHistory(otherUserId, filter));
    }

    @Test
    void getTransactionHistory_WhenNoTransactions_ReturnsEmptyPage() {
        // Given
        when(accountService.getAccountById(testAccount.getId())).thenReturn(testAccount);
        when(transactionService.getRecentTransactions(eq(testAccount.getId()), anyInt()))
                .thenReturn(Collections.emptyList());
        when(filterService.filterTransactions(eq(Collections.emptyList()), any(), any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        // When
        Page<Transaction> result = transactionHistoryService.getTransactionHistory(testUserId, filter);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void exportTransactions_WhenValidFormat_ReturnsExportedData() {
        // Given
        when(accountService.getAccountById(testAccount.getId())).thenReturn(testAccount);
        when(transactionService.getRecentTransactions(eq(testAccount.getId()), anyInt())).thenReturn(transactions);
        when(filterService.filterTransactions(eq(transactions), any(), any(), any(), any(), any(), any()))
                .thenReturn(transactions);
        when(csvExporter.getFormat()).thenReturn("csv");
        when(csvExporter.exportTransactions(transactions))
                .thenReturn(ResponseEntity.ok("test".getBytes()));

        // When
        ResponseEntity<byte[]> result = transactionHistoryService.exportTransactions(testUserId, filter, "csv");

        // Then
        assertNotNull(result);
        assertNotNull(result.getBody());
        assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Test
    void exportTransactions_WhenInvalidFormat_ThrowsException() {
        // Given
        when(accountService.getAccountById(testAccount.getId())).thenReturn(testAccount);
        when(csvExporter.getFormat()).thenReturn("csv");
        when(transactionService.getRecentTransactions(eq(testAccount.getId()), anyInt())).thenReturn(transactions);
        when(filterService.filterTransactions(eq(transactions), any(), any(), any(), any(), any(), any()))
                .thenReturn(transactions);

        // When/Then
        assertThrows(UnsupportedOperationException.class,
            () -> transactionHistoryService.exportTransactions(testUserId, filter, "invalid"));
    }
}