package info.mackiewicz.bankapp.transaction.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestBody;

import info.mackiewicz.bankapp.transaction.model.dto.BankingOperationRequest;
import info.mackiewicz.bankapp.transaction.model.dto.EmailTransferRequest;
import info.mackiewicz.bankapp.transaction.model.dto.IbanTransferRequest;
import info.mackiewicz.bankapp.transaction.model.dto.TransferResponse;
import jakarta.validation.Valid;

public class BankingOperationsController implements BankingOperationsControllerInterface {

    @Override
    public ResponseEntity<TransferResponse> ibanTransfer(@Valid @RequestBody IbanTransferRequest request, @AuthenticationPrincipal UserDetails authUser) {
        
        return ResponseEntity.ok(new TransferResponse());
    }

    @Override
    public ResponseEntity<TransferResponse> emailTransfer(@Valid @RequestBody EmailTransferRequest request, @AuthenticationPrincipal UserDetails authUser) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'EmailTransfer'");
    }

    @Override
    public ResponseEntity<?> withdraw(@Valid @RequestBody BankingOperationRequest request, @AuthenticationPrincipal UserDetails authUser) {
        throw new UnsupportedOperationException("Unimplemented method 'withdraw'");
    }

    @Override
    public ResponseEntity<?> deposit(@Valid @RequestBody BankingOperationRequest request, @AuthenticationPrincipal UserDetails authUser) {
        throw new UnsupportedOperationException("Unimplemented method 'deposit'");
    }

}
