package info.mackiewicz.bankapp.core.transaction.validation;

import info.mackiewicz.bankapp.core.transaction.exception.InsufficientFundsException;
import info.mackiewicz.bankapp.core.transaction.exception.TransactionAccountConflictException;
import info.mackiewicz.bankapp.core.transaction.exception.TransactionValidationException;
import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import info.mackiewicz.bankapp.core.transaction.model.TransactionType;
import info.mackiewicz.bankapp.core.transaction.model.TransactionTypeCategory;
import info.mackiewicz.bankapp.core.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class DefaultTransactionValidator implements TransactionValidator {

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

    private void validateNotNull(Transaction transaction) {
        if (transaction == null) {
            throw new TransactionValidationException("Transaction cannot be null");
        }
    }

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

    private void validateAmount(Transaction transaction) {
        if (transaction.getAmount() == null) {
            throw new TransactionValidationException("Transaction amount cannot be null");
        }
        if (transaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionValidationException("Transaction amount must be positive");
        }
    }

    private void validateType(Transaction transaction) {
        if (transaction.getType() == null) {
            throw new TransactionValidationException("Transaction type cannot be null");
        }
    }

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

    private void validateTransferAccounts(Transaction transaction) {
        if (transaction.getSourceAccount() == null) {
            throw new TransactionValidationException("Transfer transaction must have a source account");
        }
        if (transaction.getDestinationAccount() == null) {
            throw new TransactionValidationException("Transfer transaction must have a destination account");
        }
    }

    private void validateFeeAccounts(Transaction transaction) {
        if (transaction.getSourceAccount() == null) {
            throw new TransactionValidationException("Fee transaction must have a source account");
        }
    }
}