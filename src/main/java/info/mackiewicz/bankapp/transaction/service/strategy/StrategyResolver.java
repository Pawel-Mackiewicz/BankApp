package info.mackiewicz.bankapp.transaction.service.strategy;

import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionCategory;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

/**
 * Resolves the appropriate transaction strategy based on transaction type.
 */
@Component
@RequiredArgsConstructor
public class StrategyResolver {

    private final DepositTransaction depositTransaction;
    private final WithdrawalTransaction withdrawalTransaction;
    private final TransferTransaction transferTransaction;
    private final FeeTransaction feeTransaction;

    /**
     * Returns appropriate transaction strategy based on transaction type.
     */
    public TransactionStrategy resolveStrategy(Transaction transaction) {
        if (transaction.getType() == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }

        TransactionCategory category = transaction.getType().getCategory();
        
        return switch (category) {
            case DEPOSIT -> depositTransaction;
            case WITHDRAWAL -> withdrawalTransaction;
            case TRANSFER -> transferTransaction;
            case FEE -> feeTransaction;
            default -> throw new IllegalArgumentException("Unsupported transaction category: " + category);
        };
    }
}