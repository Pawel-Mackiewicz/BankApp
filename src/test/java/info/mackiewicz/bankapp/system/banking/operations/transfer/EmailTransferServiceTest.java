package info.mackiewicz.bankapp.system.banking.operations.transfer;

import info.mackiewicz.bankapp.account.exception.AccountNotFoundByIbanException;
import info.mackiewicz.bankapp.account.exception.AccountOwnershipException;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.system.banking.operations.api.dto.EmailTransferRequest;
import info.mackiewicz.bankapp.system.banking.operations.api.dto.TransferResponse;
import info.mackiewicz.bankapp.system.banking.operations.service.transfer.EmailTransferService;
import info.mackiewicz.bankapp.transaction.exception.TransactionValidationException;
import info.mackiewicz.bankapp.user.model.vo.EmailAddress;
import org.iban4j.Iban;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailTransferServiceTest extends BaseTransferServiceTest {

    private EmailTransferService emailTransferService;

    @BeforeEach
    void setUpEmailService() {
        emailTransferService = new EmailTransferService(accountService, operationsService);
    }

    @Test
    @DisplayName("Should successfully handle email transfer")
    void shouldSuccessfullyHandleEmailTransfer() {
        // Arrange
        EmailAddress destEmail = new EmailAddress("recipient@example.com");
        EmailTransferRequest request = createEmailTransferRequest(SOURCE_IBAN, destEmail);
        
        when(accountService.getAccountByOwnersEmail(eq(destEmail)))
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
        TransferResponse response = emailTransferService.handleEmailTransfer(request, userDetails);

        // Assert
        assertSuccessfulTransfer(response);
        verify(accountService).getAccountByOwnersEmail(eq(destEmail));
    }

    @Test
    @DisplayName("Should throw exception when destination email not found")
    void shouldThrowExceptionWhenDestinationEmailNotFound() {
        // Arrange
        EmailAddress destEmail = new EmailAddress("nonexistent@example.com");
        EmailTransferRequest request = createEmailTransferRequest(SOURCE_IBAN, destEmail);
        
        when(accountService.getAccountByOwnersEmail(eq(destEmail)))
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
        assertThatThrownBy(() -> emailTransferService.handleEmailTransfer(request, userDetails))
                .isInstanceOf(AccountNotFoundByIbanException.class);
    }

    @Test
    @DisplayName("Should throw exception when user is not owner of source account")
    void shouldThrowExceptionWhenUserIsNotOwnerOfSourceAccount() {
        // Arrange
        EmailAddress destEmail = new EmailAddress("recipient@example.com");
        EmailTransferRequest request = createEmailTransferRequest(SOURCE_IBAN, destEmail);
            
        when(operationsService.handleTransfer(
                eq(request),
                eq(USER_ID),
                eq(SOURCE_IBAN),
                any()))
                .thenThrow(new AccountOwnershipException("User is not the owner of the account"));

        // Act & Assert
        assertThatThrownBy(() -> emailTransferService.handleEmailTransfer(request, userDetails))
                .isInstanceOf(AccountOwnershipException.class);
    }

    @Test
    @DisplayName("Should throw exception when transaction validation fails")
    void shouldThrowExceptionWhenTransactionValidationFails() {
        // Arrange
        EmailAddress destEmail = new EmailAddress("recipient@example.com");
        EmailTransferRequest request = createEmailTransferRequest(SOURCE_IBAN, destEmail);
                
        when(operationsService.handleTransfer(
                eq(request),
                eq(USER_ID),
                eq(SOURCE_IBAN),
                any()))
                .thenThrow(new TransactionValidationException("Transaction validation failed"));

        // Act & Assert
        assertThatThrownBy(() -> emailTransferService.handleEmailTransfer(request, userDetails))
                .isInstanceOf(TransactionValidationException.class);
    }

    private EmailTransferRequest createEmailTransferRequest(Iban sourceIban, EmailAddress destEmail) {
        EmailTransferRequest request = new EmailTransferRequest();
        request.setSourceIban(sourceIban);
        request.setDestinationEmail(destEmail);
        request.setAmount(TRANSFER_AMOUNT);
        request.setTitle(TRANSFER_TITLE);
        return request;
    }
}