package info.mackiewicz.bankapp.presentation.dashboard.settings.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangeUsernameRequest {

    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters long")
    @NotBlank(message = "Username cannot be blank")
    @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "Username can only contain letters, numbers, dots, dashes and underscores")
    private String newUsername;
}