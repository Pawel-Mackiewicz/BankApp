package info.mackiewicz.bankapp.user.service;

import info.mackiewicz.bankapp.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserValidationServiceTest {

    @Mock
    private UserQueryService userQueryService;

    @InjectMocks
    private UserValidationService userValidationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateUsernameUnique_whenUsernameExists_shouldThrowException() {
        String username = "existingUsername";
        when(userQueryService.checkUsernameExists(username)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> userValidationService.validateUsernameUnique(username));

        verify(userQueryService).checkUsernameExists(username);
    }

    @Test
    void validateUsernameUnique_whenUsernameDoesNotExist_shouldNotThrowException() {
        String username = "newUsername";
        when(userQueryService.checkUsernameExists(username)).thenReturn(false);

        userValidationService.validateUsernameUnique(username);

        verify(userQueryService).checkUsernameExists(username);
    }

    @Test
    void validateEmailUnique_whenEmailExists_shouldThrowException() {
        String email = "existing@example.com";
        when(userQueryService.userExistsByEmail(email)).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> userValidationService.validateEmailUnique(email));

        verify(userQueryService).userExistsByEmail(email);
    }

    @Test
    void validateEmailUnique_whenEmailDoesNotExist_shouldNotThrowException() {
        String email = "new@example.com";
        when(userQueryService.userExistsByEmail(email)).thenReturn(false);

        userValidationService.validateEmailUnique(email);

        verify(userQueryService).userExistsByEmail(email);
    }

    @Test
    void validateNewUser_whenBothUsernameAndEmailAreUnique_shouldNotThrowException() {
        User user = new User();
        user.setUsername("newUsername");
        user.setEmail("new@example.com");

        when(userQueryService.checkUsernameExists(user.getUsername())).thenReturn(false);
        when(userQueryService.userExistsByEmail(user.getEmail())).thenReturn(false);

        userValidationService.validateNewUser(user);

        verify(userQueryService).checkUsernameExists(user.getUsername());
        verify(userQueryService).userExistsByEmail(user.getEmail());
    }

    @Test
    void validateNewUser_whenUsernameExists_shouldThrowException() {
        User user = new User();
        user.setUsername("existingUsername");
        user.setEmail("new@example.com");

        when(userQueryService.checkUsernameExists(user.getUsername())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> userValidationService.validateNewUser(user));

        verify(userQueryService).checkUsernameExists(user.getUsername());
        verify(userQueryService, never()).userExistsByEmail(any());
    }

    @Test
    void validateNewUser_whenEmailExists_shouldThrowException() {
        User user = new User();
        user.setUsername("newUsername");
        user.setEmail("existing@example.com");

        when(userQueryService.checkUsernameExists(user.getUsername())).thenReturn(false);
        when(userQueryService.userExistsByEmail(user.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> userValidationService.validateNewUser(user));

        verify(userQueryService).checkUsernameExists(user.getUsername());
        verify(userQueryService).userExistsByEmail(user.getEmail());
    }
}