package info.mackiewicz.bankapp.integration.registration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.mackiewicz.bankapp.system.notification.email.EmailService;
import info.mackiewicz.bankapp.system.registration.dto.RegistrationRequest;
import info.mackiewicz.bankapp.system.registration.dto.RegistrationResponse;
import info.mackiewicz.bankapp.system.registration.service.BonusGrantingService;
import info.mackiewicz.bankapp.testutils.TestRequestFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RegistrationControllerIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationControllerIntegrationTest.class);
    private static final String TEST_EMAIL = "jan.kowalski@example.com";
    private static final String TEST_FIRSTNAME = "Jan";
    private static final String TEST_LASTNAME = "Kowalski";
    private static final String REGISTRATION_ENDPOINT = "/api/registration/regular";
    private static final String DATE_OF_BIRTH_FIELD = "dateOfBirth";
    private static final String FIRSTNAME_FIELD = "firstname";
    private static final String PESEL_FIELD = "pesel";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private BonusGrantingService bonusGrantingService;

    private RegistrationRequest validRequest;

    @BeforeEach
    void setUp() {
        logger.info("Setting up integration test for registration process");
        
        // Prepare valid registration request using TestRequestFactory
        validRequest = TestRequestFactory.createValidRegistrationRequest();
        // Set constant email for duplicate checking tests
        validRequest.setEmail(TEST_EMAIL);

        objectMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @Test
    @DisplayName("Should register a new user and return 201 status code")
    void registerUser_WhenValidRequest_ThenReturnCreatedStatus() throws Exception {
        // Arrange
        String requestJson = objectMapper.writeValueAsString(validRequest);
    
        // Act & Assert
        MvcResult result = mockMvc.perform(post(REGISTRATION_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstname").value(TEST_FIRSTNAME))
                .andExpect(jsonPath("$.lastname").value(TEST_LASTNAME))
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.username").exists())
                .andReturn();
        
        // Verify response
        String responseJson = result.getResponse().getContentAsString();
        RegistrationResponse response = objectMapper.readValue(responseJson, RegistrationResponse.class);
        assertNotNull(response);
        assertEquals(TEST_FIRSTNAME, response.firstname());
        assertEquals(TEST_LASTNAME, response.lastname());
        assertEquals(TEST_EMAIL, response.email());
        assertNotNull(response.username());
    }
    
    @Test
    @DisplayName("Should reject registration for minor with 400 status code")
    void registerUser_WhenMinor_ThenReturnBadRequest() throws Exception {
        // Arrange
        RegistrationRequest minorRequest = TestRequestFactory.createRegistrationRequestForMinor();
        String requestJson = objectMapper.writeValueAsString(minorRequest);
        
        // Act & Assert
        mockMvc.perform(post(REGISTRATION_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field=='" + DATE_OF_BIRTH_FIELD + "')]").exists());
    }
    
    @Test
    @DisplayName("Should reject registration with invalid firstname with 400 status code")
    void registerUser_WhenInvalidFirstname_ThenReturnBadRequest() throws Exception {
        // Arrange
        RegistrationRequest invalidFirstnameRequest = TestRequestFactory.createRegistrationRequestWithInvalidFirstname();
        String requestJson = objectMapper.writeValueAsString(invalidFirstnameRequest);
        
        // Act & Assert
        mockMvc.perform(post(REGISTRATION_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field=='" + FIRSTNAME_FIELD + "')]").exists());
    }
    
    @Test
    @DisplayName("Should reject registration with invalid PESEL with 400 status code")
    void registerUser_WhenInvalidPesel_ThenReturnBadRequest() throws Exception {
        // Arrange
        RegistrationRequest invalidPeselRequest = TestRequestFactory.createRegistrationRequestWithInvalidPesel();
        String requestJson = objectMapper.writeValueAsString(invalidPeselRequest);
        
        // Act & Assert
        mockMvc.perform(post(REGISTRATION_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[?(@.field=='" + PESEL_FIELD + "')]").exists());
    }
}
