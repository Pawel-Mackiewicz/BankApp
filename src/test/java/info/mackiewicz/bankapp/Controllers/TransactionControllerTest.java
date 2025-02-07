package info.mackiewicz.bankapp.Controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import info.mackiewicz.bankapp.controller.TransactionController;
import info.mackiewicz.bankapp.model.Account;
import info.mackiewicz.bankapp.model.Transaction;
import info.mackiewicz.bankapp.model.TransactionBuilder;
import info.mackiewicz.bankapp.service.AccountService;
import info.mackiewicz.bankapp.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@SuppressWarnings("deprecation")
@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    // POST /api/transactions
    @Test
    public void testCreateTransactionSuccess() throws Exception {
        // JSON wejściowy – zakładamy, że 0 oznacza brak konta (ale tu mamy oba konta)
        String requestBody = "{\"fromAccountId\":1,\"toAccountId\":2,\"amount\":100.0,\"type\":\"TRANSFER\"}";

        // Przygotowanie obiektu Account dla konta źródłowego
        Account fromAccount = new Account();
        ReflectionTestUtils.setField(fromAccount, "id", 1);
        // Ustawiamy domyślny balance, aby nie był null
        ReflectionTestUtils.setField(fromAccount, "balance", BigDecimal.ZERO);

        // Przygotowanie obiektu Account dla konta docelowego
        Account toAccount = new Account();
        ReflectionTestUtils.setField(toAccount, "id", 2);
        ReflectionTestUtils.setField(toAccount, "balance", BigDecimal.ZERO);

        // Mockowanie wywołań do AccountService
        when(accountService.getAccountById(1)).thenReturn(fromAccount);
        when(accountService.getAccountById(2)).thenReturn(toAccount);

        // Budowanie transakcji przy użyciu TransactionBuilder
        Transaction transaction = new TransactionBuilder()
                .withFromAccount(fromAccount)
                .withToAccount(toAccount)
                .withAmount(BigDecimal.valueOf(100.0))
                .withType("TRANSFER")
                .build();
        ReflectionTestUtils.setField(transaction, "id", 1);

        when(transactionService.createTransaction(any(Transaction.class))).thenReturn(transaction);

        // Wykonanie żądania POST do endpointu
        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }


    // GET /api/transactions/{id}
    @Test
    public void testGetTransactionByIdSuccess() throws Exception {
        Transaction transaction = new TransactionBuilder()
                .withAmount(BigDecimal.valueOf(50.0))
                .withType("DEPOSIT")
                .build();
        ReflectionTestUtils.setField(transaction, "id", 1);
        when(transactionService.getTransactionById(1)).thenReturn(transaction);

        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    // GET /api/transactions – pobranie wszystkich transakcji
    @Test
    public void testGetAllTransactions() throws Exception {
        Transaction transaction1 = new TransactionBuilder()
                .withAmount(BigDecimal.valueOf(50.0))
                .withType("DEPOSIT")
                .build();
        ReflectionTestUtils.setField(transaction1, "id", 1);
        Transaction transaction2 = new TransactionBuilder()
                .withAmount(BigDecimal.valueOf(75.0))
                .withType("WITHDRAWAL")
                .build();
        ReflectionTestUtils.setField(transaction2, "id", 2);

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2);
        when(transactionService.getAllTransactions()).thenReturn(transactions);

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    // GET /api/transactions/account/{accountId} – pobranie transakcji dla danego konta
    @Test
    public void testGetTransactionsByAccountId() throws Exception {
        Transaction transaction = new TransactionBuilder()
                .withAmount(BigDecimal.valueOf(100.0))
                .withType("TRANSFER")
                .build();
        ReflectionTestUtils.setField(transaction, "id", 1);

        List<Transaction> transactions = Arrays.asList(transaction);
        when(transactionService.getTransactionsByAccountId(1)).thenReturn(transactions);

        mockMvc.perform(get("/api/transactions/account/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    // POST /api/transactions/{id}/process – przetwarzanie pojedynczej transakcji
    @Test
    public void testProcessTransactionByIdSuccess() throws Exception {
        // Metoda processTransactionById zwraca void, więc wystarczy sprawdzić status OK
        mockMvc.perform(post("/api/transactions/1/process"))
                .andExpect(status().isOk());
    }

    // POST /api/transactions/process-all – przetwarzanie wielu transakcji
    @Test
    public void testProcessAllTransactionsSuccess() throws Exception {
        List<Integer> transactionIds = Arrays.asList(1, 2);
        String requestBody = objectMapper.writeValueAsString(transactionIds);

        Transaction transaction1 = new TransactionBuilder()
                .withAmount(BigDecimal.valueOf(50.0))
                .withType("DEPOSIT")
                .build();
        ReflectionTestUtils.setField(transaction1, "id", 1);
        Transaction transaction2 = new TransactionBuilder()
                .withAmount(BigDecimal.valueOf(75.0))
                .withType("WITHDRAWAL")
                .build();
        ReflectionTestUtils.setField(transaction2, "id", 2);

        when(transactionService.getTransactionById(1)).thenReturn(transaction1);
        when(transactionService.getTransactionById(2)).thenReturn(transaction2);

        // Wywołanie endpointu – zakładamy, że metoda processAllTransactions działa bez zwracania rezultatu
        mockMvc.perform(post("/api/transactions/process-all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    // Test dla pozytywnego scenariusza usunięcia transakcji
    @Test
    public void testDeleteTransactionByIdSuccess() throws Exception {
        // Przyjmujemy, że wywołanie metody deleteTransactionById przebiega poprawnie (brak wyjątku)
        mockMvc.perform(delete("/api/transactions/1"))
                .andExpect(status().isOk());
    }

    // Test dla sytuacji, gdy transakcja nie zostanie znaleziona (rzucany wyjątek)
    @Test
    public void testDeleteTransactionByIdNotFound() throws Exception {
        // Symulujemy rzucenie wyjątku przez transactionService przy próbie usunięcia transakcji o id 99
        doThrow(new RuntimeException("Transaction not found"))
                .when(transactionService).deleteTransactionById(99);

        mockMvc.perform(delete("/api/transactions/99"))
                .andExpect(status().isNotFound());
    }

}
