package info.mackiewicz.bankapp.presentation.auth.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import info.mackiewicz.bankapp.presentation.auth.validation.PasswordMatches;
import info.mackiewicz.bankapp.shared.validation.ValidationConstants;
import info.mackiewicz.bankapp.shared.web.dto.interfaces.PasswordConfirmation;
import info.mackiewicz.bankapp.user.validation.Adult;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@PasswordMatches
public class UserRegistrationDto implements PasswordConfirmation {

    @NotBlank(message = "Firstname is required")
    @Pattern(regexp = "^[A-Za-zĄĆĘŁŃÓŚŹŻąćęłńóśźż]+$", message = "Firstname can only contain letters")
    private String firstname;

    @NotBlank(message = "Lastname is required")
    private String lastname;

    @NotNull(message = "Date of Birth is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Adult(message = "You must be at least 18 years old")
    private LocalDate dateOfBirth;

    @NotBlank(message = "PESEL is required")
    @Pattern(regexp = "\\d{11}", message = "PESEL must be exactly 11 digits")
    private String pesel;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    public String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^(\\+48\\d{9}|0\\d{9}|[1-9]\\d{8})$", 
            message = "Invalid phone number format. Use +48XXXXXXXXX, 0XXXXXXXXX or XXXXXXXXX format")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    @Size(min = ValidationConstants.PASSWORD_MIN_LENGTH, message = "Password must be at least 8 characters long")
    @Pattern(
        regexp = ValidationConstants.PASSWORD_PATTERN, 
        message = ValidationConstants.PASSWORD_DESCRIPTION)
    private String password;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;
}