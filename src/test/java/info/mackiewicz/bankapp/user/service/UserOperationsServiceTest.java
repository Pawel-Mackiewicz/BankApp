package info.mackiewicz.bankapp.user.service;

import info.mackiewicz.bankapp.security.service.PasswordService;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateUser_shouldEncodePasswordAndSaveUser() {
        User user = new User();
        user.setId(1);
        user.setPassword("rawPassword");
        User encodedUser = new User();
        encodedUser.setId(1);
        encodedUser.setPassword("encodedPassword");
        User savedUser = new User();
        savedUser.setId(1);
        savedUser.setPassword("encodedPassword");

        doNothing().when(userValidationService).validateUserExists(1);
        when(passwordService.ensurePasswordEncoded(user)).thenReturn(encodedUser);
        when(userRepository.save(encodedUser)).thenReturn(savedUser);

        User result = userOperationsService.updateUser(user);

        assertNotNull(result);
        assertEquals("encodedPassword", result.getPassword());
        verify(passwordService).ensurePasswordEncoded(user);
        verify(userRepository).save(encodedUser);
    }

    @Test
    void changeUsersPassword_shouldUpdatePasswordByEmail() {
        String email = "test@example.com";
        String newPassword = "newPassword";
        String encodedPassword = "encodedNewPassword";

        when(passwordService.encodePassword(newPassword)).thenReturn(encodedPassword);
        doNothing().when(userRepository).updatePasswordByEmail(email, encodedPassword);

        userOperationsService.changeUsersPassword(email, newPassword);

        verify(passwordService).encodePassword(newPassword);
        verify(userRepository).updatePasswordByEmail(email, encodedPassword);
    }

    @Test
    void deleteUser_shouldDeleteExistingUser() {
        Integer userId = 1;
        User user = new User();
        user.setId(userId);

        doNothing().when(userValidationService).validateUserExists(userId);
        when(userQueryService.getUserById(userId)).thenReturn(user);
        doNothing().when(userRepository).delete(user);

        userOperationsService.deleteUser(userId);

        verify(userQueryService).getUserById(userId);
        verify(userRepository).delete(user);
    }
}