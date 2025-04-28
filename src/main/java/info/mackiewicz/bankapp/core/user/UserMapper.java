package info.mackiewicz.bankapp.core.user;

import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.core.user.model.dto.UpdateUserRequest;
import info.mackiewicz.bankapp.core.user.model.dto.UserResponseDto;
import info.mackiewicz.bankapp.core.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.core.user.model.vo.PhoneNumber;
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

    public User updateUserFromRequest(User existingUser, UpdateUserRequest request) {
        if (request.getEmail() != null) {
            existingUser.setEmail(new EmailAddress(request.getEmail()));
        }
        if (request.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(new PhoneNumber(request.getPhoneNumber()));
        }
        if (request.getPassword() != null) {
            existingUser.setPassword(request.getPassword());
        }
        return existingUser;
    }

    public UserResponseDto toResponseDto(User user) {
        return UserResponseDto.builder()
                .withId(user.getId())
                .withFirstname(user.getFirstname())
                .withLastname(user.getLastname())
                .withEmail(user.getEmail().toString())
                .withPhoneNumber(user.getPhoneNumber().toString())
                .withDateOfBirth(user.getDateOfBirth())
                .withUsername(user.getUsername())
                .build();
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
