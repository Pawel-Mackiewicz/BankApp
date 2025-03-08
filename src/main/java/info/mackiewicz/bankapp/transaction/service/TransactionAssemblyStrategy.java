package info.mackiewicz.bankapp.transaction.service;

import info.mackiewicz.bankapp.transaction.model.Transaction;

/**
 * Strategy interface for assembling different types of transactions.
 * @param <T> typ requestu transakcji
 */
public interface TransactionAssemblyStrategy<T> {
    
    /**
     * Assembles a Transaction object from the given request.
     *
     * @param request obiekt żądania transferu
     * @return skonstruowany obiekt Transaction
     */
    Transaction assembleTransaction(T request);
    
    /**
     * Returns the type of request this strategy can handle.
     *
     * @return klasa obsługiwanego typu requestu
     */
    Class<T> getSupportedRequestType();
}