package info.mackiewicz.bankapp.account.repository;

import java.util.List;
import java.util.Optional;

import org.iban4j.Iban;
import org.springframework.data.jpa.repository.JpaRepository;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.user.model.vo.Pesel;


public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<List<Account>> findAccountsByOwner_pesel(Pesel ownerPESEL);
    Optional<List<Account>> findAccountsByOwner_username(String ownerUsername);
    Optional<List<Account>> findAccountsByOwner_id(Integer ownerId);
    Optional<Account> findFirstByOwner_email(EmailAddress email);
    
    Optional<Account> findByIban(Iban iban);

    boolean existsByOwner_email(EmailAddress email);
}
