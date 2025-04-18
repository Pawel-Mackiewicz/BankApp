package info.mackiewicz.bankapp.system.banking.operations.api;

import info.mackiewicz.bankapp.system.banking.operations.api.dto.EmailTransferRequest;
import info.mackiewicz.bankapp.system.banking.operations.api.dto.IbanTransferRequest;
import info.mackiewicz.bankapp.system.banking.operations.service.transfer.EmailTransferService;
import info.mackiewicz.bankapp.system.banking.operations.service.transfer.IbanTransferService;
import info.mackiewicz.bankapp.system.banking.shared.dto.TransactionResponse;
import info.mackiewicz.bankapp.user.model.interfaces.UserDetailsWithId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class BankingOperationsController implements BankingOperationsControllerInterface {

    private final EmailTransferService emailTransferService;
    private final IbanTransferService ibanTransferService;

    @PreAuthorize("@ibanAccountAuthorizationService.validateAccountOwnership(#request.sourceIban, authentication.principal)")
    @Override
    public ResponseEntity<TransactionResponse> ibanTransfer(@Valid @RequestBody IbanTransferRequest request, @AuthenticationPrincipal UserDetailsWithId authUser) {
        TransactionResponse response = ibanTransferService.handleIbanTransfer(request);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("@ibanAccountAuthorizationService.validateAccountOwnership(#request.sourceIban, authentication.principal)")
    @Override
    public ResponseEntity<TransactionResponse> emailTransfer(@Valid @RequestBody EmailTransferRequest request, @AuthenticationPrincipal UserDetailsWithId authUser) {
        TransactionResponse response = emailTransferService.handleEmailTransfer(request);
        return ResponseEntity.ok(response);
    }
}
