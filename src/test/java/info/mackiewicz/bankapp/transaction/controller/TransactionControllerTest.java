package info.mackiewicz.bankapp.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.testutils.TestAccountBuilder;
import info.mackiewicz.bankapp.testutils.TestUserBuilder;
import info.mackiewicz.bankapp.testutils.config.TestConfig;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.dto.CreateTransactionRequest;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import info.mackiewicz.bankapp.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@WithMockUser
@Import(TestConfig.class)
public class TransactionControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockitoBean
    private TransactionService transactionService;
    
    @MockitoBean
    private AccountService accountService;
    
    private Transaction mockTransaction;
    private Account sourceAccount;
    private Account destinationAccount;
    private CreateTransactionRequest createRequest;

    @BeforeEach
    void setUp() {
        // Prepare test user
        User testUser = TestUserBuilder.createTestUser();
        
        // Prepare test accounts
        sourceAccount = TestAccountBuilder.createTestAccount(1, new BigDecimal("1000.00"), testUser);
        destinationAccount = TestAccountBuilder.createTestAccount(2, new BigDecimal("500.00"), testUser);
        
        // Prepare test transaction
        mockTransaction = Transaction.buildTransfer()
                .from(sourceAccount)
                .to(destinationAccount)
                .withAmount(new BigDecimal("100.00"))
                .withTitle("Test transaction")
                .build();
        
        // Set additional transaction fields using reflection
        try {
            java.lang.reflect.Field idField = mockTransaction.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(mockTransaction, 1);
            
            java.lang.reflect.Field dateField = mockTransaction.getClass().getDeclaredField("date");
            dateField.setAccessible(true);
            dateField.set(mockTransaction, LocalDateTime.now());
        } catch (Exception e) {
            throw new RuntimeException("Failed to set transaction fields", e);
        }
        
        // Prepare transaction creation request
        createRequest = new CreateTransactionRequest();
        createRequest.setSourceAccountId(1);
        createRequest.setDestinationAccountId(2);
        createRequest.setAmount(new BigDecimal("100.00"));
        createRequest.setTitle("Test transaction");
        createRequest.setType("TRANSFER_INTERNAL");
    }
    
    @Test
    @DisplayName("Should create a new transaction")
    void shouldCreateTransaction() throws Exception {
        // given
        when(accountService.getAccountById(1)).thenReturn(sourceAccount);
        when(accountService.getAccountById(2)).thenReturn(destinationAccount);
        when(transactionService.registerTransaction(any(Transaction.class))).thenReturn(mockTransaction);
        
        // when & then
        mockMvc.perform(post("/api/transactions")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.title").value("Test transaction"))
                .andExpect(jsonPath("$.status.name").value("NEW"));
        
        verify(transactionService).registerTransaction(any(Transaction.class));
    }
    
    @Test
    @DisplayName("Should delete transaction by ID")
    void shouldDeleteTransactionById() throws Exception {
        // given
        doNothing().when(transactionService).deleteTransactionById(1);
        
        // when & then
        mockMvc.perform(delete("/api/transactions/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
        
        verify(transactionService).deleteTransactionById(1);
    }
    
    @Test
    @DisplayName("Should return transaction by ID")
    void shouldGetTransactionById() throws Exception {
        // given
        when(transactionService.getTransactionById(1)).thenReturn(mockTransaction);
        
        // when & then
        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.title").value("Test transaction"));
        
        verify(transactionService).getTransactionById(1);
    }
    
    @Test
    @DisplayName("Should return all transactions")
    void shouldGetAllTransactions() throws Exception {
        // given
        when(transactionService.getAllTransactions()).thenReturn(Collections.singletonList(mockTransaction));
        
        // when & then
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].amount").value(100.00))
                .andExpect(jsonPath("$[0].title").value("Test transaction"));
        
        verify(transactionService).getAllTransactions();
    }
    
    @Test
    @DisplayName("Should return all transactions for account")
    void shouldGetTransactionsByAccountId() throws Exception {
        // given
        when(accountService.getAccountById(1)).thenReturn(sourceAccount);
        when(transactionService.getTransactionsByAccountId(1)).thenReturn(Collections.singletonList(mockTransaction));
        
        // when & then
        mockMvc.perform(get("/api/transactions/account/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].amount").value(100.00))
                .andExpect(jsonPath("$[0].title").value("Test transaction"));
        
        verify(accountService).getAccountById(1);
        verify(transactionService).getTransactionsByAccountId(1);
    }
    
    @Test
    @DisplayName("Should return empty list when no transactions for account")
    void shouldReturnEmptyListWhenNoTransactionsForAccount() throws Exception {
        // given
        when(accountService.getAccountById(1)).thenReturn(sourceAccount);
        when(transactionService.getTransactionsByAccountId(1)).thenReturn(Collections.emptyList());
        
        // when & then
        mockMvc.perform(get("/api/transactions/account/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        
        verify(accountService).getAccountById(1);
        verify(transactionService).getTransactionsByAccountId(1);
    }
    
    @Test
    @DisplayName("Should process transaction by ID")
    void shouldProcessTransactionById() throws Exception {
        // given
        doNothing().when(transactionService).processTransactionById(1);
        when(transactionService.getTransactionById(1)).thenReturn(mockTransaction);
        
        // when & then
        mockMvc.perform(post("/api/transactions/1/process")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(100.00));
        
        verify(transactionService).processTransactionById(1);
        verify(transactionService).getTransactionById(1);
    }
    
    @Test
    @DisplayName("Should process all new transactions")
    void shouldProcessAllNewTransactions() throws Exception {
        // given
        doNothing().when(transactionService).processAllNewTransactions();
        
        // when & then
        mockMvc.perform(post("/api/transactions/process-all")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
        
        verify(transactionService).processAllNewTransactions();
    }
}