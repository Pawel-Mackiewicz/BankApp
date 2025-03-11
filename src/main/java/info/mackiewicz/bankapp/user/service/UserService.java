package info.mackiewicz.bankapp.user.service;

import info.mackiewicz.bankapp.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Facade service that coordinates all user-related operations
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class UserService implements UserServiceInterface {

    private final UserCreationService userCreationService;
    private final UserQueryService userQueryService;
    private final UserOperationsService userOperationsService;
    private final UserValidationService userValidationService;

    @Override
    public User createUser(User user) {
        userValidationService.validateNewUser(user);
        return userCreationService.createUser(user);
    }

    @Override
    public User updateUser(User user) {
        userValidationService.validateUserExists(user.getId());
        return userOperationsService.updateUser(user);
    }

    @Override
    public User getUserById(Integer id) {
        return userQueryService.getUserById(id);
    }

    @Override
    public User getUserByIdWithPessimisticLock(Integer id) {
        return userQueryService.getUserByIdWithPessimisticLock(id);
    }

    @Override
    public User getUserByUsername(String username) {
        return userQueryService.getUserByUsername(username);
    }

    @Override
    public List<User> getAllUsers() {
        return userQueryService.getAllUsers();
    }

    @Override
    public void deleteUser(Integer id) {
        userOperationsService.deleteUser(id);
    }

    @Override
    public boolean checkUsernameExists(String username) {
        return userQueryService.checkUsernameExists(username);
    }

    @Override
    public boolean userExistsByEmail(String email) {
        return userQueryService.userExistsByEmail(email);
    }

    @Override
    public User getUserByEmail(String email) {
        return userQueryService.getUserByEmail(email);
    }

    public void changeUsersPassword(String email, String newPassword) {
        userOperationsService.changeUsersPassword(email, newPassword);
    }
}
