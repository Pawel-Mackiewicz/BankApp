package info.mackiewicz.bankapp.system.banking.service.transfer;

import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.account.exception.AccountNotFoundByIbanException;
import info.mackiewicz.bankapp.account.exception.AccountOwnershipException;
import info.mackiewicz.bankapp.account.service.interfaces.AccountServiceInterface;
import info.mackiewicz.bankapp.shared.exception.IbanAnalysisException;
import info.mackiewicz.bankapp.system.banking.api.dto.IbanTransferRequest;
import info.mackiewicz.bankapp.system.banking.api.dto.TransferResponse;
import info.mackiewicz.bankapp.system.banking.service.TransferOperationService;
import info.mackiewicz.bankapp.transaction.exception.TransactionBuildingException;
import info.mackiewicz.bankapp.transaction.exception.TransactionValidationException;
import info.mackiewicz.bankapp.user.model.interfaces.UserDetailsWithId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
// this class could extend TransferOperationService, but DI is more flexible
public class IbanTransferService {

    private final AccountServiceInterface accountService;
    private final TransferOperationService operationsService;

    /**
     * Handles the transfer of funds between accounts using IBANs.
     *
     * @param transferRequest The request containing transfer details
     * @param user            The user initiating the transfer
     * @return A response containing details of the transfer
     * @throws AccountNotFoundByIbanException if no account is found with the given
     *                                        Iban
     * @throws AccountOwnershipException      if the user does not own the source
     *                                        account
     * @throws TransactionBuildingException   if the transaction cannot be built
     * @throws TransactionValidationException if the transaction fails validation
     * @throws IbanAnalysisException          if there is an error analyzing the
     *                                        IBANs
     * 
     */
    public TransferResponse handleIbanTransfer(IbanTransferRequest transferRequest, UserDetailsWithId user) {
        log.info("Handling transfer from :{} to {}", transferRequest.getSourceIban(), transferRequest.getRecipientIban());

        return operationsService.handleTransfer(
                transferRequest,
                user.getId(),
                transferRequest.getSourceIban(),
                () -> accountService.getAccountByIban(transferRequest.getRecipientIban()));
    }

}
