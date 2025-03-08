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

    public User registerUser(UserRegistrationDto registrationDto) {
        User user = userMapper.toUser(registrationDto);

        // Validate first name
        if (!isValidLetters(user.getFirstname())) {
            throw new IllegalArgumentException("Invalid first name: only letters allowed.");
        }
        // Validate last name
        if (!isValidLetters(user.getLastname())) {
            throw new IllegalArgumentException("Invalid last name: only letters allowed.");
        }
        
        User createdUser = userService.createUser(user);
        Account newAccount = accountService.createAccount(createdUser.getId());
        Account bankAccount = accountService.getAccountById(-1);

        Transaction transaction = Transaction.buildTransfer()
            .from(bankAccount)
            .to(newAccount)
            .asInternalTransfer()
            .withAmount(new BigDecimal("1000"))
            .withTitle("Welcome bonus")
            .build();

        transactionService.createTransaction(transaction);

        emailService.sendWelcomeEmail(createdUser.getEmail(), createdUser.getFullName(), createdUser.getUsername());

        return createdUser;
    }

    private boolean isValidLetters(String input) {
        return input != null && input.matches(LETTERS_REGEX);
    }
}
