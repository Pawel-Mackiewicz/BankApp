package info.mackiewicz.bankapp.account.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import info.mackiewicz.bankapp.account.model.dto.AccountOwnerDTO;
import info.mackiewicz.bankapp.account.service.AccountValidationService;
import info.mackiewicz.bankapp.user.model.User;

@ExtendWith(MockitoExtension.class)
class AccountFactoryTest {

    @Mock
    private AccountValidationService validationService;

    @Mock
    private User owner;

    @InjectMocks
    private AccountFactory accountFactory;

    @Test
    void createAccount_ShouldCreateValidAccount() {
        // given
        when(owner.getId()).thenReturn(1);
        when(owner.getFullName()).thenReturn("Jan Kowalski");
        int accountNumber = 12;
        when(owner.getNextAccountNumber()).thenReturn(accountNumber);
        doNothing().when(validationService).validateNewAccountOwner(owner);

        // when
        Account account = accountFactory.createAccount(owner);

        // then
        assertNotNull(account);
        AccountOwnerDTO ownerDto = account.getOwner();
        assertEquals(1, ownerDto.getId());
        assertEquals("Jan Kowalski", ownerDto.getFullName());
        assertEquals(accountNumber, account.getUserAccountNumber());
        assertNotNull(account.getIban().toString());
        String iban = account.getIban().toString();
        assertTrue(iban.startsWith("PL")); // Country code
        assertEquals(28, iban.length()); // Standard IBAN length for Poland
        assertTrue(iban.substring(4, 7).equals("485")); // Bank code
        assertTrue(iban.substring(7, 11).equals("1123")); // Branch code
        verify(validationService).validateNewAccountOwner(owner);
        verify(owner).getNextAccountNumber();
    }

    @Test
    void createAccount_WithNullOwner_ShouldThrowException() {
        // when & then
        assertThrows(NullPointerException.class, () -> 
            accountFactory.createAccount(null)
        );
        verify(validationService, never()).validateNewAccountOwner(any());
    }

    @Test
    void createAccount_WhenValidationFails_ShouldThrowException() {
        // given
        doThrow(new IllegalArgumentException("Validation failed"))
            .when(validationService).validateNewAccountOwner(owner);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> 
            accountFactory.createAccount(owner)
        );
        verify(validationService).validateNewAccountOwner(owner);
        verify(owner, never()).getNextAccountNumber();
    }

    @Test
    void createAccount_ShouldCreateUniqueAccounts() {
        // given
        when(owner.getNextAccountNumber())
            .thenReturn(1)
            .thenReturn(2);
        doNothing().when(validationService).validateNewAccountOwner(owner);

        // when
        Account account1 = accountFactory.createAccount(owner);
        Account account2 = accountFactory.createAccount(owner);

        // then
        assertNotEquals(account1.getIban(), account2.getIban());
        assertNotEquals(account1.getUserAccountNumber(), account2.getUserAccountNumber());
        verify(validationService, times(2)).validateNewAccountOwner(owner);
        verify(owner, times(2)).getNextAccountNumber();
    }
}