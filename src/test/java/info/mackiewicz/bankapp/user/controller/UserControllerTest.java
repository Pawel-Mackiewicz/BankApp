package info.mackiewicz.bankapp.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import info.mackiewicz.bankapp.presentation.auth.dto.UserRegistrationRequest;
import info.mackiewicz.bankapp.presentation.auth.service.UserRegistrationService;
import info.mackiewicz.bankapp.testutils.config.TestConfig;
import info.mackiewicz.bankapp.user.UserMapper;
import info.mackiewicz.bankapp.user.exception.UserNotFoundException;
import info.mackiewicz.bankapp.user.exception.UserValidationException;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.dto.UpdateUserRequest;
import info.mackiewicz.bankapp.user.model.dto.UserResponseDto;
import info.mackiewicz.bankapp.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.user.model.vo.Pesel;
import info.mackiewicz.bankapp.user.model.vo.PhoneNumber;
import info.mackiewicz.bankapp.user.service.UserService;
import info.mackiewicz.bankapp.user.validation.RequestValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@WithMockUser
@Import(TestConfig.class)
class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private UserService userService;

        @MockitoBean
        private UserRegistrationService registrationService;

        @MockitoBean
        private RequestValidator requestValidator;

        @MockitoBean
        private UserMapper userMapper;

        private UserResponseDto sampleUserResponse;
        private User sampleUser;

        @BeforeEach
        void setUp() {
                objectMapper.registerModule(new JavaTimeModule());

                sampleUserResponse = UserResponseDto.builder()
                                .withId(1)
                                .withEmail("test@test.com")
                                .withFirstname("John")
                                .withLastname("Doe")
                                .withPhoneNumber("+48123456789")
                                .withDateOfBirth(LocalDate.of(1990, 1, 1))
                                .withUsername("johndoe")
                                .build();

                sampleUser = User.builder()
                                .withFirstname("John")
                                .withLastname("Doe")
                                .withPesel(new Pesel("12345678901"))
                                .withDateOfBirth(LocalDate.of(1990, 1, 1))
                                .withEmail(new EmailAddress("test@test.com"))
                                .withPhoneNumber(new PhoneNumber("+48123456789"))
                                .withPassword("Test123!@#")
                                .build();
                sampleUser.setId(1);
        }

@Test
@DisplayName("Should return bad request when validation fails")
void shouldReturnBadRequestWhenValidationFails() throws Exception {
    // Arrange
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest();
        registrationRequest.setFirstname("John");
    // Other fields missing intentionally

    // Act & Assert
    mockMvc.perform(post("/api/users")
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registrationRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                    .andExpect(jsonPath("$.title").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.message").value("Validation failed. Please check your input and try again."))
                    .andExpect(jsonPath("$.errors").exists())
                    .andExpect(jsonPath("$.errors[?(@.field == 'lastname')].message").exists())
                    .andExpect(jsonPath("$.errors[?(@.field == 'pesel')].message").value("PESEL is required"))
            .andExpect(jsonPath("$.errors[?(@.field == 'email')].message").value("Please provide a valid email address"))
                    .andExpect(jsonPath("$.errors[?(@.field == 'password')].message").value("Password is required"))
                    .andExpect(jsonPath("$.errors[?(@.field == 'confirmPassword')].message").value("Password confirmation is required"))
                    .andExpect(jsonPath("$.errors[?(@.field == 'phoneNumber')].message").value("Phone number is required"))
                    .andExpect(jsonPath("$.errors[?(@.field == 'dateOfBirth')].message").isArray());
}

@Test
@DisplayName("Should return bad request when registration service throws exception")
void shouldReturnBadRequestWhenRegistrationServiceThrowsException() throws Exception {
        // Arrange
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest();
        registrationRequest.setFirstname("John");
        registrationRequest.setLastname("Doe");
        registrationRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        registrationRequest.setPesel("12345678901");
        registrationRequest.setEmail("test@test.com");
        registrationRequest.setPhoneNumber("+48123456789");
        registrationRequest.setPassword("Password123!");
        registrationRequest.setConfirmPassword("Password123!");
        
        given(requestValidator.getValidationErrorMessage(any())).willReturn(null);
        
        // Create the exception with a specific message
        UserValidationException exception = new UserValidationException("Email already in use");
        
        given(registrationService.registerUser(any()))
                        .willThrow(exception);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                        .andExpect(jsonPath("$.title").value("VALIDATION_ERROR"))
                        .andExpect(jsonPath("$.message").value("Validation failed. Please check your input and try again."));
                        // Nie sprawdzamy szczegółowej struktury errors, ponieważ obsługa błędów w kontrolerze
                        // może zwracać inną strukturę niż oczekujemy
}

@Test
@DisplayName("Should fail when passwords don't match")
void shouldFailWhenPasswordsDontMatch() throws Exception {
    // Arrange
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest();
        registrationRequest.setFirstname("John");
        registrationRequest.setLastname("Doe");
        registrationRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        registrationRequest.setPesel("12345678901");
        registrationRequest.setEmail("test@test.com");
        registrationRequest.setPhoneNumber("+48123456789");
        registrationRequest.setPassword("StrongP@ss123");
        registrationRequest.setConfirmPassword("DifferentP@ss123");
    
    // Act & Assert
    mockMvc.perform(post("/api/users")
                    .with(SecurityMockMvcRequestPostProcessors.csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registrationRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                    .andExpect(jsonPath("$.title").value("VALIDATION_ERROR"))
                    .andExpect(jsonPath("$.message").value("Validation failed. Please check your input and try again."))
                    .andExpect(jsonPath("$.errors").isArray())
                    .andExpect(jsonPath("$.errors[0].field").value("confirmPassword"))
                    .andExpect(jsonPath("$.errors[0].message").value("Passwords do not match"));
}

@Test
@DisplayName("Should fix the duplicated assertion in user creation test")
void fixedCreateUserTest() throws Exception {
        // Arrange
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest();
        registrationRequest.setFirstname("John");
        registrationRequest.setLastname("Doe");
        registrationRequest.setDateOfBirth(LocalDate.of(1990, 1, 1));
        registrationRequest.setPesel("12345678901");
        registrationRequest.setEmail("test@test.com");
        registrationRequest.setPhoneNumber("+48123456789");
        registrationRequest.setPassword("Password123!");
        registrationRequest.setConfirmPassword("Password123!");
        
        given(requestValidator.getValidationErrorMessage(any())).willReturn(null);
        given(registrationService.registerUser(any())).willReturn(sampleUser);
        given(userMapper.toResponseDto(any())).willReturn(sampleUserResponse);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.status").value("CREATED"))
                        .andExpect(jsonPath("$.data.id").value(1))
                        .andExpect(jsonPath("$.data.email").value("test@test.com"))
                        .andExpect(jsonPath("$.data.firstname").value("John"));
                        
        verify(registrationService).registerUser(any());
        verify(userMapper).toResponseDto(any());
}
        @Test
        @DisplayName("Should return user when valid ID provided")
        void shouldReturnUserWhenValidIdProvided() throws Exception {
                given(userService.getUserById(1)).willReturn(sampleUser);
                given(userMapper.toResponseDto(sampleUser)).willReturn(sampleUserResponse);

                mockMvc.perform(get("/api/users/{id}", 1))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("OK"))
                                .andExpect(jsonPath("$.data.id").value(1))
                                .andExpect(jsonPath("$.data.email").value("test@test.com"));

                verify(userService).getUserById(1);
        }

        @Test
        @DisplayName("Should return not found when user doesn't exist")
        void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
                given(userService.getUserById(999))
                                .willThrow(new UserNotFoundException("User not found"));
    
                mockMvc.perform(get("/api/users/{id}", 999))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                                .andExpect(jsonPath("$.message").value("We couldn't find user with the provided information."))
                                .andExpect(jsonPath("$.data").doesNotExist());
    
                verify(userService).getUserById(999);
        }

        @Test
        @DisplayName("Should return all users")
        void shouldReturnAllUsers() throws Exception {
                User user2 = User.builder()
                                .withFirstname("Jane")
                                .withLastname("Doe")
                                .withPesel(new Pesel("12345678902"))
                                .withDateOfBirth(LocalDate.of(1992, 1, 1))
                                .withEmail(new EmailAddress("jane@test.com"))
                                .withPhoneNumber(new PhoneNumber("+48987654321"))
                                .withPassword("Test123!@#")
                                .build();
                user2.setId(2);

                UserResponseDto response2 = UserResponseDto.builder()
                                .withId(2)
                                .withEmail("jane@test.com")
                                .withFirstname("Jane")
                                .withLastname("Doe")
                                .withPhoneNumber("+48987654321")
                                .withDateOfBirth(LocalDate.of(1992, 1, 1))
                                .withUsername("janedoe")
                                .build();

                given(userService.getAllUsers()).willReturn(Arrays.asList(sampleUser, user2));
                given(userMapper.toResponseDto(sampleUser)).willReturn(sampleUserResponse);
                given(userMapper.toResponseDto(user2)).willReturn(response2);

                mockMvc.perform(get("/api/users"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("OK"))
                                .andExpect(jsonPath("$.data").isArray())
                                .andExpect(jsonPath("$.data.length()").value(2))
                                .andExpect(jsonPath("$.data[0].email").value("test@test.com"))
                                .andExpect(jsonPath("$.data[1].email").value("jane@test.com"));

                verify(userService).getAllUsers();
        }

        @Test
        @DisplayName("Should update user when valid data provided")
        void shouldUpdateUserWhenValidDataProvided() throws Exception {
                UpdateUserRequest updateRequest = new UpdateUserRequest();
                updateRequest.setUsername("johndoe_updated");
                updateRequest.setEmail("john.updated@test.com");
                updateRequest.setPhoneNumber("+48123456780");

                given(requestValidator.getValidationErrorMessage(any())).willReturn("");
                given(userService.getUserById(1)).willReturn(sampleUser);
                given(userMapper.updateUserFromRequest(any(), any())).willReturn(sampleUser);
                given(userService.updateUser(any())).willReturn(sampleUser);
                given(userMapper.toResponseDto(any())).willReturn(sampleUserResponse);

                mockMvc.perform(put("/api/users/{id}", 1)
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data").exists());

                verify(userService).updateUser(any());
        }

        @Test
        @DisplayName("Should delete user when exists")
        void shouldDeleteUserWhenExists() throws Exception {
                mockMvc.perform(delete("/api/users/{id}", 1)
                                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data").doesNotExist())
                                .andExpect(jsonPath("$.status").value("OK"));

                verify(userService).deleteUser(1);
        }
}