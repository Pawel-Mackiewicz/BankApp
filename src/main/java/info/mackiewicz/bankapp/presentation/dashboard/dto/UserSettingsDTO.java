package info.mackiewicz.bankapp.presentation.dashboard.dto;

import info.mackiewicz.bankapp.core.user.model.interfaces.PersonalInfo;
import lombok.Value;

@Value
public class UserSettingsDTO {
    String firstname;
    String lastname;
    String phoneNumber;
    String email;
    String username;

    public static UserSettingsDTO fromUser(PersonalInfo user) {
        return new UserSettingsDTO(
            user.getFirstname(),
            user.getLastname(),
            user.getPhoneNumber().toString(),
            user.getEmail().toString(),
            user.getUsername()
        );
    }
}