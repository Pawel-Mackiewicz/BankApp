package info.mackiewicz.bankapp.repository;

import info.mackiewicz.bankapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByPESEL(String pesel);

    User getUserById(Integer id);
}
