package info.mackiewicz.bankapp.account.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.iban4j.Iban;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import info.mackiewicz.bankapp.account.exception.AccountOwnershipException;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.model.TestAccountBuilder;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.utils.TestIbanProvider;




@ExtendWith(MockitoExtension.class)
class AccountSecurityServiceTest {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountSecurityService accountSecurityService;

    private Account account;
    private User owner;
    private Iban iban;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setId(1);
        
        account = TestAccountBuilder.createTestAccountWithOwner(owner);
        
        iban = TestIbanProvider.getNextIbanObject();
    }

    @Test
    void validateAccountOwnership_WhenUserIsOwner_ShouldReturnAccount() {
        when(accountService.getAccountByIban(iban)).thenReturn(account);
        
        Account result = accountSecurityService.validateAccountOwnership(1, iban);
        
        assertNotNull(result);
        assertEquals(account, result);
    }

    @Test
    void validateAccountOwnership_WhenUserIsNotOwner_ShouldThrowException() {
        when(accountService.getAccountByIban(iban)).thenReturn(account);
        
        assertThrows(AccountOwnershipException.class, () -> 
            accountSecurityService.validateAccountOwnership(2, iban)
        );
    }
}