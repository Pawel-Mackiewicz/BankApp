package info.mackiewicz.bankapp.transaction.service.error;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.transaction.model.Transaction;

/**
 * Manages transaction error observers and notifies them about errors.
 * Not implemented yet.
 */
@Component
public class TransactionErrorNotifier {
    private final List<TransactionErrorObserver> observers = new ArrayList<>();

    /**
     * Adds an observer to be notified of transaction errors
     *
     * @param observer The observer to add
     */
    public void addObserver(TransactionErrorObserver observer) {
        observers.add(observer);
    }

    /**
     * Removes an observer from being notified of transaction errors
     *
     * @param observer The observer to remove
     */
    public void removeObserver(TransactionErrorObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifies all registered observers about a transaction error
     *
     * @param transaction The transaction that encountered an error
     * @param error The exception that was thrown
     */
    public void notifyError(Transaction transaction, Exception error) {
        for (TransactionErrorObserver observer : observers) {
            observer.onTransactionError(transaction, error);
        }
    }
}