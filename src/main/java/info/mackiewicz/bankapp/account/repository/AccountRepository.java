package info.mackiewicz.bankapp.account.repository;

import java.util.List;
import java.util.Optional;

import org.iban4j.Iban;
import org.springframework.data.jpa.repository.JpaRepository;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.user.model.vo.Email;
import info.mackiewicz.bankapp.user.model.vo.Pesel;


public interface AccountRepository extends JpaRepository<Account, Integer> {
    /**
 * Retrieves a list of accounts associated with the specified owner's PESEL number.
 *
 * @param ownerPESEL the PESEL identifier of the account owner
 * @return an Optional containing the list of accounts if found, otherwise an empty Optional
 */
Optional<List<Account>> findAccountsByOwner_pesel(Pesel ownerPESEL);
    /**
 * Retrieves a list of accounts associated with the specified owner's username.
 *
 * @param ownerUsername the username of the account owner to search for
 * @return an Optional containing a list of matching accounts if any are found, or an empty Optional otherwise
 */
Optional<List<Account>> findAccountsByOwner_username(String ownerUsername);
    /**
 * Retrieves a list of accounts associated with the specified owner ID.
 *
 * @param ownerId the unique identifier of the account owner
 * @return an Optional containing the list of accounts if they exist, or an empty Optional otherwise
 */
Optional<List<Account>> findAccountsByOwner_id(Integer ownerId);
    /**
 * Finds the first account associated with the specified owner's email.
 *
 * @param email the email of the account owner
 * @return an Optional containing the first matching account, or an empty Optional if no account exists
 */
Optional<Account> findFirstByOwner_email(Email email);
    
    /**
 * Searches for an account by its IBAN.
 *
 * @param iban the IBAN of the account to retrieve
 * @return an Optional containing the corresponding Account if found, or an empty Optional otherwise
 */
Optional<Account> findByIban(Iban iban);

    boolean existsByOwner_email(Email email);
}
