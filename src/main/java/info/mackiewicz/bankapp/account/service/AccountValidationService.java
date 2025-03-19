package info.mackiewicz.bankapp.account.service;

import info.mackiewicz.bankapp.account.exception.AccountLimitException;
import info.mackiewicz.bankapp.account.exception.AccountOwnerExpiredException;
import info.mackiewicz.bankapp.account.exception.AccountOwnerLockedException;
import info.mackiewicz.bankapp.account.exception.AccountOwnerNullException;
import info.mackiewicz.bankapp.account.exception.AccountValidationException;
import info.mackiewicz.bankapp.transaction.exception.InsufficientFundsException;
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

    private static final int MAX_ACCOUNTS = 3;
    /**
     * Validates if user can own a new account
     * 
     * @param owner the potential account owner
     * @throws AccountValidationException if validation fails
     */
    void validateNewAccountOwner(User owner) {
        if (owner == null) {
            log.warn("Validation fail. User is null");
            throw new AccountOwnerNullException("Owner cannot be null");
        }
        log.debug("Validating account owner: {}", owner.getId());
        if (owner.getAccounts().size() >= MAX_ACCOUNTS) {
            log.warn("Validation fail. Account limit exceeded for owner: {}", owner.getId());
            throw new AccountLimitException("Account limit exceeded. User can't have more than " + MAX_ACCOUNTS + " accounts");
        }
        if (owner.isLocked()) {
            log.warn("Validation fail. User {} is locked", owner.getId());
            throw new AccountOwnerLockedException("Owner of this account is locked");
        }
        if (owner.isExpired()) {
            log.warn("Validation fail. User {} is expired", owner.getId());
            throw new AccountOwnerExpiredException("Owner of this account is expired");
        }

        log.debug("Account owner validated: {}", owner.getId());
    }

    /**
     * Validates if withdrawal can be performed
     * 
     * @param balance current balance
     * @param amount amount to withdraw
     * @throws AccountValidationException if validation fails
     */
    void validateWithdrawal(BigDecimal balance, BigDecimal amount) {
        log.debug("Validating withdrawal: balance={}, amount={}", balance, amount);
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Validation fail. Amount is null or not positive: {}", amount);
            throw new AccountValidationException("Amount must be positive");
        }
        if (balance.compareTo(amount) < 0) {
            log.warn("Validation fail. Insufficient funds: balance={}, amount={}", balance, amount);
            throw new InsufficientFundsException("Insufficient funds for withdrawal");
        }
        log.debug("Withdrawal validation successful");
    }

    /**
     * Validates deposit amount
     * 
     * @param amount amount to deposit
     * @throws AccountValidationException if validation fails
     */
    void validateDeposit(BigDecimal amount) {
        log.debug("Validating deposit amount: {}", amount);
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Validation fail. Amount is null or not positive: {}", amount);
            throw new AccountValidationException("Amount must be positive");
        }
        log.debug("Deposit validation successful");
    }
}