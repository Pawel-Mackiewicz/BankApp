package info.mackiewicz.bankapp.repository;

import info.mackiewicz.bankapp.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByPESEL(String pesel);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    User getUserById(Integer id);

    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = ?2 WHERE u.email = ?1")
    void updatePasswordByEmail(String email, String newPassword);
}
