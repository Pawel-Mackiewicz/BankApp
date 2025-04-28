package info.mackiewicz.bankapp.presentation.dashboard.service.transfer.assembler;

import info.mackiewicz.bankapp.core.transaction.model.Transaction;

/**
 * Strategy interface for assembling different types of transactions.
 * @param <T> type of request this strategy can handle
 */
public interface TransactionAssemblyStrategy<T> {
    
    /**
     * Assembles a Transaction object from the given request.
     *
     * @param request the request to assemble a transaction from
     * @return {@link Transaction} object
     */
    Transaction assembleTransaction(T request);
    
    /**
     * Returns the type of request this strategy can handle.
     *
     * @return class of the supported request type
     */
    Class<T> getSupportedRequestType();
}