package info.mackiewicz.bankapp.mapper;

import info.mackiewicz.bankapp.dto.UserRegistrationDto;
import info.mackiewicz.bankapp.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toUser(UserRegistrationDto dto) {
        User user = new User();
        user.setFirstname(dto.getName().toLowerCase());
        user.setLastname(dto.getLastname().toLowerCase());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setPESEL(dto.getPESEL());
        user.setEmail(dto.getEmail().toLowerCase());
        user.setPassword(dto.getPassword()); // password will be encoded in service layer
        return user;
    }
}
