package info.mackiewicz.bankapp.core.user.repository;

import info.mackiewicz.bankapp.core.user.model.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    Optional<AdminUser> findByUsername(String username);
}
