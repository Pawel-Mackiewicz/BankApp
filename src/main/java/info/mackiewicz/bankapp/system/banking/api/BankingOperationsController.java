package info.mackiewicz.bankapp.system.banking.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import info.mackiewicz.bankapp.system.banking.api.dto.EmailTransferRequest;
import info.mackiewicz.bankapp.system.banking.api.dto.IbanTransferRequest;
import info.mackiewicz.bankapp.system.banking.api.dto.TransferResponse;
import info.mackiewicz.bankapp.system.banking.service.transfer.EmailTransferService;
import info.mackiewicz.bankapp.system.banking.service.transfer.IbanTransferService;
import info.mackiewicz.bankapp.user.model.interfaces.UserDetailsWithId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class BankingOperationsController implements BankingOperationsControllerInterface {

    private final EmailTransferService emailTransferService;
    private final IbanTransferService ibanTransferService;

    /**
     * Processes an IBAN transfer request.
     *
     * Delegates the transfer operation to the IBAN transfer service using the provided transfer details
     * and authenticated user, and returns the resulting transfer response encapsulated in an HTTP 200 OK ResponseEntity.
     *
     * @param request the validated IBAN transfer request containing transfer details
     * @param authUser the authenticated user initiating the transfer
     * @return a ResponseEntity containing the transfer response
     */
    @Override
    public ResponseEntity<TransferResponse> ibanTransfer(@Valid @RequestBody IbanTransferRequest request, @AuthenticationPrincipal UserDetailsWithId authUser) {
        TransferResponse response =  ibanTransferService.handleIbanTransfer(request, authUser);
        return ResponseEntity.ok(response);
    }

    /**
     * Processes an email transfer request.
     *
     * <p>This method validates the incoming email transfer request and authenticated user,
     * delegates the transfer handling to the email transfer service, and returns the result 
     * wrapped in an HTTP 200 OK ResponseEntity.
     *
     * @param request a valid email transfer request containing the necessary transfer details
     * @param authUser the authenticated user initiating the transfer
     * @return a ResponseEntity containing the transfer response with an HTTP 200 status
     */
    @Override
    public ResponseEntity<TransferResponse> emailTransfer(@Valid @RequestBody EmailTransferRequest request, @AuthenticationPrincipal UserDetailsWithId authUser) {
        TransferResponse response =  emailTransferService.handleEmailTransfer(request, authUser);
        return ResponseEntity.ok(response);
    }
}
