package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.dto.UserRegistrationDto;
import info.mackiewicz.bankapp.mapper.UserMapper;
import info.mackiewicz.bankapp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.when;

class UserRegistrationServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationServiceTest.class);

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private UserRegistrationService registrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser() {
        logger.info("testRegisterUser: Starting test");
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setFirstname("John");
        registrationDto.setLastname("Doe");
        registrationDto.setEmail("john.doe@example.com");
        registrationDto.setPassword("password");

        User user = new User();
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password");

        when(userMapper.toUser(registrationDto)).thenReturn(user);
        when(userService.createUser(user)).thenReturn(user);

        registrationService.registerUser(registrationDto);

        logger.info("testRegisterUser: Test passed");
    }
}