package info.mackiewicz.bankapp.user.service;

import info.mackiewicz.bankapp.shared.exception.UserNotFoundException;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserQueryServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserQueryService userQueryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserById_whenUserExists_shouldReturnUser() {
        Integer userId = 1;
        User expectedUser = new User();
        expectedUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        User actualUser = userQueryService.getUserById(userId);

        assertNotNull(actualUser);
        assertEquals(userId, actualUser.getId());
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserById_whenUserDoesNotExist_shouldThrowException() {
        Integer userId = 1;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userQueryService.getUserById(userId));
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserByIdWithPessimisticLock_whenUserExists_shouldReturnUser() {
        Integer userId = 1;
        User expectedUser = new User();
        expectedUser.setId(userId);

        when(userRepository.findByIdWithPessimisticLock(userId)).thenReturn(Optional.of(expectedUser));

        User actualUser = userQueryService.getUserByIdWithPessimisticLock(userId);

        assertNotNull(actualUser);
        assertEquals(userId, actualUser.getId());
        verify(userRepository).findByIdWithPessimisticLock(userId);
    }

    @Test
    void getUserByIdWithPessimisticLock_whenUserDoesNotExist_shouldThrowException() {
        Integer userId = 1;

        when(userRepository.findByIdWithPessimisticLock(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userQueryService.getUserByIdWithPessimisticLock(userId));
        verify(userRepository).findByIdWithPessimisticLock(userId);
    }

    @Test
    void getUserByUsername_whenUserExists_shouldReturnUser() {
        String username = "testUser";
        User expectedUser = new User();
        expectedUser.setUsername(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(expectedUser));

        User actualUser = userQueryService.getUserByUsername(username);

        assertNotNull(actualUser);
        assertEquals(username, actualUser.getUsername());
        verify(userRepository).findByUsername(username);
    }

    @Test
    void getUserByUsername_whenUserDoesNotExist_shouldThrowException() {
        String username = "testUser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userQueryService.getUserByUsername(username));
        verify(userRepository).findByUsername(username);
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        List<User> expectedUsers = List.of(new User(), new User());

        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<User> actualUsers = userQueryService.getAllUsers();

        assertNotNull(actualUsers);
        assertEquals(expectedUsers.size(), actualUsers.size());
        verify(userRepository).findAll();
    }

    @Test
    void checkUsernameExists_whenUsernameExists_shouldReturnTrue() {
        String username = "testUser";

        when(userRepository.existsByUsername(username)).thenReturn(true);

        assertTrue(userQueryService.checkUsernameExists(username));
        verify(userRepository).existsByUsername(username);
    }

    @Test
    void checkUsernameExists_whenUsernameDoesNotExist_shouldReturnFalse() {
        String username = "testUser";

        when(userRepository.existsByUsername(username)).thenReturn(false);

        assertFalse(userQueryService.checkUsernameExists(username));
        verify(userRepository).existsByUsername(username);
    }

    @Test
    void userExistsByEmail_whenEmailExists_shouldReturnTrue() {
        String email = "test@example.com";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertTrue(userQueryService.userExistsByEmail(email));
        verify(userRepository).existsByEmail(email);
    }

    @Test
    void userExistsByEmail_whenEmailDoesNotExist_shouldReturnFalse() {
        String email = "test@example.com";

        when(userRepository.existsByEmail(email)).thenReturn(false);

        assertFalse(userQueryService.userExistsByEmail(email));
        verify(userRepository).existsByEmail(email);
    }
}