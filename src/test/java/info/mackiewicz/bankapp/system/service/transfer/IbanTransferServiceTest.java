package info.mackiewicz.bankapp.system.service.transfer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Supplier;

import org.iban4j.Iban;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import info.mackiewicz.bankapp.account.exception.AccountNotFoundByIbanException;
import info.mackiewicz.bankapp.account.exception.AccountOwnershipException;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.system.banking.api.dto.IbanTransferRequest;
import info.mackiewicz.bankapp.system.banking.api.dto.TransferResponse;
import info.mackiewicz.bankapp.system.banking.service.transfer.IbanTransferService;
import info.mackiewicz.bankapp.transaction.exception.TransactionValidationException;

@ExtendWith(MockitoExtension.class)
class IbanTransferServiceTest extends BaseTransferServiceTest {

    private IbanTransferService ibanTransferService;

    @BeforeEach
    void setUpIbanService() {
        ibanTransferService = new IbanTransferService(accountService, operationsService);
    }

    @Test
    @DisplayName("Should successfully handle IBAN transfer")
    void shouldSuccessfullyHandleIbanTransfer() {
        // Arrange
        IbanTransferRequest request = createIbanTransferRequest(SOURCE_IBAN, DEST_IBAN);
        
        when(accountService.getAccountByIban(eq(DEST_IBAN)))
                .thenReturn(destinationAccount);
                
        when(operationsService.handleTransfer(
                eq(request),
                eq(USER_ID),
                eq(SOURCE_IBAN),
                any()))
                .thenAnswer(invocation -> {
                    Supplier<Account> accountSupplier = invocation.getArgument(3);
                    Account dest = accountSupplier.get();
                    return new TransferResponse(sourceAccount, dest, transaction);
                });

        // Act
        TransferResponse response = ibanTransferService.handleIbanTransfer(request, userDetails);

        // Assert
        assertSuccessfulTransfer(response);
        verify(accountService).getAccountByIban(eq(DEST_IBAN));
    }

    @Test
    @DisplayName("Should throw exception when destination IBAN not found")
    void shouldThrowExceptionWhenDestinationIbanNotFound() {
        // Arrange
        IbanTransferRequest request = createIbanTransferRequest(SOURCE_IBAN, DEST_IBAN);
        
        when(accountService.getAccountByIban(eq(DEST_IBAN)))
                .thenThrow(new AccountNotFoundByIbanException("Account not found"));
                
        when(operationsService.handleTransfer(
                eq(request),
                eq(USER_ID),
                eq(SOURCE_IBAN),
                any()))
                .thenAnswer(invocation -> {
                    Supplier<Account> accountSupplier = invocation.getArgument(3);
                    return accountSupplier.get(); // This will throw the exception
                });

        // Act & Assert
        assertThatThrownBy(() -> ibanTransferService.handleIbanTransfer(request, userDetails))
                .isInstanceOf(AccountNotFoundByIbanException.class);
    }

    @Test
    @DisplayName("Should throw exception when user is not owner of source account")
    void shouldThrowExceptionWhenUserIsNotOwnerOfSourceAccount() {
        // Arrange
        IbanTransferRequest request = createIbanTransferRequest(SOURCE_IBAN, DEST_IBAN);
                
        when(operationsService.handleTransfer(
                eq(request),
                eq(USER_ID),
                eq(SOURCE_IBAN),
                any()))
                .thenThrow(new AccountOwnershipException("User is not the owner of the account"));

        // Act & Assert
        assertThatThrownBy(() -> ibanTransferService.handleIbanTransfer(request, userDetails))
                .isInstanceOf(AccountOwnershipException.class);
    }

    @Test
    @DisplayName("Should throw exception when transaction validation fails")
    void shouldThrowExceptionWhenTransactionValidationFails() {
        // Arrange
        IbanTransferRequest request = createIbanTransferRequest(SOURCE_IBAN, DEST_IBAN);
                
        when(operationsService.handleTransfer(
                eq(request),
                eq(USER_ID),
                eq(SOURCE_IBAN),
                any()))
                .thenThrow(new TransactionValidationException("Transaction validation failed"));

        // Act & Assert
        assertThatThrownBy(() -> ibanTransferService.handleIbanTransfer(request, userDetails))
                .isInstanceOf(TransactionValidationException.class);
    }

    private IbanTransferRequest createIbanTransferRequest(Iban sourceIban, Iban destIban) {
        IbanTransferRequest request = new IbanTransferRequest();
        request.setSourceIban(sourceIban);
        request.setRecipientIban(destIban);
        request.setAmount(TRANSFER_AMOUNT);
        request.setTitle(TRANSFER_TITLE);
        return request;
    }
}