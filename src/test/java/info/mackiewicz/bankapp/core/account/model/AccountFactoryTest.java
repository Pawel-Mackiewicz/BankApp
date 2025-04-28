package info.mackiewicz.bankapp.core.account.model;

import info.mackiewicz.bankapp.core.account.model.dto.AccountOwnerDTO;
import info.mackiewicz.bankapp.core.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountFactoryTest {

    @Mock
    private User owner;

    private AccountFactory accountFactory;

    @BeforeEach
    void setUp() {
        accountFactory = new AccountFactory();
    }

    @Test
    void createAccount_ShouldCreateValidAccount() {
        // given
        when(owner.getId()).thenReturn(1);
        when(owner.getFullName()).thenReturn("Jan Kowalski");
        int accountNumber = 12;
        when(owner.getNextAccountNumber()).thenReturn(accountNumber);

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
    }

    @Test
    void createAccount_WithNullOwner_ShouldThrowException() {
        // when & then
        assertThrows(NullPointerException.class, () ->
            accountFactory.createAccount(null)
        );
    }

    @Test
    void createAccount_ShouldCreateUniqueAccounts() {
        // given
        when(owner.getNextAccountNumber())
            .thenReturn(1)
            .thenReturn(2);

        // when
        Account account1 = accountFactory.createAccount(owner);
        Account account2 = accountFactory.createAccount(owner);

        // then
        assertNotEquals(account1.getIban(), account2.getIban());
        assertNotEquals(account1.getUserAccountNumber(), account2.getUserAccountNumber());
    }
}