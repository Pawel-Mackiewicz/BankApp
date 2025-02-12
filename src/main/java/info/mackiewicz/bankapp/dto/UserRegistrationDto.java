package info.mackiewicz.bankapp.dto;

import info.mackiewicz.bankapp.validation.Adult;
import info.mackiewicz.bankapp.validation.PasswordMatches;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@PasswordMatches
public class UserRegistrationDto {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Lastname is required")
    private String lastname;

    @NotNull(message = "Date of Birth is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Adult(message = "You must be at least 18 years old")
    private LocalDate dateOfBirth;

    @NotBlank(message = "PESEL is required")
    @Pattern(regexp = "\\d{11}", message = "PESEL must be exactly 11 digits")
    private String PESEL;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", 
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;
}