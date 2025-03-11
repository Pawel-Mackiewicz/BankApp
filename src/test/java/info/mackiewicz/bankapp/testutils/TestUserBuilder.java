package info.mackiewicz.bankapp.testutils;

import java.util.Random;

import info.mackiewicz.bankapp.user.model.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestUserBuilder {

    private static final String DOMAIN = "test.test";
    private static final String PASSWORD = "Password312123/";
    private static final String PESEL = "12345678901";
    private static final String PHONE_NUMBER = "123456789";
    private static int currentId = 1;


    /** 
     * Creates a test user with random values
     * @return User
     */
    public User createRandomTestUser() {
        User user = new User();
        user.setId(generateNextId());
        user.setFirstname("Test");
        user.setLastname("User");
        user.setEmail(generateRandomEmail());
        user.setPesel(PESEL);
        user.setPhoneNumber(PHONE_NUMBER);
        user.setPassword(PASSWORD);
        user.setUsername(generateRandomUsername(user.getFirstname()));
        return user;
    }

    /** 
     * Creates a test user with predefined values
     * @return User
     */
    public User createTestUser() {
        User user = new User();
        user.setId(1);
        user.setFirstname("Test");
        user.setLastname("User");
        user.setEmail("test" + "@" + DOMAIN);
        user.setPesel(PESEL);
        user.setPhoneNumber(PHONE_NUMBER);
        user.setPassword(PASSWORD);
        user.setUsername("testUser");
        return user;
    }

    public String generateRandomEmail() {
        String username = generateRandomString(8);
        return username + "@" + DOMAIN;
    }

    public int generateNextId() {
        return currentId++;
    }

    public String generateRandomUsername(String name) {
        return name + generateRandomString(4);
    }

    public String generateRandomString(int length) {
        return new Random().ints(48, 122)
                .filter(i -> (i < 57 || i > 65) && (i < 90 || i > 97))
                .mapToObj(i -> (char) i)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();

    }
}
