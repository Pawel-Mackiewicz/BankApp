package info.mackiewicz.bankapp.presentation.auth.registration.dto;

import info.mackiewicz.bankapp.core.user.model.User;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class RegistrationMapperImpl implements RegistrationMapper {

    @Override
    public User toUser(@NonNull RegistrationRequest request) {
        return User.builder()
                .withFirstname(request.getFirstname())
                .withLastname(request.getLastname())
                .withPesel(request.getPesel())
                .withDateOfBirth(request.getDateOfBirth())
                .withEmail(request.getEmail())
                .withPhoneNumber(request.getPhoneNumber())
                .withPassword(request.getPassword())
                .build();
    }

    @Override
    public RegistrationResponse toResponse(@NonNull User user) {
        return RegistrationResponse.builder()
                .withEmail(user.getEmail().getValue())
                .withUsername(user.getUsername())
                .withFirstname(user.getFirstname())
                .withLastname(user.getLastname())
                .build();
    }

}
