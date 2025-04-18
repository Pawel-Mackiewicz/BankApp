package info.mackiewicz.bankapp.system.banking.operations.transfer;

import info.mackiewicz.bankapp.system.banking.operations.api.dto.IbanTransferRequest;
import info.mackiewicz.bankapp.system.banking.operations.service.TransferOperationService;
import info.mackiewicz.bankapp.system.banking.shared.dto.TransactionResponse;
import info.mackiewicz.bankapp.transaction.exception.TransactionBuildingException;
import info.mackiewicz.bankapp.transaction.exception.TransactionValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferOperationsServiceTest extends BaseTransferServiceTest {

    private TransferOperationService transferOperationsService;

    @BeforeEach
    void setUp() {
        super.setUp();
        transferOperationsService = new TransferOperationService(
            transactionBuilderService,
                transactionService,
                accountService
        );
    }

    @Test
    @DisplayName("Should successfully handle transfer operation")
    void shouldSuccessfullyHandleTransfer() {
        // Arrange
        IbanTransferRequest request = createTestTransferRequest();

        when(accountService.getAccountByIban(eq(SOURCE_IBAN)))
                .thenReturn(sourceAccount);
                
        when(transactionBuilderService.buildTransferTransaction(
                eq(TRANSFER_AMOUNT),
                eq(TRANSFER_TITLE),
                eq(sourceAccount),
                eq(destinationAccount)))
                .thenReturn(transaction);
                
        when(transactionService.registerTransaction(eq(transaction)))
                .thenReturn(transaction);

        // Act
        TransactionResponse response = transferOperationsService.handleTransfer(
                request,
                USER_ID,
                SOURCE_IBAN,
                () -> destinationAccount);

        // Assert
        assertThat(response.getSourceAccount().getFormattedIban()).isEqualTo(sourceAccount.getFormattedIban());
        assertThat(response.getTargetAccount().getFormattedIban()).isEqualTo(destinationAccount.getFormattedIban());
        assertThat(response.getTransactionInfo().getId()).isEqualTo(transaction.getId());
        assertThat(response.getTransactionInfo().getAmount()).isEqualTo(TRANSFER_AMOUNT);
        assertThat(response.getTransactionInfo().getTitle()).isEqualTo(TRANSFER_TITLE);
        verify(transactionService).registerTransaction(transaction);
    }


    @Test
    @DisplayName("Should throw exception when transaction building fails")
    void shouldThrowExceptionWhenTransactionBuildingFails() {
        // Arrange
        IbanTransferRequest request = createTestTransferRequest();

        when(accountService.getAccountByIban(eq(SOURCE_IBAN)))
                .thenReturn(sourceAccount);
                
        when(transactionBuilderService.buildTransferTransaction(
                eq(TRANSFER_AMOUNT),
                eq(TRANSFER_TITLE),
                eq(sourceAccount),
                eq(destinationAccount)))
                .thenThrow(new TransactionBuildingException("Failed to build transaction"));

        // Act & Assert
        assertThatThrownBy(() -> transferOperationsService.handleTransfer(
                request,
                USER_ID,
                SOURCE_IBAN,
                () -> destinationAccount))
                .isInstanceOf(TransactionBuildingException.class);
    }

    @Test
    @DisplayName("Should throw exception when transaction registration fails")
    void shouldThrowExceptionWhenTransactionRegistrationFails() {
        // Arrange
        IbanTransferRequest request = createTestTransferRequest();

        when(accountService.getAccountByIban(eq(SOURCE_IBAN)))
                .thenReturn(sourceAccount);
                
        when(transactionBuilderService.buildTransferTransaction(
                eq(TRANSFER_AMOUNT),
                eq(TRANSFER_TITLE),
                eq(sourceAccount),
                eq(destinationAccount)))
                .thenReturn(transaction);
                
        when(transactionService.registerTransaction(eq(transaction)))
                .thenThrow(new TransactionValidationException("Transaction validation failed"));

        // Act & Assert
        assertThatThrownBy(() -> transferOperationsService.handleTransfer(
                request,
                USER_ID,
                SOURCE_IBAN,
                () -> destinationAccount))
                .isInstanceOf(TransactionValidationException.class);
    }

    private IbanTransferRequest createTestTransferRequest() {
        IbanTransferRequest request = new IbanTransferRequest();
        request.setSourceIban(SOURCE_IBAN);
        request.setRecipientIban(DEST_IBAN);
        request.setAmount(TRANSFER_AMOUNT);
        request.setTitle(TRANSFER_TITLE);
        return request;
    }
}