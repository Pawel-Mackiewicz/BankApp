package info.mackiewicz.bankapp.transaction.service.strategy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.model.TestAccountBuilder;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.user.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class FeeTransactionTest {

    @Mock
    private StrategyHelper strategyHelper;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private FeeTransaction feeTransaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void execute_ShouldSetBankAccountAndExecuteTransfer() {
        // given
        User owner = new User();
        owner.setId(1);
        Account bankAccount = TestAccountBuilder.createTestAccountWithOwner(owner);
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.FEE);
        
        when(accountService.getAccountById(-1)).thenReturn(bankAccount);
        doNothing().when(strategyHelper).transfer(transaction);

        // when
        feeTransaction.execute(transaction);

        // then
        assertEquals(bankAccount, transaction.getDestinationAccount());
        verify(strategyHelper).transfer(transaction);
        verify(accountService).getAccountById(-1);
    }
}