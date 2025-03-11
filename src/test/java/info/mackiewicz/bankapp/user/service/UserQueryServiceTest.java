package info.mackiewicz.bankapp.user.service;

import info.mackiewicz.bankapp.user.exception.UserNotFoundException;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.vo.Email;
import info.mackiewicz.bankapp.user.model.vo.Pesel;
import info.mackiewicz.bankapp.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("UserQueryService Tests")
class UserQueryServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserQueryService userQueryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("getUserById tests")
    class GetUserByIdTests {
        
        @Test
        @DisplayName("Should return user when user exists")
        void getUserById_whenUserExists_shouldReturnUser() {
            // given
            Integer userId = 1;
            User expectedUser = new User();
            expectedUser.setId(userId);

            when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

            // when
            User actualUser = userQueryService.getUserById(userId);

            // then
            assertNotNull(actualUser);
            assertEquals(userId, actualUser.getId());
            verify(userRepository).findById(userId);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user does not exist")
        void getUserById_whenUserDoesNotExist_shouldThrowException() {
            // given
            Integer userId = 1;
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // when & then
            UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userQueryService.getUserById(userId)
            );
            assertEquals("User not found with id: " + userId, exception.getMessage());
            verify(userRepository).findById(userId);
        }
    }

    @Nested
    @DisplayName("getUserByIdWithPessimisticLock tests")
    class GetUserByIdWithPessimisticLockTests {

        @Test
        @DisplayName("Should return user when user exists")
        void getUserByIdWithPessimisticLock_whenUserExists_shouldReturnUser() {
            // given
            Integer userId = 1;
            User expectedUser = new User();
            expectedUser.setId(userId);

            when(userRepository.findByIdWithPessimisticLock(userId)).thenReturn(Optional.of(expectedUser));

            // when
            User actualUser = userQueryService.getUserByIdWithPessimisticLock(userId);

            // then
            assertNotNull(actualUser);
            assertEquals(userId, actualUser.getId());
            verify(userRepository).findByIdWithPessimisticLock(userId);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user does not exist")
        void getUserByIdWithPessimisticLock_whenUserDoesNotExist_shouldThrowException() {
            // given
            Integer userId = 1;
            when(userRepository.findByIdWithPessimisticLock(userId)).thenReturn(Optional.empty());

            // when & then
            UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userQueryService.getUserByIdWithPessimisticLock(userId)
            );
            assertEquals("User not found with id: " + userId, exception.getMessage());
            verify(userRepository).findByIdWithPessimisticLock(userId);
        }
    }

    @Nested
    @DisplayName("getUserByUsername tests")
    class GetUserByUsernameTests {

        @Test
        @DisplayName("Should return user when username exists")
        void getUserByUsername_whenUserExists_shouldReturnUser() {
            // given
            String username = "jkowalski";
            User expectedUser = new User();
            expectedUser.setUsername(username);

            when(userRepository.findByUsername(username)).thenReturn(Optional.of(expectedUser));

            // when
            User actualUser = userQueryService.getUserByUsername(username);

            // then
            assertNotNull(actualUser);
            assertEquals(username, actualUser.getUsername());
            verify(userRepository).findByUsername(username);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when username does not exist")
        void getUserByUsername_whenUserDoesNotExist_shouldThrowException() {
            // given
            String username = "nonexistent";
            when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

            // when & then
            UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userQueryService.getUserByUsername(username)
            );
            assertEquals("User not found with username: " + username, exception.getMessage());
            verify(userRepository).findByUsername(username);
        }
    }

    @Nested
    @DisplayName("getUserByEmail tests")
    class GetUserByEmailTests {

        @Test
        @DisplayName("Should return user when email exists (String parameter)")
        void getUserByEmail_withStringParameter_whenUserExists_shouldReturnUser() {
            // given
            String emailStr = "jan.kowalski@example.com";
            Email email = new Email(emailStr);
            User expectedUser = new User();
            expectedUser.setEmail(email);

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(expectedUser));

            // when
            User actualUser = userQueryService.getUserByEmail(emailStr);

            // then
            assertNotNull(actualUser);
            assertEquals(email, actualUser.getEmail());
            verify(userRepository).findByEmail(email);
        }

        @Test
        @DisplayName("Should return user when email exists (Email parameter)")
        void getUserByEmail_withEmailParameter_whenUserExists_shouldReturnUser() {
            // given
            Email email = new Email("jan.kowalski@example.com");
            User expectedUser = new User();
            expectedUser.setEmail(email);

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(expectedUser));

            // when
            User actualUser = userQueryService.getUserByEmail(email);

            // then
            assertNotNull(actualUser);
            assertEquals(email, actualUser.getEmail());
            verify(userRepository).findByEmail(email);
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when email does not exist")
        void getUserByEmail_whenUserDoesNotExist_shouldThrowException() {
            // given
            Email email = new Email("nonexistent@example.com");
            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            // when & then
            UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userQueryService.getUserByEmail(email)
            );
            assertEquals("User not found with email: " + email, exception.getMessage());
            verify(userRepository).findByEmail(email);
        }
    }

    @Nested
    @DisplayName("getAllUsers tests")
    class GetAllUsersTests {

        @Test
        @DisplayName("Should return all users")
        void getAllUsers_shouldReturnAllUsers() {
            // given
            User user1 = new User();
            user1.setUsername("user1");
            User user2 = new User();
            user2.setUsername("user2");
            List<User> expectedUsers = List.of(user1, user2);

            when(userRepository.findAll()).thenReturn(expectedUsers);

            // when
            List<User> actualUsers = userQueryService.getAllUsers();

            // then
            assertNotNull(actualUsers);
            assertEquals(expectedUsers.size(), actualUsers.size());
            verify(userRepository).findAll();
        }
    }

    @Nested
    @DisplayName("checkUsernameExists tests")
    class CheckUsernameExistsTests {

        @Test
        @DisplayName("Should return true when username exists")
        void checkUsernameExists_whenUsernameExists_shouldReturnTrue() {
            // given
            String username = "jkowalski";
            when(userRepository.existsByUsername(username)).thenReturn(true);

            // when
            boolean exists = userQueryService.checkUsernameExists(username);

            // then
            assertTrue(exists);
            verify(userRepository).existsByUsername(username);
        }

        @Test
        @DisplayName("Should return false when username does not exist")
        void checkUsernameExists_whenUsernameDoesNotExist_shouldReturnFalse() {
            // given
            String username = "nonexistent";
            when(userRepository.existsByUsername(username)).thenReturn(false);

            // when
            boolean exists = userQueryService.checkUsernameExists(username);

            // then
            assertFalse(exists);
            verify(userRepository).existsByUsername(username);
        }
    }

    @Nested
    @DisplayName("userExistsByEmail tests")
    class UserExistsByEmailTests {

        @Test
        @DisplayName("Should return true when email exists (String parameter)")
        void userExistsByEmail_withStringParameter_whenEmailExists_shouldReturnTrue() {
            // given
            String emailStr = "jan.kowalski@example.com";
            Email email = new Email(emailStr);
            when(userRepository.existsByEmail(email)).thenReturn(true);

            // when
            boolean exists = userQueryService.userExistsByEmail(emailStr);

            // then
            assertTrue(exists);
            verify(userRepository).existsByEmail(email);
        }

        @Test
        @DisplayName("Should return true when email exists (Email parameter)")
        void userExistsByEmail_withEmailParameter_whenEmailExists_shouldReturnTrue() {
            // given
            Email email = new Email("jan.kowalski@example.com");
            when(userRepository.existsByEmail(email)).thenReturn(true);

            // when
            boolean exists = userQueryService.userExistsByEmail(email);

            // then
            assertTrue(exists);
            verify(userRepository).existsByEmail(email);
        }

        @Test
        @DisplayName("Should return false when email does not exist")
        void userExistsByEmail_whenEmailDoesNotExist_shouldReturnFalse() {
            // given
            Email email = new Email("nonexistent@example.com");
            when(userRepository.existsByEmail(email)).thenReturn(false);

            // when
            boolean exists = userQueryService.userExistsByEmail(email);

            // then
            assertFalse(exists);
            verify(userRepository).existsByEmail(email);
        }
    }

    @Nested
    @DisplayName("userExistsByPesel tests")
    class UserExistsByPeselTests {

        @Test
        @DisplayName("Should return true when PESEL exists (String parameter)")
        void userExistsByPesel_withStringParameter_whenPeselExists_shouldReturnTrue() {
            // given
            String peselStr = "12345678901";
            Pesel pesel = new Pesel(peselStr);
            when(userRepository.existsByPesel(pesel)).thenReturn(true);

            // when
            boolean exists = userQueryService.userExistsByPesel(peselStr);

            // then
            assertTrue(exists);
            verify(userRepository).existsByPesel(pesel);
        }

        @Test
        @DisplayName("Should return true when PESEL exists (Pesel parameter)")
        void userExistsByPesel_withPeselParameter_whenPeselExists_shouldReturnTrue() {
            // given
            Pesel pesel = new Pesel("12345678901");
            when(userRepository.existsByPesel(pesel)).thenReturn(true);

            // when
            boolean exists = userQueryService.userExistsByPesel(pesel);

            // then
            assertTrue(exists);
            verify(userRepository).existsByPesel(pesel);
        }

        @Test
        @DisplayName("Should return false when PESEL does not exist")
        void userExistsByPesel_whenPeselDoesNotExist_shouldReturnFalse() {
            // given
            Pesel pesel = new Pesel("12345678901");
            when(userRepository.existsByPesel(pesel)).thenReturn(false);

            // when
            boolean exists = userQueryService.userExistsByPesel(pesel);

            // then
            assertFalse(exists);
            verify(userRepository).existsByPesel(pesel);
        }
    }
}