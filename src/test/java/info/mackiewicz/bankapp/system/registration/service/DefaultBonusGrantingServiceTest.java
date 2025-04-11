package info.mackiewicz.bankapp.system.registration.service;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.shared.util.BankAccountProvider;
import info.mackiewicz.bankapp.testutils.TestAccountBuilder;
import info.mackiewicz.bankapp.testutils.TestUserBuilder;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import info.mackiewicz.bankapp.utils.TestIbanProvider;
import org.iban4j.Iban;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultBonusGrantingServiceTest {

    private static final BigDecimal DEFAULT_BONUS_AMOUNT = BigDecimal.valueOf(50);
    private static final int TEST_ACCOUNT_ID_1 = 1;
    private static final int TEST_ACCOUNT_ID_2 = 2;
    private static final int REGISTERED_TRANSACTION_ID = 100;
    private static final String RECIPIENT_NOT_FOUND_MESSAGE = "Recipient account not found";
    private static final String TRANSACTION_REGISTRATION_FAILED_MESSAGE = "Transaction registration failed";
    private static final BigDecimal ZERO_BALANCE = BigDecimal.ZERO;

    @Mock
    private BankAccountProvider bankAccountProvider;

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private DefaultBonusGrantingService defaultBonusGrantingService;

    @Test
    @DisplayName("Should grant welcome bonus when valid details provided")
    void grantWelcomeBonus_WhenValidDetails_ThenSucceeds() {
        // Arrange
        Iban recipientIban = TestIbanProvider.getNextIbanObject();
        BigDecimal amount = DEFAULT_BONUS_AMOUNT;

        Account bankAccount = TestAccountBuilder.createBankAccount();
        Account recipientAccount = TestAccountBuilder.createTestAccount(TEST_ACCOUNT_ID_1, ZERO_BALANCE, TestUserBuilder.createTestUser());

        Transaction createdTransaction = Transaction.buildTransfer()
                .from(bankAccount)
                .to(recipientAccount)
                .asInternalTransfer()
                .withAmount(amount)
                .withTitle(DefaultBonusGrantingService.DEFAULT_TITLE)
                .build();

        Transaction registeredTransaction = Transaction.buildTransfer()
                .from(bankAccount)
                .to(recipientAccount)
                .asInternalTransfer()
                .withAmount(amount)
                .withTitle(DefaultBonusGrantingService.DEFAULT_TITLE)
                .build();
        registeredTransaction.setId(REGISTERED_TRANSACTION_ID);

        when(bankAccountProvider.getBankAccount()).thenReturn(bankAccount);
        when(accountService.getAccountByIban(eq(recipientIban))).thenReturn(recipientAccount);
        when(transactionService.registerTransaction(any(Transaction.class))).thenReturn(registeredTransaction);

        // Act
        defaultBonusGrantingService.grantWelcomeBonus(recipientIban, amount);

        // Assert
        verify(bankAccountProvider).getBankAccount();
        verify(accountService).getAccountByIban(eq(recipientIban));
        verify(transactionService).registerTransaction(eq(createdTransaction));
        verify(transactionService).processTransactionById(eq(REGISTERED_TRANSACTION_ID));
    }

    @Test
    @DisplayName("Should throw exception when recipient account is not found")
    void grantWelcomeBonus_WhenRecipientAccountNotFound_ThenThrowsException() {
        // Arrange
        Iban recipientIban = TestIbanProvider.getNextIbanObject();
        BigDecimal amount = DEFAULT_BONUS_AMOUNT;

        Account bankAccount = TestAccountBuilder.createBankAccount();

        when(bankAccountProvider.getBankAccount()).thenReturn(bankAccount);
        when(accountService.getAccountByIban(eq(recipientIban)))
                .thenThrow(new IllegalArgumentException(RECIPIENT_NOT_FOUND_MESSAGE));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
                defaultBonusGrantingService.grantWelcomeBonus(recipientIban, amount));

        verify(bankAccountProvider).getBankAccount();
        verify(accountService).getAccountByIban(eq(recipientIban));
        verifyNoInteractions(transactionService);
    }

    @Test
    @DisplayName("Should throw exception when transaction registration fails")
    void grantWelcomeBonus_WhenTransactionRegistrationFails_ThenThrowsException() {
        // Arrange
        Iban recipientIban = TestIbanProvider.getNextIbanObject();
        BigDecimal amount = DEFAULT_BONUS_AMOUNT;

        Account bankAccount = TestAccountBuilder.createBankAccount();
        Account recipientAccount = TestAccountBuilder.createTestAccount(TEST_ACCOUNT_ID_2, ZERO_BALANCE, TestUserBuilder.createTestUser());

        Transaction createdTransaction = Transaction.buildTransfer()
                .from(bankAccount)
                .to(recipientAccount)
                .asInternalTransfer()
                .withAmount(amount)
                .withTitle(DefaultBonusGrantingService.DEFAULT_TITLE)
                .build();

        when(bankAccountProvider.getBankAccount()).thenReturn(bankAccount);
        when(accountService.getAccountByIban(eq(recipientIban))).thenReturn(recipientAccount);
        when(transactionService.registerTransaction(eq(createdTransaction)))
                .thenThrow(new IllegalStateException(TRANSACTION_REGISTRATION_FAILED_MESSAGE));

        // Act & Assert
        assertThrows(IllegalStateException.class, () ->
                defaultBonusGrantingService.grantWelcomeBonus(recipientIban, amount));

        verify(bankAccountProvider).getBankAccount();
        verify(accountService).getAccountByIban(eq(recipientIban));
        verify(transactionService).registerTransaction(eq(createdTransaction));
        verifyNoMoreInteractions(transactionService);
    }
}