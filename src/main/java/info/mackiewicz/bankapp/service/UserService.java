package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
//BY chatGPT
@Service
public class UserService {

    private final UserRepository userRepository;

    // Constructor injection of UserRepository
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Creates a new User and saves it in the repository.
     *
     * @param user the User to create
     * @return the saved User with an assigned ID
     */
    public User createUser(User user) {
        if (userRepository.existsByPESEL(user.getPESEL())) {
            throw new IllegalArgumentException("User with the same PESEL already exists");
        }
        return userRepository.save(user);
    }

        /**
         * Retrieves a User by its ID.
         *
         * @param id the ID of the User
         * @return an Optional containing the User if found, otherwise empty
         */
        public User getUserById(Integer id) {
            return userRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
        }

        /**
         * Retrieves all User entities.
         *
         * @return a list of all Users
         */
        public List<User> getAllUsers() {
            return userRepository.findAll();
        }

        /**
         * Updates an existing User.
         * The User must have a non-null ID.
         *
         * @param user the User with updated details
         * @return the updated User
         * @throws IllegalArgumentException if the User ID is null
         */
        @Transactional
        public User updateUser(User user) {
            if (user.getId() == null) {
                throw new IllegalArgumentException("User ID must not be null for update.");
            }
            return userRepository.save(user);
        }

        /**
         * Deletes the User with the specified ID.
         *
         * @param id the ID of the User to delete
         */
        public void deleteUser(Integer id) {
            userRepository.deleteById(id);
        }
}
