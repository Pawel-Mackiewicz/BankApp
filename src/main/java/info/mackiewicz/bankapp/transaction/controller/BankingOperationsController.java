package info.mackiewicz.bankapp.transaction.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import info.mackiewicz.bankapp.transaction.model.dto.BankingOperationRequest;
import info.mackiewicz.bankapp.transaction.model.dto.EmailTransferRequest;
import info.mackiewicz.bankapp.transaction.model.dto.IbanTransferRequest;
import info.mackiewicz.bankapp.transaction.model.dto.TransferResponse;
import info.mackiewicz.bankapp.user.model.interfaces.UserDetailsWithId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BankingOperationsController implements BankingOperationsControllerInterface {

    private final BankingOperationsServiceInterface bankingOperationsService;

    @Override
    public ResponseEntity<TransferResponse> ibanTransfer(@Valid @RequestBody IbanTransferRequest request, @AuthenticationPrincipal UserDetailsWithId authUser) {
        TransferResponse response =  bankingOperationsService.handleIbanTransfer(request, authUser);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<TransferResponse> emailTransfer(@Valid @RequestBody EmailTransferRequest request, @AuthenticationPrincipal UserDetailsWithId authUser) {
        TransferResponse response =  bankingOperationsService.handleEmailTransfer(request, authUser);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> withdraw(@Valid @RequestBody BankingOperationRequest request, @AuthenticationPrincipal UserDetailsWithId authUser) {
        throw new UnsupportedOperationException("Unimplemented method 'withdraw'");
    }

    @Override
    public ResponseEntity<?> deposit(@Valid @RequestBody BankingOperationRequest request, @AuthenticationPrincipal UserDetailsWithId authUser) {
        throw new UnsupportedOperationException("Unimplemented method 'deposit'");
    }

}
