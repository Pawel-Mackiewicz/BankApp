package info.mackiewicz.bankapp.core.user.model.dto;

import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.presentation.auth.dto.UserRegistrationRequest;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUser(@NonNull UserRegistrationRequest dto) {
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

    private String capitalize(@NonNull String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
