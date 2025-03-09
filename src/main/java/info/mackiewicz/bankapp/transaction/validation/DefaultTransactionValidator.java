package info.mackiewicz.bankapp.transaction.validation;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.transaction.model.TransactionTypeCategory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DefaultTransactionValidator implements TransactionValidator {

    @Override
    public void validate(Transaction transaction) {
        log.debug("Validating transaction: {}", transaction);
        validateNotNull(transaction);
        validateAmount(transaction);
        validateType(transaction);
        validateBothAccountsNotNull(transaction);
        validateAccounts(transaction);
    }

    @Override
    public boolean isValid(Transaction transaction) {
        try {
            validate(transaction);
            return true;
        } catch (TransactionValidationException e) {
            log.debug("Transaction validation failed: {}", e.getMessage());
            return false;
        }
    }

    private void validateNotNull(Transaction transaction) {
        if (transaction == null) {
            throw new TransactionValidationException("Transaction cannot be null");
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

    private void validateBothAccountsNotNull(Transaction transaction) {
        if (transaction.getSourceAccount() == null && transaction.getDestinationAccount() == null) {
            throw new TransactionValidationException("Both accounts cannot be null");
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
        if (transaction.getSourceAccount().equals(transaction.getDestinationAccount()) 
            && transaction.getType() != TransactionType.TRANSFER_OWN) {
            throw new TransactionValidationException("Only TRANSFER_OWN can have the same source and destination account");
        }
    }

    private void validateFeeAccounts(Transaction transaction) {
        if (transaction.getSourceAccount() == null) {
            throw new TransactionValidationException("Fee transaction must have a source account");
        }
    }
}