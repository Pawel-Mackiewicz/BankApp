package info.mackiewicz.bankapp.presentation.auth.dto;

import java.time.LocalDate;

import info.mackiewicz.bankapp.presentation.auth.validation.Password;
 import info.mackiewicz.bankapp.shared.validation.ValidationConstants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO for user registration requests.
 * Contains all necessary fields for creating a new user.
 */
public record UserRegistrationRequest(
    @NotBlank
    @Size(min = 2, max = 50)
    String firstname,

    @NotBlank
    @Size(min = 2, max = 50)
    String lastname,

    @NotBlank
    @Size(min = 11, max = 11)
    String pesel,

    @NotBlank
    @Email
    String email,

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = ValidationConstants.PHONE_NUMBER_PATTERN, 
            message = "Invalid phone number format. Use +48XXXXXXXXX, 0XXXXXXXXX or XXXXXXXXX format")
    String phoneNumber,

    @NotNull
    @Past
    LocalDate dateOfBirth,

    @Password
    String password
) {}