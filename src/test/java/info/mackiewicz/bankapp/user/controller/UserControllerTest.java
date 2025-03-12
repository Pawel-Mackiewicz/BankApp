package info.mackiewicz.bankapp.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.mackiewicz.bankapp.presentation.auth.dto.UserRegistrationDto;
import info.mackiewicz.bankapp.presentation.auth.service.UserRegistrationService;
import info.mackiewicz.bankapp.user.UserMapper;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.dto.UpdateUserRequest;
import info.mackiewicz.bankapp.user.model.dto.UserResponseDto;
import info.mackiewicz.bankapp.user.service.UserService;
import info.mackiewicz.bankapp.user.validation.RequestValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private UserRegistrationService registrationService;

    @MockBean
    private RequestValidator requestValidator;

    @MockBean
    private UserMapper userMapper;

    @Nested
    @DisplayName("Create User Tests")
    class CreateUserTests {
        
        @Test
        @DisplayName("Should create user successfully when valid registration data provided")
        void shouldCreateUserSuccessfullyWhenValidDataProvided() throws Exception {
            // Arrange
            UserRegistrationDto registrationDto = new UserRegistrationDto();
            registrationDto.setEmail("test@test.com");
            registrationDto.setPassword("password123");
            registrationDto.setFirstName("John");
            registrationDto.setLastName("Doe");
            
            User createdUser = User.builder()
                .id(1)
                .email("test@test.com")
                .firstName("John")
                .lastName("Doe")
                .build();
                
            UserResponseDto responseDto = new UserResponseDto();
            responseDto.setId(1);
            responseDto.setEmail("test@test.com");
            responseDto.setFirstName("John");
            responseDto.setLastName("Doe");

            when(registrationService.registerUser(any(UserRegistrationDto.class))).thenReturn(createdUser);
            when(userMapper.toResponseDto(any(User.class))).thenReturn(responseDto);

            // Act & Assert
