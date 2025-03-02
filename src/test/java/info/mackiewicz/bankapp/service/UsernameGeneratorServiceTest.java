package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.service.UsernameGeneratorService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UsernameGeneratorServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(UsernameGeneratorServiceTest.class);

    @InjectMocks
    private UsernameGeneratorService generatorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateUsernameByUser() {
        logger.info("testGenerateUsernameByUser: Starting test");
        User user = new User();
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmail("john.doe@example.com");

        User generatedUser = generatorService.generateUsername(user);

        String expectedUsername = "john.doe" + String.valueOf(user.getEmail().hashCode()).substring(0, 6);
        assertEquals(expectedUsername, generatedUser.getUsername());
        logger.info("testGenerateUsernameByUser: Test passed");
    }

    @Test
    void testGenerateUsernameByParams() {
        logger.info("testGenerateUsernameByParams: Starting test");
        String firstname = "John";
        String lastname = "Doe";
        String email = "john.doe@example.com";

        String username = generatorService.generateUsername(firstname, lastname, email);

        String expectedUsername = "john.doe" + String.valueOf(email.hashCode()).substring(0, 6);
        assertEquals(expectedUsername, username);
        logger.info("testGenerateUsernameByParams: Test passed");
    }
}