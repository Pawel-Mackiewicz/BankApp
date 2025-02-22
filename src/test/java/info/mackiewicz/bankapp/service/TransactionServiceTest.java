package info.mackiewicz.bankapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.repository.TransactionRepository;

class TransactionServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceTest.class);

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTransactionById() {
        logger.info("testGetTransactionById: Starting test");
        Integer transactionId = 1;
        Transaction transaction = new Transaction();
        transaction.setId(transactionId);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        Optional<Transaction> result = Optional.ofNullable(transactionService.getTransactionById(transactionId));

        assertTrue(result.isPresent());
        assertEquals(transactionId, result.get().getId());
        logger.info("testGetTransactionById: Test passed");
    }

    @Test
    void testGetAllTransactions() {
        logger.info("testGetAllTransactions: Starting test");
        Transaction transaction1 = new Transaction();
        Transaction transaction2 = new Transaction();
        List<Transaction> transactions = List.of(transaction1, transaction2);

        when(transactionRepository.findAll()).thenReturn(transactions);

        List<Transaction> result = transactionService.getAllTransactions();

        assertEquals(2, result.size());
        logger.info("testGetAllTransactions: Test passed");
    }

    @Test
    void testSaveTransaction() {
         logger.info("testSaveTransaction: Starting test");
        Transaction transaction = new Transaction();
        transaction.setId(1);

        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Transaction result = transactionService.createTransaction(transaction);

        assertEquals(transaction.getId(), result.getId());
         logger.info("testSaveTransaction: Test passed");
    }

    @Test
    void testDeleteTransaction() {
        logger.info("testDeleteTransaction: Starting test");
        Integer transactionId = 1;
        Transaction transaction = new Transaction();
        transaction.setId(transactionId);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        transactionService.deleteTransactionById(transactionId);
        logger.info("testDeleteTransaction: Test passed");
    }
}