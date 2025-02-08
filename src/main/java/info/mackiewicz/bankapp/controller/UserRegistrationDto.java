package info.mackiewicz.bankapp.controller;

import info.mackiewicz.bankapp.validation.PasswordMatches;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@PasswordMatches
public class UserRegistrationDto {

    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank(message = "Lastname is required")
    private String lastname;
    @NotNull(message = "Date Of Birth is required")
    private LocalDate dateOfBirth;
    @NotBlank(message = "PESEL is required")
    private String PESEL;
    @Email
    @NotBlank(message = "Email is required")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password has to be at least 8 characters long")
    private String password;
    @NotBlank(message = "Password confirm is required")
    private String confirmPassword;
}
