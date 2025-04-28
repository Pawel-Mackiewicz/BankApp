package info.mackiewicz.bankapp.core.account.service;

import info.mackiewicz.bankapp.core.account.exception.*;
import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.model.TestAccountBuilder;
import info.mackiewicz.bankapp.transaction.exception.InsufficientFundsException;
import info.mackiewicz.bankapp.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AccountValidationServiceTest {

    private AccountValidationService validationService;
    private User user;

    @BeforeEach
    void setUp() {
        validationService = new AccountValidationService();
        user = new User();
        user.setId(1);
        user.setFirstname("Jan");
        user.setLastname("Kowalski");
        user.setAccounts(new HashSet<>());
    }

    @Test
    void validateNewAccountOwner_WithValidUser_ShouldNotThrowException() {
        // when & then
        assertDoesNotThrow(() -> validationService.validateNewAccountOwner(user));
    }

    @Test
    void validateNewAccountOwner_WithNullUser_ShouldThrowException() {
        // when & then
        Exception exception = assertThrows(AccountOwnerNullException.class, 
            () -> validationService.validateNewAccountOwner(null));
        assertEquals("Owner cannot be null", exception.getMessage());
    }

    @Test
    void validateNewAccountOwner_WithLockedUser_ShouldThrowException() {
        // given
        user.setLocked(true);

        // when & then
        Exception exception = assertThrows(AccountOwnerLockedException.class, 
            () -> validationService.validateNewAccountOwner(user));
        assertEquals("Owner of this account is locked", exception.getMessage());
    }

    @Test
    void validateNewAccountOwner_WithExpiredUser_ShouldThrowException() {
        // given
        user.setExpired(true);

        // when & then
        Exception exception = assertThrows(AccountOwnerExpiredException.class, 
            () -> validationService.validateNewAccountOwner(user));
        assertEquals("Owner of this account is expired", exception.getMessage());
    }

    @Test
    void validateNewAccountOwner_WithTooManyAccounts_ShouldThrowException() {
        // given
        Set<Account> accounts = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            Account account = TestAccountBuilder.createTestAccountWithOwner(user);
            accounts.add(account);
        }
        user.setAccounts(accounts);
        
        // when & then
        Exception exception = assertThrows(AccountLimitException.class, 
            () -> validationService.validateNewAccountOwner(user));
        assertEquals("Account limit exceeded. User can't have more than 3 accounts", exception.getMessage());
    }

    @Test
    void validateWithdrawal_WithValidAmounts_ShouldNotThrowException() {
        // given
        BigDecimal balance = new BigDecimal("1000");
        BigDecimal amount = new BigDecimal("500");

        // when & then
        assertDoesNotThrow(() -> validationService.validateWithdrawal(balance, amount));
    }

    @Test
    void validateWithdrawal_WithInsufficientFunds_ShouldThrowException() {
        // given
        BigDecimal balance = new BigDecimal("100");
        BigDecimal amount = new BigDecimal("500");

        // when & then
        Exception exception = assertThrows(InsufficientFundsException.class, 
            () -> validationService.validateWithdrawal(balance, amount));
        assertEquals("Insufficient funds for withdrawal", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"-100", "0", "-0.01"})
    void validateWithdrawal_WithInvalidAmount_ShouldThrowException(String amount) {
        // given
        BigDecimal balance = new BigDecimal("1000");
        BigDecimal withdrawalAmount = new BigDecimal(amount);

        // when & then
        Exception exception = assertThrows(AccountValidationException.class, 
            () -> validationService.validateWithdrawal(balance, withdrawalAmount));
        assertEquals("Amount must be positive", exception.getMessage());
    }

    @Test
    void validateWithdrawal_WithNullAmount_ShouldThrowException() {
        // given
        BigDecimal balance = new BigDecimal("1000");

        // when & then
        Exception exception = assertThrows(AccountValidationException.class, 
            () -> validationService.validateWithdrawal(balance, null));
        assertEquals("Amount must be positive", exception.getMessage());
    }

    @Test
    void validateDeposit_WithValidAmount_ShouldNotThrowException() {
        // given
        BigDecimal amount = new BigDecimal("100");

        // when & then
        assertDoesNotThrow(() -> validationService.validateDeposit(amount));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-100", "0", "-0.01"})
    void validateDeposit_WithInvalidAmount_ShouldThrowException(String amount) {
        // given
        BigDecimal depositAmount = new BigDecimal(amount);

        // when & then
        Exception exception = assertThrows(AccountValidationException.class, 
            () -> validationService.validateDeposit(depositAmount));
        assertEquals("Amount must be positive", exception.getMessage());
    }

    @Test
    void validateDeposit_WithNullAmount_ShouldThrowException() {
        // when & then
        Exception exception = assertThrows(AccountValidationException.class, 
            () -> validationService.validateDeposit(null));
        assertEquals("Amount must be positive", exception.getMessage());
    }
}