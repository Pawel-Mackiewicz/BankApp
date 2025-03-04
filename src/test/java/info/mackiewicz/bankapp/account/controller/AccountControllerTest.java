package info.mackiewicz.bankapp.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.mackiewicz.bankapp.config.TestConfig;
import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.model.TestAccountBuilder;
import info.mackiewicz.bankapp.account.model.dto.CreateAccountRequest;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.shared.exception.AccountNotFoundByIdException;
import info.mackiewicz.bankapp.shared.exception.OwnerAccountsNotFoundException;
import info.mackiewicz.bankapp.user.model.User;
import org.iban4j.Iban;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@Import(TestConfig.class)
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    private Account createTestAccount(int id) {
        User owner = new User();
        owner.setId(1);
        owner.setFirstname("Jan");
        owner.setLastname("Kowalski");
        
        Account account = TestAccountBuilder.createTestAccountWithOwner(owner);
        TestAccountBuilder.setField(account, "id", id);
        TestAccountBuilder.setField(account, "userAccountNumber", 1001);
        TestAccountBuilder.setField(account, "iban", Iban.valueOf("PL61109010140000071219812874"));
        return account;
    }

    @Test
    @WithMockUser
    void getAccountById_WhenAccountExists_ShouldReturnAccount() throws Exception {
        // given
        Account account = createTestAccount(1);
        when(accountService.getAccountById(1)).thenReturn(account);

        // when & then
        mockMvc.perform(get("/api/accounts/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.owner.fullName").value("Jan Kowalski"));
    }

    @Test
    @WithMockUser
    void getAccountById_WhenAccountDoesNotExist_ShouldReturn404() throws Exception {
        // given
        when(accountService.getAccountById(999))
                .thenThrow(new AccountNotFoundByIdException("Account not found"));

        // when & then
        mockMvc.perform(get("/api/accounts/999")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void getAllAccounts_ShouldReturnListOfAccounts() throws Exception {
        // given
        Account account1 = createTestAccount(1);
        Account account2 = createTestAccount(2);
        when(accountService.getAllAccounts()).thenReturn(Arrays.asList(account1, account2));

        // when & then
        mockMvc.perform(get("/api/accounts")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    @WithMockUser
    void getAllAccounts_WhenNoAccounts_ShouldReturnEmptyList() throws Exception {
        // given
        when(accountService.getAllAccounts()).thenReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/api/accounts")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser
    void getAccountsByOwnerPesel_WhenAccountsExist_ShouldReturnAccounts() throws Exception {
        // given
        Account account = createTestAccount(1);
        when(accountService.getAccountsByOwnersPESEL("12345678901"))
                .thenReturn(Collections.singletonList(account));

        // when & then
        mockMvc.perform(get("/api/accounts/owner/12345678901")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser
    void getAccountsByOwnerPesel_WhenNoAccounts_ShouldReturn404() throws Exception {
        // given
        when(accountService.getAccountsByOwnersPESEL("99999999999"))
                .thenThrow(new OwnerAccountsNotFoundException("No accounts found"));

        // when & then
        mockMvc.perform(get("/api/accounts/owner/99999999999")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void createAccount_WithValidRequest_ShouldReturnCreatedAccount() throws Exception {
        // given
        CreateAccountRequest request = new CreateAccountRequest();
        request.setUserId(1);
        Account createdAccount = createTestAccount(1);
        when(accountService.createAccount(1)).thenReturn(createdAccount);

        // when & then
        mockMvc.perform(post("/api/accounts")
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void deleteAccount_WhenAccountExists_ShouldReturn204() throws Exception {
        // given
        doNothing().when(accountService).deleteAccountById(1);

        // when & then
        mockMvc.perform(delete("/api/accounts/1")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void deleteAccount_WhenAccountDoesNotExist_ShouldReturn404() throws Exception {
        // given
        doThrow(new AccountNotFoundByIdException("Account not found"))
                .when(accountService).deleteAccountById(999);

        // when & then
        mockMvc.perform(delete("/api/accounts/999")
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isNotFound());
    }
}