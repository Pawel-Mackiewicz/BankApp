package info.mackiewicz.bankapp.transaction.service.strategy;

import static org.junit.jupiter.api.Assertions.*;

import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class StrategyResolverTest {

    @Mock
    private DepositTransaction depositStrategy;

    @Mock
    private WithdrawalTransaction withdrawalStrategy;

    @Mock
    private TransferTransaction transferStrategy;

    @Mock
    private FeeTransaction feeStrategy;

    @InjectMocks
    private StrategyResolver resolver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void resolveStrategy_Deposit_ShouldReturnDepositStrategy() {
        // given
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.DEPOSIT);

        // when
        TransactionStrategy strategy = resolver.resolveStrategy(transaction);

        // then
        assertSame(depositStrategy, strategy);
    }

    @Test
    void resolveStrategy_Withdrawal_ShouldReturnWithdrawalStrategy() {
        // given
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.WITHDRAWAL);

        // when
        TransactionStrategy strategy = resolver.resolveStrategy(transaction);

        // then
        assertSame(withdrawalStrategy, strategy);
    }

    @Test
    void resolveStrategy_Transfer_ShouldReturnTransferStrategy() {
        // given
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.TRANSFER_INTERNAL);

        // when
        TransactionStrategy strategy = resolver.resolveStrategy(transaction);

        // then
        assertSame(transferStrategy, strategy);
    }

    @Test
    void resolveStrategy_Fee_ShouldReturnFeeStrategy() {
        // given
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.FEE);

        // when
        TransactionStrategy strategy = resolver.resolveStrategy(transaction);

        // then
        assertSame(feeStrategy, strategy);
    }

    @Test
    void resolveStrategy_NullType_ShouldThrowException() {
        // given
        Transaction transaction = new Transaction();

        // when & then
        assertThrows(IllegalArgumentException.class, 
            () -> resolver.resolveStrategy(transaction));
    }
}