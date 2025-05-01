package info.mackiewicz.bankapp.core.transaction.repository;

import info.mackiewicz.bankapp.core.transaction.model.Transaction;
import info.mackiewicz.bankapp.core.transaction.model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

       @Query("""
               SELECT t FROM Transaction t
                                    WHERE t.sourceAccount.id = :accountId OR t.destinationAccount.id = :accountId
                                    ORDER BY t.date DESC
               """)
       Optional<List<Transaction>> findByAccountId(@Param("accountId") int accountId);

       List<Transaction> findByStatus(TransactionStatus status);

       @Query("""
               SELECT t FROM Transaction t
                                    WHERE t.sourceAccount.id = :accountId OR t.destinationAccount.id = :accountId
                                    ORDER BY t.date DESC
                                    LIMIT :limit
               """)
       Optional<List<Transaction>> findTopNByAccountIdOrderByCreatedDesc(
                     @Param("accountId") int accountId,
                     @Param("limit") int limit);
       
       /**
        * Updates only the status of a transaction
        * Using @Modifying with @Query ensures this operation is done as a single database operation
        * 
        * @param id The ID of the transaction to update
        * @param status The new status to set
        * @return The number of affected rows
        */
       @Modifying
       @Query("""
                UPDATE Transaction t 
                    SET t.status = :status 
                    WHERE t.id = :id
               """)
       int updateTransactionStatus(@Param("id") int id, @Param("status") TransactionStatus status);

       /**
        * Calculates the total balance that is on hold for a given source account ID.
        * This includes the sum of all pending or new transactions linked to the source account.
        *
        * @param accountId The ID of the source account for which the balance on hold is to be calculated.
        *
        * @return The total amount on hold as a BigDecimal, or 0 if no matching transactions are found.
        */
       @Query("""
                   SELECT COALESCE(SUM(t.amount), 0) 
                          FROM Transaction t 
                          WHERE t.sourceAccount.id = :accountId 
                          AND t.status IN ('NEW', 'PENDING')
               """)
       BigDecimal findBalanceOnHoldBySourceAccount_Id(@Param("accountId") Integer accountId);
}
