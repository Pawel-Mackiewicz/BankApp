package info.mackiewicz.bankapp.user.service;

import info.mackiewicz.bankapp.security.service.PasswordService;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

class UserServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private UsernameGeneratorService usernameGeneratorService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser() {
        logger.info("testCreateUser: Starting test");
        User user = new User();

        when(passwordService.ensurePasswordEncoded(user)).thenReturn(user);
        when(usernameGeneratorService.generateUsername(user)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        userService.createUser(user);

        logger.info("testCreateUser: Test passed");
    }

    @Test
    void testUpdateUser() {
        logger.info("testUpdateUser: Starting test");
        User user = new User();
        user.setId(1);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(passwordService.ensurePasswordEncoded(user)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        userService.updateUser(user);

        logger.info("testUpdateUser: Test passed");
    }

    @Test
    void testGetUserById() {
        logger.info("testGetUserById: Starting test");
        User user = new User();
        user.setId(1);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.getUserById(user.getId());

        logger.info("testGetUserById: Test passed");
    }

    @Test
    void testGetUserByUsername() {
        logger.info("testGetUserByUsername: Starting test");
        User user = new User();
        user.setUsername("testuser");

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        userService.getUserByUsername(user.getUsername());

        logger.info("testGetUserByUsername: Test passed");
    }

    @Test
    void testGetAllUsers() {
        logger.info("testGetAllUsers: Starting test");
        when(userRepository.findAll()).thenReturn(List.of(new User()));

        userService.getAllUsers();

        logger.info("testGetAllUsers: Test passed");
    }

    @Test
    void testDeleteUser() {
        logger.info("testDeleteUser: Starting test");
        User user = new User();
        user.setId(1);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.deleteUser(user.getId());

        logger.info("testDeleteUser: Test passed");
    }

    @Test
    void testCheckUsernameExists() {
        logger.info("testCheckUsernameExists: Starting test");
        String username = "testuser";

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));

        userService.checkUsernameExists(username);

        logger.info("testCheckUsernameExists: Test passed");
    }
}