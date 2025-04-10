package info.mackiewicz.bankapp.user.service.util;

import info.mackiewicz.bankapp.user.exception.DuplicatedUserException;
import info.mackiewicz.bankapp.user.exception.InvalidAgeException;
import info.mackiewicz.bankapp.user.exception.UserNotFoundException;
import info.mackiewicz.bankapp.user.exception.UserValidationException;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.user.model.vo.Pesel;
import info.mackiewicz.bankapp.user.model.vo.PhoneNumber;
import info.mackiewicz.bankapp.user.service.crud.UserQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserValidationServiceTest {

    @Mock
    private UserQueryService userQueryService;

    @InjectMocks
    private UserValidationService userValidationService;

    @Nested
    @DisplayName("Name Validation Tests")
    class NameValidationTests {

        @Test
        @DisplayName("Should pass for valid name with Polish letters")
        void shouldPassForValidNameWithPolishLetters() {
            User user = User.builder()
                    .withFirstname("Żółć")
                    .withLastname("Ćma")
                    .withPesel("12345678901")
                    .withDateOfBirth(LocalDate.now().minusYears(30))
                    .withEmail("test@example.com")
                    .withPhoneNumber("123456789")
                    .withPassword("password")
                    .build();

            assertThatCode(() -> userValidationService.validateNewUser(user))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should throw exception for invalid name with numbers")
        void shouldThrowExceptionForInvalidName() {
            User user = User.builder()
                    .withFirstname("John123")
                    .withLastname("Doe")
                    .withPesel("12345678901")
                    .withDateOfBirth(LocalDate.now().minusYears(30))
                    .withEmail("test@example.com")
                    .withPhoneNumber("123456789")
                    .withPassword("password")
                    .build();

            assertThatThrownBy(() -> userValidationService.validateNewUser(user))
                    .isInstanceOf(UserValidationException.class)
                    .hasMessageContaining("Invalid name");
        }
    }

    @Nested
    @DisplayName("Username Validation Tests")
    class UsernameValidationTests {

        @Test
        @DisplayName("Should pass for unique username")
        void shouldPassForUniqueUsername() {
            when(userQueryService.userExistsByUsername(any())).thenReturn(false);
            
            assertThatCode(() -> userValidationService.validateUsernameUnique("newuser"))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should throw exception for duplicate username")
        void shouldThrowExceptionForDuplicateUsername() {
            when(userQueryService.userExistsByUsername("existinguser")).thenReturn(true);

            assertThatThrownBy(() -> userValidationService.validateUsernameUnique("existinguser"))
                    .isInstanceOf(DuplicatedUserException.class)
                    .hasMessageContaining("Username already in use");
        }
    }

    @Nested
    @DisplayName("Email Validation Tests")
    class EmailValidationTests {

        @Test
        @DisplayName("Should pass for unique email")
        void shouldPassForUniqueEmail() {
            EmailAddress email = new EmailAddress("unique@example.com");
            when(userQueryService.userExistsByEmail(email)).thenReturn(false);

            assertThatCode(() -> userValidationService.validateEmailUnique(email))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should throw exception for duplicate email")
        void shouldThrowExceptionForDuplicateEmail() {
            EmailAddress email = new EmailAddress("existing@example.com");
            when(userQueryService.userExistsByEmail(email)).thenReturn(true);

            assertThatThrownBy(() -> userValidationService.validateEmailUnique(email))
                    .isInstanceOf(DuplicatedUserException.class)
                    .hasMessageContaining("Email already in use");
        }
    }

    @Nested
    @DisplayName("PESEL Validation Tests")
    class PeselValidationTests {

        @Test
        @DisplayName("Should pass for unique PESEL")
        void shouldPassForUniquePesel() {
            Pesel pesel = new Pesel("12345678901");
            when(userQueryService.userExistsByPesel(pesel)).thenReturn(false);

            assertThatCode(() -> userValidationService.validatePeselUnique(pesel))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should throw exception for duplicate PESEL")
        void shouldThrowExceptionForDuplicatePesel() {
            Pesel pesel = new Pesel("12345678901");
            when(userQueryService.userExistsByPesel(pesel)).thenReturn(true);

            assertThatThrownBy(() -> userValidationService.validatePeselUnique(pesel))
                    .isInstanceOf(DuplicatedUserException.class)
                    .hasMessageContaining("PESEL already in use");
        }
    }

    @Nested
    @DisplayName("Phone Number Validation Tests")
    class PhoneNumberValidationTests {

        @Test
        @DisplayName("Should pass for unique phone number")
        void shouldPassForUniquePhoneNumber() {
            PhoneNumber phoneNumber = new PhoneNumber("123456789");
            when(userQueryService.userExistsByPhoneNumber(phoneNumber)).thenReturn(false);

            User user = User.builder()
                    .withFirstname("John")
                    .withLastname("Doe")
                    .withPesel("12345678901")
                    .withDateOfBirth(LocalDate.now().minusYears(30))
                    .withEmail("test@example.com")
                    .withPhoneNumber(phoneNumber)
                    .withPassword("password")
                    .build();

            assertThatCode(() -> userValidationService.validateNewUser(user))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should throw exception for duplicate phone number")
        void shouldThrowExceptionForDuplicatePhoneNumber() {
            PhoneNumber phoneNumber = new PhoneNumber("123456789");
            when(userQueryService.userExistsByPhoneNumber(phoneNumber)).thenReturn(true);

            User user = User.builder()
                    .withFirstname("John")
                    .withLastname("Doe")
                    .withPesel("12345678901")
                    .withDateOfBirth(LocalDate.now().minusYears(30))
                    .withEmail("test@example.com")
                    .withPhoneNumber(phoneNumber)
                    .withPassword("password")
                    .build();

            assertThatThrownBy(() -> userValidationService.validateNewUser(user))
                    .isInstanceOf(DuplicatedUserException.class)
                    .hasMessageContaining("Phone number already in use");
        }
    }

    @Nested
    @DisplayName("User Existence Validation Tests")
    class UserExistenceValidationTests {

        @Test
        @DisplayName("Should pass for existing user")
        void shouldPassForExistingUser() {
            when(userQueryService.userExistsById(1)).thenReturn(true);

            assertThatCode(() -> userValidationService.validateUserExists(1))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should throw exception for non-existing user")
        void shouldThrowExceptionForNonExistingUser() {
            when(userQueryService.userExistsById(999)).thenReturn(false);

            assertThatThrownBy(() -> userValidationService.validateUserExists(999))
                    .isInstanceOf(UserNotFoundException.class)
                    .hasMessageContaining("User with ID 999 does not exist");
        }
    }

    @Nested
    @DisplayName("Age Validation Tests")
    class AgeValidationTests {

        @Test
        @DisplayName("Should pass validation for 18 years old user")
        void shouldPassValidationFor18YearsOld() {
            LocalDate eighteenYearsOld = LocalDate.now().minusYears(18);

            assertThatCode(() -> userValidationService.validateAge(eighteenYearsOld))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should pass validation for 120 years old user")
        void shouldPassValidationFor120YearsOld() {
            LocalDate maxAge = LocalDate.now().minusYears(120);

            assertThatCode(() -> userValidationService.validateAge(maxAge))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should throw InvalidAgeException for null birth date")
        void shouldThrowExceptionForNullBirthDate() {
            assertThatThrownBy(() -> userValidationService.validateAge(null))
                    .isInstanceOf(InvalidAgeException.class)
                    .hasMessage("Birth date is required");
        }

        @ParameterizedTest(name = "Age {0} should be invalid")
        @ValueSource(ints = {1, 10, 17})
        @DisplayName("Should throw InvalidAgeException for users under 18")
        void shouldThrowExceptionForUsersUnder18(int age) {
            LocalDate underage = LocalDate.now().minusYears(age);

            assertThatThrownBy(() -> userValidationService.validateAge(underage))
                    .isInstanceOf(InvalidAgeException.class)
                    .hasMessage("User must be at least 18 years old");
        }

        @ParameterizedTest(name = "Age {0} should be invalid")
        @ValueSource(ints = {121, 150, 200})
        @DisplayName("Should throw InvalidAgeException for users over 120")
        void shouldThrowExceptionForUsersOver120(int age) {
            LocalDate overMaxAge = LocalDate.now().minusYears(age);

            assertThatThrownBy(() -> userValidationService.validateAge(overMaxAge))
                    .isInstanceOf(InvalidAgeException.class)
                    .hasMessage("User cannot be older than 120 years old");
        }
    }
}