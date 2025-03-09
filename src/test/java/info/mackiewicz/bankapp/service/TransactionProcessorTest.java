package info.mackiewicz.bankapp.service;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

import java.math.BigDecimal;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.model.TestAccountBuilder;
import info.mackiewicz.bankapp.account.util.AccountLockManager;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionStatus;
import info.mackiewicz.bankapp.transaction.model.TransactionStatusCategory;
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
        verify(accountLockManager).lockAccounts(transaction.getSourceAccount(), transaction.getDestinationAccount());
        verify(accountLockManager).unlockAccounts(transaction.getDestinationAccount(), transaction.getSourceAccount());
        verify(strategyResolver).resolveStrategy(transaction);
        verify(strategy).execute(transaction);
        verify(validator).validate(transaction);
        verify(repository).save(argThat(t -> t.getStatus().getCategory() == TransactionStatusCategory.FAULTY));
        
        logger.info("testProcessTransaction: Test passed");
    }

    @Test
    void testProcessTransaction_FailedExecution() {
        // given
        User owner = new User();
        owner.setId(1);
        Account account = TestAccountBuilder.createTestAccountWithOwner(owner);
        Transaction transaction = Transaction.buildDeposit()
                                .to(account)
                                .withAmount(new BigDecimal(100))
                                .withTitle("test")
                                .build();

        when(strategyResolver.resolveStrategy(any(Transaction.class))).thenReturn(strategy);
        when(strategy.execute(any(Transaction.class))).thenReturn(false);
        doNothing().when(validator).validate(any(Transaction.class));

        // when & then
        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
            RuntimeException.class,
            () -> processor.processTransaction(transaction)
        );
        
        org.junit.jupiter.api.Assertions.assertEquals(
            "Unexpected error during transaction processing",
            exception.getMessage()
        );

        // Verify locking with correct order (sourceAccount, destinationAccount)
        verify(accountLockManager).lockAccounts(null, account);
        verify(accountLockManager).unlockAccounts(null, account);
        verify(strategyResolver).resolveStrategy(transaction);
        verify(strategy).execute(transaction);
        verify(validator).validate(transaction);
        // Verify that repository.save was called multiple times and the final status is SYSTEM_ERROR
        verify(repository, atLeast(1)).save(any(Transaction.class));
        verify(repository, atLeastOnce()).save(argThat(t ->
            t.getStatus() == TransactionStatus.SYSTEM_ERROR
        ));
    }
}