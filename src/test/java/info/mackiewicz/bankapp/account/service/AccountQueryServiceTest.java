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

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountQueryServiceTest {

    private static final int TEST_OWNER_ID = 1;
    private static final int TEST_ACCOUNT_ID = 1;
    private static final int NONEXISTENT_ACCOUNT_ID = 999;
    private static final String TEST_FIRSTNAME = "Jan";
    private static final String TEST_LASTNAME = "Kowalski";
    private static final String TEST_USERNAME = "jkowalski";
    private static final String TEST_PESEL = "12345678901";
    private static final String NONEXISTENT_PESEL = "99999999999";
    private static final String TEST_EMAIL = "jan.kowalski@example.com";
    private static final int DEFAULT_IBAN_INDEX = 0;
    private static final int ALTERNATIVE_IBAN_INDEX = 1;
    private static final int EXPECTED_SINGLE_RESULT_SIZE = 1;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountQueryService accountQueryService;

    private User owner;
    private Account getTestAccount() {
        Account account = TestAccountBuilder.createTestAccountWithOwner(owner);
        Iban testIban = TestIbanProvider.getIbanObject(DEFAULT_IBAN_INDEX);
        TestAccountBuilder.setField(account, "iban", testIban);
        TestAccountBuilder.setField(account, "id", TEST_ACCOUNT_ID);
        return account;
    }

    @BeforeEach
    void setUp() {
        // Użycie TestUserBuilder do stworzenia użytkownika testowego
        owner = TestUserBuilder.createTestUser();
        owner.setId(TEST_OWNER_ID);
        owner.setFirstname(TEST_FIRSTNAME);
        owner.setLastname(TEST_LASTNAME);
        owner.setUsername(TEST_USERNAME);
    }

    @Test
    void getAccountById_WhenAccountExists_ShouldReturnAccount() {
        // given
        Account testAccount = getTestAccount();
        when(accountRepository.findById(TEST_ACCOUNT_ID)).thenReturn(Optional.of(testAccount));

        // when
        Account result = accountQueryService.getAccountById(TEST_ACCOUNT_ID);

        // then
        assertNotNull(result);
        assertEquals(testAccount.getId(), result.getId());
        verify(accountRepository).findById(TEST_ACCOUNT_ID);
    }

    @Test
    void getAccountById_WhenAccountDoesNotExist_ShouldThrowException() {
        // given
        when(accountRepository.findById(NONEXISTENT_ACCOUNT_ID)).thenReturn(Optional.empty());

        // when & then
        assertThrows(AccountNotFoundByIdException.class, 
            () -> accountQueryService.getAccountById(NONEXISTENT_ACCOUNT_ID));
        verify(accountRepository).findById(NONEXISTENT_ACCOUNT_ID);
    }

    @Test
    void getAllAccounts_ShouldReturnAllAccounts() {
        // given
        Account testAccount = getTestAccount();
        List<Account> accounts = Collections.singletonList(testAccount);
        when(accountRepository.findAll()).thenReturn(accounts);

        // when
        List<Account> result = accountQueryService.getAllAccounts();

        // then
        assertEquals(EXPECTED_SINGLE_RESULT_SIZE, result.size());
        assertEquals(testAccount, result.getFirst());
        verify(accountRepository).findAll();
    }

    @Test
    void getAccountsByOwnersPESEL_WhenAccountsExist_ShouldReturnAccounts() {
        // given
        Account testAccount = getTestAccount();
        List<Account> accounts = Collections.singletonList(testAccount);
        when(accountRepository.findAccountsByOwner_pesel(new Pesel(TEST_PESEL)))
            .thenReturn(Optional.of(accounts));

        // when
        List<Account> result = accountQueryService.getAccountsByOwnersPesel(TEST_PESEL);

        // then
        assertEquals(EXPECTED_SINGLE_RESULT_SIZE, result.size());
        assertEquals(testAccount, result.getFirst());
        verify(accountRepository).findAccountsByOwner_pesel(new Pesel(TEST_PESEL));
    }

    @Test
    void getAccountsByOwnersPESEL_WhenNoAccountsExist_ShouldThrowException() {
        // given
        when(accountRepository.findAccountsByOwner_pesel(new Pesel(NONEXISTENT_PESEL)))
            .thenReturn(Optional.empty());

        // when & then
        assertThrows(OwnerAccountsNotFoundException.class,
            () -> accountQueryService.getAccountsByOwnersPesel(NONEXISTENT_PESEL));
        verify(accountRepository).findAccountsByOwner_pesel(new Pesel(NONEXISTENT_PESEL));
    }

    @Test
    void getAccountsByOwnersUsername_WhenAccountsExist_ShouldReturnAccounts() {
        // given
        Account testAccount = getTestAccount();
        List<Account> accounts = Collections.singletonList(testAccount);
        when(accountRepository.findAccountsByOwner_username(TEST_USERNAME))
            .thenReturn(Optional.of(accounts));

        // when
        List<Account> result = accountQueryService.getAccountsByOwnersUsername(TEST_USERNAME);

        // then
        assertEquals(EXPECTED_SINGLE_RESULT_SIZE, result.size());
        assertEquals(testAccount, result.getFirst());
        verify(accountRepository).findAccountsByOwner_username(TEST_USERNAME);
    }

    @Test
    void findAccountByOwnersEmail_WhenAccountExists_ShouldReturnAccount() {
        // given
        Account testAccount = getTestAccount();
        EmailAddress email = new EmailAddress(TEST_EMAIL);
        when(accountRepository.findFirstByOwner_email(email))
            .thenReturn(Optional.of(testAccount));

        // when
        Account result = accountQueryService.getAccountByOwnersEmail(email);

        // then
        assertNotNull(result);
        assertEquals(testAccount, result);
        verify(accountRepository).findFirstByOwner_email(email);
    }

    @Test
    void getAccountByIban_WhenAccountExists_ShouldReturnAccount() {
        // given
        Account testAccount = getTestAccount();
        Iban testIban = TestIbanProvider.getIbanObject(DEFAULT_IBAN_INDEX);
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
        Iban testIban = TestIbanProvider.getIbanObject(ALTERNATIVE_IBAN_INDEX);
        when(accountRepository.findByIban(testIban)).thenReturn(Optional.empty());

        // when & then
        assertThrows(AccountNotFoundByIbanException.class,
            () -> accountQueryService.getAccountByIban(testIban.toString()));
        verify(accountRepository).findByIban(testIban);
    }

    @Test
    void getAccountsByOwnersId_WhenAccountsExist_ShouldReturnAccounts() {
        // given
        Account testAccount = getTestAccount();
        List<Account> accounts = Collections.singletonList(testAccount);
        when(accountRepository.findAccountsByOwner_id(TEST_OWNER_ID))
            .thenReturn(Optional.of(accounts));

        // when
        List<Account> result = accountQueryService.getAccountsByOwnersId(TEST_OWNER_ID);

        // then
        assertEquals(EXPECTED_SINGLE_RESULT_SIZE, result.size());
        assertEquals(testAccount, result.getFirst());
        verify(accountRepository).findAccountsByOwner_id(TEST_OWNER_ID);
    }
}