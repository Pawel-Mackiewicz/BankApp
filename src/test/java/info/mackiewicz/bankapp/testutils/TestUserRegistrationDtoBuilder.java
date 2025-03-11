package info.mackiewicz.bankapp.testutils;

import info.mackiewicz.bankapp.presentation.auth.dto.UserRegistrationDto;
import java.time.LocalDate;

public class TestUserRegistrationDtoBuilder {
    
    public static UserRegistrationDto createValid() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setFirstname("Jan");
        dto.setLastname("Kowalski");
        dto.setDateOfBirth(LocalDate.of(1990, 1, 1));
        dto.setPESEL("90010112345");
        dto.setEmail("jan.kowalski@example.com");
        dto.setPhoneNumber("+48123456789");
        dto.setPassword("Test123!@#");
        dto.setConfirmPassword("Test123!@#");
        return dto;
    }
    
    public static UserRegistrationDto createValidWithEmail(String email) {
        UserRegistrationDto dto = createValid();
        dto.setEmail(email);
        return dto;
    }
    
    public static UserRegistrationDto createWithInvalidFirstName() {
        UserRegistrationDto dto = createValid();
        dto.setFirstname("Jan123");
        return dto;
    }
    
    public static UserRegistrationDto createWithInvalidPesel() {
        UserRegistrationDto dto = createValid();
        dto.setPESEL("1234"); // Za krótki PESEL
        return dto;
    }
    
    public static UserRegistrationDto createWithInvalidPhoneNumber() {
        UserRegistrationDto dto = createValid();
        dto.setPhoneNumber("123"); // Nieprawidłowy format
        return dto;
    }
    
    public static UserRegistrationDto createWithPasswordMismatch() {
        UserRegistrationDto dto = createValid();
        dto.setConfirmPassword("DifferentPassword123!@#");
        return dto;
    }
    
    public static UserRegistrationDto createWithInvalidAge() {
        UserRegistrationDto dto = createValid();
        dto.setDateOfBirth(LocalDate.now().minusYears(17)); // Osoba niepełnoletnia
        return dto;
    }
}