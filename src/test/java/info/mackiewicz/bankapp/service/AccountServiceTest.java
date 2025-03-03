package info.mackiewicz.bankapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import info.mackiewicz.bankapp.account.model.AccountFactory;
import info.mackiewicz.bankapp.account.repository.AccountRepository;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.shared.exception.AccountNotFoundByIdException;
import info.mackiewicz.bankapp.shared.exception.OwnerAccountsNotFoundException;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.service.UserService;

class AccountServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceTest.class);

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserService userService;

    @Mock
    private AccountFactory accountFactory;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;
    private User testUser;
    private final int TEST_ACCOUNT_ID = 1;
    private final int TEST_USER_ID = 1;
    private final String TEST_IBAN = "PL12345678901234567890123456";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Initialize test user
        testUser = new User();
        testUser.setId(TEST_USER_ID);
        
        // Create test account using factory
        testAccount = createTestAccount();
        
        // Setup common mocks
        when(accountFactory.createAccount(any(User.class))).thenReturn(testAccount);
    }

    private Account createTestAccount() {
        try {
            // Use reflection to create Account instance
            java.lang.reflect.Constructor<Account> constructor = Account.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            Account account = constructor.newInstance();

            // Set required fields using reflection
            java.lang.reflect.Field idField = Account.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(account, TEST_ACCOUNT_ID);

            java.lang.reflect.Field ownerField = Account.class.getDeclaredField("owner");
            ownerField.setAccessible(true);
            ownerField.set(account, testUser);

            java.lang.reflect.Field ibanField = Account.class.getDeclaredField("iban");
            ibanField.setAccessible(true);
            ibanField.set(account, TEST_IBAN);
            
            return account;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create test account", e);
        }
    }

    @Test
    void testGetAccountById() {
        logger.info("testGetAccountById: Starting test");
        when(accountRepository.findById(TEST_ACCOUNT_ID)).thenReturn(Optional.of(testAccount));
        
        Account result = accountService.getAccountById(TEST_ACCOUNT_ID);
        
        assertNotNull(result);
        assertEquals(TEST_ACCOUNT_ID, result.getId());
    }

    @Test
    void testCreateAccount() {
        logger.info("testCreateAccount: Starting test");
        when(userService.getUserById(TEST_USER_ID)).thenReturn(testUser);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        Account result = accountService.createAccount(TEST_USER_ID);
        
        assertNotNull(result);
        assertEquals(TEST_USER_ID, result.getOwner().getId());
    }

    @Test
    void testGetAccountById_NotFound() {
        logger.info("testGetAccountById_NotFound: Starting test");
        when(accountRepository.findById(TEST_ACCOUNT_ID)).thenReturn(Optional.empty());
        
        assertThrows(AccountNotFoundByIdException.class, () -> 
            accountService.getAccountById(TEST_ACCOUNT_ID));
    }

    @Test
    void testGetAccountsByOwnersPESEL() {
        String testPesel = "1234567890";
        testUser.setPESEL(testPesel);
        List<Account> accounts = Collections.singletonList(testAccount);

        when(accountRepository.findAccountsByOwner_PESEL(testPesel))
            .thenReturn(Optional.of(accounts));
            
        List<Account> result = accountService.getAccountsByOwnersPESEL(testPesel);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testPesel, result.get(0).getOwner().getPESEL());
    }

    @Test
    void testGetAccountsByOwnersPESEL_NotFound() {
        String testPesel = "1234567890";
        when(accountRepository.findAccountsByOwner_PESEL(testPesel))
            .thenReturn(Optional.empty());
            
        assertThrows(OwnerAccountsNotFoundException.class, () -> 
            accountService.getAccountsByOwnersPESEL(testPesel));
    }

    @Test
    void testGetAccountsByOwnersUsername() {
        String testUsername = "testuser";
        testUser.setUsername(testUsername);
        List<Account> accounts = Collections.singletonList(testAccount);

        when(accountRepository.findAccountsByOwner_username(testUsername))
            .thenReturn(Optional.of(accounts));
            
        List<Account> result = accountService.getAccountsByOwnersUsername(testUsername);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUsername, result.get(0).getOwner().getUsername());
    }

    @Test
    void testGetAccountsByOwnersUsername_NotFound() {
        String testUsername = "testuser";
        when(accountRepository.findAccountsByOwner_username(testUsername))
            .thenReturn(Optional.empty());
            
        assertThrows(OwnerAccountsNotFoundException.class, () -> 
            accountService.getAccountsByOwnersUsername(testUsername));
    }

    @Test
    void testGetAccountsByOwnersId() {
        List<Account> accounts = Collections.singletonList(testAccount);

        when(accountRepository.findAccountsByOwner_id(TEST_USER_ID))
            .thenReturn(Optional.of(accounts));
            
        List<Account> result = accountService.getAccountsByOwnersId(TEST_USER_ID);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TEST_USER_ID, result.get(0).getOwner().getId());
    }

    @Test
    void testGetAccountsByOwnersId_NotFound() {
        when(accountRepository.findAccountsByOwner_id(TEST_USER_ID))
            .thenReturn(Optional.empty());
            
        assertThrows(OwnerAccountsNotFoundException.class, () -> 
            accountService.getAccountsByOwnersId(TEST_USER_ID));
    }

    @Test
    void testFindAccountByIban() {
        when(accountRepository.findByIban(TEST_IBAN))
            .thenReturn(Optional.of(testAccount));
            
        Optional<Account> result = accountService.findAccountByIban(TEST_IBAN);

        assertNotNull(result);
        assertEquals(TEST_IBAN, result.get().getIban());
    }

    @Test
    void testFindAccountByIban_NotFound() {
        when(accountRepository.findByIban(TEST_IBAN))
            .thenReturn(Optional.empty());
            
        Optional<Account> result = accountService.findAccountByIban(TEST_IBAN);
        assertEquals(Optional.empty(), result);
    }

    @Test
    void testGetAllAccounts() {
        List<Account> accounts = Collections.singletonList(testAccount);

        when(accountRepository.findAll()).thenReturn(accounts);
        
        List<Account> result = accountService.getAllAccounts();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testWithdraw() throws NoSuchFieldException, IllegalAccessException {
        BigDecimal initialBalance = new BigDecimal("100.00");
        BigDecimal withdrawAmount = new BigDecimal("50.00");

        // Set initial balance
        java.lang.reflect.Field balanceField = Account.class.getDeclaredField("balance");
        balanceField.setAccessible(true);
        balanceField.set(testAccount, initialBalance);

        when(accountRepository.findById(TEST_ACCOUNT_ID)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(testAccount)).thenReturn(testAccount);

        Account result = accountService.withdraw(TEST_ACCOUNT_ID, withdrawAmount);

        assertNotNull(result);
        assertEquals(initialBalance.subtract(withdrawAmount), result.getBalance());
    }
}