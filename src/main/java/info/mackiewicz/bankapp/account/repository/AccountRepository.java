package info.mackiewicz.bankapp.account.repository;

import java.util.List;
import java.util.Optional;

import org.iban4j.Iban;
import org.springframework.data.jpa.repository.JpaRepository;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.user.model.vo.Email;
import info.mackiewicz.bankapp.user.model.vo.Pesel;


public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<List<Account>> findAccountsByOwner_pesel(Pesel ownerPESEL);
    Optional<List<Account>> findAccountsByOwner_username(String ownerUsername);
    Optional<List<Account>> findAccountsByOwner_id(Integer ownerId);
    List<Account> findByIbanIsNull();
    Optional<Account> findFirstByOwner_email(Email email);
    
    Optional<Account> findByIban(Iban iban);
}
