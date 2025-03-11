package info.mackiewicz.bankapp.transaction.service.execution.impl;

import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.transaction.service.execution.base.BaseTransferExecutor;

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
