package info.mackiewicz.bankapp.user.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {

    private String firstname;
    private String lastname;

    @Email(message = "Please provide a valid email address")
    private String email;

    @Pattern(regexp = "\\d{11}", message = "PESEL must be exactly 11 digits")
    private String PESEL;

    @Pattern(regexp = "^(\\+48\\d{9}|0\\d{9}|[1-9]\\d{8})$", 
            message = "Invalid phone number format. Use +48XXXXXXXXX, 0XXXXXXXXX or XXXXXXXXX format")
    private String phoneNumber;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", 
    message = "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character")
    private String password;

}
