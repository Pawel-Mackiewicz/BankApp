package info.mackiewicz.bankapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.model.TransactionStatus;

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
}
