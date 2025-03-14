package info.mackiewicz.bankapp.presentation.auth.dto;

import java.time.LocalDate;

/**
 * DTO for user responses.
 * Contains user information safe to expose via API.
 */
public record UserResponse(
    Integer id,
    String firstname,
    String lastname,
    String pesel,
    String email,
    String phoneNumber,
    LocalDate dateOfBirth,
    String username
) {}