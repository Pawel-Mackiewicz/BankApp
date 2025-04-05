package info.mackiewicz.bankapp.transaction.validation;

import info.mackiewicz.bankapp.transaction.exception.InsufficientFundsException;
import info.mackiewicz.bankapp.transaction.exception.TransactionAccountConflictException;
import info.mackiewicz.bankapp.transaction.exception.TransactionValidationException;
import info.mackiewicz.bankapp.transaction.model.Transaction;

public interface TransactionValidator {
    /**
     * Validates the transaction and throws exceptions if invalid.
     *
     * @param transaction Transaction to validate
     * @throws TransactionValidationException if validation fails due to:
     *         - null transaction
     *         - null or non-positive amount
     *         - null transaction type
     *         - both accounts being null
     *         - missing required accounts for specific transaction types
     *         - unsupported transaction category
     *         - different owners for own transfer
     * @throws TransactionAccountConflictException if source and destination accounts are the same
     * @throws InsufficientFundsException if source account has insufficient funds
     */
    void validate(Transaction transaction);

    /**
     * Checks if the transaction is valid without throwing an exception.
     *
     * @param transaction Transaction to validate
     * @return true if transaction is valid, false otherwise
     */
    boolean isValid(Transaction transaction);
}