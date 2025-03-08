package info.mackiewicz.bankapp.service;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import java.math.BigDecimal;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.model.TestAccountBuilder;
import info.mackiewicz.bankapp.account.util.AccountLockManager;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.transaction.repository.TransactionRepository;
import info.mackiewicz.bankapp.transaction.service.TransactionProcessor;
import info.mackiewicz.bankapp.transaction.service.strategy.StrategyResolver;
import info.mackiewicz.bankapp.transaction.service.strategy.TransactionStrategy;
import info.mackiewicz.bankapp.transaction.validation.TransactionValidator;
import info.mackiewicz.bankapp.user.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TransactionProcessorTest {

    private static final Logger logger = LoggerFactory.getLogger(TransactionProcessorTest.class);

    @Mock
    private StrategyResolver strategyResolver;

    @Mock
    private TransactionRepository repository;

    @Mock
    private AccountLockManager accountLockManager;

    @Mock
    private TransactionStrategy strategy;

    @Mock
    private TransactionValidator validator;

    @InjectMocks
    private TransactionProcessor processor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessTransaction() {
        // given
        logger.info("testProcessTransaction: Starting test");
        User owner = new User();
        owner.setId(1);
        
        Account destinationAccount = TestAccountBuilder.createTestAccountWithOwner(owner);
        TestAccountBuilder.setField(destinationAccount, "id", 1);
        
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setAmount(new BigDecimal("100"));

        when(strategyResolver.resolveStrategy(any(Transaction.class))).thenReturn(strategy);
        when(strategy.execute(any(Transaction.class))).thenReturn(true);
        doNothing().when(validator).validate(any(Transaction.class));

        // when
        processor.processTransaction(transaction);

        // then
        verify(accountLockManager).lockAccounts(transaction.getDestinationAccount(), transaction.getSourceAccount());
        verify(accountLockManager).unlockAccounts(transaction.getSourceAccount(), transaction.getDestinationAccount());
        verify(strategyResolver).resolveStrategy(transaction);
        verify(strategy).execute(transaction);
        verify(validator).validate(transaction);
        verify(repository, atLeastOnce()).save(transaction);
        
        logger.info("testProcessTransaction: Test passed");
    }

    @Test
    void testProcessTransaction_FailedExecution() {
        // given
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setAmount(new BigDecimal("100"));

        when(strategyResolver.resolveStrategy(any(Transaction.class))).thenReturn(strategy);
        when(strategy.execute(any(Transaction.class))).thenReturn(false);
        doNothing().when(validator).validate(any(Transaction.class));

        // when
        processor.processTransaction(transaction);

        // then
        verify(strategyResolver).resolveStrategy(transaction);
        verify(strategy).execute(transaction);
        verify(validator).validate(transaction);
        verify(repository, atLeastOnce()).save(transaction);
    }
}