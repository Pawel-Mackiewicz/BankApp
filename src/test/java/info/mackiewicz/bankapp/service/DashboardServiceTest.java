package info.mackiewicz.bankapp.service;

import info.mackiewicz.bankapp.dto.DashboardDTO;
import info.mackiewicz.bankapp.model.Account;
import info.mackiewicz.bankapp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

class DashboardServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(DashboardServiceTest.class);

    @Mock
    private AccountService accountService;

    @Mock
    private UserService userService;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDashboardData() {
        logger.info("testGetDashboardData: Starting test");
        Integer userId = 1;
        User user = new User();
        user.setId(userId);

        List<Account> accounts = new ArrayList<>();
        Account account1 = new Account();
        try {
            Field balanceField = Account.class.getDeclaredField("balance");
            balanceField.setAccessible(true);
            balanceField.set(account1, BigDecimal.TEN);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        accounts.add(account1);
        Account account2 = new Account();
        try {
            Field balanceField = Account.class.getDeclaredField("balance");
            balanceField.setAccessible(true);
            balanceField.set(account2, BigDecimal.ONE);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        accounts.add(account2);

        when(userService.getUserById(userId)).thenReturn(user);
        when(accountService.getAccountsByOwnersId(userId)).thenAnswer(invocation -> {
            List<Account> mockedAccounts = new ArrayList<>();
            Account mockedAccount1 = new Account();
            try {
                Field idField = Account.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(mockedAccount1, 1);
                 Field balanceField = Account.class.getDeclaredField("balance");
                balanceField.setAccessible(true);
                balanceField.set(mockedAccount1, BigDecimal.TEN);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            mockedAccounts.add(mockedAccount1);
            Account mockedAccount2 = new Account();
            try {
                Field idField = Account.class.getDeclaredField("id");
                idField.setAccessible(true);
                idField.set(mockedAccount2, 2);
                 Field balanceField = Account.class.getDeclaredField("balance");
                balanceField.setAccessible(true);
                balanceField.set(mockedAccount2, BigDecimal.ONE);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            mockedAccounts.add(mockedAccount2);
            return mockedAccounts;
        });

        DashboardDTO dashboardData = dashboardService.getDashboardData(userId);

        assertNotNull(dashboardData);
        assertEquals(2, dashboardData.getAccounts().size());
        logger.info("testGetDashboardData: Test passed");
    }
}