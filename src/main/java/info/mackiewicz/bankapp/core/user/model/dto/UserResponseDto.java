package info.mackiewicz.bankapp.core.user.model.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder(setterPrefix = "with")
public class UserResponseDto {
    Integer id;
    String firstname;
    String lastname;
    String email;
    String phoneNumber;
    LocalDate dateOfBirth;
    String username;
}