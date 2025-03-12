package info.mackiewicz.bankapp.user;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.presentation.auth.dto.UserRegistrationDto;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.dto.UpdateUserRequest;
import info.mackiewicz.bankapp.user.model.vo.Email;
import info.mackiewicz.bankapp.user.model.vo.PhoneNumber;

@Component
public class UserMapper {
    
    public User toUser(UserRegistrationDto dto) {
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
            existingUser.setEmail(new Email(request.getEmail()));
        }
        if (request.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(new PhoneNumber(request.getPhoneNumber()));
        }
        if (request.getPassword() != null) {
            existingUser.setPassword(request.getPassword());
        }
        return existingUser;
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
