package info.mackiewicz.bankapp.user.service.crud;

import info.mackiewicz.bankapp.security.service.PasswordService;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.user.repository.UserRepository;
import info.mackiewicz.bankapp.user.service.util.UserValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserOperationsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private UserQueryService userQueryService;

    @Mock
    private UserValidationService userValidationService;

    @InjectMocks
    private UserOperationsService userOperationsService;

    @Test
    void updateUser_shouldEncodePasswordAndSaveUser() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setUsername("testUser");
        user.setPassword("rawPassword");
        
        User encodedUser = new User();
        encodedUser.setId(1);
        encodedUser.setUsername("testUser");
        encodedUser.setPassword("encodedPassword");
        
        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setUsername("testUser");
        savedUser.setPassword("encodedPassword");

        doNothing().when(userValidationService).validateUserExists(1);
        when(passwordService.ensurePasswordEncoded(user)).thenReturn(encodedUser);
        when(userRepository.save(encodedUser)).thenReturn(savedUser);

        // Act
        User result = userOperationsService.updateUser(user);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("testUser", result.getUsername());
        assertEquals("encodedPassword", result.getPassword());
        
        verify(userValidationService).validateUserExists(1);
        verify(passwordService).ensurePasswordEncoded(user);
        verify(userRepository).save(encodedUser);
    }

    @Test
    void changeUsersPassword_shouldUpdatePasswordByEmailString() {
        String emailStr = "test@example.com";
        String newPassword = "newPassword";
        String encodedPassword = "encodedNewPassword";
        EmailAddress email = new EmailAddress(emailStr);

        when(passwordService.ensurePasswordEncoded(newPassword)).thenReturn(encodedPassword);
        doNothing().when(userRepository).updatePasswordByEmail(email, encodedPassword);

        userOperationsService.changeUsersPassword(emailStr, newPassword);

        verify(passwordService).ensurePasswordEncoded(newPassword);
        verify(userRepository).updatePasswordByEmail(email, encodedPassword);
    }

    @Test
    void changeUsersPassword_shouldUpdatePasswordByEmailObject() {
        EmailAddress email = new EmailAddress("test@example.com");
        String newPassword = "newPassword";
        String encodedPassword = "encodedNewPassword";

        when(passwordService.ensurePasswordEncoded(newPassword)).thenReturn(encodedPassword);
        doNothing().when(userRepository).updatePasswordByEmail(email, encodedPassword);

        userOperationsService.changeUsersPassword(email, newPassword);

        verify(passwordService).ensurePasswordEncoded(newPassword);
        verify(userRepository).updatePasswordByEmail(email, encodedPassword);
    }

    @Test
    void deleteUser_shouldDeleteExistingUser() {
        // Arrange
        Integer userId = 1;
        User user = new User();
        user.setId(userId);
        user.setUsername("userToDelete");
        user.setEmail(new EmailAddress("delete@test.com"));

        when(userQueryService.getUserById(userId)).thenReturn(user);
        doNothing().when(userRepository).delete(user);

        // Act
        userOperationsService.deleteUser(userId);

        // Assert
        verify(userQueryService).getUserById(userId);
        verify(userRepository).delete(argThat(userToDelete ->
            userToDelete.getId().equals(userId) &&
            userToDelete.getUsername().equals("userToDelete") &&
            userToDelete.getEmail().getValue().equals("delete@test.com")
        ));
        
        // Verify proper order of operations
        inOrder(userQueryService, userRepository).verify(userQueryService).getUserById(userId);
        inOrder(userQueryService, userRepository).verify(userRepository).delete(user);
    }
}