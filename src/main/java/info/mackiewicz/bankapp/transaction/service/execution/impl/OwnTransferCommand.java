package info.mackiewicz.bankapp.transaction.service.execution.impl;

import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.transaction.service.execution.base.BaseTransferCommand;

/**
 * Service for executing TRANSFER_OWN transactions.
 * Transfers funds between user's own accounts.
 */
@Service
public class OwnTransferCommand extends BaseTransferCommand {
    @Override
    public TransactionType getTransactionType() {
        return TransactionType.TRANSFER_OWN;
    }
}
