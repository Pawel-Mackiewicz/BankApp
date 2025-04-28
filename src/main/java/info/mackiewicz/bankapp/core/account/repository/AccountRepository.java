package info.mackiewicz.bankapp.core.account.repository;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.user.model.vo.Pesel;
import org.iban4j.Iban;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<List<Account>> findAccountsByOwner_pesel(Pesel ownerPESEL);
    Optional<List<Account>> findAccountsByOwner_username(String ownerUsername);
    Optional<List<Account>> findAccountsByOwner_id(Integer ownerId);
    Optional<Account> findFirstByOwner_email(EmailAddress email);
    
    Optional<Account> findByIban(Iban iban);

    boolean existsByOwner_email(EmailAddress email);
}
