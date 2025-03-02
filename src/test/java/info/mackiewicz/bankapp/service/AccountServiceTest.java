package info.mackiewicz.bankapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.repository.AccountRepository;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.shared.exception.AccountNotFoundByIdException;
import info.mackiewicz.bankapp.shared.exception.OwnerAccountsNotFoundException;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.service.UserService;

class AccountServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceTest.class);

    private int accountId = 1;
    private Account account = new Account(new User());
    {
        try {
            java.lang.reflect.Field field = Account.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(account, accountId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAccountById() {
        logger.info("testGetAccountById: Starting test");
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        Account result = accountService.getAccountById(accountId);
        assertNotNull(result);
        assertEquals(accountId, result.getId());
    }

    @Test
    void testCreateAccount() {
        logger.info("testCreateAccount: Starting test");
        int userId = 1;
        User user = new User();
        user.setId(userId);
        Account account = new Account(user);

        when(userService.getUserById(userId)).thenReturn(user);
        when(accountRepository.save(account)).thenReturn(account);

        Account result = accountService.createAccount(userId);
        assertNotNull(result);
    }

    @Test
    void testDeleteAccountById() {
        logger.info("testDeleteAccountById: Starting test");
        int accountId = 1;
        User user = new User();
        Account account = new Account(user);
        try {
            java.lang.reflect.Field field = Account.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(account, accountId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        accountService.deleteAccountById(accountId);
    }

    @Test
    void testGetAccountById_NotFound() {
        logger.info("testGetAccountById_NotFound: Starting test");
        int accountId = 1;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundByIdException.class, () -> accountService.getAccountById(accountId));
    }

    @Test
    void testGetAccountsByOwnersPESEL() {
        String pesel = "1234567890";
        User user = new User();
        user.setPESEL(pesel);
        Account account = new Account(user);
        List<Account> accounts = Collections.singletonList(account);

        when(accountRepository.findAccountsByOwner_PESEL(pesel)).thenReturn(Optional.of(accounts));
        List<Account> result = accountService.getAccountsByOwnersPESEL(pesel);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(pesel, result.get(0).getOwner().getPESEL());
    }

    @Test
    void testGetAccountsByOwnersPESEL_NotFound() {
        String pesel = "1234567890";
        when(accountRepository.findAccountsByOwner_PESEL(pesel)).thenReturn(Optional.empty());
        assertThrows(OwnerAccountsNotFoundException.class, () -> accountService.getAccountsByOwnersPESEL(pesel));
    }

    @Test
    void testGetAccountsByOwnersUsername() {
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        Account account = new Account(user);
        List<Account> accounts = Collections.singletonList(account);

        when(accountRepository.findAccountsByOwner_username(username)).thenReturn(Optional.of(accounts));
        List<Account> result = accountService.getAccountsByOwnersUsername(username);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(username, result.get(0).getOwner().getUsername());
    }

    @Test
    void testGetAccountsByOwnersUsername_NotFound() {
        String username = "testuser";
        when(accountRepository.findAccountsByOwner_username(username)).thenReturn(Optional.empty());
        assertThrows(OwnerAccountsNotFoundException.class, () -> accountService.getAccountsByOwnersUsername(username));
    }

    @Test
    void testGetAccountsByOwnersId() {
        int userId = 1;
        User user = new User();
        user.setId(userId);
        Account account = new Account(user);
        List<Account> accounts = Collections.singletonList(account);

        when(accountRepository.findAccountsByOwner_id(userId)).thenReturn(Optional.of(accounts));
        List<Account> result = accountService.getAccountsByOwnersId(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getOwner().getId());
    }

    @Test
    void testGetAccountsByOwnersId_NotFound() {
        int userId = 1;
        when(accountRepository.findAccountsByOwner_id(userId)).thenReturn(Optional.empty());
        assertThrows(OwnerAccountsNotFoundException.class, () -> accountService.getAccountsByOwnersId(userId));
    }

    @Test
    void testFindAccountByIban() {
        String iban = "PL12345678901234567890123456";
        User user = new User();
        Account account = new Account(user);
        account.setIban(iban);

        when(accountRepository.findByIban(iban)).thenReturn(Optional.of(account));
        Optional<Account> result = accountService.findAccountByIban(iban);

        assertNotNull(result);
        assertEquals(iban, result.get().getIban());
    }

    @Test
    void testFindAccountByIban_NotFound() {
        String iban = "PL12345678901234567890123456";
        when(accountRepository.findByIban(iban)).thenReturn(Optional.empty());
        Optional<Account> result = accountService.findAccountByIban(iban);
        assertEquals(Optional.empty(), result);
    }

    @Test
    void testGetAllAccounts() {
        User user = new User();
        Account account = new Account(user);
        List<Account> accounts = Collections.singletonList(account);

        when(accountRepository.findAll()).thenReturn(accounts);
        List<Account> result = accountService.getAllAccounts();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testChangeAccountOwner() {
        int accountId = 1;
        int newUserId = 2;
        User oldUser = new User();
        oldUser.setId(1);
        User newUser = new User();
        newUser.setId(newUserId);
        Account account = new Account(oldUser);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(userService.getUserById(newUserId)).thenReturn(newUser);
        when(accountRepository.save(account)).thenReturn(account);

        Account result = accountService.changeAccountOwner(accountId, newUserId);

        assertNotNull(result);
        assertEquals(newUserId, result.getOwner().getId());
    }

    @Test
    void testWithdraw() throws NoSuchFieldException, IllegalAccessException {
        int accountId = 1;
        BigDecimal amount = new BigDecimal("50.00");
        User user = new User();
        Account account = new Account(user);
        BigDecimal initialBalance = new BigDecimal("100.00");

        java.lang.reflect.Field balanceField = Account.class.getDeclaredField("balance");
        balanceField.setAccessible(true);
        balanceField.set(account, initialBalance);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        Account result = accountService.withdraw(accountId, amount);

        assertNotNull(result);
        assertEquals(initialBalance.subtract(amount), result.getBalance());
    }
}