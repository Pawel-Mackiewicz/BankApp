package info.mackiewicz.bankapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.mackiewicz.bankapp.exception.AccountNotFoundByIdException;
import info.mackiewicz.bankapp.exception.NoTransactionsForAccountException;
import info.mackiewicz.bankapp.exception.TransactionNotFoundException;
import info.mackiewicz.bankapp.model.Account;
import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.model.TransactionStatus;
import info.mackiewicz.bankapp.model.User;
import info.mackiewicz.bankapp.service.AccountService;
import info.mackiewicz.bankapp.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    /**
     * Pomocnicza klasa do serializacji żądań tworzenia transakcji.
     */
    public static class CreateTransactionRequest {
        private int fromAccountId;
        private int toAccountId;
        private BigDecimal amount;
        private String type; // e.g., "TRANSFER", "DEPOSIT", etc.

        public CreateTransactionRequest() {}

        public CreateTransactionRequest(int fromAccountId, int toAccountId, BigDecimal amount, String type) {
            this.fromAccountId = fromAccountId;
            this.toAccountId = toAccountId;
            this.amount = amount;
            this.type = type;
        }

        public int getFromAccountId() {
            return fromAccountId;
        }

        public void setFromAccountId(int fromAccountId) {
            this.fromAccountId = fromAccountId;
        }

        public int getToAccountId() {
            return toAccountId;
        }

        public void setToAccountId(int toAccountId) {
            this.toAccountId = toAccountId;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    @Mock
    private TransactionService transactionService;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionController transactionController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Dodajemy GlobalExceptionHandler, aby sprawdzić, czy obsługa wyjątków działa tak jak w produkcji
        mockMvc = MockMvcBuilders
                .standaloneSetup(transactionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    // ------------------------------------------------------------
    // 1) TEST: GET /api/transactions/{id} – poprawny scenariusz
    // ------------------------------------------------------------
    @Test
    void testGetTransactionById() throws Exception {
        Transaction transaction = new Transaction();
        ReflectionTestUtils.setField(transaction, "id", 1);
        transaction.setStatus(TransactionStatus.NEW);
        transaction.setAmount(new BigDecimal("50.00"));

        // Stubujemy wywołanie w serwisie
        when(transactionService.getTransactionById(1))
                .thenReturn(transaction);

        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.amount", is(50.00)));
    }

    // ------------------------------------------------------------
    // 2) TEST: GET /api/transactions/{id} – brak transakcji
    // ------------------------------------------------------------
    @Test
    void testGetTransactionById_NotFound() throws Exception {
        // Stubujemy, że serwis rzuci wyjątek
        when(transactionService.getTransactionById(1))
                .thenThrow(new TransactionNotFoundException("Transaction 1 not found"));

        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Transaction 1 not found"));
    }

    // ------------------------------------------------------------
    // 3) TEST: GET /api/transactions – pobieranie wszystkich
    // ------------------------------------------------------------
    @Test
    void testGetAllTransactions() throws Exception {
        Transaction t1 = new Transaction();
        ReflectionTestUtils.setField(t1, "id", 1);
        t1.setStatus(TransactionStatus.NEW);
        t1.setAmount(new BigDecimal("10.00"));

        Transaction t2 = new Transaction();
        ReflectionTestUtils.setField(t2, "id", 2);
        t2.setStatus(TransactionStatus.NEW);
        t2.setAmount(new BigDecimal("20.00"));

        List<Transaction> transactions = Arrays.asList(t1, t2);

        when(transactionService.getAllTransactions()).thenReturn(transactions);

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(transactions.size())))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].amount", is(10.00)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].amount", is(20.00)));
    }

    // ------------------------------------------------------------
    // 4) TEST: GET /api/transactions/account/{accountId} – brak transakcji
    // ------------------------------------------------------------
    @Test
    void testGetTransactionsByAccountId_NoTransactionsFound() throws Exception {
        when(transactionService.getTransactionsByAccountId(100))
                .thenThrow(new NoTransactionsForAccountException("Account 100 did not make any transactions"));

        mockMvc.perform(get("/api/transactions/account/100"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Account 100 did not make any transactions"));
    }

    // ------------------------------------------------------------
    // 5) TEST: GET /api/transactions/account/{accountId} – konto nie istnieje
    // ------------------------------------------------------------
    @Test
    void testGetTransactionsByAccountId_AccountNotFound() throws Exception {
        when(transactionService.getTransactionsByAccountId(200))
                .thenThrow(new AccountNotFoundByIdException("Account 200 not found"));

        mockMvc.perform(get("/api/transactions/account/200"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Account 200 not found"));
    }

    // ------------------------------------------------------------
    // 6) TEST: POST /api/transactions – tworzenie transakcji
    // ------------------------------------------------------------
    @Test
    void testCreateTransaction() throws Exception {
        // Symulujemy, że konta istnieją
        User fromUser = new User();
        User toUser = new User();
        Account fromAccount = new Account(fromUser);
        Account toAccount = new Account(toUser);

        ReflectionTestUtils.setField(fromAccount, "id", 10);
        ReflectionTestUtils.setField(toAccount, "id", 20);

        when(accountService.getAccountById(10)).thenReturn(fromAccount);
        when(accountService.getAccountById(20)).thenReturn(toAccount);

        // Symulacja tworzenia nowej transakcji
        Transaction transaction = new Transaction();
        ReflectionTestUtils.setField(transaction, "id", 1);
        transaction.setStatus(TransactionStatus.NEW);
        transaction.setAmount(new BigDecimal("100.00"));

        when(transactionService.createTransaction(any(Transaction.class)))
                .thenReturn(transaction);

        CreateTransactionRequest request = new CreateTransactionRequest(10, 20, new BigDecimal("100.00"), "TRANSFER");

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.amount", is(100.00)));
    }

    // ------------------------------------------------------------
    // 7) TEST: DELETE /api/transactions/{id}
    // ------------------------------------------------------------
    @Test
    void testDeleteTransaction() throws Exception {
        // Zakładamy, że usunięcie zakończy się sukcesem, bez dodatkowych stubów
        mockMvc.perform(delete("/api/transactions/1"))
                .andExpect(status().isOk());
    }

    // ------------------------------------------------------------
    // 8) TEST: POST /api/transactions/{id}/process – przetwarzanie transakcji
    // ------------------------------------------------------------
    @Test
    void testProcessTransactionById() throws Exception {
        // Symulujemy istnienie transakcji
        Transaction transaction = new Transaction();
        ReflectionTestUtils.setField(transaction, "id", 1);
        transaction.setStatus(TransactionStatus.NEW);
        transaction.setAmount(new BigDecimal("75.00"));

        // Gdy kontroler wywoła getTransactionById(1), zwrócimy transakcję
        when(transactionService.getTransactionById(1)).thenReturn(transaction);

        mockMvc.perform(post("/api/transactions/1/process"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.amount", is(75.00)));
    }

    // ------------------------------------------------------------
    // 9) TEST: POST /api/transactions/process-all – przetwarzanie wszystkich transakcji
    // ------------------------------------------------------------
    @Test
    void testProcessAllNewTransactions() throws Exception {
        // Załóżmy, że nie stubujemy nic więcej, bo nie pobiera zwracanych danych – wystarczy 200
        mockMvc.perform(post("/api/transactions/process-all"))
                .andExpect(status().isOk());
    }
}
