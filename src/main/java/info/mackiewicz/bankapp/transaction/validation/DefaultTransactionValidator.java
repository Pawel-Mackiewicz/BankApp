package info.mackiewicz.bankapp.transaction.validation;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.transaction.exception.InsufficientFundsException;
import info.mackiewicz.bankapp.transaction.exception.TransactionAccountConflictException;
import info.mackiewicz.bankapp.transaction.exception.TransactionValidationException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.transaction.model.TransactionTypeCategory;
import info.mackiewicz.bankapp.user.model.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DefaultTransactionValidator implements TransactionValidator {

    /**
     * Validates the specified transaction by performing a sequence of integrity checks to ensure
     * it meets all required criteria before processing.
     * <p>
     * The validation steps include checking that the transaction is non-null, the source and destination
     * accounts are distinct, the transaction amount is valid, the transaction type is defined, and the necessary
     * account information is present. Additional validations ensure that the source account has sufficient funds
     * and, for own transfers, that both accounts belong to the same user.
     * </p>
     *
     * @param transaction the transaction to validate
     * @throws TransactionValidationException if any required transaction field is invalid or missing
     * @throws TransactionAccountConflictException if the source and destination accounts are the same
     * @throws InsufficientFundsException if the source account lacks sufficient funds for the transaction
     */
    @Override
    public void validate(Transaction transaction) {
        log.debug("Validating transaction: {}", transaction);
        validateNotNull(transaction);
        validateDifferentAccounts(transaction);
        validateAmount(transaction);
        validateType(transaction);
        validateAccountsNotNull(transaction);
        validateAccounts(transaction);
        validateSufficientFunds(transaction);
        validateSameOwnerForOwnTransfer(transaction);
    }

    /**
     * Ensures that the transaction's source account has enough balance to cover the transaction amount.
     *
     * <p>If the source account is absent, no validation is performed. Otherwise, if the transaction amount
     * exceeds the source account's balance, an {@code InsufficientFundsException} is thrown with details
     * including the formatted IBAN, required amount, and available balance.</p>
     *
     * @param transaction the transaction containing the amount and source account to validate
     * @throws InsufficientFundsException if the transaction amount is greater than the source account balance
     */
    private void validateSufficientFunds(Transaction transaction) {
        if (transaction.getSourceAccount() == null) {
            return; // No source account, no validation needed
        }
        
        BigDecimal transactionAmount = transaction.getAmount();
        BigDecimal accountBalance = transaction.getSourceAccount().getBalance();

        if (transactionAmount.compareTo(accountBalance) > 0) {
            throw new InsufficientFundsException("Insufficient funds for transaction from account: " +
                    transaction.getSourceAccount().getFormattedIban() +
                    ". Required: " + transactionAmount +
                    ", Available: " + accountBalance);
        }
    }

    /**
     * Determines if the specified transaction is valid.
     *
     * <p>This method attempts to validate a transaction using the established business rules. If the
     * transaction passes all checks, it returns {@code true}. If a validation error or insufficient funds
     * situation is detected, the corresponding exception is caught, a debug message is logged, and the method
     * returns {@code false}.
     *
     * @param transaction the transaction to validate
     * @return {@code true} if the transaction is valid; {@code false} otherwise
     */
    @Override
    public boolean isValid(Transaction transaction) {
        try {
            validate(transaction);
            return true;
        } catch (TransactionValidationException e) {
            log.debug("Transaction validation failed: {}", e.getMessage());
            return false;
        } catch (InsufficientFundsException e) {
            log.debug("Transaction validation failed due to insufficient funds: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Ensures that the provided transaction is not null.
     *
     * @param transaction the transaction to validate
     * @throws TransactionValidationException if the transaction is null
     */
    private void validateNotNull(Transaction transaction) {
        if (transaction == null) {
            throw new TransactionValidationException("Transaction cannot be null");
        }
    }

    /**
     * Validates that the source and destination accounts in the provided transaction are different.
     *
     * <p>If both accounts are non-null and reference the same account, a TransactionAccountConflictException is thrown.
     * If either account is null, the conflict check is skipped.</p>
     *
     * @param transaction the transaction to validate
     * @throws TransactionAccountConflictException if the source and destination accounts are identical
     */
    private void validateDifferentAccounts(Transaction transaction) {
        if (transaction.getSourceAccount() == null ||
                transaction.getDestinationAccount() == null) {
            return;
        }
        if (transaction.getSourceAccount().equals(transaction.getDestinationAccount())) {
            throw new TransactionAccountConflictException(
                    "Source and destination accounts is the same for transaction from: " + transaction.getSourceAccount().getFormattedIban());
        }
    }

    /**
     * Validates that the provided transaction has a non-null and positive amount.
     * <p>
     * Throws a TransactionValidationException if the amount is null or not greater than zero.
     *
     * @param transaction the transaction to validate
     * @throws TransactionValidationException if the transaction amount is null or not positive
     */
    private void validateAmount(Transaction transaction) {
        if (transaction.getAmount() == null) {
            throw new TransactionValidationException("Transaction amount cannot be null");
        }
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionValidationException("Transaction amount must be positive");
        }
    }

    /**
     * Validates that the transaction type is specified.
     *
     * @param transaction the transaction to validate
     * @throws TransactionValidationException if the transaction type is null
     */
    private void validateType(Transaction transaction) {
        if (transaction.getType() == null) {
            throw new TransactionValidationException("Transaction type cannot be null");
        }
    }

    /**
     * Ensures that the transaction has at least one non-null account.
     * <p>
     * Validates that either the source or destination account is provided for the transaction.
     * If both accounts are null, a {@link TransactionValidationException} is thrown.
     *
     * @param transaction the transaction to validate
     * @throws TransactionValidationException if both the source and destination accounts are null
     */
    private void validateAccountsNotNull(Transaction transaction) {
        if (transaction.getSourceAccount() == null && transaction.getDestinationAccount() == null) {
            throw new TransactionValidationException("Both accounts cannot be null");
        }
    }

    private void validateSameOwnerForOwnTransfer(Transaction transaction) {
        if (transaction.getType() == TransactionType.TRANSFER_OWN) {
            if (transaction.getSourceAccount() != null && transaction.getDestinationAccount() != null) {
                // Check if accounts belong to the same owner
                User sourceOwner = transaction.getSourceAccount().getRawOwner();
                User destinationOwner = transaction.getDestinationAccount().getRawOwner();
                if (!sourceOwner.equals(destinationOwner)) {
                    throw new TransactionValidationException(
                            "Own transfer must be between accounts of the same owner");
                }
            }
        }
    }

    /**
     * Validates the transaction's account configuration based on its type category.
     *
     * <p>This method delegates to the appropriate validation routine depending on the transaction's
     * category: for deposits, it verifies the presence of a destination account; for withdrawals, it
     * ensures a source account is provided; for transfers, it checks that both source and destination accounts
     * are set; and for fee transactions, it confirms that a source account exists. If the transaction's
     * category is unsupported, a TransactionValidationException is thrown.</p>
     *
     * @param transaction the transaction to validate
     * @throws TransactionValidationException if the transaction category is unsupported
     */
    private void validateAccounts(Transaction transaction) {
        TransactionType type = transaction.getType();
        TransactionTypeCategory category = type.getCategory();

        switch (category) {
            case DEPOSIT -> validateDepositAccounts(transaction);
            case WITHDRAWAL -> validateWithdrawalAccounts(transaction);
            case TRANSFER -> validateTransferAccounts(transaction);
            case FEE -> validateFeeAccounts(transaction);
            default -> throw new TransactionValidationException("Unsupported transaction category: " + category);
        }
    }

    private void validateDepositAccounts(Transaction transaction) {
        if (transaction.getDestinationAccount() == null) {
            throw new TransactionValidationException("Deposit transaction must have a destination account");
        }
    }

    private void validateWithdrawalAccounts(Transaction transaction) {
        if (transaction.getSourceAccount() == null) {
            throw new TransactionValidationException("Withdrawal transaction must have a source account");
        }
    }

    /**
     * Validates that a transfer transaction includes both a source and a destination account.
     * If either account is missing, a TransactionValidationException is thrown.
     *
     * @param transaction the transfer transaction to validate
     * @throws TransactionValidationException if the source or destination account is null
     */
    private void validateTransferAccounts(Transaction transaction) {
        if (transaction.getSourceAccount() == null) {
            throw new TransactionValidationException("Transfer transaction must have a source account");
        }
        if (transaction.getDestinationAccount() == null) {
            throw new TransactionValidationException("Transfer transaction must have a destination account");
        }
    }

    /**
     * Validates that a fee transaction includes a non-null source account.
     *
     * <p>If the source account is missing from the transaction, a TransactionValidationException is thrown
     * indicating that fee transactions must have an associated source account.
     *
     * @param transaction the fee transaction to validate
     * @throws TransactionValidationException if the transaction's source account is null
     */
    private void validateFeeAccounts(Transaction transaction) {
        if (transaction.getSourceAccount() == null) {
            throw new TransactionValidationException("Fee transaction must have a source account");
        }
    }
}