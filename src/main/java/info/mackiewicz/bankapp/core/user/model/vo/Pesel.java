package info.mackiewicz.bankapp.core.user.model.vo;

import info.mackiewicz.bankapp.core.user.exception.InvalidPeselFormatException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Value Object representing a PESEL (Polish National Identity Number).
 * Ensures validation and proper formatting of PESEL numbers.
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED) // For JPA
public class Pesel {
    private String value;

    private static final int PESEL_LENGTH = 11;
    private static final int[] WEIGHTS = {1, 3, 7, 9, 1, 3, 7, 9, 1, 3};

    public Pesel(String pesel) {
        validate(pesel);
        this.value = pesel;
    }

    private void validate(String pesel) {
        if (pesel == null || pesel.length() != PESEL_LENGTH) {
            throw new InvalidPeselFormatException("PESEL must be exactly 11 digits");
        }

        if (!pesel.matches("\\d{11}")) {
            throw new InvalidPeselFormatException("PESEL must contain only digits");
        }

       // validateChecksum(pesel); // It's not required for portfolio project
    }

    @SuppressWarnings("unused")
    private void validateChecksum(String pesel) {
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += Integer.parseInt(String.valueOf(pesel.charAt(i))) * WEIGHTS[i];
        }
        
        int checksum = (10 - (sum % 10)) % 10;
        int lastDigit = Integer.parseInt(String.valueOf(pesel.charAt(10)));

        if (checksum != lastDigit) {
            throw new InvalidPeselFormatException("Invalid PESEL checksum");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}