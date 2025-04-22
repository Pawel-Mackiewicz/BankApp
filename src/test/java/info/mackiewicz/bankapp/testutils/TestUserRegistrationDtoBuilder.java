package info.mackiewicz.bankapp.testutils;

import info.mackiewicz.bankapp.presentation.auth.dto.UserRegistrationRequest;

import java.time.LocalDate;

public class TestUserRegistrationDtoBuilder {

    public static UserRegistrationRequest createValid() {
        UserRegistrationRequest dto = new UserRegistrationRequest();
        dto.setFirstname("Jan");
        dto.setLastname("Kowalski");
        dto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        dto.setPesel("90010112345");
        dto.setEmail("jan.kowalski@example.com");
        dto.setPhoneNumber("123456789");
        dto.setPassword("Test123!@#");
        dto.setConfirmPassword("Test123!@#");
        return dto;
    }

    public static UserRegistrationRequest createValidWithEmail(String email) {
        UserRegistrationRequest dto = createValid();
        dto.setEmail(email);
        return dto;
    }

    public static UserRegistrationRequest createWithInvalidFirstName() {
        UserRegistrationRequest dto = createValid();
        dto.setFirstname("Jan123");
        return dto;
    }

    public static UserRegistrationRequest createWithInvalidPesel() {
        UserRegistrationRequest dto = createValid();
        dto.setPesel("1234"); // Too short PESEL
        return dto;
    }

    public static UserRegistrationRequest createWithInvalidPhoneNumber() {
        UserRegistrationRequest dto = createValid();
        dto.setPhoneNumber("123"); // Invalid phone number
        return dto;
    }

    public static UserRegistrationRequest createWithPasswordMismatch() {
        UserRegistrationRequest dto = createValid();
        dto.setConfirmPassword("DifferentPassword123!@#");
        return dto;
    }

    public static UserRegistrationRequest createWithInvalidAge() {
        UserRegistrationRequest dto = createValid();
        dto.setDateOfBirth(LocalDate.now().minusYears(17)); // Minor user
        return dto;
    }
}