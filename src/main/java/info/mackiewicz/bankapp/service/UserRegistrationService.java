package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.controller.UserRegistrationDto;
import info.mackiewicz.bankapp.mapper.UserMapper;
import info.mackiewicz.bankapp.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserRegistrationService {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserRegistrationService(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    public User registerUser(UserRegistrationDto registrationDto) {
        // Walidacja zgodności hasła i potwierdzenia
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        // Mapowanie DTO do encji (pole confirmPassword pomijamy)
        User user = userMapper.toUser(registrationDto);
        // Delegujemy tworzenie użytkownika do warstwy serwisu
        return userService.createUser(user);
    }
}
