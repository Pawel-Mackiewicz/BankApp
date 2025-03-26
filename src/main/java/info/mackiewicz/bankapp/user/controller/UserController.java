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
import info.mackiewicz.bankapp.shared.web.dto.BaseApiError;
import info.mackiewicz.bankapp.shared.web.dto.ValidationApiError;
import info.mackiewicz.bankapp.shared.web.response.RestResponse;
import info.mackiewicz.bankapp.shared.web.response.RestResponseFactory;
import info.mackiewicz.bankapp.user.UserMapper;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.dto.UpdateUserRequest;
import info.mackiewicz.bankapp.user.model.dto.UserResponseDto;
import info.mackiewicz.bankapp.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Parameter(
        required = true,
        description = "User registration details",
        name = "registrationDto",
        schema = @Schema(implementation = UserRegistrationDto.class)    
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
           description = "User created successfully",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = UserResponseDto.class)
                )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = BaseApiError.class)
                )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Validation error",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ValidationApiError.class)
                )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "User already exists",
            content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = BaseApiError.class)
                )
        )
    })
    @PostMapping
    public ResponseEntity<RestResponse<UserResponseDto>> createUser(
            @Valid @RequestBody UserRegistrationDto registrationDto) {
        User created = registrationService.registerUser(registrationDto);
        return restResponseBuilder.created(userMapper.toResponseDto(created));
    }

    @Operation(summary = "Get user by ID",
            description =   "Retrieves a user by their unique ID. Returns user details if found, otherwise returns 404. " +
                            "Sensitive user information like password is never returned in the response.")
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "User found and returned successfully",
                content = @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = UserResponseDto.class)
                    )
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "User not found",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BaseApiError.class)
                    )
                )
    })
    @GetMapping("/{id}")
    public ResponseEntity<RestResponse<UserResponseDto>> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        return restResponseBuilder.ok(userMapper.toResponseDto(user));
    }

    @Operation(summary = "Get all users",
            description = "Retrieves a list of all users in the system. Returns an empty list if no users are found.")
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200",
                description = "List of users retrieved successfully",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDto.class)
                    )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "No users found",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BaseApiError.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<RestResponse<List<UserResponseDto>>> getAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers().stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
        return restResponseBuilder.ok(users);
    }

    @Operation(summary = "Update user by ID",
            description = "Updates the details of an existing user. You can update one or more information. Returns the updated user details.")
    @Parameter(
            required = true,
            description = "User ID to update",
            name = "id",
            schema = @Schema(implementation = UpdateUserRequest.class)
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200",
                description = "User updated successfully",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserResponseDto.class)
                    )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid input data",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BaseApiError.class)
                    )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "User not found",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BaseApiError.class)
                    )
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<RestResponse<UserResponseDto>> updateUser(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateUserRequest updateRequest) {
        User existingUser = userService.getUserById(id);
        existingUser = userMapper.updateUserFromRequest(existingUser, updateRequest);
        User updatedUser = userService.updateUser(existingUser);
        return restResponseBuilder.ok(userMapper.toResponseDto(updatedUser));
    }

    @Operation(summary = "Delete user by ID",
            description = "THIS METHOD IS NOT AVAILABLE RIGHT NOW. IF YOU NEED IT, MAKE ISSUE." + 
            " Deletes a user by their unique ID. Returns 204 No Content if the user was deleted successfully.")
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "204",
                description = "User deleted successfully"
            ),
            @ApiResponse(
                responseCode = "404",
                description = "User not found",
                content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = BaseApiError.class)
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<RestResponse<Void>> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return restResponseBuilder.deleted();
    }
}
