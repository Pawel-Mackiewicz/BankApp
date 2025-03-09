package info.mackiewicz.bankapp.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

       @Query("SELECT t FROM Transaction t " +
                     "WHERE t.sourceAccount.id = :accountId OR t.destinationAccount.id = :accountId " +
                     "ORDER BY t.date DESC")
       Optional<List<Transaction>> findByAccountId(@Param("accountId") int accountId);

       List<Transaction> findByStatus(TransactionStatus status);

       @Query("SELECT t FROM Transaction t " +
                     "WHERE t.sourceAccount.id = :accountId OR t.destinationAccount.id = :accountId " +
                     "ORDER BY t.date DESC " +
                     "LIMIT :limit")
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
       @Query("UPDATE Transaction t SET t.status = :status WHERE t.id = :id")
       int updateTransactionStatus(@Param("id") int id, @Param("status") TransactionStatus status);
}
