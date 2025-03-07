package info.mackiewicz.bankapp.user;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.presentation.auth.dto.UserRegistrationDto;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.dto.UpdateUserRequest;

@Component
public class UserMapper {
    
    public User toUser(UserRegistrationDto dto) {
        User user = new User();
        
        user.setFirstname(capitalize(dto.getFirstname()));
        user.setLastname(capitalize(dto.getLastname()));
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setPESEL(dto.getPESEL());
        user.setEmail(dto.getEmail().toLowerCase());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setPassword(dto.getPassword());
        return user;
    }

    public User updateUserFromRequest(User existingUser, UpdateUserRequest request) {
        if (request.getFirstname() != null) {
            existingUser.setFirstname(capitalize(request.getFirstname()));
        }
        if (request.getLastname() != null) {
            existingUser.setLastname(capitalize(request.getLastname()));
        }
        if (request.getEmail() != null) {
            existingUser.setEmail(request.getEmail().toLowerCase());
        }
        if (request.getPESEL() != null) {
            existingUser.setPESEL(request.getPESEL());
        }
        if (request.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(request.getPhoneNumber());
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
