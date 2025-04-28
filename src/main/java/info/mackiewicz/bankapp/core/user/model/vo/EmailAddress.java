package info.mackiewicz.bankapp.core.user.model.vo;

import info.mackiewicz.bankapp.core.user.exception.InvalidEmailFormatException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

/**
 * Value Object representing an email address.
 * Ensures proper email format validation.
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED) // For JPA
public class EmailAddress {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private String value;

    public EmailAddress(String email) {
        validate(email);
        this.value = email.toLowerCase();
    }

    private void validate(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidEmailFormatException("Email cannot be empty");
        }

        if (email.length() > 255) {
            throw new InvalidEmailFormatException("Email is too long (max 255 characters)");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidEmailFormatException("Invalid email format");
        }

        // Additional validations could be added here:
        // - Check for disposable email domains
        // - Validate domain has valid MX record
        // - Check for common typos
    }

    @Override
    public String toString() {
        return value;
    }
}