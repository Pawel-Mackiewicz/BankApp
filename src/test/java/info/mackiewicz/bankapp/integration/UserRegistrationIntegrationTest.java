package info.mackiewicz.bankapp.integration;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import info.mackiewicz.bankapp.notification.email.EmailService;
import info.mackiewicz.bankapp.presentation.auth.dto.UserRegistrationDto;
import info.mackiewicz.bankapp.testutils.TestUserRegistrationDtoBuilder;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserRegistrationIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationIntegrationTest.class);

    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private EmailService emailService;

    @BeforeAll
    static void beforeAll() {
        logger.info("Initializing test suite for UserRegistrationIntegrationTest");
    }

    @BeforeEach
    void setUp() {
        logger.info("Setting up test context for UserRegistrationIntegrationTest");
        logger.info("MockMvc status: {}", mockMvc != null ? "initialized" : "null");
        logger.info("EmailService mock status: {}", emailService != null ? "initialized" : "null");
    }
    
    @Test
    void shouldSuccessfullyRegisterNewUser() throws Exception {
        // given
        UserRegistrationDto dto = TestUserRegistrationDtoBuilder.createValid();
        
        // when & then
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstname", dto.getFirstname())
                .param("lastname", dto.getLastname())
                .param("email", dto.getEmail())
                .param("password", dto.getPassword())
                .param("confirmPassword", dto.getConfirmPassword())
                .param("PESEL", dto.getPesel())
                .param("phoneNumber", dto.getPhoneNumber())
                .param("dateOfBirth", dto.getDateOfBirth().toString()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(redirectedUrl("/login"));
    
        verify(emailService).sendWelcomeEmail(
            eq(dto.getEmail()),
            eq(dto.getFirstname() + " " + dto.getLastname()),
            eq("user" + dto.getPesel())
        );
    }
    
    @Test
    void shouldRejectRegistrationWithDuplicateEmail() throws Exception {
        // given
        UserRegistrationDto dto = TestUserRegistrationDtoBuilder.createValid();
        
        // when & then
        // First registration
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstname", dto.getFirstname())
                .param("lastname", dto.getLastname())
                .param("email", dto.getEmail())
                .param("password", dto.getPassword())
                .param("confirmPassword", dto.getConfirmPassword())
                .param("PESEL", dto.getPesel())
                .param("phoneNumber", dto.getPhoneNumber())
                .param("dateOfBirth", dto.getDateOfBirth().toString()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(redirectedUrl("/login"));
    
        // Second registration with same email
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstname", dto.getFirstname())
                .param("lastname", dto.getLastname())
                .param("email", dto.getEmail())
                .param("password", dto.getPassword())
                .param("confirmPassword", dto.getConfirmPassword())
                .param("PESEL", dto.getPesel())
                .param("phoneNumber", dto.getPhoneNumber())
                .param("dateOfBirth", dto.getDateOfBirth().toString()))
                .andExpect(status().is4xxClientError())
                .andExpect(view().name("registration"));
                
        // Verify that welcome email is only sent once
        verify(emailService).sendWelcomeEmail(anyString(), anyString(), anyString());
    }
    
    @Test
    void shouldRejectRegistrationWithInvalidFirstname() throws Exception {
        // given
        UserRegistrationDto dto = TestUserRegistrationDtoBuilder.createWithInvalidFirstName();
        
        // when & then
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstname", dto.getFirstname())
                .param("lastname", dto.getLastname())
                .param("email", dto.getEmail())
                .param("password", dto.getPassword())
                .param("confirmPassword", dto.getConfirmPassword())
                .param("PESEL", dto.getPesel())
                .param("phoneNumber", dto.getPhoneNumber())
                .param("dateOfBirth", dto.getDateOfBirth().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"));
        
        // Verify that no welcome email is sent
        verify(emailService, never()).sendWelcomeEmail(anyString(), anyString(), anyString());
    }
    
    @Test
    void shouldRejectRegistrationWithInvalidPesel() throws Exception {
        // given
        UserRegistrationDto dto = TestUserRegistrationDtoBuilder.createWithInvalidPesel();
        
        // when & then
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstname", dto.getFirstname())
                .param("lastname", dto.getLastname())
                .param("email", dto.getEmail())
                .param("password", dto.getPassword())
                .param("confirmPassword", dto.getConfirmPassword())
                .param("PESEL", dto.getPesel())
                .param("phoneNumber", dto.getPhoneNumber())
                .param("dateOfBirth", dto.getDateOfBirth().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"));
        
        // Verify that no welcome email is sent
        verify(emailService, never()).sendWelcomeEmail(anyString(), anyString(), anyString());
    }
    
    @Test
    void shouldRejectRegistrationForMinor() throws Exception {
        // given
        UserRegistrationDto dto = TestUserRegistrationDtoBuilder.createWithInvalidAge();
        
        // when & then
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("firstname", dto.getFirstname())
                .param("lastname", dto.getLastname())
                .param("email", dto.getEmail())
                .param("password", dto.getPassword())
                .param("confirmPassword", dto.getConfirmPassword())
                .param("PESEL", dto.getPesel())
                .param("phoneNumber", dto.getPhoneNumber())
                .param("dateOfBirth", dto.getDateOfBirth().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"));
        
        // Verify that no welcome email is sent
        verify(emailService, never()).sendWelcomeEmail(anyString(), anyString(), anyString());
    }
}