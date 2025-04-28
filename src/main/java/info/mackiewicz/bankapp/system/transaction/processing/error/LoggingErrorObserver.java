package info.mackiewicz.bankapp.system.transaction.processing.error;

import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Observer implementation that logs transaction errors
 */
@Slf4j
@Component
public class LoggingErrorObserver implements TransactionErrorObserver {
    
    @Override
    public void onTransactionError(Transaction transaction, Exception error) {
        log.error("Transaction error: {} - {}", transaction.getId(), error.getMessage(), error);
    }
}