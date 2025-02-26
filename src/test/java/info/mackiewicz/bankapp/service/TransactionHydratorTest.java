package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.model.TransactionType;
import info.mackiewicz.bankapp.service.strategy.DepositTransaction;
import info.mackiewicz.bankapp.service.strategy.FeeTransaction;
import info.mackiewicz.bankapp.service.strategy.TransferTransaction;
import info.mackiewicz.bankapp.service.strategy.WithdrawalTransaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class TransactionHydratorTest {

    @Mock
    private DepositTransaction depositTransaction;

    @Mock
    private WithdrawalTransaction withdrawalTransaction;

    @Mock
    private FeeTransaction feeTransaction;

    @Mock
    private TransferTransaction transferTransaction;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionHydrator transactionHydrator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void hydrate_TransactionTypeDeposit_SetsDepositStrategy() {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.DEPOSIT);

        // Act
        Transaction hydratedTransaction = transactionHydrator.hydrate(transaction);

        // Assert
        assertEquals(depositTransaction, hydratedTransaction.getStrategy());
    }

    @Test
    void hydrate_TransactionTypeWithdrawal_SetsWithdrawalStrategy() {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.WITHDRAWAL);

        // Act
        Transaction hydratedTransaction = transactionHydrator.hydrate(transaction);

        // Assert
        assertEquals(withdrawalTransaction, hydratedTransaction.getStrategy());
    }

    @Test
    void hydrate_TransactionTypeTransfer_SetsTransferStrategy() {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.TRANSFER_EXTERNAL);

        // Act
        Transaction hydratedTransaction = transactionHydrator.hydrate(transaction);

        // Assert
        assertEquals(transferTransaction, hydratedTransaction.getStrategy());
    }

//     @Test
//     void hydrate_TransactionTypeFee_SetsTransferStrategyAndBankAccount() {
//         // Arrange
//         Transaction transaction = new Transaction();
//         transaction.setType(TransactionType.FEE);
//         Account bankAccount = new Account();
//         when(accountService.getAccountById(-1)).thenReturn(bankAccount);

//         // Act
//         Transaction hydratedTransaction = transactionHydrator.hydrate(transaction);

//         // Assert
//         assertEquals(transferTransaction, hydratedTransaction.getStrategy());
//         assertEquals(bankAccount, hydratedTransaction.getDestinationAccount());
//         verify(accountService, times(1)).getAccountById(-1);
//     }
}