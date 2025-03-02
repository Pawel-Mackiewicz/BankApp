package info.mackiewicz.bankapp.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import info.mackiewicz.bankapp.user.model.AdminUser;

public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {
    Optional<AdminUser> findByUsername(String username);
}
