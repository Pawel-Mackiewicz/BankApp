package info.mackiewicz.bankapp.system.transaction.processing.core.execution.impl;

import info.mackiewicz.bankapp.system.transaction.processing.core.execution.base.BaseTransferExecutor;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import org.springframework.stereotype.Service;

/**
 * Service for executing TRANSFER_OWN transactions.
 * Transfers funds between user's own accounts.
 */
@Service
public class OwnTransferExecutor extends BaseTransferExecutor {
    @Override
    public TransactionType getTransactionType() {
        return TransactionType.TRANSFER_OWN;
    }
}
