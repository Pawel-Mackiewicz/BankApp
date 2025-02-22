package info.mackiewicz.bankapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class PasswordServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(PasswordServiceTest.class);

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordService passwordService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testVerifyPassword() {
        logger.info("testVerifyPassword: Starting test");
        String rawPassword = "password";
        String encodedPassword = "encodedPassword";

        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        boolean result = passwordService.verifyPassword(rawPassword, encodedPassword);

        assertTrue(result);
        logger.info("testVerifyPassword: Test passed");
    }

    @Test
    void testEncodePassword() {
        logger.info("testEncodePassword: Starting test");
        String rawPassword = "password";
        String encodedPassword = "encodedPassword";

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        String result = passwordEncoder.encode(rawPassword);

        assertEquals(encodedPassword, result);
        logger.info("testEncodePassword: Test passed");
    }
}