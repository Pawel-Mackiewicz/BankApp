package info.mackiewicz.bankapp.presentation.auth.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import info.mackiewicz.bankapp.account.model.Account;
import info.mackiewicz.bankapp.account.service.AccountService;
import info.mackiewicz.bankapp.notification.email.EmailService;
import info.mackiewicz.bankapp.presentation.auth.dto.UserRegistrationDto;
import info.mackiewicz.bankapp.transaction.model.Transaction;
import info.mackiewicz.bankapp.transaction.service.TransactionService;
import info.mackiewicz.bankapp.user.UserMapper;
import info.mackiewicz.bankapp.user.model.User;
import info.mackiewicz.bankapp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserRegistrationService {

    private final UserService userService;
    private final UserMapper userMapper;
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final EmailService emailService;

    // Only allow letters (English and Polish)
    private static final String LETTERS_REGEX = "^[A-Za-zĄĆĘŁŃÓŚŹŻąćęłńóśźż]+$";

    //TODO: ADD VALIDATION FOR NAME AND SURNAME (BUT WHERE?)
    public User registerUser(UserRegistrationDto registrationDto) {
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
            .asInternalTransfer()
            .withAmount(new BigDecimal("1000"))
            .withTitle("Welcome bonus")
            .build();
        log.debug("Prepared welcome bonus transaction");

        transactionService.registerTransaction(transaction);
        log.debug("Registered welcome bonus transaction");

        emailService.sendWelcomeEmail(createdUser.getEmail().toString(), createdUser.getFullName(), createdUser.getUsername());
        log.info("Completed user registration process for user: {}", createdUser.getUsername());

        return createdUser;
    }

    private boolean isValidLetters(String input) {
        return input != null && input.matches(LETTERS_REGEX);
    }
}
