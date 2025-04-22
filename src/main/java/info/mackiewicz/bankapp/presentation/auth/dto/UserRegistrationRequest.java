package info.mackiewicz.bankapp.presentation.auth.dto;

import info.mackiewicz.bankapp.shared.annotations.Password;
import info.mackiewicz.bankapp.shared.annotations.PasswordMatches;
import info.mackiewicz.bankapp.shared.annotations.ValidEmail;
import info.mackiewicz.bankapp.shared.validation.ValidationConstants;
import info.mackiewicz.bankapp.shared.web.dto.interfaces.PasswordConfirmation;
import info.mackiewicz.bankapp.user.validation.AgeRange;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@PasswordMatches
public class UserRegistrationRequest implements PasswordConfirmation {

    @NotBlank(message = "Firstname is required")
    @Pattern(regexp = ValidationConstants.NAME_PATTERN, message = "Firstname can only contain letters")
    private String firstname;

    @NotBlank(message = "Lastname is required")
    @Pattern(regexp = ValidationConstants.NAME_PATTERN, message = "Lastname can only contain letters")
    private String lastname;

    @NotNull(message = "Date of Birth is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @AgeRange
    @Schema(description = "Date of birth in the format yyyy-MM-dd." +
            " User must be at least 18 years old." +
            " User cannot be older than 120 years old")
    private LocalDate dateOfBirth;

    @NotBlank(message = "PESEL is required")
    @Pattern(regexp = "\\d{11}", message = "PESEL must be exactly 11 digits")
    private String pesel;

    @ValidEmail(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = ValidationConstants.PHONE_NUMBER_PATTERN, 
            message = "Invalid phone number format. Use +48XXXXXXXXX, 0XXXXXXXXX or XXXXXXXXX format")
    private String phoneNumber;

    @Schema(
    description = "Password must be at least 8 characters long, contain at least one digit, " +
                  "one lowercase letter, one uppercase letter, and one special character from the set: @$!%*?&",
    minLength = 8,
    pattern = ValidationConstants.PASSWORD_PATTERN,
    example = "StrongP@ss123"
                  )
    @Password
    private String password;

    @NotBlank(message = "Password confirmation is required")
    @Schema(description = "Password confirmation must match the password",
            example = "StrongP@ss123")
    private String confirmPassword;
}