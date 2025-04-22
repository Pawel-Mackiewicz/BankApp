package info.mackiewicz.bankapp.system.transaction.processing.core.execution.impl;

import info.mackiewicz.bankapp.system.transaction.processing.core.execution.base.BaseTransferExecutor;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import org.springframework.stereotype.Service;

/**
 * Service for executing TRANSFER_EXTERNAL transactions.
 * Transfers funds to another bank, potentially with fees.
 */
@Service
public class ExternalTransferExecutor extends BaseTransferExecutor {
    @Override
    public TransactionType getTransactionType() {
        return TransactionType.TRANSFER_EXTERNAL;
    }
}
