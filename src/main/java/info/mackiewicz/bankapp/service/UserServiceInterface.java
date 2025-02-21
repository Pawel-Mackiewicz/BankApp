package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.model.User;
import java.util.List;

public interface UserServiceInterface {
    User createUser(User user);
    User getUserById(Integer id);
    User getUserByUsername(String username);
    List<User> getAllUsers();
    User updateUser(User user);
    void deleteUser(Integer id);
    boolean checkUsernameExists(String username);
}