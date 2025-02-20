package info.mackiewicz.bankapp.repository;

import info.mackiewicz.bankapp.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByPESEL(String pesel);

    boolean existsByUsername(String username);

    User getUserById(Integer id);

    Optional<User> findByUsername(String username);
}
