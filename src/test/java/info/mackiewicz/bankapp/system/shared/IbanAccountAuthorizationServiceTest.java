package info.mackiewicz.bankapp.system.shared;

import info.mackiewicz.bankapp.account.exception.AccountOwnershipException;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.testutils.TestIbanProvider;
import info.mackiewicz.bankapp.user.exception.InvalidUserDataException;
import info.mackiewicz.bankapp.user.model.User;
import org.iban4j.Iban;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class IbanAccountAuthorizationServiceTest {

    private final IbanAccountAuthorizationService ibanAccountAuthorizationService = new IbanAccountAuthorizationService();

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
    }

    @Test
    void shouldThrowExceptionWhenOwnerHasAccountsButNotTheRequestedOne() {
        // Given
        Iban requestedIban = TestIbanProvider.getNextIbanObject();
        Iban otherIban = TestIbanProvider.getNextIbanObject();
        User mockUser = mock(User.class);
        Set<Account> accounts = new HashSet<>();
        Account mockOtherAccount = mock(Account.class);
        when(mockOtherAccount.getIban()).thenReturn(otherIban);
        accounts.add(mockOtherAccount);
        when(mockUser.getAccounts()).thenReturn(accounts);

        // When & Then
        assertThrows(AccountOwnershipException.class, () ->
                ibanAccountAuthorizationService.validateAccountOwnership(requestedIban, mockUser));

        verify(mockUser).getAccounts();
        verify(mockOtherAccount).getIban();
    }
}
