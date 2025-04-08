package info.mackiewicz.bankapp.transaction.service.assembler;

import java.util.Map;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.presentation.dashboard.dto.InternalTransferRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.OwnTransferRequest;
import info.mackiewicz.bankapp.presentation.dashboard.dto.WebTransferRequest;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Assembles Transaction objects from different types of transfer requests.
 * Uses Strategy pattern to handle different types of transfers.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class TransactionAssembler {

    private final Map<Class<?>, TransactionAssemblyStrategy<?>> assemblyStrategies;

    /**
     * Assembles a Transaction from the provided WebTransferRequest.
     *
     * <p>This method delegates to a generic transaction assembly mechanism that selects the appropriate strategy based on the request type.
     *
     * @param request the web transfer request containing transfer details
     * @return the assembled Transaction object
     */
    public Transaction assembleExternalTransfer(WebTransferRequest request) {
        return assembleTransaction(request);
    }

    public Transaction assembleInternalTransfer(InternalTransferRequest request) {
        return assembleTransaction(request);
    }

    public Transaction assembleOwnTransfer(OwnTransferRequest request) {
        return assembleTransaction(request);
    }

    /**
     * Generic method to assemble a transaction using appropriate strategy
     */
    @SuppressWarnings("unchecked")
    private Transaction assembleTransaction(Object request) {
        TransactionAssemblyStrategy<?> strategy = assemblyStrategies.get(request.getClass());
        
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for request type: " + request.getClass());
        }
        
        log.info("Assembling transaction using strategy: {}", strategy.getClass().getSimpleName());
        Transaction transaction = ((TransactionAssemblyStrategy<Object>) strategy).assembleTransaction(request);
        log.info("Transaction assembled successfully with ID: {}", transaction.getId());
        return transaction;
    }
}
