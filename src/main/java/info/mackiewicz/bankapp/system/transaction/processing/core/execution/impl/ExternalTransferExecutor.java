package info.mackiewicz.bankapp.system.transaction.processing.core.execution.impl;

import info.mackiewicz.bankapp.core.transaction.model.TransactionType;
import info.mackiewicz.bankapp.system.transaction.processing.core.execution.base.BaseTransferExecutor;
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
