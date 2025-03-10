package info.mackiewicz.bankapp.transaction.service.execution.impl;

import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.transaction.service.execution.base.BaseTransferCommand;

/**
 * Service for executing TRANSFER_EXTERNAL transactions.
 * Transfers funds to another bank, potentially with fees.
 */
@Service
public class ExternalTransferCommand extends BaseTransferCommand {
    @Override
    public TransactionType getTransactionType() {
        return TransactionType.TRANSFER_EXTERNAL;
    }
}
