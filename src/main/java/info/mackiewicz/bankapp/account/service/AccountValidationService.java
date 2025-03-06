package info.mackiewicz.bankapp.account.service;

import info.mackiewicz.bankapp.user.model.User;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

/**
 * Service responsible for account-related validations
 */
@Slf4j
@Service
class AccountValidationService {
    /**
     * Validates if user can own a new account
     * 
     * @param owner the potential account owner
     * @throws IllegalArgumentException if validation fails
     */
    void validateNewAccountOwner(User owner) {
        if (owner == null) {
            log.debug("Validation fail. User is null");
            throw new IllegalArgumentException("Owner cannot be null");
        }
        log.debug("Validating account owner: {}", owner.getId());
        if (owner.getAccounts().size() >= 3) {
            log.debug("Validation fail. Account limit exceeded for owner: {}", owner.getId());
            throw new IllegalArgumentException("User account limit: Please contact us if You need more accounts");
        }
        if (owner.isLocked()) {
            log.debug("Validation fail. User {} is locked", owner.getId());
            throw new IllegalArgumentException("User is locked");
        }
        if (owner.isExpired()) {
            log.debug("Validation fail. User {} is expired", owner.getId());
            throw new IllegalArgumentException("User is expired");
        }

        log.debug("Account owner validated: {}", owner.getId());
    }

    /**
     * Validates if withdrawal can be performed
     * 
     * @param balance current balance
     * @param amount amount to withdraw
     * @throws IllegalArgumentException if validation fails
     */
    void validateWithdrawal(BigDecimal balance, BigDecimal amount) {
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
    void validateDeposit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}