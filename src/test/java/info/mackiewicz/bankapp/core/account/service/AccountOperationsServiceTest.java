package info.mackiewicz.bankapp.core.account.service;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.model.TestAccountBuilder;
import info.mackiewicz.bankapp.core.account.repository.AccountRepository;
import info.mackiewicz.bankapp.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountOperationsServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountValidationService validationService;

    @InjectMocks
    private AccountOperationsService operationsService;

    private Account account;
    private User owner;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1);
        owner.setFirstname("Jan");
        owner.setLastname("Kowalski");
        
        account = TestAccountBuilder.createTestAccountWithOwner(owner);
        TestAccountBuilder.setField(account, "balance", new BigDecimal("1000.00"));
    }

    @Test
    void deposit_WithValidAmount_ShouldIncreaseBalance() {
        // given
        BigDecimal depositAmount = new BigDecimal("500.00");
        BigDecimal expectedBalance = new BigDecimal("1500.00");
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(validationService).validateDeposit(depositAmount);

        // when
        Account updatedAccount = operationsService.deposit(account, depositAmount);

        // then
        assertEquals(expectedBalance, updatedAccount.getBalance());
        verify(validationService).validateDeposit(depositAmount);
        verify(accountRepository).save(account);
    }

    @Test
    void withdraw_WithValidAmount_ShouldDecreaseBalance() {
        // given
        BigDecimal initialBalance = account.getBalance(); // 1000.00
        BigDecimal withdrawalAmount = new BigDecimal("500.00");
        BigDecimal expectedBalance = new BigDecimal("500.00");
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(validationService).validateWithdrawal(initialBalance, withdrawalAmount);

        // when
        Account updatedAccount = operationsService.withdraw(account, withdrawalAmount);

        // then
        assertEquals(expectedBalance, updatedAccount.getBalance());
        verify(validationService).validateWithdrawal(initialBalance, withdrawalAmount);
        verify(accountRepository).save(account);
    }

    @Test
    void deposit_ShouldSaveAccountWithCorrectBalance() {
        // given
        BigDecimal depositAmount = new BigDecimal("500.00");
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        when(accountRepository.save(accountCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(validationService).validateDeposit(depositAmount);

        // when
        operationsService.deposit(account, depositAmount);

        // then
        Account savedAccount = accountCaptor.getValue();
        assertEquals(new BigDecimal("1500.00"), savedAccount.getBalance());
    }

    @Test
    void withdraw_ShouldSaveAccountWithCorrectBalance() {
        // given
        BigDecimal withdrawalAmount = new BigDecimal("500.00");
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        when(accountRepository.save(accountCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(validationService).validateWithdrawal(account.getBalance(), withdrawalAmount);

        // when
        operationsService.withdraw(account, withdrawalAmount);

        // then
        Account savedAccount = accountCaptor.getValue();
        assertEquals(new BigDecimal("500.00"), savedAccount.getBalance());
    }

    @Test
    void deposit_WhenValidationFails_ShouldThrowException() {
        // given
        BigDecimal depositAmount = new BigDecimal("-100.00");
        doThrow(new IllegalArgumentException("Invalid amount")).when(validationService).validateDeposit(depositAmount);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> operationsService.deposit(account, depositAmount));
        verify(accountRepository, never()).save(any());
    }

    @Test
    void withdraw_WhenValidationFails_ShouldThrowException() {
        // given
        BigDecimal withdrawalAmount = new BigDecimal("2000.00");
        doThrow(new IllegalArgumentException("Insufficient funds")).when(validationService)
            .validateWithdrawal(account.getBalance(), withdrawalAmount);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> operationsService.withdraw(account, withdrawalAmount));
        verify(accountRepository, never()).save(any());
    }
}