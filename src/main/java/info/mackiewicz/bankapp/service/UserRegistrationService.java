package info.mackiewicz.bankapp.service;

import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.dto.UserRegistrationDto;
import info.mackiewicz.bankapp.mapper.UserMapper;
import info.mackiewicz.bankapp.model.User;

@Service
public class UserRegistrationService {

    private final UserService userService;
    private final UserMapper userMapper;
    private final AccountService accountService;

    // Only allow letters (English and Polish)
    private static final String LETTERS_REGEX = "^[A-Za-zĄĆĘŁŃÓŚŹŻąćęłńóśźż]+$";

    public UserRegistrationService(UserService userService, UserMapper userMapper, AccountService accountService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.accountService = accountService;
    }

    public User registerUser(UserRegistrationDto registrationDto) {
        User user = userMapper.toUser(registrationDto);

        // Validate first name
        if (!isValidLetters(user.getFirstname())) {
            throw new IllegalArgumentException("Invalid first name: only letters allowed.");
        }
        // Validate last name
        if (!isValidLetters(user.getLastname())) {
            throw new IllegalArgumentException("Invalid last name: only letters allowed.");
        }

        User createdUser = userService.createUser(user);

        accountService.createAccount(createdUser.getId());

        return createdUser;
    }

    private boolean isValidLetters(String input) {
        return input != null && input.matches(LETTERS_REGEX);
    }
}
