package info.mackiewicz.bankapp.system.shared;

import info.mackiewicz.bankapp.account.exception.AccountOwnershipException;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.user.exception.InvalidUserDataException;
import info.mackiewicz.bankapp.user.model.User;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AccountAuthorizationServiceTest {

    private final AccountAuthorizationService accountAuthorizationService = new AccountAuthorizationService();

    @Test
    void shouldValidateAccountOwnershipWhenOwnerHasAccount() {
        // Given
        int accountId = 1;
        User mockUser = mock(User.class);
        Set<Account> accounts = new HashSet<>();
        Account mockAccount = mock(Account.class);
        when(mockAccount.getId()).thenReturn(accountId);
        accounts.add(mockAccount);
        when(mockUser.getAccounts()).thenReturn(accounts);

        // When & Then
        assertDoesNotThrow(() -> accountAuthorizationService.validateAccountOwnership(accountId, mockUser));
        verify(mockUser).getAccounts();
        verify(mockAccount).getId();
    }

    @Test
    void shouldThrowExceptionWhenAccountIdIsInvalid() {
        // Given
        int invalidAccountId = 0;
        User mockUser = mock(User.class);

        // When & Then
        AccountOwnershipException exception = assertThrows(AccountOwnershipException.class, () ->
                accountAuthorizationService.validateAccountOwnership(invalidAccountId, mockUser));

        assertTrue(exception.getMessage().contains("tried to access account"));
    }

    @Test
    void shouldThrowExceptionWhenOwnerDoesNotHaveAccount() {
        // Given
        int accountId = 1;
        User mockUser = mock(User.class);
        when(mockUser.getAccounts()).thenReturn(new HashSet<>());

        // When & Then
        assertThrows(InvalidUserDataException.class, () ->
                accountAuthorizationService.validateAccountOwnership(accountId, mockUser));

        verify(mockUser).getAccounts();
    }

    @Test
    void shouldThrowExceptionWhenOwnerHasAccountsButNotTheRequestedOne() {
        // Given
        int accountId = 1;
        int otherAccountId = 2;
        User mockUser = mock(User.class);
        Set<Account> accounts = new HashSet<>();
        Account mockOtherAccount = mock(Account.class);
        when(mockOtherAccount.getId()).thenReturn(otherAccountId);
        accounts.add(mockOtherAccount);
        when(mockUser.getAccounts()).thenReturn(accounts);

        // When & Then
        AccountOwnershipException exception = assertThrows(AccountOwnershipException.class, () ->
                accountAuthorizationService.validateAccountOwnership(accountId, mockUser));

        assertTrue(exception.getMessage().contains("tried to access account"));
        verify(mockUser).getAccounts();
        verify(mockOtherAccount).getId();
    }
}