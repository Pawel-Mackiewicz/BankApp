package info.mackiewicz.bankapp.user.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import info.mackiewicz.bankapp.user.exception.UserValidationException;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.vo.Email;
import info.mackiewicz.bankapp.user.model.vo.Pesel;

@DisplayName("UserValidationService Tests")
class UserValidationServiceTest {

    @Mock
    private UserQueryService userQueryService;

    @InjectMocks
    private UserValidationService userValidationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("validateUsernameUnique tests")
    class ValidateUsernameUniqueTests {

        @Test
        @DisplayName("Should throw exception when username exists")
        void validateUsernameUnique_whenUsernameExists_shouldThrowException() {
            // given
            String username = "existingUsername";
            when(userQueryService.userExistsByUsername(username)).thenReturn(true);

            // when & then
            assertThrows(UserValidationException.class,
                    () -> userValidationService.validateUsernameUnique(username));

            verify(userQueryService).userExistsByUsername(username);
        }

        @Test
        @DisplayName("Should not throw exception when username does not exist")
        void validateUsernameUnique_whenUsernameDoesNotExist_shouldNotThrowException() {
            // given
            String username = "newUsername";
            when(userQueryService.userExistsByUsername(username)).thenReturn(false);

            // when
            userValidationService.validateUsernameUnique(username);

            // then
            verify(userQueryService).userExistsByUsername(username);
        }
    }

    @Nested
    @DisplayName("validateEmailUnique tests")
    class ValidateEmailUniqueTests {

        @Test
        @DisplayName("Should throw exception when email exists")
        void validateEmailUnique_whenEmailExists_shouldThrowException() {
            // given
            Email email = new Email("existing@example.com");
            when(userQueryService.userExistsByEmail(email)).thenReturn(true);

            // when & then
            assertThrows(UserValidationException.class,
                    () -> userValidationService.validateEmailUnique(email));

            verify(userQueryService).userExistsByEmail(email);
        }

        @Test
        @DisplayName("Should not throw exception when email does not exist")
        void validateEmailUnique_whenEmailDoesNotExist_shouldNotThrowException() {
            // given
            Email email = new Email("new@example.com");
            when(userQueryService.userExistsByEmail(email)).thenReturn(false);

            // when
            userValidationService.validateEmailUnique(email);

            // then
            verify(userQueryService).userExistsByEmail(email);
        }
    }

    @Nested
    @DisplayName("validatePeselUnique tests")
    class ValidatePeselUniqueTests {

        @Test
        @DisplayName("Should throw exception when PESEL exists")
        void validatePeselUnique_whenPeselExists_shouldThrowException() {
            // given
            Pesel pesel = new Pesel("12345678901");
            when(userQueryService.userExistsByPesel(pesel)).thenReturn(true);

            // when & then
            assertThrows(UserValidationException.class,
                    () -> userValidationService.validatePeselUnique(pesel));

            verify(userQueryService).userExistsByPesel(pesel);
        }

        @Test
        @DisplayName("Should not throw exception when PESEL does not exist")
        void validatePeselUnique_whenPeselDoesNotExist_shouldNotThrowException() {
            // given
            Pesel pesel = new Pesel("12345678901");
            when(userQueryService.userExistsByPesel(pesel)).thenReturn(false);

            // when
            userValidationService.validatePeselUnique(pesel);

            // then
            verify(userQueryService).userExistsByPesel(pesel);
        }
    }

    @Nested
    @DisplayName("validateNewUser tests")
    class ValidateNewUserTests {

        @Test
        @DisplayName("Should not throw exception when all fields are unique")
        void validateNewUser_whenAllFieldsAreUnique_shouldNotThrowException() {
            // given
            User user = new User();
            user.setUsername("newUsername");
            user.setEmail(new Email("new@example.com"));
            user.setPesel(new Pesel("12345678901"));

            when(userQueryService.userExistsByUsername(user.getUsername())).thenReturn(false);
            when(userQueryService.userExistsByEmail(user.getEmail())).thenReturn(false);
            when(userQueryService.userExistsByPesel(user.getPesel())).thenReturn(false);

            // when
            userValidationService.validateNewUser(user);

            // then
            verify(userQueryService).userExistsByUsername(user.getUsername());
            verify(userQueryService).userExistsByEmail(user.getEmail());
            verify(userQueryService).userExistsByPesel(user.getPesel());
        }

        @Test
        @DisplayName("Should throw exception when username exists")
        void validateNewUser_whenUsernameExists_shouldThrowException() {
            // given
            User user = new User();
            user.setUsername("existingUsername");
            user.setEmail(new Email("new@example.com"));
            user.setPesel(new Pesel("12345678901"));

            when(userQueryService.userExistsByUsername(user.getUsername())).thenReturn(true);

            // when & then
            assertThrows(UserValidationException.class,
                    () -> userValidationService.validateNewUser(user));

            verify(userQueryService).userExistsByUsername(user.getUsername());
            verify(userQueryService, never()).userExistsByEmail(any(Email.class));
            verify(userQueryService, never()).userExistsByPesel(any(Pesel.class));
        }

        @Test
        @DisplayName("Should throw exception when email exists")
        void validateNewUser_whenEmailExists_shouldThrowException() {
            // given
            User user = new User();
            user.setUsername("newUsername");
            user.setEmail(new Email("existing@example.com"));
            user.setPesel(new Pesel("12345678901"));

            when(userQueryService.userExistsByUsername(user.getUsername())).thenReturn(false);
            when(userQueryService.userExistsByEmail(user.getEmail())).thenReturn(true);

            // when & then
            assertThrows(UserValidationException.class,
                    () -> userValidationService.validateNewUser(user));

            verify(userQueryService).userExistsByUsername(user.getUsername());
            verify(userQueryService).userExistsByEmail(user.getEmail());
            verify(userQueryService, never()).userExistsByPesel(any(Pesel.class));
        }

        @Test
        @DisplayName("Should throw exception when PESEL exists")
        void validateNewUser_whenPeselExists_shouldThrowException() {
            // given
            User user = new User();
            user.setUsername("newUsername");
            user.setEmail(new Email("new@example.com"));
            user.setPesel(new Pesel("12345678901"));

            when(userQueryService.userExistsByUsername(user.getUsername())).thenReturn(false);
            when(userQueryService.userExistsByEmail(user.getEmail())).thenReturn(false);
            when(userQueryService.userExistsByPesel(user.getPesel())).thenReturn(true);

            // when & then
            assertThrows(UserValidationException.class,
                    () -> userValidationService.validateNewUser(user));

            verify(userQueryService).userExistsByUsername(user.getUsername());
            verify(userQueryService).userExistsByEmail(user.getEmail());
            verify(userQueryService).userExistsByPesel(user.getPesel());
        }
    }
}