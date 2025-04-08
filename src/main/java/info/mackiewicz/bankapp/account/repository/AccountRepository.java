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
 * Retrieves accounts associated with the specified owner's PESEL.
 *
 * <p>This method returns an Optional containing a list of accounts linked to the provided PESEL.
 * If no accounts are found, the Optional will be empty.</p>
 *
 * @param ownerPESEL the PESEL identifier of the account owner
 * @return an Optional containing a list of matching accounts, or an empty Optional if none are found
 */
Optional<List<Account>> findAccountsByOwner_pesel(Pesel ownerPESEL);
    /**
 * Retrieves an Optional containing a list of accounts associated with the specified owner's username.
 *
 * <p>If no accounts are found for the given username, an empty Optional is returned.</p>
 *
 * @param ownerUsername the username of the account owner
 * @return an Optional with a list of accounts, or an empty Optional if none are found
 */
Optional<List<Account>> findAccountsByOwner_username(String ownerUsername);
    /**
 * Retrieves all accounts associated with the specified owner's ID.
 *
 * @param ownerId the unique identifier of the account owner
 * @return an Optional containing a list of accounts if any exist; otherwise, an empty Optional
 */
Optional<List<Account>> findAccountsByOwner_id(Integer ownerId);
    /**
 * Finds the first account associated with the specified owner's email.
 *
 * @param email the email linked to the account owner
 * @return an Optional containing the matching account if one exists, otherwise an empty Optional
 */
Optional<Account> findFirstByOwner_email(Email email);
    
    /**
 * Retrieves an account corresponding to the specified IBAN.
 *
 * @param iban the IBAN identifier of the account
 * @return an Optional containing the account if found, or an empty Optional if no account exists for the provided IBAN
 */
Optional<Account> findByIban(Iban iban);

    boolean existsByOwner_email(Email email);
}
