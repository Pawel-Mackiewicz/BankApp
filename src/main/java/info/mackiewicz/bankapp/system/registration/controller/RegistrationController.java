package info.mackiewicz.bankapp.system.registration.controller;

import info.mackiewicz.bankapp.presentation.auth.dto.UserRegistrationDto;
import info.mackiewicz.bankapp.shared.web.dto.BaseApiError;
import info.mackiewicz.bankapp.shared.web.dto.ValidationApiError;
import info.mackiewicz.bankapp.shared.web.response.RestResponse;
import info.mackiewicz.bankapp.user.model.dto.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/registration")
public interface RegistrationController {

    @Operation(summary = "Register a new user", description = "Creates a new user in the system. The user will be registered with the provided details.")
    @RequestBody(required = true, description = "User registration details", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserRegistrationDto.class), examples = {
            @ExampleObject(name = "Standard registration", value = "{\n" +
                    "  \"email\": \"user@example.com\",\n" +
                    "  \"password\": \"secureP@ssword123\",\n" +
                    "  \"confirmPassword\": \"secureP@ssword123\",\n" +
                    "  \"firstName\": \"John\",\n" +
                    "  \"lastName\": \"Smith\",\n" +
                    "  \"pesel\": \"12345678901\",\n" +
                    "  \"phoneNumber\": \"+48123456789\",\n" +
                    "  \"dateOfBirth\": \"1990-01-01\"\n" +

                    "}")
    }))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseApiError.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationApiError.class))),
            @ApiResponse(responseCode = "409", description = "User already exists", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BaseApiError.class)))
    })
    ResponseEntity<RestResponse<UserResponseDto>> registerUser(
            UserRegistrationDto registrationDto);
}
