package info.mackiewicz.bankapp.presentation.dashboard.dto;

import info.mackiewicz.bankapp.user.model.User;
import lombok.Value;

@Value
public class UserSettingsDTO {
    String firstname;
    String lastname;
    String phoneNumber;
    String email;
    String username;

    public static UserSettingsDTO fromUser(User user) {
        return new UserSettingsDTO(
            user.getFirstname(),
            user.getLastname(),
            user.getPhoneNumber().toString(), // wykorzysta metodę toString() z value object
            user.getEmail().toString(),       // wykorzysta metodę toString() z value object
            user.getUsername()
        );
    }
}