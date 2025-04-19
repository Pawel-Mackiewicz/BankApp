package info.mackiewicz.bankapp.system.registration.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import info.mackiewicz.bankapp.presentation.auth.validation.Password;
import info.mackiewicz.bankapp.presentation.auth.validation.PasswordMatches;
import info.mackiewicz.bankapp.shared.util.Util;
import info.mackiewicz.bankapp.shared.validation.ValidationConstants;
import info.mackiewicz.bankapp.shared.web.dto.interfaces.PasswordConfirmation;
import info.mackiewicz.bankapp.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.user.model.vo.Pesel;
import info.mackiewicz.bankapp.user.model.vo.PhoneNumber;
import info.mackiewicz.bankapp.user.validation.AgeRange;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * User registration DTO.
 * <p>
 * Contains personal data (first name, last name, PESEL, date of birth),
 * contact details (email, phone) and password with confirmation.
 * <p>
 * Includes field validation and domain object conversion.
 */

@Getter
@Setter
@Schema(description = "Registration request DTO")
@PasswordMatches
public class RegistrationRequest implements PasswordConfirmation {

    @Schema(description = "Request ID", hidden = true, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private final long requestId = Util.getTimeFromStartOfTheMonth();

    @NotBlank(message = "Firstname is required")
    @Pattern(regexp = ValidationConstants.NAME_PATTERN, message = "Firstname can only contain letters")
    @Schema(description = "Firstname can only contain letters", example = "John")
    private String firstname;

    @NotBlank(message = "Lastname is required")
    @Pattern(regexp = ValidationConstants.NAME_PATTERN, message = "Lastname can only contain letters")
    @Schema(description = "Lastname can only contain letters", example = "Smith")
    private String lastname;

    @NotNull(message = "Date of Birth is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @AgeRange
    @Schema(description = "Date of birth in the format yyyy-MM-dd." +
            " User must be at least 18 years old." +
            " User cannot be older than 120 years old",
    example = "1997-07-07")
    private LocalDate dateOfBirth;

    @NotBlank(message = "PESEL is required")
    @Pattern(regexp = "\\d{11}", message = "PESEL must be exactly 11 digits")
    @Schema(description = "PESEL must be exactly 11 digits. PESEL must be unique.", example = "12345678901")
    @JsonProperty("pesel")
    private String pesel;

    @NotBlank(message = "Email is required")
    @Pattern(regexp = ValidationConstants.EMAIL_PATTERN, message = "Invalid email format")
    @Schema(description = "Email must be a valid email address", example = "john.smith@example.com")
    @JsonProperty("email")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = ValidationConstants.PHONE_NUMBER_PATTERN, message = "Invalid phone number format. Use +48XXXXXXXXX, 0XXXXXXXXX or XXXXXXXXX format")
    @Schema(description = "Phone number must be in the format +48XXXXXXXXX, 0XXXXXXXXX or XXXXXXXXX", example = "+48798754321")
    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @Password
    @Schema(description = "Password must be at least 8 characters long, contain at least one digit, " +
            "one lowercase letter, one uppercase letter, and one special character from the set: @$!%*?&", minLength = 8, pattern = ValidationConstants.PASSWORD_PATTERN, example = "StrongP@ss123")
    private String password;

    @NotBlank(message = "Password confirmation is required")
    @Schema(description = "Password confirmation must match the password", example = "StrongP@ss123")
    private String confirmPassword;

    @JsonIgnore
    public Pesel getPesel() {
        return new Pesel(pesel);
    }

    @JsonIgnore
    public EmailAddress getEmail() {
        return new EmailAddress(email);
    }

    @JsonIgnore
    public PhoneNumber getPhoneNumber() {
        return new PhoneNumber(phoneNumber);
    }

}
