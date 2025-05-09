package info.mackiewicz.bankapp.presentation.auth.registration.dto.demo;

import info.mackiewicz.bankapp.core.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.presentation.auth.registration.dto.RegistrationRequest;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Random;

/**
 * Class that is responsible for creating random data for demo user creation.
 */
@Component
public class RegistrationRequestFactory {


    private static final Random RANDOM = new Random();
    private static final int PESEL_LENGTH = 11;
    private static final int PHONE_NUMBER_LENGTH = 9;
    private static final int MAX_PHONE_NUMBER = 999999999;
    private static final int RANDOM_DIGIT_BOUND = 10;
    private static final LocalDate DEFAULT_DATE_OF_BIRTH = LocalDate.of(1990, 1, 1);
    private static final String DEFAULT_FIRSTNAME = "Demo";
    private static final String DEFAULT_LASTNAME = "User";


    public RegistrationRequest createDemoRegistrationRequest(@NonNull EmailAddress email, @NonNull String password) {
        RegistrationRequest request = new RegistrationRequest();

        request.setEmail(email.getValue());
        request.setPassword(password);
        request.setConfirmPassword(password);
        request.setFirstname(DEFAULT_FIRSTNAME);
        request.setLastname(DEFAULT_LASTNAME);
        request.setDateOfBirth(DEFAULT_DATE_OF_BIRTH);
        request.setPesel(generateRandomPesel());
        request.setPhoneNumber(generateRandomPhoneNumber());

        return request;
    }

    /**
     * Generates a random PESEL (Polish national identification number) consisting of a sequence
     * of digits of a fixed length.
     *
     * @return a randomly generated PESEL number in string format
     */
    private static String generateRandomPesel() {
        StringBuilder pesel = new StringBuilder();
        for (int i = 0; i < PESEL_LENGTH; i++) {
            pesel.append(RANDOM.nextInt(RANDOM_DIGIT_BOUND));
        }
        return pesel.toString();
    }

    /**
     * Generates a random phone number consisting of a fixed number of digits.
     *
     * @return a randomly generated phone number in string format
     */
    private static String generateRandomPhoneNumber() {
        return String.format("%0" + PHONE_NUMBER_LENGTH + "d", RANDOM.nextInt(MAX_PHONE_NUMBER));
    }
}
