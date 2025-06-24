package info.mackiewicz.bankapp.shared.validation;

import lombok.experimental.UtilityClass;

/**
 * Centralized repository of validation constants used throughout the
 * application.
 *
 * <p>
 * This utility class provides standardized validation patterns, rules, and
 * descriptions
 * that are used across different parts of the application to ensure consistent
 * validation
 * behavior. It is particularly focused on password validation requirements.
 * </p>
 *
 * <p>
 * The class is final and has a private constructor to prevent instantiation,
 * as it only contains static constants.
 * </p>
 *
 * <p>
 * Thread-safe: This class is immutable and only contains static final fields.
 * </p>
 *
 * @see <a href=
 *      "https://github.com/Pawel-Mackiewicz/BankApp/wiki/Password-Reset-System">Password
 *      Reset System</a>
 * @see <a href=
 *      "https://github.com/Pawel-Mackiewicz/BankApp/wiki/Registration-System">Registration
 *      System</a>
 */
@UtilityClass
public final class ValidationConstants {

    /**
     * A regular expression pattern used to validate the format of a transfer title.
     * This pattern ensures the title consists of letters, digits, and specific
     * allowable special characters including .,/?@!%#&()- as well as spaces.
     */
    public static final String TRANSFER_TITLE_PATTERN = "^[\\p{L}0-9.,/?@!%#&()\\-\\s]+$";

    /**
     * Regular expression pattern for validating email addresses.<br>
     * This pattern allows:<br>
     * - Alphanumeric characters (a-z, A-Z, 0-9)<br>
     * - Special characters: _!#$%&'*+/=?`{|}~^.-<br>
     * - @ symbol separating local and domain parts<br>
     * - Domain with at least one dot and minimum 2 characters TLD
     * <p>
     * Examples of valid emails:
     * <ul>
     * <li>user@domain.com</li>
     * <li>user.name@domain.co.uk</li>
     * <li>user+label@domain.org</li>
     * </ul>
     */
    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    /**
     * Regular expression pattern for name validation.<br>
     * Ensures name contains only letters (including Polish characters).
     */
    public static final String NAME_PATTERN = "^[A-Za-zĄĆĘŁŃÓŚŹŻąćęłńóśźż]+$";

    /**
     * Regular expression pattern for phone number validation.<br>
     * Ensures phone number is in one of the following formats:
     * <ul>
     * <li>+48XXXXXXXXX</li>
     * <li>0XXXXXXXXX</li>
     * <li>XXXXXXXXX</li>
     * </ul>
     */
    public static final String PHONE_NUMBER_PATTERN = "^(\\+48\\d{9}|0\\d{9}|[1-9]\\d{8})$";

    /**
     * Regular expression pattern for password validation.
     * Ensures password contains at least:
     * <ul>
     * <li>One digit (0-9)</li>
     * <li>One lowercase letter (a-z)</li>
     * <li>One uppercase letter (A-Z)</li>
     * <li>One special character from the set: "@$!%*?&"</li>
     * </ul>
     */
    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@$!%*?&]).*$";

    /**
     * User-friendly description of the password requirements.
     * Used in validation error messages and UI hints.
     */
    public static final String PASSWORD_DESCRIPTION = "Password must contain at least one digit, one lowercase, one uppercase letter and one special character (@$!%*?&)";

    /**
     * Minimum required length for passwords.
     * This value is used in conjunction with PASSWORD_PATTERN for complete password
     * validation.
     */
    public static final int PASSWORD_MIN_LENGTH = 8;
}
