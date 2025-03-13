package info.mackiewicz.bankapp.user.service;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.mackiewicz.bankapp.user.model.User;

class UserServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);

    @Mock
    private UserCreationService userCreationService;

    @Mock
    private UserQueryService userQueryService;

    @Mock
    private UserOperationsService userOperationsService;

    @Mock
    private UserValidationService userValidationService;

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
        User savedUser = new User();
        savedUser.setId(1);

        when(userCreationService.createUser(user)).thenReturn(savedUser);

        userService.createUser(user);

        verify(userCreationService).createUser(user);
        logger.info("testCreateUser: Test passed");
    }

    @Test
    void testUpdateUser() {
        logger.info("testUpdateUser: Starting test");
        User user = new User();
        user.setId(1);
        User updatedUser = new User();
        updatedUser.setId(1);

        when(userOperationsService.updateUser(user)).thenReturn(updatedUser);

        userService.updateUser(user);

        verify(userOperationsService).updateUser(user);
        logger.info("testUpdateUser: Test passed");
    }

    @Test
    void testGetUserById() {
        logger.info("testGetUserById: Starting test");
        Integer userId = 1;
        User user = new User();
        user.setId(userId);

        when(userQueryService.getUserById(userId)).thenReturn(user);

        userService.getUserById(userId);

        verify(userQueryService).getUserById(userId);
        logger.info("testGetUserById: Test passed");
    }

    @Test
    void testGetUserByUsername() {
        logger.info("testGetUserByUsername: Starting test");
        String username = "testuser";
        User user = new User();
        user.setUsername(username);

        when(userQueryService.getUserByUsername(username)).thenReturn(user);

        userService.getUserByUsername(username);

        verify(userQueryService).getUserByUsername(username);
        logger.info("testGetUserByUsername: Test passed");
    }

    @Test
    void testGetAllUsers() {
        logger.info("testGetAllUsers: Starting test");
        List<User> users = List.of(new User());

        when(userQueryService.getAllUsers()).thenReturn(users);

        userService.getAllUsers();

        verify(userQueryService).getAllUsers();
        logger.info("testGetAllUsers: Test passed");
    }

    @Test
    void testDeleteUser() {
        logger.info("testDeleteUser: Starting test");
        Integer userId = 1;

        doNothing().when(userOperationsService).deleteUser(userId);

        userService.deleteUser(userId);

        verify(userOperationsService).deleteUser(userId);
        logger.info("testDeleteUser: Test passed");
    }

    @Test
    void testCheckUsernameExists() {
        logger.info("testCheckUsernameExists: Starting test");
        String username = "testuser";

        when(userQueryService.userExistsByUsername(username)).thenReturn(true);

        userService.userExistsByUsername(username);

        verify(userQueryService).userExistsByUsername(username);
        logger.info("testCheckUsernameExists: Test passed");
    }
}