package info.mackiewicz.bankapp.transaction.service.error;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.transaction.model.Transaction;
import lombok.extern.slf4j.Slf4j;

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