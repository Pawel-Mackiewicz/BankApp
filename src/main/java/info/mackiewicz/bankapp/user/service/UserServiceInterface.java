package info.mackiewicz.bankapp.user.service;

import info.mackiewicz.bankapp.user.model.User;
import java.util.List;

public interface UserServiceInterface {
    User createUser(User user);
    User updateUser(User user);
    User getUserById(Integer id);
    User getUserByIdWithPessimisticLock(Integer id);
    User getUserByUsername(String username);
    List<User> getAllUsers();
    void deleteUser(Integer id);
    boolean checkUsernameExists(String username);
    boolean userExistsByEmail(String email);
    User getUserByEmail(String email);
}