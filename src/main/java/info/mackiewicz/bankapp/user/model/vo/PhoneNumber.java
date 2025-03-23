package info.mackiewicz.bankapp.user.model.vo;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Value Object representing a phone number.
 * Ensures proper phone number format validation for Polish numbers.
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED) // For JPA
public class PhoneNumber {
    private String value;

    public PhoneNumber(String phoneNumber) {
        validate(phoneNumber);
        this.value = normalizePhoneNumber(phoneNumber);
    }

    private void validate(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }

        String normalized = normalizePhoneNumber(phoneNumber);

        if (!isValidPolishPhoneNumber(normalized)) {
            throw new IllegalArgumentException("Invalid Polish phone number format");
        } 
    }

    private String normalizePhoneNumber(String phoneNumber) {
        // Remove all non-digit characters
        String normalized = phoneNumber.replaceAll("[^0-9]", "");

        // Handle different formats (e.g., +48, 48 prefix)
        if (normalized.startsWith("48") && normalized.length() == 11) {
            normalized = normalized.substring(2);
        }

        return normalized;
    }

    private boolean isValidPolishPhoneNumber(String number) {
        // Polish phone numbers are 9 digits
        if (number.length() != 9) {
            return false;
        }

        // This validation is not required for the portfolio project
        // // Check if starts with valid Polish prefix
        // String prefix = number.substring(0, 2);
        // return switch (prefix) {
        //     case "45", "50", "51", "53", "57", "60", "66", "69", // mobile
        //           "22", "23", "24", "25", "29", "32", "33", "34", // landline
        //           "41", "42", "43", "44", "46", "48", "52", "54",
        //           "55", "56", "58", "59", "61", "62", "63", "65",
        //           "67", "68", "71", "74", "75", "76", "77", "81",
        //           "82", "83", "84", "85", "86", "87", "89", "91",
        //           "94", "95" -> true;
        //     default -> false;
        // };
        return true;
    }

    @Override
    public String toString() {
        return "+48" + value;
    }
}