package info.mackiewicz.bankapp.transaction.service.operations;

import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.account.exception.AccountOwnershipException;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.interfaces.AccountServiceInterface;
import info.mackiewicz.bankapp.shared.exception.IbanAnalysisException;
import info.mackiewicz.bankapp.transaction.exception.TransactionBuildingException;
import info.mackiewicz.bankapp.transaction.exception.TransactionValidationException;
import info.mackiewicz.bankapp.transaction.model.dto.EmailTransferRequest;
import info.mackiewicz.bankapp.transaction.model.dto.TransferResponse;
import info.mackiewicz.bankapp.user.model.interfaces.UserDetailsWithId;
import info.mackiewicz.bankapp.user.model.vo.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailTransferService {

    private final AccountServiceInterface accountService;
    private final BankingOperationsService operationsService;

        /**
     * Handles the transfer of funds between accounts using recipient email address.
     *
     * @param transferRequest The request containing transfer details
     * @param user            The user initiating the transfer
     * @return A response containing details of the transfer
     * @throws OwnerAccountNotFoundException if no account is found with the given email
     * @throws AccountOwnershipException      if the user does not own the source account
     * @throws TransactionBuildingException   if the transaction cannot be built
     * @throws TransactionValidationException if the transaction fails validation
     * @throws IbanAnalysisException          if there is an error analyzing the IBANs
     * 
     */
    public TransferResponse handleEmailTransfer(EmailTransferRequest transferRequest, UserDetailsWithId user) {
        log.info("Handling transfer from :{} to {}", transferRequest.getSourceIban(),
                transferRequest.getDestinationEmail());

        return operationsService.handleTransfer(transferRequest,
                user.getId(),
                transferRequest.getSourceIban(),
                () -> retrieveAccountFromEmail(transferRequest.getDestinationEmail()));
    }

    /**
     * Retrieves the account associated with the given email address.
     *
     * @param destinationEmail The email address of the account to retrieve
     * @return The account associated with the given email address
     * @throws OwnerAccountNotFoundException if no account is found with the given email
     */
    private Account retrieveAccountFromEmail(Email destinationEmail) {
        return accountService.getAccountByOwnersEmail(destinationEmail);
    }
}
