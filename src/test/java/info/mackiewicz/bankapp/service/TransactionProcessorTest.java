package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.transaction.repository.TransactionRepository;
import info.mackiewicz.bankapp.transaction.service.TransactionHydrator;
import info.mackiewicz.bankapp.transaction.service.TransactionProcessor;

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

    @InjectMocks
    private TransactionProcessor processor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessTransaction() {
        logger.info("testProcessTransaction: Starting test");
        Transaction transaction = new Transaction();
        transaction.setType(TransactionType.DEPOSIT);

        processor.processTransaction(transaction);

        logger.info("testProcessTransaction: Test passed");
    }
}