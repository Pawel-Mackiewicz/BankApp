package info.mackiewicz.bankapp.system.registration.dto;

import info.mackiewicz.bankapp.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class RegistrationMapperImpl implements RegistrationMapper {

    @Override
    public User toUser(RegistrationRequest request) {
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
    public RegistrationResponse toResponse(User user) {
        return RegistrationResponse.builder()
                .withEmail(user.getEmail().getValue())
                .withUsername(user.getUsername())
                .withFirstname(user.getFirstname())
                .withLastname(user.getLastname())
                .build();
    }

}
