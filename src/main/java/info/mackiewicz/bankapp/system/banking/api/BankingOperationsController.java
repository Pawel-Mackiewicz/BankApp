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
     * Processes an IBAN transfer request by delegating to the IBAN transfer service.
     * <p>
     * This method validates the incoming IBAN transfer request and uses the authenticated user's details
     * to perform the transfer. It then returns the resulting transfer response wrapped in an HTTP 200 OK response.
     * </p>
     *
     * @param request the validated IBAN transfer request containing transfer details
     * @param authUser the authenticated user initiating the transfer
     * @return a ResponseEntity containing the transfer response with an HTTP 200 OK status
     */
    @Override
    public ResponseEntity<TransferResponse> ibanTransfer(@Valid @RequestBody IbanTransferRequest request, @AuthenticationPrincipal UserDetailsWithId authUser) {
        TransferResponse response =  ibanTransferService.handleIbanTransfer(request, authUser);
        return ResponseEntity.ok(response);
    }

    /**
     * Processes an email transfer request.
     *
     * <p>This method receives a validated {@code EmailTransferRequest} and an authenticated user,
     * delegates the email transfer processing to the {@code emailTransferService}, and returns a
     * ResponseEntity with an HTTP 200 OK status containing the resulting {@code TransferResponse}.
     *
     * @param request the email transfer request payload with transfer details
     * @param authUser the authenticated user initiating the transfer
     * @return a ResponseEntity containing the transfer response
     */
    @Override
    public ResponseEntity<TransferResponse> emailTransfer(@Valid @RequestBody EmailTransferRequest request, @AuthenticationPrincipal UserDetailsWithId authUser) {
        TransferResponse response =  emailTransferService.handleEmailTransfer(request, authUser);
        return ResponseEntity.ok(response);
    }
}
