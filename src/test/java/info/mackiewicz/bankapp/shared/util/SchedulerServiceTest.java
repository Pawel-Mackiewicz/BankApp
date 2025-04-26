package info.mackiewicz.bankapp.shared.util;

import info.mackiewicz.bankapp.system.transaction.processing.TransactionProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SchedulerServiceTest {

    @Mock
    private TransactionProcessingService transactionProcessingService;

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
        verify(transactionProcessingService, times(1)).processAllNewTransactions();
    }
}