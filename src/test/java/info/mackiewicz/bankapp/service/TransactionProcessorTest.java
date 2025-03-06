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
import info.mackiewicz.bankapp.transaction.service.TransactionHydrator;
import info.mackiewicz.bankapp.transaction.service.TransactionProcessor;
import info.mackiewicz.bankapp.transaction.service.strategy.TransactionStrategy;
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
    private TransactionHydrator hydrator;

    @Mock
    private TransactionRepository repository;

    @Mock
    private AccountLockManager accountLockManager;

    @Mock
    private TransactionStrategy strategy;

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
        transaction.setStrategy(strategy);

        when(hydrator.hydrate(any(Transaction.class))).thenReturn(transaction);
        when(strategy.execute(any(Transaction.class))).thenReturn(true);

        // when
        processor.processTransaction(transaction);

        // then
        verify(accountLockManager).lockAccounts(transaction.getDestinationAccount(), transaction.getSourceAccount());
        verify(accountLockManager).unlockAccounts(transaction.getSourceAccount(), transaction.getDestinationAccount());
        verify(hydrator).hydrate(transaction);
        verify(repository, atLeastOnce()).save(transaction);
        verify(strategy).execute(transaction);
        
        logger.info("testProcessTransaction: Test passed");
    }
}