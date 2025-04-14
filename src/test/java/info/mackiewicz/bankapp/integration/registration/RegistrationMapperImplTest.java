package info.mackiewicz.bankapp.integration.registration;

import info.mackiewicz.bankapp.system.registration.dto.RegistrationMapperImpl;
import info.mackiewicz.bankapp.system.registration.dto.RegistrationRequest;
import info.mackiewicz.bankapp.system.registration.dto.RegistrationResponse;
import info.mackiewicz.bankapp.testutils.TestRequestFactory;
import info.mackiewicz.bankapp.testutils.TestUserBuilder;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.user.model.vo.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
/**
 * Tests for the RegistrationMapperImpl class.
 * Validates that mapping between RegistrationRequest, User entity, and 
 * RegistrationResponse DTOs functions correctly.
 */
class RegistrationMapperImplTest {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationMapperImplTest.class);

    private RegistrationMapperImpl mapper;

    @BeforeEach
    void setUp() {
        logger.info("Initializing RegistrationMapperImpl for testing");
        mapper = new RegistrationMapperImpl();
    }

    @Test
    @DisplayName("Should transform RegistrationRequest into User object")
    void toUser_ShouldMapRegistrationRequestToUser() {
        // Arrange
        RegistrationRequest request = TestRequestFactory.createValidRegistrationRequest();
        // Set fixed values for testing to ensure consistent assertions
        request.setEmail("jan.kowalski@example.com");
        request.setDateOfBirth(LocalDate.parse("1990-01-01"));

        // Act
        User user = mapper.toUser(request);

        // Assert
        assertNotNull(user);
        assertEquals("Jan", user.getFirstname());
        assertEquals("Kowalski", user.getLastname());
        assertEquals(new EmailAddress("jan.kowalski@example.com"), user.getEmail());
        assertEquals(new PhoneNumber("+48123456789"), user.getPhoneNumber());
        assertEquals("12345678901", user.getPesel().toString());
        assertEquals(LocalDate.parse("1990-01-01"), user.getDateOfBirth());
        assertEquals("StrongP@ss123", user.getPassword());
    }

    @Test
    @DisplayName("Should transform User object into RegistrationResponse")
    void toResponse_ShouldMapUserToRegistrationResponse() {
        // Arrange
        User user = TestUserBuilder.createTestUser();
        // Ensure predictable values for testing
        user.setFirstname("Jan");
        user.setLastname("Kowalski");
        user.setEmail(new EmailAddress("jan.kowalski@example.com"));
        user.setUsername("jan.kowalski123");

        // Act
        RegistrationResponse response = mapper.toResponse(user);

        // Assert
        assertNotNull(response);
        assertEquals("Jan", response.firstname());
        assertEquals("Kowalski", response.lastname());
        assertEquals("jan.kowalski@example.com", response.email());
        assertEquals("jan.kowalski123", response.username());
    }
}
