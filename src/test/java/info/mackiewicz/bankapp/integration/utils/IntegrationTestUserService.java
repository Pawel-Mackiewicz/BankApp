package info.mackiewicz.bankapp.integration.utils;

import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.core.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.core.user.model.vo.Pesel;
import info.mackiewicz.bankapp.core.user.model.vo.PhoneNumber;
import info.mackiewicz.bankapp.core.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Utility service for creating test users in integration tests.
 * Generates consistent test data with unique identifiers for each test run.
 */
@TestComponent
public class IntegrationTestUserService {

    // Constants for test data generation
    private static final int TEST_RUN_ID_LENGTH = 8;
    private static final int HASH_CODE_MODULO = 1000000;
    private static final int PHONE_NUMBER_LENGTH = 9;
    private static final char PHONE_PADDING_CHAR = '9';
    private static final int BIRTH_YEAR = 1999;
    private static final int BIRTH_MONTH = 1;
    private static final int MAX_DAYS_IN_MONTH = 28;
    private static final String DEFAULT_FIRST_NAME = "Test";
    private static final String DEFAULT_LAST_NAME = "User";
    private static final String DEFAULT_PASSWORD = "Password123!";
    private static final String EMAIL_DOMAIN = "@test.com";
    private static final String EMAIL_PREFIX = "test.user";

    @Autowired
    private UserService userService;

    private final String testRunId = UUID.randomUUID().toString().substring(0, TEST_RUN_ID_LENGTH);

    /**
     * Creates a test user with unique identifiers based on the index.
     *
     * @param index The index used to create unique user data
     *
     * @return The created User entity persisted in the database
     */
    public User createTestUser(int index) {
        User user = new User();
        String uniqueSuffix = testRunId + "-" + index;
        int hashCode = Math.abs(testRunId.hashCode() % HASH_CODE_MODULO);

        // Generate unique PESEL that matches date of birth
        Pesel pesel = TestPeselGenerator.generatePesel(hashCode, index);

        // Configure user with test data
        user.setPesel(pesel);
        user.setFirstname(DEFAULT_FIRST_NAME);
        user.setLastname(DEFAULT_LAST_NAME);
        user.setEmail(new EmailAddress(EMAIL_PREFIX + uniqueSuffix + EMAIL_DOMAIN));
        user.setPassword(DEFAULT_PASSWORD);
        user.setPhoneNumber(new PhoneNumber(paddedNumber(hashCode, index)));

        // Calculate birth day (1-28) based on index, correlating with PESEL
        int birthDay = (index % MAX_DAYS_IN_MONTH) + 1;
        user.setDateOfBirth(LocalDate.of(BIRTH_YEAR, BIRTH_MONTH, birthDay));

        // Persist and return the created user
        return userService.createUser(user);
    }

    /**
     * Generates a phone number string with padding.
     * Ensures the number is always 9 digits by padding with 9s.
     *
     * @param hashCode Base hash code for generating the number
     * @param index    Index to make the number unique
     *
     * @return A 9-digit phone number string
     */
    private String paddedNumber(int hashCode, int index) {
        String number = String.valueOf(hashCode + index);
        int paddingLength = PHONE_NUMBER_LENGTH - number.length();
        return String.valueOf(PHONE_PADDING_CHAR).repeat(paddingLength) + number;
    }
}
