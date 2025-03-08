package info.mackiewicz.bankapp.transaction.validation;

import info.mackiewicz.bankapp.transaction.model.Transaction;

public interface TransactionValidator {
    /**
     * Validates the transaction and throws TransactionValidationException if invalid.
     *
     * @param transaction Transaction to validate
     * @throws TransactionValidationException if validation fails
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