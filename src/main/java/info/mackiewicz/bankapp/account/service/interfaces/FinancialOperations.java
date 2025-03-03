package info.mackiewicz.bankapp.account.service.interfaces;

import java.math.BigDecimal;

/**
 * Interface defining financial operations that can be performed on an account.
 * This includes basic operations like deposits and withdrawals.
 */
public interface FinancialOperations {
    /**
     * Deposits the specified amount into the account.
     *
     * @param amount The amount to deposit
     */
    void deposit(BigDecimal amount);

    /**
     * Withdraws the specified amount from the account.
     *
     * @param amount The amount to withdraw
     */
    void withdraw(BigDecimal amount);

    /**
     * Checks if the specified amount can be withdrawn from the account.
     *
     * @param amount The amount to check
     * @return true if the withdrawal is possible, false otherwise
     */
    boolean canWithdraw(BigDecimal amount);

    /**
     * Gets the current balance of the account.
     *
     * @return The current balance
     */
    BigDecimal getBalance();
}