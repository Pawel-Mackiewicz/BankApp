package info.mackiewicz.bankapp.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.repository.AccountRepository;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.vo.Email;
import info.mackiewicz.bankapp.user.model.vo.Pesel;
import info.mackiewicz.bankapp.user.model.vo.PhoneNumber;
import info.mackiewicz.bankapp.user.service.UserService;

@ExtendWith(MockitoExtension.class)
class AccountCreationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private AccountValidationService validationService;

    @InjectMocks
    private AccountCreationService accountCreationService;

    private User testUser;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setPesel(new Pesel("12345678901"));
        testUser.setFirstname("John");
        testUser.setLastname("Doe");
        testUser.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testUser.setUsername("johndoe");
        testUser.setEmail(new Email("john@example.com"));
        testUser.setPhoneNumber(new PhoneNumber("+48123456789"));
        
        testAccount = Account.factory().createAccount(testUser);
    }

    @Test
    void createAccount_Success() {
        // Arrange
        when(userService.getUserById(testUser.getId())).thenReturn(testUser);
        when(userService.getUserByIdWithPessimisticLock(testUser.getId())).thenReturn(testUser);
        doNothing().when(validationService).validateNewAccountOwner(testUser);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Act
        Account createdAccount = accountCreationService.createAccount(testUser.getId());

        // Assert
        assertThat(createdAccount).isNotNull();
        // Verify account owner details through DTO
        assertThat(createdAccount.getOwner().getId()).isEqualTo(testUser.getId());
        assertThat(createdAccount.getOwner().getFullName()).isEqualTo(testUser.getFullName());
        
        verify(userService, times(1)).getUserById(testUser.getId());
        verify(userService, times(1)).getUserByIdWithPessimisticLock(testUser.getId());
        verify(validationService, times(1)).validateNewAccountOwner(testUser);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void createAccount_WithInvalidUser_ThrowsException() {
        // Arrange
        when(userService.getUserById(testUser.getId())).thenReturn(testUser);
        doThrow(new RuntimeException("Invalid user")).when(validationService).validateNewAccountOwner(testUser);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> accountCreationService.createAccount(testUser.getId()));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void createAccount_WithNonExistentUser_ThrowsException() {
        // Arrange
        when(userService.getUserById(999)).thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> accountCreationService.createAccount(999));
        verify(validationService, never()).validateNewAccountOwner(any());
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void createAccount_WithRepositoryFailure_RetryAndSucceed() {
        // Arrange
        when(userService.getUserById(testUser.getId())).thenReturn(testUser);
        when(userService.getUserByIdWithPessimisticLock(testUser.getId())).thenReturn(testUser);
        doNothing().when(validationService).validateNewAccountOwner(testUser);
        when(accountRepository.save(any(Account.class)))
                .thenThrow(new RuntimeException("DB Error")) // First attempt fails
                .thenReturn(testAccount); // Second attempt succeeds

        // Act
        Account createdAccount = accountCreationService.createAccount(testUser.getId());

        // Assert
        assertThat(createdAccount).isNotNull();
        assertThat(createdAccount.getOwner().getId()).isEqualTo(testUser.getId());
        assertThat(createdAccount.getOwner().getFullName()).isEqualTo(testUser.getFullName());
        verify(accountRepository, times(2)).save(any(Account.class));
    }
}