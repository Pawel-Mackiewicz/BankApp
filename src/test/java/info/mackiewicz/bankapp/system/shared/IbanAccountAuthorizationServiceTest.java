package info.mackiewicz.bankapp.system.shared;

import info.mackiewicz.bankapp.core.account.exception.AccountOwnershipException;
import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.user.exception.InvalidUserDataException;
import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.core.user.service.UserService;
import info.mackiewicz.bankapp.testutils.TestIbanProvider;
import org.iban4j.Iban;
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
public class IbanAccountAuthorizationServiceTest {

    @Mock
    private UserService userService;

    private IbanAccountAuthorizationService ibanAccountAuthorizationService;

    @BeforeEach
    void setUp() {
        ibanAccountAuthorizationService = new IbanAccountAuthorizationService(userService);
    }

    @Test
    void shouldValidateAccountOwnershipWhenOwnerHasAccount() {
        // Given
        Iban iban = TestIbanProvider.getNextIbanObject();
        User mockUser = mock(User.class);
        Set<Account> accounts = new HashSet<>();
        Account mockAccount = mock(Account.class);
        when(mockAccount.getIban()).thenReturn(iban);
        accounts.add(mockAccount);
        when(mockUser.getAccounts()).thenReturn(accounts);

        // When & Then
        assertDoesNotThrow(() -> ibanAccountAuthorizationService.validateAccountOwnership(iban, mockUser));
        verify(mockUser).getAccounts();
        verify(mockAccount).getIban();
        verify(userService, never()).getUserById(anyInt());
    }

    @Test
    void shouldThrowExceptionWhenOwnerDoesNotHaveAccount() {
        // Given
        Iban iban = TestIbanProvider.getNextIbanObject();
        User mockUser = mock(User.class);
        when(mockUser.getAccounts()).thenReturn(new HashSet<>());

        // When & Then
        assertThrows(InvalidUserDataException.class, () ->
                ibanAccountAuthorizationService.validateAccountOwnership(iban, mockUser));

        verify(mockUser).getAccounts();
        verify(userService, never()).getUserById(anyInt());
    }

    @Test
    void shouldThrowExceptionWhenOwnerHasAccountsButNotTheRequestedOne() {
        // Given
        Iban requestedIban = TestIbanProvider.getNextIbanObject();
        Iban otherIban = TestIbanProvider.getNextIbanObject();
        int userId = 100;
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);

        Set<Account> accounts = new HashSet<>();
        Account mockOtherAccount = mock(Account.class);
        when(mockOtherAccount.getIban()).thenReturn(otherIban);
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
                ibanAccountAuthorizationService.validateAccountOwnership(requestedIban, mockUser));

        verify(mockUser).getAccounts();
        verify(mockOtherAccount, times(2)).getIban();
        verify(userService).getUserById(userId);
        verify(updatedMockUser).getAccounts();
    }

    @Test
    void shouldValidateAccountOwnershipAfterUserUpdate() {
        // Given
        Iban requestedIban = TestIbanProvider.getNextIbanObject();
        Iban otherIban = TestIbanProvider.getNextIbanObject();
        int userId = 100;
        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(userId);

        // Początkowy użytkownik nie ma wymaganego konta
        Set<Account> initialAccounts = new HashSet<>();
        Account mockOtherAccount = mock(Account.class);
        when(mockOtherAccount.getIban()).thenReturn(otherIban);
        initialAccounts.add(mockOtherAccount);
        when(mockUser.getAccounts()).thenReturn(initialAccounts);

        // Zaktualizowany użytkownik ma wymagane konto
        User updatedMockUser = mock(User.class);
        Set<Account> updatedAccounts = new HashSet<>();
        Account requestedAccount = mock(Account.class);
        when(requestedAccount.getIban()).thenReturn(requestedIban);
        updatedAccounts.add(requestedAccount);
        when(updatedMockUser.getAccounts()).thenReturn(updatedAccounts);

        when(userService.getUserById(userId)).thenReturn(updatedMockUser);

        // When & Then
        assertDoesNotThrow(() ->
                ibanAccountAuthorizationService.validateAccountOwnership(requestedIban, mockUser));

        verify(mockUser).getAccounts();
        verify(mockOtherAccount).getIban();
        verify(userService).getUserById(userId);
        verify(updatedMockUser).getAccounts();
        verify(requestedAccount).getIban();
    }

    @Test
    void shouldThrowExceptionWhenOwnerAccountsListIsNull() {
        // Given
        Iban iban = TestIbanProvider.getNextIbanObject();
        User mockUser = mock(User.class);
        when(mockUser.getAccounts()).thenReturn(null);

        // When & Then
        assertThrows(InvalidUserDataException.class, () ->
                ibanAccountAuthorizationService.validateAccountOwnership(iban, mockUser));

        verify(mockUser).getAccounts();
        verify(userService, never()).getUserById(anyInt());
    }
}
