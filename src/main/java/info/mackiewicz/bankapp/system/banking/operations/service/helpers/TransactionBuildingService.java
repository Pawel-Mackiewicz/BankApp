package info.mackiewicz.bankapp.system.banking.operations.service.helpers;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.transaction.exception.TransactionBuildingException;
import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Service for building transactions.
 * This service is responsible for creating transaction objects based on the provided request and account information.
 */
@Service
@RequiredArgsConstructor
public class TransactionBuildingService {

    /**
     * Builds a transfer transaction between two accounts.
     *
     * @param amount            The amount to be transferred
     * @param title             The title of the transaction
     * @param sourceAccount      The account from which the amount is transferred
     * @param destinationAccount The account to which the amount is transferred
     * @return A Transaction object representing the transfer
     * @throws TransactionBuildingException if the transaction cannot be built
     */
    public Transaction buildTransferTransaction(BigDecimal amount, String title, Account sourceAccount,
            Account destinationAccount) {

        return Transaction.buildTransfer()
                .from(sourceAccount)
                .to(destinationAccount)
                .withAmount(amount)
                .withTitle(title)
                .build();
    }
}