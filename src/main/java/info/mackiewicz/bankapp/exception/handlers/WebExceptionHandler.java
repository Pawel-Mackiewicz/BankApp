package info.mackiewicz.bankapp.exception.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import info.mackiewicz.bankapp.exception.AccountNotFoundByIdException;
import info.mackiewicz.bankapp.exception.DuplicatedUserException;
import info.mackiewicz.bankapp.exception.InvalidUserException;
import info.mackiewicz.bankapp.exception.NoTransactionsForAccountException;
import info.mackiewicz.bankapp.exception.OwnerAccountsNotFoundException;
import info.mackiewicz.bankapp.exception.TransactionAlreadyProcessedException;
import info.mackiewicz.bankapp.exception.TransactionAmountNotSpecifiedException;
import info.mackiewicz.bankapp.exception.TransactionCannotBeProcessedException;
import info.mackiewicz.bankapp.exception.TransactionDestinationAccountNotSpecifiedException;
import info.mackiewicz.bankapp.exception.TransactionNotFoundException;
import info.mackiewicz.bankapp.exception.TransactionSourceAccountNotSpecifiedException;
import info.mackiewicz.bankapp.exception.TransactionTypeNotSpecifiedException;
import info.mackiewicz.bankapp.exception.UserNotFoundException;

@ControllerAdvice(basePackages = "info.mackiewicz.bankapp.web")
public class WebExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebExceptionHandler.class);

    // Handling User Exceptions
    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleUserNotFoundException(UserNotFoundException ex) {
        logger.error("User not found: {}", ex.getMessage(), ex);
        return createErrorModelAndView("User not found", ex.getMessage());
    }

    @ExceptionHandler(DuplicatedUserException.class)
    public ModelAndView handleDuplicatedUserException(DuplicatedUserException ex) {
        logger.error("Duplicate user error: {}", ex.getMessage(), ex);
        return createErrorModelAndView("User already exists", ex.getMessage());
    }

    @ExceptionHandler(InvalidUserException.class)
    public ModelAndView handleInvalidUserException(InvalidUserException ex) {
        logger.error("Invalid user error: {}", ex.getMessage(), ex);
        return createErrorModelAndView("Invalid user data", ex.getMessage());
    }

    // Handling Account Exceptions
    @ExceptionHandler(AccountNotFoundByIdException.class)
    public ModelAndView handleAccountNotFoundByIdException(AccountNotFoundByIdException ex) {
        logger.error("Account not found by ID: {}", ex.getMessage(), ex);
        return createErrorModelAndView("Account not found", ex.getMessage());
    }

    @ExceptionHandler(OwnerAccountsNotFoundException.class)
    public ModelAndView handleOwnerAccountsNotFoundException(OwnerAccountsNotFoundException ex) {
        logger.error("Owner accounts not found: {}", ex.getMessage(), ex);
        return createErrorModelAndView("No accounts found", ex.getMessage());
    }

    // Handling Transaction Exceptions
    @ExceptionHandler(TransactionNotFoundException.class)
    public ModelAndView handleTransactionNotFoundException(TransactionNotFoundException ex) {
        logger.error("Transaction not found: {}", ex.getMessage(), ex);
        return createErrorModelAndView("Transaction not found", ex.getMessage());
    }

    @ExceptionHandler({
        NoTransactionsForAccountException.class,
        TransactionAlreadyProcessedException.class,
        TransactionCannotBeProcessedException.class,
        TransactionAmountNotSpecifiedException.class,
        TransactionTypeNotSpecifiedException.class,
        TransactionSourceAccountNotSpecifiedException.class,
        TransactionDestinationAccountNotSpecifiedException.class
    })
    public ModelAndView handleTransactionExceptions(Exception ex) {
        logger.error("Transaction error: {}", ex.getMessage(), ex);
        return createErrorModelAndView("Transaction Error", ex.getMessage());
    }

    // Global Exception Handler for all other exceptions
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAllExceptions(Exception ex) {
        logger.error("An exception occurred: {}", ex.getMessage(), ex);
        return createErrorModelAndView("Error", "An unexpected error occurred. Please try again later.");
    }

    private ModelAndView createErrorModelAndView(String title, String message) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("title", title);
        mav.addObject("message", message);
        return mav;
    }
}