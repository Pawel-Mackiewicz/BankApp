package info.mackiewicz.bankapp.repository;

import info.mackiewicz.bankapp.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<List<Account>> findAccountsByOwner_PESEL(String ownerPESEL);
}
