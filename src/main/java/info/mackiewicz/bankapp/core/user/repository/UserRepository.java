package info.mackiewicz.bankapp.core.user.repository;

import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.core.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.core.user.model.vo.Pesel;
import info.mackiewicz.bankapp.core.user.model.vo.PhoneNumber;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsById(@NonNull Integer id);
    boolean existsByPesel(Pesel pesel);
    boolean existsByEmail(EmailAddress email);
    boolean existsByUsername(String username);
    boolean existsByPhoneNumber(PhoneNumber phoneNumber);
        
    Optional<User> getUserById(Integer id);

    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(EmailAddress email);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByIdWithPessimisticLock(Integer id);
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = ?2 WHERE u.email = ?1")
    void updatePasswordByEmail(EmailAddress email, String newPassword);
}
