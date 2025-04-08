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
     * Validates the provided transaction by ensuring it meets all required criteria.
     * <p>
     * This method performs a series of checks on the transaction, including:
     * <ul>
     *   <li>Verifying the transaction is not null.</li>
     *   <li>Ensuring the source and destination accounts are not the same.</li>
     *   <li>Checking that the transaction amount is provided and positive.</li>
     *   <li>Confirming the transaction type is not null.</li>
     *   <li>Ensuring that at least one account is specified.</li>
     *   <li>Validating that the appropriate accounts are present based on the transaction type.</li>
     *   <li>Verifying the source account has sufficient funds.</li>
     *   <li>For own transfers, ensuring both accounts belong to the same owner.</li>
     * </ul>
     * </p>
     *
     * @param transaction the transaction to validate
     * @throws TransactionValidationException if the transaction is null, has an invalid amount or type,
     *         or if the required accounts are missing or inconsistent
     * @throws TransactionAccountConflictException if the source and destination accounts are identical
     * @throws InsufficientFundsException if the source account does not have enough funds for the transaction
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
     * Validates that the source account of the transaction has sufficient funds.
     *
     * <p>If the transaction has a source account, this method compares the transaction amount against the account's balance.
     * If the amount exceeds the available balance, it throws an {@link InsufficientFundsException} with detailed information.
     * If there is no source account, no validation is performed.</p>
     *
     * @param transaction the transaction to validate
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
     * Determines whether the provided transaction meets all required validation criteria.
     *
     * <p>This method validates the transaction by invoking the internal validation process. If the transaction
     * passes without triggering a validation or insufficient funds exception, it returns {@code true}. Otherwise,
     * it logs the specific validation failure and returns {@code false}.
     *
     * @param transaction the transaction to validate
     * @return {@code true} if the transaction complies with all validation rules; {@code false} otherwise
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
     * Validates that the provided transaction is not null.
     *
     * @param transaction the transaction to validate; must not be null
     * @throws TransactionValidationException if the transaction is null
     */
    private void validateNotNull(Transaction transaction) {
        if (transaction == null) {
            throw new TransactionValidationException("Transaction cannot be null");
        }
    }

    /**
     * Ensures that a transaction does not involve the same account as both source and destination.
     * <p>
     * If either the source or destination account is null, the check is skipped.
     * Otherwise, if the accounts are identical, a TransactionAccountConflictException is thrown.
     *
     * @param transaction the transaction to validate
     * @throws TransactionAccountConflictException if the source and destination accounts are the same
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
     * Validates that the transaction's amount is non-null and positive.
     *
     * <p>This method checks if the transaction amount is set and strictly greater than zero.
     * If the amount is null or not positive, a TransactionValidationException is thrown.</p>
     *
     * @param transaction the transaction whose amount is being validated
     * @throws TransactionValidationException if the transaction amount is null or not greater than zero
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
     * Validates that the transaction's type is not null.
     *
     * @param transaction the transaction being validated
     * @throws TransactionValidationException if the transaction's type is null
     */
    private void validateType(Transaction transaction) {
        if (transaction.getType() == null) {
            throw new TransactionValidationException("Transaction type cannot be null");
        }
    }

    /**
     * Ensures that the transaction has at least one non-null account.
     *
     * <p>This method checks that at least one of the source or destination accounts in the provided
     * transaction is not null. If both accounts are null, it throws a TransactionValidationException
     * with the message "Both accounts cannot be null".</p>
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
     * Validates the accounts for the given transaction based on its category.
     *
     * <p>This method checks the transaction type's category and calls the corresponding account validation method:
     * <ul>
     *   <li><strong>DEPOSIT:</strong> Validates that a destination account exists.</li>
     *   <li><strong>WITHDRAWAL:</strong> Validates that a source account exists.</li>
     *   <li><strong>TRANSFER:</strong> Validates that both source and destination accounts exist.</li>
     *   <li><strong>FEE:</strong> Validates that a source account exists.</li>
     * </ul>
     * If the transaction category is unsupported, a TransactionValidationException is thrown.
     *
     * @param transaction the transaction to validate accounts for
     * @throws TransactionValidationException if the transaction's category is unsupported
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
     * Validates that a transfer transaction contains both a source and a destination account.
     *
     * This method checks whether the provided transaction has a non-null source account and a non-null destination account.
     * If either account is missing, a TransactionValidationException is thrown with a descriptive error message.
     *
     * @param transaction the transfer transaction to validate
     * @throws TransactionValidationException if the source account or destination account is null
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
     * Ensures that a fee transaction includes a source account.
     *
     * <p>This method verifies that the source account of the provided fee transaction is not null.
     * If the source account is missing, it throws a TransactionValidationException indicating
     * that a fee transaction must have a source account.</p>
     *
     * @param transaction the fee transaction to be validated
     * @throws TransactionValidationException if the source account is null
     */
    private void validateFeeAccounts(Transaction transaction) {
        if (transaction.getSourceAccount() == null) {
            throw new TransactionValidationException("Fee transaction must have a source account");
        }
    }
}