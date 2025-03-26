package info.mackiewicz.bankapp.user.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import info.mackiewicz.bankapp.presentation.auth.dto.UserRegistrationDto;
import info.mackiewicz.bankapp.presentation.auth.service.UserRegistrationService;
import info.mackiewicz.bankapp.shared.web.response.RestResponse;
import info.mackiewicz.bankapp.shared.web.response.RestResponseFactory;
import info.mackiewicz.bankapp.user.UserMapper;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.dto.UpdateUserRequest;
import info.mackiewicz.bankapp.user.model.dto.UserResponseDto;
import info.mackiewicz.bankapp.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for managing user operations.
 * Provides endpoints for user CRUD operations with standardized responses.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRegistrationService registrationService;
    private final UserMapper userMapper;
    private final RestResponseFactory restResponseBuilder;

    @Operation(summary = "Create a new user",
            description = "Creates a new user in the system. The user will be registered with the provided details.")
    @PostMapping
    public ResponseEntity<RestResponse<UserResponseDto>> createUser(
            @Valid @RequestBody UserRegistrationDto registrationDto) {
        User created = registrationService.registerUser(registrationDto);
        return restResponseBuilder.created(userMapper.toResponseDto(created));
    }

    @Operation(summary = "Get user by ID",
            description = "Retrieves a user by their unique ID. Returns user details if found, otherwise returns 404.")
    @GetMapping("/{id}")
    public ResponseEntity<RestResponse<UserResponseDto>> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        return restResponseBuilder.ok(userMapper.toResponseDto(user));
    }

    @GetMapping
    public ResponseEntity<RestResponse<List<UserResponseDto>>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers().stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
        return restResponseBuilder.ok(users);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestResponse<UserResponseDto>> updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateUserRequest updateRequest) {
        User existingUser = userService.getUserById(id);
        existingUser = userMapper.updateUserFromRequest(existingUser, updateRequest);
        User updatedUser = userService.updateUser(existingUser);
        return restResponseBuilder.ok(userMapper.toResponseDto(updatedUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RestResponse<Void>> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return restResponseBuilder.deleted();
    }
}
