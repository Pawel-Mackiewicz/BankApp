package info.mackiewicz.bankapp.system.banking.history.service;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.system.banking.history.controller.dto.TransactionFilterRequest;
import info.mackiewicz.bankapp.system.banking.history.exception.UnsupportedExporterException;
import info.mackiewicz.bankapp.system.banking.history.export.TransactionExporter;
import info.mackiewicz.bankapp.system.banking.shared.dto.TransactionResponse;
import info.mackiewicz.bankapp.testutils.TestAccountBuilder;
import info.mackiewicz.bankapp.testutils.TestUserBuilder;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import info.mackiewicz.bankapp.user.model.User;
import org.hibernate.query.SortDirection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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
    private TransactionFilterService filterService;
    
    @Mock
    private TransactionExporter csvExporter;

    @InjectMocks
    private TransactionHistoryService transactionHistoryService;

    private Account testAccount;
    private TransactionFilterRequest filter;
    private List<TransactionResponse> responses;
    private List<Transaction> transactions;

    @BeforeEach
    void setUp() {
        // Create test user and accounts
        User testUser = TestUserBuilder.createTestUser();

        testAccount = TestAccountBuilder.createTestAccount(1, BigDecimal.valueOf(1000), testUser);
        Account destinationAccount = TestAccountBuilder.createTestAccount(2, BigDecimal.valueOf(2000), TestUserBuilder.createRandomTestUser());

        filter = TransactionFilterRequest.builder()
                .accountId(testAccount.getId())
                .page(0)
                .size(20)
                .sortBy("date")
                .sortDirection(SortDirection.DESCENDING)
                .build();

        // Create test transactions
        transactions = Arrays.asList(
            Transaction.buildTransfer()
                .from(testAccount)
                .to(destinationAccount)
                .withAmount(BigDecimal.valueOf(100))
                .withTitle("Test transaction 1")
                .build(),
            Transaction.buildTransfer()
                .from(destinationAccount)
                .to(testAccount)
                .withAmount(BigDecimal.valueOf(200))
                .withTitle("Test transaction 2")
                .build()
        );

        responses = transactions.stream()
                .map(t -> new TransactionResponse(
                            t.getSourceAccount(),
                            t.getDestinationAccount(),
                            t))
                .toList();

        // Initialize exporters list
        List<TransactionExporter> exporters = Collections.singletonList(csvExporter);
        ReflectionTestUtils.setField(transactionHistoryService, "exporters", exporters);
    }

    @Test
    void getTransactionHistory_ReturnsFilteredAndPaginatedTransactions() {
        // Given
        when(transactionService.getRecentTransactions(eq(testAccount.getId()), anyInt())).thenReturn(transactions);
        when(filterService.filterTransactions(eq(transactions), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(transactions);

        // When
        Page<TransactionResponse> result = transactionHistoryService.getTransactionHistory(filter);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(responses, result.getContent());
    }

    @Test
    void getTransactionHistory_WhenNoTransactions_ReturnsEmptyPage() {
        // Given
        when(transactionService.getRecentTransactions(eq(testAccount.getId()), anyInt()))
                .thenReturn(Collections.emptyList());
        when(filterService.filterTransactions(eq(Collections.emptyList()), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        // When
        Page<TransactionResponse> result = transactionHistoryService.getTransactionHistory(filter);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void getTransactionHistory_WhenPageBeyondAvailableData_ReturnsEmptyPage() {
        // Given
        when(transactionService.getRecentTransactions(eq(testAccount.getId()), anyInt())).thenReturn(transactions);
        when(filterService.filterTransactions(eq(transactions), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(transactions);
        
        TransactionFilterRequest pageOutOfBoundsFilter = TransactionFilterRequest.builder()
                .accountId(testAccount.getId())
                .page(10) // Far beyond available data
                .size(20)
                .sortBy("date")
                .sortDirection(SortDirection.DESCENDING)
                .build();

        // When
        Page<TransactionResponse> result = transactionHistoryService.getTransactionHistory(pageOutOfBoundsFilter);

        // Then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements()); // The total size should still be correct
        assertTrue(result.getContent().isEmpty()); // But content should be empty
    }

    @Test
    void exportTransactions_WhenValidFormat_ReturnsExportedData() {
        // Given
        when(transactionService.getRecentTransactions(eq(testAccount.getId()), anyInt())).thenReturn(transactions);
        when(filterService.filterTransactions(eq(transactions), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(transactions);
        when(csvExporter.getFormat()).thenReturn("csv");
        when(csvExporter.exportTransactions(transactions))
                .thenReturn(ResponseEntity.ok("test".getBytes()));

        // When
        ResponseEntity<byte[]> result = transactionHistoryService.exportTransactions(filter, "csv");

        // Then
        assertNotNull(result);
        assertNotNull(result.getBody());
        assertTrue(result.getStatusCode().is2xxSuccessful());
    }

    @Test
    void exportTransactions_WhenInvalidFormat_ThrowsException() {
        // Given
        when(transactionService.getRecentTransactions(eq(testAccount.getId()), anyInt())).thenReturn(transactions);
        when(filterService.filterTransactions(eq(transactions), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(transactions);
        when(csvExporter.getFormat()).thenReturn("csv");

        // When/Then
        assertThrows(UnsupportedExporterException.class,
            () -> transactionHistoryService.exportTransactions(filter, "invalid"));
    }
}