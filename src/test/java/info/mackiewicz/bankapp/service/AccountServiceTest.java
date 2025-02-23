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

import info.mackiewicz.bankapp.exception.AccountNotFoundByIdException;
import info.mackiewicz.bankapp.exception.OwnerAccountsNotFoundException;
import info.mackiewicz.bankapp.model.Account;
import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.repository.AccountRepository;

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

        logger.info("testGetAccountById: Calling accountRepository.findById with accountId: {}", accountId);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        logger.info("testGetAccountById: accountRepository.findById returned: {}", Optional.of(account));

        logger.info("testGetAccountById: Calling accountService.getAccountById with accountId: {}", accountId);
        Account result = accountService.getAccountById(accountId);
        logger.info("testGetAccountById: accountService.getAccountById returned: {}", result);

        assertNotNull(result);
        assertEquals(accountId, result.getId());
        logger.info("testGetAccountById: Test passed");
    }

    @Test
    void testCreateAccount() {
        logger.info("testCreateAccount: Starting test");
        int userId = 1;
        User user = new User();
        user.setId(userId);

        Account account = new Account(user);

        logger.info("testCreateAccount: Calling userService.getUserById with userId: {}", userId);
        when(userService.getUserById(userId)).thenReturn(user);
        logger.info("testCreateAccount: userService.getUserById returned: {}", user);
        logger.info("testCreateAccount: Calling accountRepository.save with account: {}", account);
        when(accountRepository.save(account)).thenReturn(account);
        logger.info("testCreateAccount: accountRepository.save returned: {}", account);

        logger.info("testCreateAccount: Calling accountService.createAccount with userId: {}", userId);
        Account result = accountService.createAccount(userId);
        logger.info("testCreateAccount: accountService.createAccount returned: {}", result);

        assertNotNull(result);
        logger.info("testCreateAccount: Test passed");
    }

    @Test
    void testDeleteAccountById() {
        logger.info("testDeleteAccountById: Starting test");
        int accountId = 1;
        User user = new User();
        Account account = new Account(user);
        // account.setId(accountId); // Removed setId as it doesn't exist
        // Manually set the id using reflection
        try {
            java.lang.reflect.Field field = Account.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(account, accountId);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        logger.info("testDeleteAccountById: Calling accountRepository.findById with accountId: {}", accountId);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        logger.info("testDeleteAccountById: accountRepository.findById returned: {}", Optional.of(account));

        logger.info("testDeleteAccountById: Calling accountService.deleteAccountById with accountId: {}", accountId);
        accountService.deleteAccountById(accountId);
        logger.info("testDeleteAccountById: accountService.deleteAccountById called");
        logger.info("testDeleteAccountById: Test passed");
    }

    @Test
    void testGetAccountById_NotFound() {
        logger.info("testGetAccountById_NotFound: Starting test");
        int accountId = 1;

        logger.info("testGetAccountById_NotFound: Calling accountRepository.findById with accountId: {}", accountId);
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());
        logger.info("testGetAccountById_NotFound: accountRepository.findById returned: {}", Optional.empty());

        logger.info("testGetAccountById_NotFound: Calling accountService.getAccountById with accountId: {}", accountId);
        assertThrows(AccountNotFoundByIdException.class, () -> accountService.getAccountById(accountId));
        logger.info("testGetAccountById_NotFound: AccountNotFoundByIdException thrown");
        logger.info("testGetAccountById_NotFound: Test passed");
    }

    @Test
    void testGetAccountsByOwnersPESEL() {
        logger.info("testGetAccountsByOwnersPESEL: Starting test");
        String pesel = "1234567890";
        User user = new User();
        user.setPESEL(pesel);
        Account account = new Account(user);
        List<Account> accounts = Collections.singletonList(account);

        logger.info("testGetAccountsByOwnersPESEL: Calling accountRepository.findAccountsByOwner_PESEL with pesel: {}", pesel);
        when(accountRepository.findAccountsByOwner_PESEL(pesel)).thenReturn(Optional.of(accounts));
        logger.info("testGetAccountsByOwnersPESEL: accountRepository.findAccountsByOwner_PESEL returned: {}", Optional.of(accounts));

        logger.info("testGetAccountsByOwnersPESEL: Calling accountService.getAccountsByOwnersPESEL with pesel: {}", pesel);
        List<Account> result = accountService.getAccountsByOwnersPESEL(pesel);
        logger.info("testGetAccountsByOwnersPESEL: accountService.getAccountsByOwnersPESEL returned: {}", result);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(pesel, result.get(0).getOwner().getPESEL());
        logger.info("testGetAccountsByOwnersPESEL: Test passed");
    }

    @Test
    void testGetAccountsByOwnersPESEL_NotFound() {
        logger.info("testGetAccountsByOwnersPESEL_NotFound: Starting test");
        String pesel = "1234567890";

        logger.info("testGetAccountsByOwnersPESEL_NotFound: Calling accountRepository.findAccountsByOwner_PESEL with pesel: {}", pesel);
        when(accountRepository.findAccountsByOwner_PESEL(pesel)).thenReturn(Optional.empty());
        logger.info("testGetAccountsByOwnersPESEL_NotFound: accountRepository.findAccountsByOwner_PESEL returned: {}", Optional.empty());

        logger.info("testGetAccountsByOwnersPESEL_NotFound: Calling accountService.getAccountsByOwnersPESEL with pesel: {}", pesel);
        assertThrows(OwnerAccountsNotFoundException.class, () -> accountService.getAccountsByOwnersPESEL(pesel));
        logger.info("testGetAccountsByOwnersPESEL_NotFound: OwnerAccountsNotFoundException thrown");
        logger.info("testGetAccountsByOwnersPESEL_NotFound: Test passed");
    }

    @Test
    void testGetAccountsByOwnersUsername() {
        logger.info("testGetAccountsByOwnersUsername: Starting test");
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        Account account = new Account(user);
        List<Account> accounts = Collections.singletonList(account);

        logger.info("testGetAccountsByOwnersUsername: Calling accountRepository.findAccountsByOwner_username with username: {}", username);
        when(accountRepository.findAccountsByOwner_username(username)).thenReturn(Optional.of(accounts));
        logger.info("testGetAccountsByOwnersUsername: accountRepository.findAccountsByOwner_username returned: {}", Optional.of(accounts));

        logger.info("testGetAccountsByOwnersUsername: Calling accountService.getAccountsByOwnersUsername with username: {}", username);
        List<Account> result = accountService.getAccountsByOwnersUsername(username);
        logger.info("testGetAccountsByOwnersUsername: accountService.getAccountsByOwnersUsername returned: {}", result);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(username, result.get(0).getOwner().getUsername());
        logger.info("testGetAccountsByOwnersUsername: Test passed");
    }

    @Test
    void testGetAccountsByOwnersUsername_NotFound() {
        logger.info("testGetAccountsByOwnersUsername_NotFound: Starting test");
        String username = "testuser";

        logger.info("testGetAccountsByOwnersUsername_NotFound: Calling accountRepository.findAccountsByOwner_username with username: {}", username);
        when(accountRepository.findAccountsByOwner_username(username)).thenReturn(Optional.empty());
        logger.info("testGetAccountsByOwnersUsername_NotFound: accountRepository.findAccountsByOwner_username returned: {}", Optional.empty());

        logger.info("testGetAccountsByOwnersUsername_NotFound: Calling accountService.getAccountsByOwnersUsername with username: {}", username);
        assertThrows(OwnerAccountsNotFoundException.class, () -> accountService.getAccountsByOwnersUsername(username));
        logger.info("testGetAccountsByOwnersUsername_NotFound: OwnerAccountsNotFoundException thrown");
        logger.info("testGetAccountsByOwnersUsername_NotFound: Test passed");
    }

    @Test
    void testGetAccountsByOwnersId() {
        logger.info("testGetAccountsByOwnersId: Starting test");
        int userId = 1;
        User user = new User();
        user.setId(userId);
        Account account = new Account(user);
        List<Account> accounts = Collections.singletonList(account);

        logger.info("testGetAccountsByOwnersId: Calling accountRepository.findAccountsByOwner_id with userId: {}", userId);
        when(accountRepository.findAccountsByOwner_id(userId)).thenReturn(Optional.of(accounts));
        logger.info("testGetAccountsByOwnersId: accountRepository.findAccountsByOwner_id returned: {}", Optional.of(accounts));

        logger.info("testGetAccountsByOwnersId: Calling accountService.getAccountsByOwnersId with userId: {}", userId);
        List<Account> result = accountService.getAccountsByOwnersId(userId);
        logger.info("testGetAccountsByOwnersId: accountService.getAccountsByOwnersId returned: {}", result);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getOwner().getId());
        logger.info("testGetAccountsByOwnersId: Test passed");
    }

    @Test
    void testGetAccountsByOwnersId_NotFound() {
        logger.info("testGetAccountsByOwnersId_NotFound: Starting test");
        int userId = 1;

        logger.info("testGetAccountsByOwnersId_NotFound: Calling accountRepository.findAccountsByOwner_id with userId: {}", userId);
        when(accountRepository.findAccountsByOwner_id(userId)).thenReturn(Optional.empty());
        logger.info("testGetAccountsByOwnersId_NotFound: accountRepository.findAccountsByOwner_id returned: {}", Optional.empty());

        logger.info("testGetAccountsByOwnersId_NotFound: Calling accountService.getAccountsByOwnersId with userId: {}", userId);
        assertThrows(OwnerAccountsNotFoundException.class, () -> accountService.getAccountsByOwnersId(userId));
        logger.info("testGetAccountsByOwnersId_NotFound: OwnerAccountsNotFoundException thrown");
        logger.info("testGetAccountsByOwnersId_NotFound: Test passed");
    }

    @Test
    void testFindByIban() {
        logger.info("testFindByIban: Starting test");
        String iban = "PL12345678901234567890123456";
        User user = new User();
        Account account = new Account(user);
        account.setIban(iban);

        logger.info("testFindByIban: Calling accountRepository.findByIban with iban: {}", iban);
        when(accountRepository.findByIban(iban)).thenReturn(Optional.of(account));
        logger.info("testFindByIban: accountRepository.findByIban returned: {}", Optional.of(account));

        logger.info("testFindByIban: Calling accountService.findByIban with iban: {}", iban);
        Optional<Account> result = accountService.findByIban(iban);
        logger.info("testFindByIban: accountService.findByIban returned: {}", result);

        assertNotNull(result);
        assertEquals(iban, result.get().getIban());
        logger.info("testFindByIban: Test passed");
    }

    @Test
    void testFindByIban_NotFound() {
        logger.info("testFindByIban_NotFound: Starting test");
        String iban = "PL12345678901234567890123456";

        logger.info("testFindByIban_NotFound: Calling accountRepository.findByIban with iban: {}", iban);
        when(accountRepository.findByIban(iban)).thenReturn(Optional.empty());
        logger.info("testFindByIban_NotFound: accountRepository.findByIban returned: {}", Optional.empty());

        logger.info("testFindByIban_NotFound: Calling accountService.findByIban with iban: {}", iban);
        Optional<Account> result = accountService.findByIban(iban);
        assertEquals(Optional.empty(), result);
        logger.info("testFindByIban_NotFound: Test passed");
    }

    @Test
    void testGetAllAccounts() {
        logger.info("testGetAllAccounts: Starting test");
        User user = new User();
        Account account = new Account(user);
        List<Account> accounts = Collections.singletonList(account);

        logger.info("testGetAllAccounts: Calling accountRepository.findAll");
        when(accountRepository.findAll()).thenReturn(accounts);
        logger.info("testGetAllAccounts: accountRepository.findAll returned: {}", accounts);

        logger.info("testGetAllAccounts: Calling accountService.getAllAccounts");
        List<Account> result = accountService.getAllAccounts();
        logger.info("testGetAllAccounts: accountService.getAllAccounts returned: {}", result);

        assertNotNull(result);
        assertEquals(1, result.size());
        logger.info("testGetAllAccounts: Test passed");
    }

    @Test
    void testChangeAccountOwner() {
        logger.info("testChangeAccountOwner: Starting test");
        int accountId = 1;
        int newUserId = 2;
        User oldUser = new User();
        oldUser.setId(1);
        User newUser = new User();
        newUser.setId(newUserId);
        Account account = new Account(oldUser);

        logger.info("testChangeAccountOwner: Calling accountService.getAccountById with accountId: {}", accountId);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        logger.info("testChangeAccountOwner: accountRepository.findById returned: {}", Optional.of(account));

        logger.info("testChangeAccountOwner: Calling userService.getUserById with newUserId: {}", newUserId);
        when(userService.getUserById(newUserId)).thenReturn(newUser);
        logger.info("testChangeAccountOwner: userService.getUserById returned: {}", newUser);

        logger.info("testChangeAccountOwner: Calling accountRepository.save with account: {}", account);
        when(accountRepository.save(account)).thenReturn(account);
        logger.info("testChangeAccountOwner: accountRepository.save returned: {}", account);

        logger.info("testChangeAccountOwner: Calling accountService.changeAccountOwner with accountId: {} and newUserId: {}", accountId, newUserId);
        Account result = accountService.changeAccountOwner(accountId, newUserId);
        logger.info("testChangeAccountOwner: accountService.changeAccountOwner returned: {}", result);

        assertNotNull(result);
        assertEquals(newUserId, result.getOwner().getId());
        logger.info("testChangeAccountOwner: Test passed");
    }

    @Test
    void testWithdraw() throws NoSuchFieldException, IllegalAccessException {
        logger.info("testWithdraw: Starting test");
        int accountId = 1;
        BigDecimal amount = new BigDecimal("50.00");
        User user = new User();
        Account account = new Account(user);
        BigDecimal initialBalance = new BigDecimal("100.00");

        java.lang.reflect.Field balanceField = Account.class.getDeclaredField("balance");
        balanceField.setAccessible(true);
        balanceField.set(account, initialBalance);

        logger.info("testWithdraw: Calling accountService.getAccountById with accountId: {}", accountId);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        logger.info("testWithdraw: accountRepository.findById returned: {}", Optional.of(account));

        logger.info("testWithdraw: Calling accountRepository.save with account: {}", account);
        when(accountRepository.save(account)).thenReturn(account);
        logger.info("testWithdraw: accountRepository.save returned: {}", account);

        logger.info("testWithdraw: Calling accountService.withdraw with accountId: {} and amount: {}", accountId, amount);
        Account result = accountService.withdraw(accountId, amount);
        logger.info("testWithdraw: accountService.withdraw returned: {}", result);

        assertNotNull(result);
        assertEquals(initialBalance.subtract(amount), result.getBalance());
        logger.info("testWithdraw: Test passed");
    }
}