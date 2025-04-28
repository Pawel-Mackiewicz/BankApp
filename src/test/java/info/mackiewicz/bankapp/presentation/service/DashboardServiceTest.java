package info.mackiewicz.bankapp.presentation.service;

import info.mackiewicz.bankapp.core.account.model.Account;
import info.mackiewicz.bankapp.core.account.model.TestAccountBuilder;
import info.mackiewicz.bankapp.core.account.service.AccountService;
import info.mackiewicz.bankapp.core.user.model.User;
import info.mackiewicz.bankapp.core.user.service.UserService;
import info.mackiewicz.bankapp.presentation.dashboard.dto.DashboardDTO;
import info.mackiewicz.bankapp.presentation.dashboard.service.DashboardService;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        accounts.add(TestAccountBuilder.createTestAccountWithBalance(BigDecimal.TEN));
        accounts.add(TestAccountBuilder.createTestAccountWithBalance(BigDecimal.ONE));

        when(userService.getUserById(userId)).thenReturn(user);
        when(accountService.getAccountsByOwnersId(userId)).thenAnswer(invocation -> {
            List<Account> mockedAccounts = new ArrayList<>();
            
            Account mockedAccount1 = TestAccountBuilder.createTestAccountWithBalance(BigDecimal.TEN);
            TestAccountBuilder.setField(mockedAccount1, "id", 1);
            mockedAccounts.add(mockedAccount1);
            
            Account mockedAccount2 = TestAccountBuilder.createTestAccountWithBalance(BigDecimal.ONE);
            TestAccountBuilder.setField(mockedAccount2, "id", 2);
            mockedAccounts.add(mockedAccount2);
            
            return mockedAccounts;
        });

        DashboardDTO dashboardData = dashboardService.getDashboardData(userId);

        assertNotNull(dashboardData);
        assertEquals(2, dashboardData.getAccounts().size());
        logger.info("testGetDashboardData: Test passed");
    }
}