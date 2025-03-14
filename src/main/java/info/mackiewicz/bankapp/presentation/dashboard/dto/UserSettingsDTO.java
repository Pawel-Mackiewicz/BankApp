package info.mackiewicz.bankapp.presentation.dashboard.dto;

import info.mackiewicz.bankapp.user.model.User;
import lombok.Value;

//TODO: THINK ABOUT REMOVING THIS CLASS AND USE PersonalInfo interface instead
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
            user.getPhoneNumber().toString(),
            user.getEmail().toString(),
            user.getUsername()
        );
    }
}