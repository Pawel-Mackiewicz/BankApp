package info.mackiewicz.bankapp.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.LockModeType;
import info.mackiewicz.bankapp.user.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByPESEL(String pesel);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    User getUserById(Integer id);

    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdWithPessimisticLock(Integer id);
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = ?2 WHERE u.email = ?1")
    void updatePasswordByEmail(String email, String newPassword);
}
