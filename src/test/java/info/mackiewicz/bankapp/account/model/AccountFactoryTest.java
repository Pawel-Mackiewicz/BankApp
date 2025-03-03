package info.mackiewicz.bankapp.account.model;

import info.mackiewicz.bankapp.account.service.AccountValidationService;
import info.mackiewicz.bankapp.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.iban4j.Iban;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountFactoryTest {

    @Mock
    private AccountValidationService validationService;

    @InjectMocks
    private AccountFactory accountFactory;

    private User owner;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1);
        owner.setFirstname("Jan");
        owner.setLastname("Kowalski");
    }

    @Test
    void createAccount_ShouldCreateValidAccount() {
        // given
        int accountNumber = 12345;
        when(owner.getNextAccountNumber()).thenReturn(accountNumber);
        doNothing().when(validationService).validateNewAccountOwner(owner);

        // when
        Account account = accountFactory.createAccount(owner);

        // then
        assertNotNull(account);
        assertEquals(owner, account.getOwner());
        assertEquals(accountNumber, account.getUserAccountNumber());
        assertNotNull(account.getIban());
        assertTrue(account.getIban().toString().startsWith("PL")); // Polish IBAN format
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