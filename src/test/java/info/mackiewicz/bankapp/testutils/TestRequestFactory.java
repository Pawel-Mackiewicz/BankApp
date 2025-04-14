package info.mackiewicz.bankapp.testutils;

import info.mackiewicz.bankapp.system.registration.dto.RegistrationRequest;

import java.time.LocalDate;
import java.util.Random;

/**
 * Utility class for creating request objects for tests.
 */
public class TestRequestFactory {

    private static final Random random = new Random();

    /**
     * Creates a valid user registration request.
     *
     * @return RegistrationRequest object with valid test data
     */
    public static RegistrationRequest createValidRegistrationRequest() {
        RegistrationRequest request = new RegistrationRequest();
        request.setFirstname("Jan");
        request.setLastname("Kowalski");
        request.setEmail("jan.kowalski" + random.nextInt(10000) + "@example.com");
        request.setPassword("StrongP@ss123");
        request.setConfirmPassword("StrongP@ss123");
        request.setPesel("12345678901");
        request.setPhoneNumber("+48123456789");
        request.setDateOfBirth(LocalDate.now().minusYears(30));
        return request;
    }

    /**
     * Creates a registration request with an invalid first name.
     *
     * @return RegistrationRequest object with an invalid first name
     */
    public static RegistrationRequest createRegistrationRequestWithInvalidFirstname() {
        RegistrationRequest request = createValidRegistrationRequest();
        request.setFirstname("Jan123"); // First name contains digits, which is invalid
        return request;
    }

    /**
     * Creates a registration request with an invalid PESEL number.
     *
     * @return RegistrationRequest object with an invalid PESEL number
     */
    public static RegistrationRequest createRegistrationRequestWithInvalidPesel() {
        RegistrationRequest request = createValidRegistrationRequest();
        request.setPesel("1234567890"); // PESEL is too short (10 instead of 11 digits)
        return request;
    }

    /**
     * Creates a registration request for a minor.
     *
     * @return RegistrationRequest object with a birth date of a minor
     */
    public static RegistrationRequest createRegistrationRequestForMinor() {
        RegistrationRequest request = createValidRegistrationRequest();
        request.setDateOfBirth(LocalDate.now().minusYears(17));
        return request;
    }
}
