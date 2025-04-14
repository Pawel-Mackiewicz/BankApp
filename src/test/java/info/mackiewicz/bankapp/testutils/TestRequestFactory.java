package info.mackiewicz.bankapp.testutils;

import info.mackiewicz.bankapp.system.registration.dto.RegistrationRequest;

import java.time.LocalDate;
import java.util.Random;

/**
 * Utility class for creating request objects for tests.
 */
public class TestRequestFactory {

    private static final Random random = new Random();
    private static final String TEST_FIRSTNAME = "Jan";
    private static final String TEST_LASTNAME = "Kowalski";
    private static final String TEST_EMAIL_BASE = "jan.kowalski";
    private static final String TEST_EMAIL_DOMAIN = "@example.com";
    private static final int TEST_EMAIL_RANDOM_BOUND = 10000;
    private static final String TEST_PASSWORD = "StrongP@ss123";
    private static final String TEST_PESEL = "12345678901";
    private static final String TEST_PHONE_NUMBER = "+48123456789";
    private static final int TEST_VALID_AGE_YEARS = 30;
    private static final int TEST_MINOR_AGE_YEARS = 17;
    private static final String TEST_INVALID_FIRSTNAME = "Jan123";
    private static final String TEST_INVALID_PESEL = "1234567890";

    /**
     * Creates a valid user registration request.
     *
     * @return RegistrationRequest object with valid test data
     */
    public static RegistrationRequest createValidRegistrationRequest() {
        RegistrationRequest request = new RegistrationRequest();
        request.setFirstname(TEST_FIRSTNAME);
        request.setLastname(TEST_LASTNAME);
        request.setEmail(TEST_EMAIL_BASE + random.nextInt(TEST_EMAIL_RANDOM_BOUND) + TEST_EMAIL_DOMAIN);
        request.setPassword(TEST_PASSWORD);
        request.setConfirmPassword(TEST_PASSWORD);
        request.setPesel(TEST_PESEL);
        request.setPhoneNumber(TEST_PHONE_NUMBER);
        request.setDateOfBirth(LocalDate.now().minusYears(TEST_VALID_AGE_YEARS));
        return request;
    }

    /**
     * Creates a registration request with an invalid first name.
     *
     * @return RegistrationRequest object with an invalid first name
     */
    public static RegistrationRequest createRegistrationRequestWithInvalidFirstname() {
        RegistrationRequest request = createValidRegistrationRequest();
        request.setFirstname(TEST_INVALID_FIRSTNAME); // First name contains digits, which is invalid
        return request;
    }

    /**
     * Creates a registration request with an invalid PESEL number.
     *
     * @return RegistrationRequest object with an invalid PESEL number
     */
    public static RegistrationRequest createRegistrationRequestWithInvalidPesel() {
        RegistrationRequest request = createValidRegistrationRequest();
        request.setPesel(TEST_INVALID_PESEL); // PESEL is too short (10 instead of 11 digits)
        return request;
    }

    /**
     * Creates a registration request for a minor.
     *
     * @return RegistrationRequest object with a birth date of a minor
     */
    public static RegistrationRequest createRegistrationRequestForMinor() {
        RegistrationRequest request = createValidRegistrationRequest();
        request.setDateOfBirth(LocalDate.now().minusYears(TEST_MINOR_AGE_YEARS));
        return request;
    }
}
