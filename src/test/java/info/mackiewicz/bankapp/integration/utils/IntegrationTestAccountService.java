package info.mackiewicz.bankapp.integration.utils;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

import java.math.BigDecimal;

@TestComponent
public class IntegrationTestAccountService {

    @Autowired
    private AccountService accountService;

    public Account createTestAccount(int userId) {
        return accountService.createAccount(userId);
    }

    public Account createTestAccountWithBalance(int userId, BigDecimal balance) {
        Account account = accountService.createAccount(userId);
        return accountService.deposit(account, balance);
    }
}
