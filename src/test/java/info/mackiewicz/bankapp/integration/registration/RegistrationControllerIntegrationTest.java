package info.mackiewicz.bankapp.integration.registration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.mackiewicz.bankapp.system.notification.email.EmailService;
import info.mackiewicz.bankapp.system.registration.dto.RegistrationRequest;
import info.mackiewicz.bankapp.system.registration.dto.RegistrationResponse;
import info.mackiewicz.bankapp.system.registration.service.BonusGrantingService;
import info.mackiewicz.bankapp.testutils.TestRequestFactory;
import info.mackiewicz.bankapp.user.service.UserService;
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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private BonusGrantingService bonusGrantingService;

    @Autowired
    private UserService userService;
    
    private RegistrationRequest validRequest;

    @BeforeEach
    void setUp() {
        logger.info("Setting up integration test for registration process");
        
        // Prepare valid registration request using TestRequestFactory
        validRequest = TestRequestFactory.createValidRegistrationRequest();
        // Set constant email for duplicate checking tests
        validRequest.setEmail("jan.kowalski@example.com");

        objectMapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

    }

    @Test
    @DisplayName("Should register a new user and return 201 status code")
    void registerUser_WhenValidRequest_ThenReturnCreatedStatus() throws Exception {
        // Arrange
        String requestJson = objectMapper.writeValueAsString(validRequest);
    
        // Act & Assert
        MvcResult result = mockMvc.perform(post("/api/registration/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstname").value("Jan"))
                .andExpect(jsonPath("$.lastname").value("Kowalski"))
                .andExpect(jsonPath("$.email").value("jan.kowalski@example.com"))
                .andExpect(jsonPath("$.username").exists())
                .andReturn();
        
        // Verify response
        String responseJson = result.getResponse().getContentAsString();
        RegistrationResponse response = objectMapper.readValue(responseJson, RegistrationResponse.class);
        assertNotNull(response);
        assertEquals("Jan", response.firstname());
        assertEquals("Kowalski", response.lastname());
        assertEquals("jan.kowalski@example.com", response.email());
        assertNotNull(response.username());
    }
}
