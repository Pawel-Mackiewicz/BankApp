package info.mackiewicz.bankapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mockito.ArgumentMatchers;

import info.mackiewicz.bankapp.exception.AccountNotFoundByIdException;
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
}