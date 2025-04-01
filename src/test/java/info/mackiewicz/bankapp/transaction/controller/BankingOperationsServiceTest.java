package info.mackiewicz.bankapp.transaction.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.iban4j.Iban;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import info.mackiewicz.bankapp.account.exception.AccountNotFoundByIbanException;
import info.mackiewicz.bankapp.account.exception.AccountOwnershipException;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountSecurityService;
import info.mackiewicz.bankapp.account.service.interfaces.AccountServiceInterface;
import info.mackiewicz.bankapp.shared.service.TransactionBuilderService;
import info.mackiewicz.bankapp.testutils.TestAccountBuilder;
import info.mackiewicz.bankapp.testutils.TestUserBuilder;
import info.mackiewicz.bankapp.transaction.exception.TransactionBuildingException;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.dto.EmailTransferRequest;
import info.mackiewicz.bankapp.transaction.model.dto.IbanTransferRequest;
import info.mackiewicz.bankapp.transaction.model.dto.TransferResponse;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.model.interfaces.UserDetailsWithId;
import info.mackiewicz.bankapp.user.model.vo.Email;
import info.mackiewicz.bankapp.utils.TestIbanProvider;

@ExtendWith(MockitoExtension.class)
class BankingOperationsServiceTest {

    @Mock
    private AccountServiceInterface accountService;

    @Mock
    private TransactionBuilderService transactionBuilderService;

    @Mock
    private AccountSecurityService accountSecurityService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private UserDetailsWithId userDetails;

    @Captor
    private ArgumentCaptor<Transaction> transactionCaptor;

    @InjectMocks
    private BankingOperationsService bankingOperationsService;

    private Account sourceAccount;
    private Account destinationAccount;
    private Transaction transaction;
    private static final Integer USER_ID = 1;
    private static final Iban SOURCE_IBAN = TestIbanProvider.getNextIbanObject();
    private static final Iban DEST_IBAN = TestIbanProvider.getNextIbanObject();
    private static final BigDecimal TRANSFER_AMOUNT = BigDecimal.valueOf(100);
    private static final String TRANSFER_TITLE = "Test transfer";

    @BeforeEach
    void setUp() {
        User owner1 = TestUserBuilder.createRandomTestUser();
        User owner2 = TestUserBuilder.createRandomTestUser();
        sourceAccount = TestAccountBuilder.createTestAccount(1, TRANSFER_AMOUNT, owner1);
        destinationAccount = TestAccountBuilder.createTestAccount(2, TRANSFER_AMOUNT, owner2);
        transaction = Transaction.buildTransfer()
                .from(sourceAccount)
                .to(destinationAccount)
                .withAmount(TRANSFER_AMOUNT)
                .withTitle(TRANSFER_TITLE)
                .asInternalTransfer()
                .build();

        when(userDetails.getId()).thenReturn(USER_ID);
    }

    @Nested
    @DisplayName("Email Transfer Tests")
    class EmailTransferTests {
        
        @Test
        @DisplayName("Should successfully handle email transfer")
        void shouldSuccessfullyHandleEmailTransfer() {
            // Arrange
            Email destEmail = new Email("recipient@example.com");
            EmailTransferRequest request = createEmailTransferRequest(SOURCE_IBAN, destEmail);
            
            setupSuccessfulEmailTransferMocks();

            // Act
            TransferResponse response = bankingOperationsService.handleEmailTransfer(request, userDetails);

            // Assert
            assertSuccessfulTransfer(response);
            verify(accountService).getAccountByOwnersEmail(eq(destEmail));
        }

        @Test
        @DisplayName("Should throw exception when destination email not found")
        void shouldThrowExceptionWhenDestinationEmailNotFound() {
            // Arrange
            Email destEmail = new Email("nonexistent@example.com");
            EmailTransferRequest request = createEmailTransferRequest(SOURCE_IBAN, destEmail);
            
            when(accountService.getAccountByOwnersEmail(destEmail))
                    .thenThrow(new AccountNotFoundByIbanException("Account not found"));

            // Act & Assert
            assertThatThrownBy(() -> bankingOperationsService.handleEmailTransfer(request, userDetails))
                    .isInstanceOf(AccountNotFoundByIbanException.class);
        }
    }

    @Nested
    @DisplayName("IBAN Transfer Tests")
    class IbanTransferTests {

        @Test
        @DisplayName("Should successfully handle IBAN transfer")
        void shouldSuccessfullyHandleIbanTransfer() {
            // Arrange
            IbanTransferRequest request = createIbanTransferRequest(SOURCE_IBAN, DEST_IBAN);
            
            setupSuccessfulIbanTransferMocks();

            // Act
            TransferResponse response = bankingOperationsService.handleIbanTransfer(request, userDetails);

            // Assert
            assertSuccessfulTransfer(response);
            verify(accountService).getAccountByIban(eq(DEST_IBAN));
        }

        @Test
        @DisplayName("Should throw exception when destination IBAN not found")
        void shouldThrowExceptionWhenDestinationIbanNotFound() {
            // Arrange
            IbanTransferRequest request = createIbanTransferRequest(SOURCE_IBAN, DEST_IBAN);
            
            when(accountService.getAccountByIban(DEST_IBAN))
                    .thenThrow(new AccountNotFoundByIbanException("Account not found"));

            // Act & Assert
            assertThatThrownBy(() -> bankingOperationsService.handleIbanTransfer(request, userDetails))
                    .isInstanceOf(AccountNotFoundByIbanException.class);
        }
    }

    @Nested
    @DisplayName("Account Validation Tests")
    class AccountValidationTests {

        @Test
        @DisplayName("Should throw exception when source account ownership validation fails")
        void shouldThrowExceptionWhenSourceAccountOwnershipValidationFails() {
            // Arrange
            IbanTransferRequest request = createIbanTransferRequest(SOURCE_IBAN, DEST_IBAN);
            
            when(accountSecurityService.validateAccountOwnership(USER_ID, SOURCE_IBAN))
                    .thenThrow(new AccountOwnershipException("Invalid account ownership"));

            // Act & Assert
            assertThatThrownBy(() -> bankingOperationsService.handleIbanTransfer(request, userDetails))
                    .isInstanceOf(AccountOwnershipException.class);
        }
    }

    @Nested
    @DisplayName("Transaction Building Tests")
    class TransactionBuildingTests {

        @Test
        @DisplayName("Should throw exception when transaction building fails")
        void shouldThrowExceptionWhenTransactionBuildingFails() {
            // Arrange
            IbanTransferRequest request = createIbanTransferRequest(SOURCE_IBAN, DEST_IBAN);
            
            when(accountSecurityService.validateAccountOwnership(USER_ID, SOURCE_IBAN))
                    .thenReturn(sourceAccount);
            when(accountService.getAccountByIban(DEST_IBAN))
                    .thenReturn(destinationAccount);
            when(transactionBuilderService.buildTransferTransaction(
                    any(), any(), any(), any()))
                    .thenThrow(new TransactionBuildingException("Failed to build transaction"));

            // Act & Assert
            assertThatThrownBy(() -> bankingOperationsService.handleIbanTransfer(request, userDetails))
                    .isInstanceOf(TransactionBuildingException.class);
        }
    }

    private void setupSuccessfulIbanTransferMocks() {
        when(accountSecurityService.validateAccountOwnership(USER_ID, SOURCE_IBAN))
                .thenReturn(sourceAccount);
        when(accountService.getAccountByIban(DEST_IBAN))
                .thenReturn(destinationAccount);
                
        // Zmiana sposobu mockowania aby uniknąć problemu z argumentami
        doReturn(transaction).when(transactionBuilderService)
                .buildTransferTransaction(
                        any(BigDecimal.class), 
                        any(String.class), 
                        any(Account.class), 
                        any(Account.class));
                        
        when(transactionService.registerTransaction(transaction))
                .thenReturn(transaction);
    }
    
    private void setupSuccessfulEmailTransferMocks() {
        when(accountSecurityService.validateAccountOwnership(USER_ID, SOURCE_IBAN))
                .thenReturn(sourceAccount);
        when(accountService.getAccountByOwnersEmail(any(Email.class)))
                .thenReturn(destinationAccount);
                
        // Zmiana sposobu mockowania aby uniknąć problemu z argumentami
        doReturn(transaction).when(transactionBuilderService)
                .buildTransferTransaction(
                        any(BigDecimal.class), 
                        any(String.class), 
                        any(Account.class), 
                        any(Account.class));
                        
        when(transactionService.registerTransaction(transaction))
                .thenReturn(transaction);
    }

    private void assertSuccessfulTransfer(TransferResponse response) {
        assertThat(response).isNotNull();
        assertThat(response.getSourceAccount()).isEqualTo(sourceAccount);
        assertThat(response.getTargetAccount()).isEqualTo(destinationAccount);
        assertThat(response.getTransactionInfo()).isEqualTo(transaction);
    }

    private EmailTransferRequest createEmailTransferRequest(Iban sourceIban, Email destEmail) {
        EmailTransferRequest request = new EmailTransferRequest();
        request.setSourceIban(sourceIban);
        request.setDestinationEmail(destEmail);
        request.setAmount(TRANSFER_AMOUNT);
        request.setTitle(TRANSFER_TITLE);
        return request;
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