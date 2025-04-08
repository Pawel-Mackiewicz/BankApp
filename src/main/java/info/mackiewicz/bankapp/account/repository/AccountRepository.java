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
 * Retrieves accounts associated with the given owner's PESEL.
 *
 * @param ownerPESEL the PESEL identifier of the account owner
 * @return an Optional containing a list of the owner's accounts, or an empty Optional if none are found
 */
Optional<List<Account>> findAccountsByOwner_pesel(Pesel ownerPESEL);
    /**
 * Retrieves accounts associated with the specified owner's username.
 *
 * @param ownerUsername the username of the account owner
 * @return an Optional containing a list of Account objects if any accounts are found; otherwise, an empty Optional
 */
Optional<List<Account>> findAccountsByOwner_username(String ownerUsername);
    /**
 * Retrieves a list of accounts associated with the owner by their unique ID.
 *
 * @param ownerId the unique identifier of the account owner
 * @return an Optional containing the list of accounts if found, otherwise an empty Optional
 */
Optional<List<Account>> findAccountsByOwner_id(Integer ownerId);
    /**
 * Retrieves the first account associated with the specified owner email.
 *
 * @param email the owner's email used as the search criterion
 * @return an Optional containing the matching Account if found; otherwise, an empty Optional
 */
Optional<Account> findFirstByOwner_email(Email email);
    
    /**
 * Retrieves the account associated with the specified IBAN.
 *
 * @param iban the International Bank Account Number to search for
 * @return an Optional containing the account if it exists, or an empty Optional otherwise
 */
Optional<Account> findByIban(Iban iban);

    boolean existsByOwner_email(Email email);
}
