package info.mackiewicz.bankapp.core.account.repository;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.user.model.vo.EmailAddress;
import info.mackiewicz.bankapp.core.user.model.vo.Pesel;
import org.iban4j.Iban;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<List<Account>> findAccountsByOwner_pesel(Pesel ownerPESEL);
    Optional<List<Account>> findAccountsByOwner_username(String ownerUsername);
    Optional<List<Account>> findAccountsByOwner_id(Integer ownerId);
    Optional<Account> findFirstByOwner_email(EmailAddress email);
    
    Optional<Account> findByIban(Iban iban);

    boolean existsByOwner_email(EmailAddress email);

    /**
     * Retrieves the balance of an account by its unique identifier.
     *
     * @param accountId the unique identifier of the account for which the balance is being retrieved
     *
     * @return an Optional containing the balance as a BigDecimal if the account is found, or an empty Optional otherwise
     */
    @Query("""
            SELECT a.balance 
                FROM Account a 
                WHERE a.id = :accountId
            """)
    Optional<BigDecimal> findBalanceById(@Param("accountId") Integer accountId);

}
