package info.mackiewicz.bankapp.core.user.model.dto;

import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.presentation.auth.dto.UserRegistrationRequest;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUser(UserRegistrationRequest dto) {
        User user = User.builder()
                .withFirstname(capitalize(dto.getFirstname()))
                .withLastname(capitalize(dto.getLastname()))
                .withPesel(dto.getPesel())
                .withDateOfBirth(dto.getDateOfBirth())
                .withEmail(dto.getEmail())
                .withPhoneNumber(dto.getPhoneNumber())
                .withPassword(dto.getPassword())
                .build();
        return user;
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
