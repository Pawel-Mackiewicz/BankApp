package info.mackiewicz.bankapp.user.controller;

import lombok.extern.slf4j.Slf4j;
import info.mackiewicz.bankapp.presentation.auth.dto.UserRegistrationDto;
import info.mackiewicz.bankapp.presentation.auth.service.UserRegistrationService;
import info.mackiewicz.bankapp.shared.dto.ApiResponse;
import info.mackiewicz.bankapp.user.UserMapper;
import info.mackiewicz.bankapp.user.exception.UserValidationException;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.dto.UpdateUserRequest;
import info.mackiewicz.bankapp.user.model.dto.UserResponseDto;
import info.mackiewicz.bankapp.user.service.UserService;
import info.mackiewicz.bankapp.user.validation.RequestValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing user operations.
 * Handles user creation, retrieval, update and deletion.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRegistrationService registrationService;
    private final RequestValidator requestValidator;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDto>> createUser(
            @Valid @RequestBody UserRegistrationDto registrationDto,
            BindingResult bindingResult) {
        
        long startTime = System.currentTimeMillis();
        log.debug("Received user registration request for email: {}", registrationDto.getEmail());
        
        if (bindingResult.hasErrors()) {
            log.warn("User registration validation failed: {}", bindingResult.getFieldErrors());
            String errorMessage = requestValidator.getValidationErrorMessage(bindingResult);
            log.warn("User registration validation failed: {}", errorMessage);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<UserResponseDto>error(errorMessage, HttpStatus.BAD_REQUEST));
        }

        try {
            User created = registrationService.registerUser(registrationDto);
            UserResponseDto responseDto = userMapper.toResponseDto(created);
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("Successfully created new user with ID: {}. Operation took {}ms", created.getId(), executionTime);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.created(responseDto));
        } catch (UserValidationException e) {
            log.warn("User registration failed due to validation error: {}", e.getMessage());
            log.debug("Creating error response with message: '{}' and status: {}", e.getMessage(), HttpStatus.BAD_REQUEST);
            ApiResponse<UserResponseDto> errorResponse = ApiResponse.<UserResponseDto>error(e.getMessage(), HttpStatus.BAD_REQUEST);
            log.debug("Created error response: message='{}', status={}", errorResponse.getMessage(), errorResponse.getStatus());
            return ResponseEntity.badRequest()
                    .body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@PathVariable Integer id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(ApiResponse.success(userMapper.toResponseDto(user)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<UserResponseDto>error(e.getMessage(), HttpStatus.NOT_FOUND));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponseDto>>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers().stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateUserRequest updateRequest,
            BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            String errorMessage = requestValidator.getValidationErrorMessage(bindingResult);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<UserResponseDto>error(errorMessage, HttpStatus.BAD_REQUEST));
        }

        try {
            User existingUser = userService.getUserById(id);
            existingUser = userMapper.updateUserFromRequest(existingUser, updateRequest);
            User updatedUser = userService.updateUser(existingUser);
            return ResponseEntity.ok(ApiResponse.success(userMapper.toResponseDto(updatedUser)));
        } catch (UserValidationException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<UserResponseDto>error(e.getMessage(), HttpStatus.NOT_FOUND));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Integer id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.<Void>success(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.<Void>error(e.getMessage(), HttpStatus.NOT_FOUND));
        }
    }
}
