package info.mackiewicz.bankapp.system.shared;

import info.mackiewicz.bankapp.account.exception.AccountOwnershipException;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.user.exception.InvalidUserDataException;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IdAccountAuthorizationServiceTest {

    @Mock
    private UserService userService;

    private IdAccountAuthorizationService idAccountAuthorizationService;

    @BeforeEach
    void setUp() {
        idAccountAuthorizationService = new IdAccountAuthorizationService(userService);
    }

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
        assertDoesNotThrow(() -> idAccountAuthorizationService.validateAccountOwnership(accountId, mockUser));
        verify(mockUser).getAccounts();
        verify(mockAccount).getId();
        verify(userService, never()).getUserById(anyInt());
    }

    @Test
    void shouldThrowExceptionWhenOwnerDoesNotHaveAccount() {
        // Given
        int accountId = 1;
        User mockUser = mock(User.class);
        when(mockUser.getAccounts()).thenReturn(new HashSet<>());

        // When & Then
        assertThrows(InvalidUserDataException.class, () ->
                idAccountAuthorizationService.validateAccountOwnership(accountId, mockUser));

        verify(mockUser).getAccounts();
        verify(userService, never()).getUserById(anyInt());
    }

    @Test
    void shouldThrowExceptionWhenOwnerHasAccountsButNotTheRequestedOne() {
        // Given
        int accountId = 1;
        int otherAccountId = 2;
        int userId = 100;
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);

        Set<Account> accounts = new HashSet<>();
        Account mockOtherAccount = mock(Account.class);
        when(mockOtherAccount.getId()).thenReturn(otherAccountId);
        accounts.add(mockOtherAccount);
        when(mockUser.getAccounts()).thenReturn(accounts);

        // Dla drugiego wywołania - po aktualizacji użytkownika
        User updatedMockUser = mock(User.class);
        when(updatedMockUser.getId()).thenReturn(userId);
        Set<Account> updatedAccounts = new HashSet<>();
        updatedAccounts.add(mockOtherAccount); // Bez żądanego konta
        when(updatedMockUser.getAccounts()).thenReturn(updatedAccounts);

        when(userService.getUserById(userId)).thenReturn(updatedMockUser);

        // When & Then
        assertThrows(AccountOwnershipException.class, () ->
                idAccountAuthorizationService.validateAccountOwnership(accountId, mockUser));

        verify(mockUser).getAccounts();
        verify(mockOtherAccount, times(2)).getId();
        verify(userService).getUserById(userId);
        verify(updatedMockUser).getAccounts();
    }

    @Test
    void shouldValidateAccountOwnershipAfterUserUpdate() {
        // Given
        int accountId = 1;
        int userId = 100;
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);

        // The initial user does not have the required account.
        Set<Account> initialAccounts = new HashSet<>();
        Account mockOtherAccount = mock(Account.class);
        when(mockOtherAccount.getId()).thenReturn(2);
        initialAccounts.add(mockOtherAccount);
        when(mockUser.getAccounts()).thenReturn(initialAccounts);

        // The updated user has the required account.
        User updatedMockUser = mock(User.class);
        Set<Account> updatedAccounts = new HashSet<>();
        Account requestedAccount = mock(Account.class);
        when(requestedAccount.getId()).thenReturn(accountId);
        updatedAccounts.add(requestedAccount);
        when(updatedMockUser.getAccounts()).thenReturn(updatedAccounts);

        when(userService.getUserById(userId)).thenReturn(updatedMockUser);

        // When & Then
        assertDoesNotThrow(() ->
                idAccountAuthorizationService.validateAccountOwnership(accountId, mockUser));

        verify(mockUser).getAccounts();
        verify(mockOtherAccount).getId();
        verify(userService).getUserById(userId);
        verify(updatedMockUser).getAccounts();
        verify(requestedAccount).getId();
    }
}