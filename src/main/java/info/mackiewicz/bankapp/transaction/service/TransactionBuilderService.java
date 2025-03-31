package info.mackiewicz.bankapp.transaction.service;

import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.transaction.model.dto.BankingOperationRequest;

/**
 * Service for building transactions.
 * This service is responsible for creating transaction objects based on the provided request and account information.
 */
@Service
public class TransactionBuilderService {
    
    public Transaction buildTransferTransaction(BankingOperationRequest request, Account sourceAccount,
            Account destinationAccount, TransactionType type) {
        return Transaction.buildTransfer()
                .from(sourceAccount)
                .to(destinationAccount)
                .withTransactionType(type)
                .withAmount(request.getAmount())
                .withTitle(request.getTitle())
                .build();
    }
}