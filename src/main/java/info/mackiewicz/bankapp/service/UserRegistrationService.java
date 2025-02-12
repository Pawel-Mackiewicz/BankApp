package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.dto.UserRegistrationDto;
import info.mackiewicz.bankapp.mapper.UserMapper;
import info.mackiewicz.bankapp.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserRegistrationService {

    private final UserService userService;
    private final UserMapper userMapper;
    private final AccountService accountService;

    public UserRegistrationService(UserService userService, UserMapper userMapper, AccountService accountService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.accountService = accountService;
    }

    public User registerUser(UserRegistrationDto registrationDto) {
        User user = userMapper.toUser(registrationDto);
        User createdUser = userService.createUser(user);

        accountService.createAccount(createdUser.getId());

        return createdUser;
    }
}
