package info.mackiewicz.bankapp.user.service;

import java.util.List;

import info.mackiewicz.bankapp.user.model.User;

public interface UserServiceInterface {
    User createUser(User user);
    User getUserById(Integer id);
    User getUserByUsername(String username);
    List<User> getAllUsers();
    User updateUser(User user);
    void deleteUser(Integer id);
    boolean checkUsernameExists(String username);
    boolean userExistsByEmail(String email);
    User getUserByEmail(String email);
}