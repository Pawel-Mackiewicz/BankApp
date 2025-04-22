package info.mackiewicz.bankapp.presentation.auth.service;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.presentation.auth.dto.UserRegistrationRequest;
import info.mackiewicz.bankapp.system.notification.email.EmailService;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import info.mackiewicz.bankapp.user.UserMapper;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserRegistrationService {

    private final UserService userService;
    private final UserMapper userMapper;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final EmailService emailService;


    public User registerUser(UserRegistrationRequest registrationDto) {
        log.info("Starting user registration process for email: {}", registrationDto.getEmail());
        
        User user = userMapper.toUser(registrationDto);
        log.debug("Mapped registration DTO to User entity");
        
        User createdUser = userService.createUser(user);
        log.debug("Created user with ID: {}", createdUser.getId());
        
        Account newAccount = accountService.createAccount(createdUser.getId());
        log.debug("Created new account for user with ID: {}", newAccount.getId());
        
        Account bankAccount = accountService.getAccountById(-1);
        log.trace("Retrieved bank system account");

        Transaction transaction = Transaction.buildTransfer()
            .from(bankAccount)
            .to(newAccount)
            .withAmount(new BigDecimal("1000"))
            .withTitle("Welcome bonus")
            .build();
        log.debug("Prepared welcome bonus transaction");

        Transaction registeredTransaction = transactionService.registerTransaction(transaction);
        log.debug("Registered welcome bonus transaction");

        // Process the transaction to update account balances immediately
        transactionService.processTransactionById(registeredTransaction.getId());
        log.debug("Processed welcome bonus transaction for user with ID: {}", createdUser.getId());

        emailService.sendWelcomeEmail(createdUser.getEmail().toString(), createdUser.getFullName(), createdUser.getUsername());
        log.info("Completed user registration process for user: {}", createdUser.getUsername());

        return createdUser;
    }
}
