package info.mackiewicz.bankapp.system.registration.controller;

import info.mackiewicz.bankapp.system.error.handling.dto.BaseApiError;
import info.mackiewicz.bankapp.system.error.handling.dto.ValidationApiError;
import info.mackiewicz.bankapp.system.registration.dto.demo.DemoRegistrationRequest;
import info.mackiewicz.bankapp.system.registration.dto.RegistrationRequest;
import info.mackiewicz.bankapp.system.registration.dto.RegistrationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/api/registration")
@Tag(name = "Registration", description = "API for user registration")
public interface RegistrationController {

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user in the system. The user will be registered with the provided details."
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "User registration details",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = RegistrationRequest.class)
            )
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegistrationResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseApiError.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationApiError.class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "User already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseApiError.class,
                                    example = """
                                            {
                                              "status": "CONFLICT",
                                              "title": "USER_ALREADY_EXISTS",
                                              "message": "User with these credentials already exists.",
                                              "path": "/api/registration/regular",
                                              "timestamp": "11-04-2025 16:18:29"
                                            }
                                            """)))
    })
    @PostMapping("/regular")
    ResponseEntity<RegistrationResponse> registerUser(@Valid @RequestBody RegistrationRequest request);

    @Operation(
            summary = "Register a new demo user",
            description = "Creates a new demo user in the system. The user will be registered with the provided email address."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegistrationResponse.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseApiError.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationApiError.class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "User already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseApiError.class,
                                    example = """
                                            {
                                              "status": "CONFLICT",
                                              "title": "USER_ALREADY_EXISTS",
                                              "message": "User with these credentials already exists.",
                                              "path": "/api/registration/demo",
                                              "timestamp": "11-04-2025 16:18:29"
                                            }
                                            """)))
    })
    @PostMapping("/demo")
    ResponseEntity<RegistrationResponse> registerDemoUser(@Valid @RequestBody DemoRegistrationRequest request);
}
