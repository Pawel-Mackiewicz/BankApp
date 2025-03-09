package info.mackiewicz.bankapp.shared.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import info.mackiewicz.bankapp.transaction.service.TransactionService;

import static org.mockito.Mockito.*;

class SchedulerServiceTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private SchedulerService schedulerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void scheduleProcessAllNewTransactions_CallsTransactionService() {
        // Act
        schedulerService.scheduleProcessAllNewTransactions();

        // Assert
        verify(transactionService, times(1)).processAllNewTransactions();
    }
}