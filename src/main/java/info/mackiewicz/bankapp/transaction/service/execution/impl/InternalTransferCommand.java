package info.mackiewicz.bankapp.transaction.service.execution.impl;

import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.transaction.service.execution.base.BaseTransferCommand;

/**
 * Service for executing TRANSFER_INTERNAL transactions.
 * Transfers funds between accounts within the same bank.
 */
@Service
public class InternalTransferCommand extends BaseTransferCommand {
    @Override
    public TransactionType getTransactionType() {
        return TransactionType.TRANSFER_INTERNAL;
    }
}
