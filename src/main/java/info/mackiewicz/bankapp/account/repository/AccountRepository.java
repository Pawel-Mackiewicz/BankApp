package info.mackiewicz.bankapp.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import info.mackiewicz.bankapp.account.model.Account;

import java.util.List;
import java.util.Optional;


public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<List<Account>> findAccountsByOwner_PESEL(String ownerPESEL);
    Optional<List<Account>> findAccountsByOwner_username(String ownerUsername);
    Optional<List<Account>> findAccountsByOwner_id(Integer ownerId);
    List<Account> findByIbanIsNull();
    Optional<Account> findFirstByOwner_email(String email);
    Optional<Account> findByIban(String iban);
}
