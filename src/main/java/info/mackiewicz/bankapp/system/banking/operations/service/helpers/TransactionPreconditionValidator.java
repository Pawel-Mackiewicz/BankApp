package info.mackiewicz.bankapp.system.banking.operations.service.helpers;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.transaction.exception.InsufficientFundsException;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Component responsible for validating preconditions required for processing transactions.
 * The primary objective of this class is to ensure that all the necessary conditions, such as
 * sufficient funds, are met before a transaction is executed.
 */
@Component
public class TransactionPreconditionValidator {

    /**
     * Validates whether the source account has sufficient funds to complete a transaction.
     * If the account balance is less than the required amount, an {@code InsufficientFundsException} is thrown.
     *
     * @param sourceAccount The account from which funds are being withdrawn. Must not be null.
     * @param amount        The amount to be checked against the account balance. Must not be null.
     *
     * @throws InsufficientFundsException If the account balance is less than the specified amount.
     */
    public void checkFunds(@NonNull Account sourceAccount, @NonNull BigDecimal amount) throws InsufficientFundsException {
        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }
    }

}
