package info.mackiewicz.bankapp.account.service;

import info.mackiewicz.bankapp.user.model.User;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

/**
 * Service responsible for account-related validations
 */
@Service
public class AccountValidationService {
    /**
     * Validates if user can own a new account
     * 
     * @param owner the potential account owner
     * @throws IllegalArgumentException if validation fails
     */
    public void validateNewAccountOwner(User owner) {
        if (owner == null) {
            throw new IllegalArgumentException("Owner cannot be null");
        }
        if (owner.getAccounts().size() >= 3) {
            throw new IllegalArgumentException("User cannot have more than 3 accounts");
        }
        if (owner.isLocked()) {
            throw new IllegalArgumentException("User is locked");
        }
        if(owner.isExpired()) {
            throw new IllegalArgumentException("User is expired");
        }
    }

    /**
     * Validates if withdrawal can be performed
     * 
     * @param balance current balance
     * @param amount amount to withdraw
     * @throws IllegalArgumentException if validation fails
     */
    public void validateWithdrawal(BigDecimal balance, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (balance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds for withdrawal");
        }
    }

    /**
     * Validates deposit amount
     * 
     * @param amount amount to deposit
     * @throws IllegalArgumentException if validation fails
     */
    public void validateDeposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}