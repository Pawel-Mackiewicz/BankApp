package info.mackiewicz.bankapp.system.transaction.processing.core.execution.impl;

import info.mackiewicz.bankapp.core.transaction.model.TransactionType;
import info.mackiewicz.bankapp.system.transaction.processing.core.execution.base.BaseTransferExecutor;
import org.springframework.stereotype.Service;

/**
 * Service for executing TRANSFER_INTERNAL transactions.
 * Transfers funds between accounts within the same bank.
 */
@Service
public class InternalTransferExecutor extends BaseTransferExecutor {
    @Override
    public TransactionType getTransactionType() {
        return TransactionType.TRANSFER_INTERNAL;
    }
}
