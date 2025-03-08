package info.mackiewicz.bankapp.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.model.TestAccountBuilder;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.notification.email.EmailService;
import info.mackiewicz.bankapp.presentation.auth.dto.UserRegistrationDto;
import info.mackiewicz.bankapp.presentation.auth.service.UserRegistrationService;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.model.TransactionType;
import info.mackiewicz.bankapp.transaction.model.builder.TransferBuilder;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import info.mackiewicz.bankapp.user.UserMapper;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class UserRegistrationServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationServiceTest.class);

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private EmailService emailService;

    @Mock
    private TransferBuilder transferBuilder;

    @InjectMocks
    private UserRegistrationService registrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser() {
        // given
        logger.info("testRegisterUser: Starting test");
        UserRegistrationDto registrationDto = new UserRegistrationDto();
        registrationDto.setFirstname("John");
        registrationDto.setLastname("Doe");
        registrationDto.setEmail("john.doe@example.com");
        registrationDto.setPassword("password");

        User user = new User();
        user.setId(1);
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password");
        user.setUsername("johndoe");

        Account userAccount = TestAccountBuilder.createTestAccount();
        TestAccountBuilder.setField(userAccount, "id", 1);

        Account bankAccount = TestAccountBuilder.createTestAccount();
        TestAccountBuilder.setField(bankAccount, "id", -1);

        Transaction transaction = new Transaction();

        when(userMapper.toUser(registrationDto)).thenReturn(user);
        when(userService.createUser(user)).thenReturn(user);
        when(accountService.createAccount(anyInt())).thenReturn(userAccount);
        when(accountService.getAccountById(-1)).thenReturn(bankAccount);
        when(transferBuilder.from(any(Account.class))).thenReturn(transferBuilder);
        when(transferBuilder.to(any(Account.class))).thenReturn(transferBuilder);
        when(transferBuilder.asInternalTransfer())
.thenReturn(transferBuilder);
        when(transferBuilder.withTransactionType(TransactionType.TRANSFER_INTERNAL)).thenReturn(transferBuilder);
        when(transferBuilder.withAmount(any(BigDecimal.class))).thenReturn(transferBuilder);
        when(transferBuilder.withTitle(anyString())).thenReturn(transferBuilder);
        when(transferBuilder.build()).thenReturn(transaction);

        // when
        registrationService.registerUser(registrationDto);

        // then
        verify(userService).createUser(user);
        verify(accountService).createAccount(user.getId());
        verify(transactionService).createTransaction(any(Transaction.class));
        verify(emailService).sendWelcomeEmail(anyString(), anyString(), anyString());

        logger.info("testRegisterUser: Test passed");
    }
}