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
     * Processes a validated IBAN transfer request.
     *
     * <p>This method delegates the IBAN transfer operation to the appropriate service using the provided request and
     * authenticated user details, and returns the result wrapped in an HTTP 200 OK response.
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
     * <p>This method validates the provided email transfer request and delegates its handling to the email transfer service using the authenticated user's details. It returns the result as a {@code ResponseEntity} containing a {@code TransferResponse} with an HTTP 200 OK status.
     *
     * @param request a {@code EmailTransferRequest} containing the transfer details
     * @param authUser the authenticated user's details
     * @return a {@code ResponseEntity} wrapping the {@code TransferResponse} of the transfer operation
     */
    @Override
    public ResponseEntity<TransferResponse> emailTransfer(@Valid @RequestBody EmailTransferRequest request, @AuthenticationPrincipal UserDetailsWithId authUser) {
        TransferResponse response =  emailTransferService.handleEmailTransfer(request, authUser);
        return ResponseEntity.ok(response);
    }
}
