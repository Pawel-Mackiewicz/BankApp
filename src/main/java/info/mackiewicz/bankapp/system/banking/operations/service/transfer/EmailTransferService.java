package info.mackiewicz.bankapp.system.banking.operations.service.transfer;

import info.mackiewicz.bankapp.account.exception.AccountOwnershipException;
import info.mackiewicz.bankapp.account.exception.OwnerAccountsNotFoundException;
import info.mackiewicz.bankapp.account.service.interfaces.AccountServiceInterface;
import info.mackiewicz.bankapp.shared.exception.IbanAnalysisException;
import info.mackiewicz.bankapp.system.banking.operations.api.dto.EmailTransferRequest;
import info.mackiewicz.bankapp.system.banking.operations.service.TransferOperationService;
import info.mackiewicz.bankapp.system.banking.shared.dto.TransactionResponse;
import info.mackiewicz.bankapp.transaction.exception.TransactionBuildingException;
import info.mackiewicz.bankapp.transaction.exception.TransactionValidationException;
import info.mackiewicz.bankapp.user.model.interfaces.UserDetailsWithId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
// this class could extend TransferOperationService, but DI is more flexible
public class EmailTransferService {

    private final AccountServiceInterface accountService;
    private final TransferOperationService operationsService;

        /**
     * Handles the transfer of funds between accounts using recipient email address.
     *
     * @param transferRequest The request containing transfer details
     * @param user            The user initiating the transfer
     * @return A response containing details of the transfer
     * @throws OwnerAccountsNotFoundException if no account is found with the given email
     * @throws AccountOwnershipException      if the user does not own the source account
     * @throws TransactionBuildingException   if the transaction cannot be built
     * @throws TransactionValidationException if the transaction fails validation
     * @throws IbanAnalysisException          if there is an error analyzing the IBANs
     * 
     */
    public TransactionResponse handleEmailTransfer(EmailTransferRequest transferRequest, UserDetailsWithId user) {
        return operationsService.handleTransfer(
                transferRequest,
                user.getId(),
                transferRequest.getSourceIban(),
                () -> accountService.getAccountByOwnersEmail(transferRequest.getDestinationEmail()));
    }
}
