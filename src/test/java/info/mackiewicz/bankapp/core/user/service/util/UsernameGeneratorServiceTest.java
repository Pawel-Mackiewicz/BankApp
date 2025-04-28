package info.mackiewicz.bankapp.core.user.service.util;

import info.mackiewicz.bankapp.core.user.exception.UsernameException;
import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.core.user.model.vo.EmailAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

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
        user.setEmail(new EmailAddress("john.doe@example.com"));

        String username = generatorService.generateUsername(user.getFirstname(), user.getLastname(), user.getEmail().toString());

        assertTrue(username.startsWith("john.doe"));
        assertTrue(username.substring("john.doe".length()).matches("\\d{6}"));
        logger.info("testGenerateUsernameByUser: Test passed");
    }

    @Test
    void testGenerateUsernameByParams() {
        logger.info("testGenerateUsernameByParams: Starting test");
        String firstname = "John";
        String lastname = "Doe";
        String email = "john.doe@example.com";

        String username = generatorService.generateUsername(firstname, lastname, email);

        assertTrue(username.startsWith("john.doe"));
        assertTrue(username.substring("john.doe".length()).matches("\\d{6}"));
        logger.info("testGenerateUsernameByParams: Test passed");
    }

    @Test
    void testGenerateUsernameWithDiacritics() {
        logger.info("testGenerateUsernameWithDiacritics: Starting test");
        String firstname = "Żółć";
        String lastname = "Ćma";
        String email = "zolc.cma@example.com";

        String username = generatorService.generateUsername(firstname, lastname, email);

        assertTrue(username.startsWith("zolc.cma"));
        assertFalse(username.contains("ż"));
        assertFalse(username.contains("ć"));
        assertTrue(username.substring("zolc.cma".length()).matches("\\d{6}"));
        logger.info("testGenerateUsernameWithDiacritics: Test passed");
    }

    @Test
    void testRemoveDiacriticsPrivateMethod() throws Exception {
        logger.info("testRemoveDiacriticsPrivateMethod: Starting test");
        Method method = UsernameGeneratorService.class.getDeclaredMethod("removeDiacritics", String.class);
        method.setAccessible(true);

        String input = "ąćęłńóśźżĄĆĘŁŃÓŚŹŻ";
        String expected = "acelnoszz" + "ACELNOSZZ";
        String result = (String) method.invoke(generatorService, input);

        assertEquals(expected, result);
        logger.info("testRemoveDiacriticsPrivateMethod: Test passed");
    }

    @Test
    void testGenerateBaseUsernamePrivateMethod() throws Exception {
        logger.info("testGenerateBaseUsernamePrivateMethod: Starting test");
        Method method = UsernameGeneratorService.class.getDeclaredMethod("generateBaseUsername", String.class, String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(generatorService, "Jan", "Kowalski");
        assertEquals("jan.kowalski", result);
        logger.info("testGenerateBaseUsernamePrivateMethod: Test passed");
    }

    @Test
    void testGenerateUniqueIDPrivateMethod() throws Exception {
        logger.info("testGenerateUniqueIDPrivateMethod: Starting test");
        Method method = UsernameGeneratorService.class.getDeclaredMethod("generateUniqueID", String.class);
        method.setAccessible(true);

        String email = "test@example.com";
        String result = (String) method.invoke(generatorService, email);

        assertNotNull(result);
        assertEquals(6, result.length());
        assertTrue(result.matches("\\d{6}"));
        logger.info("testGenerateUniqueIDPrivateMethod: Test passed");
    }

    @Test
    void testNullInputValidation() {
        logger.info("testNullInputValidation: Starting test");
        assertThrows(UsernameException.class, () ->
            generatorService.generateUsername(null, "Doe", "email@example.com"));
        assertThrows(UsernameException.class, () ->
            generatorService.generateUsername("John", null, "email@example.com"));
        assertThrows(UsernameException.class, () ->
            generatorService.generateUsername("John", "Doe", null));
        logger.info("testNullInputValidation: Test passed");
    }

    @ParameterizedTest
    @MethodSource("provideSpecialCharacterTestCases")
    void testSpecialCharacterHandling(String firstname, String lastname, String expectedBase) throws Exception {
        logger.info("testSpecialCharacterHandling: Starting test for {}, {}", firstname, lastname);
        Method method = UsernameGeneratorService.class.getDeclaredMethod("generateBaseUsername", String.class, String.class);
        method.setAccessible(true);

        String result = (String) method.invoke(generatorService, firstname, lastname);
        assertEquals(expectedBase, result);
        logger.info("testSpecialCharacterHandling: Test passed for {}, {}", firstname, lastname);
    }

    private static Stream<Arguments> provideSpecialCharacterTestCases() {
        return Stream.of(
            Arguments.of("John-Paul", "Smith", "johnpaul.smith"),
            Arguments.of("O'Connor", "Smith", "oconnor.smith"),
            Arguments.of("van der", "Berg", "vander.berg"),
            Arguments.of("María", "García", "maria.garcia")
        );
    }
}