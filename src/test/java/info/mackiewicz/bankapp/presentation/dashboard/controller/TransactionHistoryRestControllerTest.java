package info.mackiewicz.bankapp.presentation.dashboard.controller;

import info.mackiewicz.bankapp.system.banking.history.controller.TransactionHistoryRestController;
import info.mackiewicz.bankapp.system.banking.history.dto.TransactionFilterDTO;
import info.mackiewicz.bankapp.system.banking.history.service.TransactionHistoryService;
import info.mackiewicz.bankapp.testutils.TestUserBuilder;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionHistoryRestControllerTest {

    @Mock
    private TransactionHistoryService transactionHistoryService;

    @InjectMocks
    private TransactionHistoryRestController controller;

    private User testUser;
    private Integer testUserId;
    private TransactionFilterDTO filter;
    private Page<Transaction> transactionPage;

    @BeforeEach
    void setUp() {
        testUser = TestUserBuilder.createTestUser();
        testUserId = testUser.getId();
        filter = TransactionFilterDTO.builder()
                .accountId(1)
                .page(0)
                .size(20)
                .build();
        transactionPage = new PageImpl<>(Collections.emptyList());
    }

    @Test
    void getTransactions_ShouldReturnTransactionsFromService() {
        // Given
        when(transactionHistoryService.getTransactionHistory(eq(testUserId), any(TransactionFilterDTO.class)))
                .thenReturn(transactionPage);

        // When
        ResponseEntity<Page<Transaction>> response = controller.getTransactions(testUser, filter);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactionPage, response.getBody());
    }

    @Test
    void exportTransactions_ShouldReturnExportedDataFromService() {
        // Given
        byte[] exportedData = "test data".getBytes();
        when(transactionHistoryService.exportTransactions(eq(testUserId), any(TransactionFilterDTO.class), eq("csv")))
                .thenReturn(ResponseEntity.ok(exportedData));

        // When
        ResponseEntity<byte[]> response = controller.exportTransactions(testUser, filter, "csv");

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(exportedData, response.getBody());
    }
}