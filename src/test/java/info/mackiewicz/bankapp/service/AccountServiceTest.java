package info.mackiewicz.bankapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import info.mackiewicz.bankapp.exception.AccountNotFoundByIdException;
import info.mackiewicz.bankapp.exception.OwnerAccountsNotFoundException;
import info.mackiewicz.bankapp.model.Account;
import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.repository.AccountRepository;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    
    @Mock
    private UserService userService;

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(accountRepository, userService);
    }

    @Test
    void createAccount_ShouldCreateNewAccountWithZeroBalance() {
        // Arrange
        Integer userId = 1;
        User mockUser = new User();
        mockUser.setId(userId);
        
        Account expectedAccount = new Account(mockUser);
        when(userService.getUserById(userId)).thenReturn(mockUser);
        when(accountRepository.save(any(Account.class))).thenReturn(expectedAccount);

        // Act
        Account createdAccount = accountService.createAccount(userId);

        // Assert
        assertNotNull(createdAccount);
        assertEquals(BigDecimal.ZERO, createdAccount.getBalance());
        assertEquals(userId, createdAccount.getOwner().getId());
    }

    @Test
    void getAccountById_ShouldReturnAccount_WhenExists() {
        // Arrange
        int accountId = 1;
        User owner = new User();
        owner.setId(1);
        Account expectedAccount = new Account(owner);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(expectedAccount));

        // Act
        Account result = accountService.getAccountById(accountId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedAccount, result);
    }

    @Test
    void getAccountById_ShouldThrowException_WhenNotFound() {
        // Arrange
        int accountId = 1;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountNotFoundByIdException.class, () -> accountService.getAccountById(accountId));
    }

    @Test
    void getAccountsByOwnersId_ShouldReturnUserAccounts() {
        // Arrange
        int userId = 1;
        User owner = new User();
        owner.setId(userId);
        List<Account> expectedAccounts = Arrays.asList(
            new Account(owner),
            new Account(owner)
        );
        when(accountRepository.findAccountsByOwner_id(userId)).thenReturn(Optional.of(expectedAccounts));

        // Act
        List<Account> result = accountService.getAccountsByOwnersId(userId);

        // Assert
        assertEquals(2, result.size());
        result.forEach(account -> assertEquals(userId, account.getOwner().getId()));
    }

    @Test
    void getAccountsByOwnersId_ShouldThrowException_WhenNotFound() {
        // Arrange
        int userId = 1;
        when(accountRepository.findAccountsByOwner_id(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OwnerAccountsNotFoundException.class, () -> accountService.getAccountsByOwnersId(userId));
    }

    @Test
    void deleteAccountById_ShouldDeleteExistingAccount() {
        // Arrange
        int accountId = 1;
        User owner = new User();
        owner.setId(1);
        Account account = new Account(owner);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        doNothing().when(accountRepository).delete(account);

        // Act
        accountService.deleteAccountById(accountId);

        // Assert
        verify(accountRepository).delete(account);
    }

    @Test
    void deleteAccountById_ShouldThrowException_WhenAccountNotFound() {
        // Arrange
        int accountId = 1;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AccountNotFoundByIdException.class, () -> accountService.deleteAccountById(accountId));
    }

    @Test
    void deposit_ShouldIncreaseBalance() {
        // Arrange
        int accountId = 1;
        BigDecimal depositAmount = new BigDecimal("100.00");
        
        User owner = new User();
        Account account = new Account(owner);
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // Act
        Account updatedAccount = accountService.deposit(accountId, depositAmount);

        // Assert
        assertEquals(depositAmount, updatedAccount.getBalance());
        verify(accountRepository).save(account);
    }

    @Test
    void withdraw_ShouldDecreaseBalance() {
        // Arrange
        int accountId = 1;
        BigDecimal initialBalance = new BigDecimal("200.00");
        BigDecimal withdrawAmount = new BigDecimal("100.00");
        
        User owner = new User();
        Account account = new Account(owner);
        account.deposit(initialBalance);
        
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // Act
        Account updatedAccount = accountService.withdraw(accountId, withdrawAmount);

        // Assert
        assertEquals(new BigDecimal("100.00"), updatedAccount.getBalance());
        verify(accountRepository).save(account);
    }


    @Test
    void getAccountsByOwnersUsername_ShouldReturnAccounts() {
        // Arrange
        String username = "testuser";
        User owner = new User();
        owner.setId(1);
        List<Account> expectedAccounts = Arrays.asList(
            new Account(owner),
            new Account(owner)
        );
        when(accountRepository.findAccountsByOwner_username(username)).thenReturn(Optional.of(expectedAccounts));

        // Act
        List<Account> result = accountService.getAccountsByOwnersUsername(username);

        // Assert
        assertEquals(2, result.size());
        verify(accountRepository).findAccountsByOwner_username(username);
    }

    @Test
    void getAccountsByOwnersUsername_ShouldThrowException_WhenNotFound() {
        // Arrange
        String username = "nonexistent";
        when(accountRepository.findAccountsByOwner_username(username)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OwnerAccountsNotFoundException.class, 
            () -> accountService.getAccountsByOwnersUsername(username));
    }

    @Test
    void getAllAccounts_ShouldReturnAllAccounts() {
        // Arrange
        List<Account> expectedAccounts = Arrays.asList(
            new Account(new User()),
            new Account(new User())
        );
        when(accountRepository.findAll()).thenReturn(expectedAccounts);

        // Act
        List<Account> result = accountService.getAllAccounts();

        // Assert
        assertEquals(2, result.size());
        verify(accountRepository).findAll();
    }

    @Test
    void changeAccountOwner_ShouldUpdateOwner() {
        // Arrange
        int accountId = 1;
        int newOwnerId = 2;
        
        User oldOwner = new User();
        oldOwner.setId(1);
        User newOwner = new User();
        newOwner.setId(newOwnerId);
        
        Account account = new Account(oldOwner);
        
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(userService.getUserById(newOwnerId)).thenReturn(newOwner);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // Act
        Account updatedAccount = accountService.changeAccountOwner(accountId, newOwnerId);

        // Assert
        assertEquals(newOwnerId, updatedAccount.getOwner().getId());
        verify(accountRepository).save(account);
    }
}