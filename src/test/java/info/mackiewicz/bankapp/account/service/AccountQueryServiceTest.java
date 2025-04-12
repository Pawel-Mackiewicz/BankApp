package info.mackiewicz.bankapp.account.service;

import info.mackiewicz.bankapp.account.exception.AccountNotFoundByIbanException;
import info.mackiewicz.bankapp.account.exception.AccountNotFoundByIdException;
import info.mackiewicz.bankapp.account.exception.OwnerAccountsNotFoundException;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.model.TestAccountBuilder;
import info.mackiewicz.bankapp.account.repository.AccountRepository;
import info.mackiewicz.bankapp.testutils.TestIbanProvider;
import info.mackiewicz.bankapp.testutils.TestUserBuilder;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.user.model.vo.Pesel;
import org.iban4j.Iban;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountQueryServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountQueryService accountQueryService;

    private Account testAccount;
    private User owner;

    @BeforeEach
    void setUp() {
        // Użycie TestUserBuilder do stworzenia użytkownika testowego
        owner = TestUserBuilder.createTestUser();
        owner.setId(1);
        owner.setFirstname("Jan");
        owner.setLastname("Kowalski");
        owner.setUsername("jkowalski");

        testAccount = TestAccountBuilder.createTestAccountWithOwner(owner);
        Iban testIban = TestIbanProvider.getIbanObject(0);
        TestAccountBuilder.setField(testAccount, "iban", testIban);
        TestAccountBuilder.setField(testAccount, "id", 1);
    }

    @Test
    void getAccountById_WhenAccountExists_ShouldReturnAccount() {
        // given
        when(accountRepository.findById(1)).thenReturn(Optional.of(testAccount));

        // when
        Account result = accountQueryService.getAccountById(1);

        // then
        assertNotNull(result);
        assertEquals(testAccount.getId(), result.getId());
        verify(accountRepository).findById(1);
    }

    @Test
    void getAccountById_WhenAccountDoesNotExist_ShouldThrowException() {
        // given
        when(accountRepository.findById(999)).thenReturn(Optional.empty());

        // when & then
        assertThrows(AccountNotFoundByIdException.class, 
            () -> accountQueryService.getAccountById(999));
        verify(accountRepository).findById(999);
    }

    @Test
    void getAllAccounts_ShouldReturnAllAccounts() {
        // given
        List<Account> accounts = Collections.singletonList(testAccount);
        when(accountRepository.findAll()).thenReturn(accounts);

        // when
        List<Account> result = accountQueryService.getAllAccounts();

        // then
        assertEquals(1, result.size());
        assertEquals(testAccount, result.get(0));
        verify(accountRepository).findAll();
    }

    @Test
    void getAccountsByOwnersPESEL_WhenAccountsExist_ShouldReturnAccounts() {
        // given
        List<Account> accounts = Collections.singletonList(testAccount);
        when(accountRepository.findAccountsByOwner_pesel(new Pesel("12345678901")))
            .thenReturn(Optional.of(accounts));

        // when
        List<Account> result = accountQueryService.getAccountsByOwnersPesel("12345678901");

        // then
        assertEquals(1, result.size());
        assertEquals(testAccount, result.get(0));
        verify(accountRepository).findAccountsByOwner_pesel(new Pesel("12345678901"));
    }

    @Test
    void getAccountsByOwnersPESEL_WhenNoAccountsExist_ShouldThrowException() {
        // given
        when(accountRepository.findAccountsByOwner_pesel(new Pesel("99999999999")))
            .thenReturn(Optional.empty());

        // when & then
        assertThrows(OwnerAccountsNotFoundException.class,
            () -> accountQueryService.getAccountsByOwnersPesel("99999999999"));
        verify(accountRepository).findAccountsByOwner_pesel(new Pesel("99999999999"));
    }

    @Test
    void getAccountsByOwnersUsername_WhenAccountsExist_ShouldReturnAccounts() {
        // given
        List<Account> accounts = Collections.singletonList(testAccount);
        when(accountRepository.findAccountsByOwner_username("jkowalski"))
            .thenReturn(Optional.of(accounts));

        // when
        List<Account> result = accountQueryService.getAccountsByOwnersUsername("jkowalski");

        // then
        assertEquals(1, result.size());
        assertEquals(testAccount, result.get(0));
        verify(accountRepository).findAccountsByOwner_username("jkowalski");
    }

    @Test
    void findAccountByOwnersEmail_WhenAccountExists_ShouldReturnAccount() {
        // given
        String emailStr = "jan.kowalski@example.com";
        EmailAddress email = new EmailAddress(emailStr);
        when(accountRepository.findFirstByOwner_email(email))
            .thenReturn(Optional.of(testAccount));

        // when
        Account result = accountQueryService.findAccountByOwnersEmail(emailStr);

        // then
        assertNotNull(result);
        assertEquals(testAccount, result);
        verify(accountRepository).findFirstByOwner_email(email);
    }

    @Test
    void getAccountByIban_WhenAccountExists_ShouldReturnAccount() {
        // given
        Iban testIban = TestIbanProvider.getIbanObject(0);
        when(accountRepository.findByIban(testIban)).thenReturn(Optional.of(testAccount));

        // when
        Account result = accountQueryService.getAccountByIban(testIban.toString());

        // then
        assertNotNull(result);
        assertEquals(testAccount, result);
        verify(accountRepository).findByIban(testIban);
    }

    @Test
    void getAccountByIban_WhenAccountDoesNotExist_ShouldThrowException() {
        // given
        Iban testIban = TestIbanProvider.getIbanObject(1);
        when(accountRepository.findByIban(testIban)).thenReturn(Optional.empty());

        // when & then
        assertThrows(AccountNotFoundByIbanException.class,
            () -> accountQueryService.getAccountByIban(testIban.toString()));
        verify(accountRepository).findByIban(testIban);
    }

    @Test
    void getAccountsByOwnersId_WhenAccountsExist_ShouldReturnAccounts() {
        // given
        List<Account> accounts = Collections.singletonList(testAccount);
        when(accountRepository.findAccountsByOwner_id(1))
            .thenReturn(Optional.of(accounts));

        // when
        List<Account> result = accountQueryService.getAccountsByOwnersId(1);

        // then
        assertEquals(1, result.size());
        assertEquals(testAccount, result.get(0));
        verify(accountRepository).findAccountsByOwner_id(1);
    }
}