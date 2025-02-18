package info.mackiewicz.bankapp.mapper;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.dto.UserRegistrationDto;
import info.mackiewicz.bankapp.model.User;

@Component
public class UserMapper {
    public User toUser(UserRegistrationDto dto) {
        User user = new User();
        
        user.setFirstname(capitalize(dto.getFirstname()));
        user.setLastname(capitalize(dto.getLastname()));
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setPESEL(dto.getPESEL());
        user.setEmail(dto.getEmail().toLowerCase());
        user.setPassword(dto.getPassword()); // password will be encoded in service layer
        return user;
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
