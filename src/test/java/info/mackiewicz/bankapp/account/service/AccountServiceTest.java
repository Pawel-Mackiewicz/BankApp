package info.mackiewicz.bankapp.account.service;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.repository.AccountRepository;
import info.mackiewicz.bankapp.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountOperationsService accountOperationsService;

    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private AccountCreationService accountCreationService;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;
    private User testUser;
    private static final Integer TEST_USER_ID = 1;
    private static final String TEST_PESEL = "12345678901";
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setId(TEST_USER_ID);
        testUser.setPESEL(TEST_PESEL);
        testUser.setFirstname("John");
        testUser.setLastname("Doe");
        testUser.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testUser.setUsername(TEST_USERNAME);
        testUser.setEmail(TEST_EMAIL);
        testUser.setPhoneNumber("+48123456789");

        // Create test account using factory
        testAccount = Account.factory().createAccount(testUser);
    }

    @Test
    void createAccount_ShouldDelegateToCreationService() {
        // Arrange
        when(accountCreationService.createAccount(TEST_USER_ID)).thenReturn(testAccount);

        // Act
        Account result = accountService.createAccount(TEST_USER_ID);

        // Assert
        assertThat(result).isEqualTo(testAccount);
        verify(accountCreationService).createAccount(TEST_USER_ID);
    }

    @Test
    void getAccountById_ShouldDelegateToQueryService() {
        // Arrange
        when(accountQueryService.getAccountById(1)).thenReturn(testAccount);

        // Act
        Account result = accountService.getAccountById(1);

        // Assert
        assertThat(result).isEqualTo(testAccount);
        verify(accountQueryService).getAccountById(1);
    }

    @Test
    void getAllAccounts_ShouldDelegateToQueryService() {
        // Arrange
        List<Account> accounts = Arrays.asList(testAccount);
        when(accountQueryService.getAllAccounts()).thenReturn(accounts);

        // Act
        List<Account> result = accountService.getAllAccounts();

        // Assert
        assertThat(result).isEqualTo(accounts);
        verify(accountQueryService).getAllAccounts();
    }

    @Test
    void getAccountsByOwnersPESEL_ShouldDelegateToQueryService() {
        // Arrange
        List<Account> accounts = Arrays.asList(testAccount);
        when(accountQueryService.getAccountsByOwnersPESEL(TEST_PESEL)).thenReturn(accounts);

        // Act
        List<Account> result = accountService.getAccountsByOwnersPESEL(TEST_PESEL);

        // Assert
        assertThat(result).isEqualTo(accounts);
        verify(accountQueryService).getAccountsByOwnersPESEL(TEST_PESEL);
    }

    @Test
    void getAccountsByOwnersUsername_ShouldDelegateToQueryService() {
        // Arrange
        List<Account> accounts = Arrays.asList(testAccount);
        when(accountQueryService.getAccountsByOwnersUsername(TEST_USERNAME)).thenReturn(accounts);

        // Act
        List<Account> result = accountService.getAccountsByOwnersUsername(TEST_USERNAME);

        // Assert
        assertThat(result).isEqualTo(accounts);
        verify(accountQueryService).getAccountsByOwnersUsername(TEST_USERNAME);
    }

    @Test
    void findAccountByOwnersEmail_ShouldDelegateToQueryService() {
        // Arrange
        when(accountQueryService.findAccountByOwnersEmail(TEST_EMAIL)).thenReturn(testAccount);

        // Act
        Account result = accountService.findAccountByOwnersEmail(TEST_EMAIL);

        // Assert
        assertThat(result).isEqualTo(testAccount);
        verify(accountQueryService).findAccountByOwnersEmail(TEST_EMAIL);
    }

    @Test
    void findAccountByIban_ShouldDelegateToQueryService() {
        // Arrange
        String iban = testAccount.getIban().toString();
        when(accountQueryService.findAccountByIban(iban)).thenReturn(testAccount);

        // Act
        Account result = accountService.findAccountByIban(iban);

        // Assert
        assertThat(result).isEqualTo(testAccount);
        verify(accountQueryService).findAccountByIban(iban);
    }

    @Test
    void deposit_ShouldDelegateToOperationsService() {
        // Arrange
        BigDecimal amount = new BigDecimal("100.00");
        when(accountOperationsService.deposit(testAccount, amount)).thenReturn(testAccount);

        // Act
        Account result = accountService.deposit(testAccount, amount);

        // Assert
        assertThat(result).isEqualTo(testAccount);
        verify(accountOperationsService).deposit(testAccount, amount);
    }

    @Test
    void withdraw_ShouldDelegateToOperationsService() {
        // Arrange
        BigDecimal amount = new BigDecimal("50.00");
        when(accountOperationsService.withdraw(testAccount, amount)).thenReturn(testAccount);

        // Act
        Account result = accountService.withdraw(testAccount, amount);

        // Assert
        assertThat(result).isEqualTo(testAccount);
        verify(accountOperationsService).withdraw(testAccount, amount);
    }

    @Test
    void deleteAccountById_ShouldDeleteAccount() {
        // Arrange
        when(accountQueryService.getAccountById(1)).thenReturn(testAccount);

        // Act
        accountService.deleteAccountById(1);

        // Assert
        verify(accountQueryService).getAccountById(1);
        verify(accountRepository).delete(testAccount);
    }
}