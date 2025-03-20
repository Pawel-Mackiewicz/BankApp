package info.mackiewicz.bankapp.testutils;

import java.time.LocalDate;
import java.util.Random;

import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.vo.Email;
import info.mackiewicz.bankapp.user.model.vo.Pesel;
import info.mackiewicz.bankapp.user.model.vo.PhoneNumber;

public class TestUserBuilder {

    private static final String DOMAIN = "test.test";
    private static final String PASSWORD = "Password312123/";
    private static final String PESEL = "12345678901";
    private static final String PHONE_NUMBER = "123456789";
    private static int currentId = 1;

    private TestUserBuilder() {
        // Prywatny konstruktor aby zapobiec tworzeniu instancji
    }

    /** 
     * Creates a test user with random values
     * @return User
     */
    public static User createRandomTestUser() {
        User user = new User();
        user.setFirstname("Test");
        user.setLastname("User");
        user.setEmail(new Email(generateRandomEmail()));
        user.setPesel(new Pesel(generateRandomPesel()));
        user.setPhoneNumber(new PhoneNumber(generateRandomPhoneNumber()));
        user.setPassword(PASSWORD);
        user.setUsername(generateRandomUsername(user.getFirstname()));
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));
        return user;
    }

        /** 
     * Creates a test user with random values
     * @return User
     */
    public static User createRandomTestUserForIntegrationTests() {
        User user = new User();
        user.setFirstname("Test");
        user.setLastname("User");
        user.setEmail(new Email(generateRandomEmail()));
        user.setPesel(new Pesel(generateRandomPesel()));
        user.setPhoneNumber(new PhoneNumber(generateRandomPhoneNumber()));
        user.setPassword(PASSWORD);
        user.setUsername(generateRandomUsername(user.getFirstname()));
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));
        return user;
    }

    /** 
     * Creates a test user with predefined values
     * @return User
     */
    public static User createTestUser() {
        User user = new User();
        user.setId(1);
        user.setFirstname("Test");
        user.setLastname("User");
        user.setEmail(new Email("test@" + DOMAIN));
        user.setPesel(new Pesel(PESEL));
        user.setPhoneNumber(new PhoneNumber(PHONE_NUMBER));
        user.setPassword(PASSWORD);
        user.setUsername("testUser");
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));
        return user;
    }

    /**
     * Creates a system user for bank account testing
     * @return User representing the system/bank
     */
    public static User createSystemUser() {
        User user = new User();
        user.setId(0);
        user.setFirstname("System");
        user.setLastname("Bank");
        user.setEmail(new Email("system@bank.com"));
        user.setPesel(new Pesel("00000000000"));
        user.setPhoneNumber(new PhoneNumber("000000000"));
        user.setPassword("SystemBank123!");
        user.setUsername("system");
        user.setDateOfBirth(LocalDate.of(1970, 1, 1));
        return user;
    }

    public static String generateRandomEmail() {
        String username = generateRandomString(8);
        return username + "@" + DOMAIN;
    }

    public static int generateNextId() {
        return currentId++;
    }
    
    public static String generateRandomPesel() {
        // Generujemy unikalny PESEL - 11 losowych cyfr
        Random random = new Random();
        StringBuilder pesel = new StringBuilder();
        for (int i = 0; i < 11; i++) {
            pesel.append(random.nextInt(10));
        }
        return pesel.toString();
    }
    
    public static String generateRandomPhoneNumber() {
        // Generujemy unikalny numer telefonu - 9 losowych cyfr
        return String.format("%09d", new Random().nextInt(1000000000));
    }

    public static String generateRandomUsername(String name) {
        return name + generateRandomString(4);
    }

    public static String generateRandomString(int length) {
        return new Random().ints(48, 122)
                .filter(i -> (i < 57 || i > 65) && (i < 90 || i > 97))
                .mapToObj(i -> (char) i)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
    }
}
