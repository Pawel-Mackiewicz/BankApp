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
     * Validates the provided transaction by performing a comprehensive set of checks
     * to ensure it meets all required criteria before processing.
     * <p>
     * This method verifies that the transaction is not null, that the source and destination
     * accounts are distinct, and that the transaction has a valid amount and type. It also checks
     * that at least one account is present and that the account configuration aligns with the
     * transaction category (deposit, withdrawal, transfer, or fee). Additionally, it confirms that
     * the source account has sufficient funds and, for TRANSFER_OWN transactions, that both accounts
     * belong to the same owner.
     * </p>
     *
     * @param transaction the transaction to be validated
     * @throws TransactionValidationException if any basic validation (such as null checks, invalid amount,
     *         unspecified type, or missing required accounts) fails
     * @throws TransactionAccountConflictException if the source and destination accounts are identical
     * @throws InsufficientFundsException if the source account lacks sufficient funds
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
     * Validates that the source account of the given transaction has sufficient funds
     * for the transaction amount.
     *
     * <p>If the transaction lacks a source account, no validation is performed.
     * Otherwise, if the transaction amount exceeds the available balance of the source
     * account, an {@link InsufficientFundsException} is thrown.
     *
     * @param transaction the transaction to verify for sufficient funds
     * @throws InsufficientFundsException if the source account's balance is lower than the transaction amount
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
     * Determines whether the given transaction is valid.
     *
     * <p>This method validates the transaction by invoking {@link #validate(Transaction)}. If validation completes successfully,
     * it returns {@code true}. If a validation exception—either a {@link TransactionValidationException} or a 
     * {@link InsufficientFundsException}—is encountered, a debug message is logged and the method returns {@code false}.</p>
     *
     * @param transaction the transaction to validate
     * @return {@code true} if the transaction passes validation; {@code false} otherwise
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
     * Validates that the source and destination accounts of the given transaction are distinct.
     *
     * <p>If either account is null, no comparison is made. When both accounts are non-null and equal,
     * a TransactionAccountConflictException is thrown.
     *
     * @param transaction the transaction containing the accounts to validate
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
     * Validates that the transaction's amount is non-null and strictly positive.
     *
     * @param transaction the transaction to validate
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
     * Validates that the transaction type is not null.
     *
     * @param transaction the transaction to be validated
     * @throws TransactionValidationException if the transaction's type is null
     */
    private void validateType(Transaction transaction) {
        if (transaction.getType() == null) {
            throw new TransactionValidationException("Transaction type cannot be null");
        }
    }

    /**
     * Validates that the provided transaction contains at least one non-null account.
     *
     * <p>If both the source and destination accounts are null, this method throws a
     * TransactionValidationException.</p>
     *
     * @param transaction the transaction to validate
     * @throws TransactionValidationException if both accounts are null
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
     * Validates the account configuration of a transaction by delegating to the appropriate
     * account validation method based on the transaction type's category.
     *
     * <p>For a deposit, withdrawal, transfer, or fee transaction, this method calls
     * {@link #validateDepositAccounts(Transaction)}, {@link #validateWithdrawalAccounts(Transaction)},
     * {@link #validateTransferAccounts(Transaction)}, or {@link #validateFeeAccounts(Transaction)}
     * respectively. If the transaction category is unsupported, a TransactionValidationException
     * is thrown.
     *
     * @param transaction the transaction whose account configuration is validated
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
     * Validates that a transfer transaction has both a source and a destination account.
     * <p>
     * This method throws a TransactionValidationException if either the source account or the destination account is missing.
     *
     * @param transaction the transfer transaction to validate
     * @throws TransactionValidationException if the source account is null or the destination account is null
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
     * Validates that a fee transaction includes a source account.
     *
     * <p>This method checks that the source account is not null for fee transactions. If the source account is absent,
     * a TransactionValidationException is thrown.
     *
     * @param transaction the fee transaction to validate
     * @throws TransactionValidationException if the source account is null
     */
    private void validateFeeAccounts(Transaction transaction) {
        if (transaction.getSourceAccount() == null) {
            throw new TransactionValidationException("Fee transaction must have a source account");
        }
    }
}