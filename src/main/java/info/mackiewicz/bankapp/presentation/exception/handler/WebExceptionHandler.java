package info.mackiewicz.bankapp.presentation.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import info.mackiewicz.bankapp.transaction.exception.*;
import info.mackiewicz.bankapp.account.exception.AccountNotFoundByIdException;
import info.mackiewicz.bankapp.account.exception.OwnerAccountsNotFoundException;
import info.mackiewicz.bankapp.presentation.exception.InvalidUserException;
import info.mackiewicz.bankapp.presentation.exception.handler.dto.ErrorResponse;
import info.mackiewicz.bankapp.user.exception.DuplicatedUserException;
import info.mackiewicz.bankapp.user.exception.UserNotFoundException;
import info.mackiewicz.bankapp.user.exception.UserValidationException;

@ControllerAdvice(basePackages = "info.mackiewicz.bankapp.presentation")
public class WebExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebExceptionHandler.class);

    // Handling User Validation Exceptions
    @ExceptionHandler({
        UserValidationException.class,
        InvalidUserException.class,
        DuplicatedUserException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleUserValidationExceptions(Exception ex) {
        logger.error("User validation error: {}", ex.getMessage(), ex);
        return createErrorModelAndView(
            "User Validation Error",
            ex.getMessage(),
            HttpStatus.BAD_REQUEST
        );
    }

    // Handling User Not Found Exception
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleUserNotFoundException(UserNotFoundException ex) {
        logger.error("User not found: {}", ex.getMessage(), ex);
        return createErrorModelAndView(
            "User Not Found",
            ex.getMessage(),
            HttpStatus.NOT_FOUND
        );
    }

    // Handling Account Not Found Exceptions
    @ExceptionHandler({
        AccountNotFoundByIdException.class,
        OwnerAccountsNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleAccountNotFoundExceptions(Exception ex) {
        logger.error("Account not found: {}", ex.getMessage(), ex);
        return createErrorModelAndView(
            "Account Not Found",
            ex.getMessage(),
            HttpStatus.NOT_FOUND
        );
    }

    // Handling Transaction Not Found Exception
    @ExceptionHandler(TransactionNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleTransactionNotFoundException(TransactionNotFoundException ex) {
        logger.error("Transaction not found: {}", ex.getMessage(), ex);
        return createErrorModelAndView(
            "Transaction Not Found",
            ex.getMessage(),
            HttpStatus.NOT_FOUND
        );
    }

    // Handling Transaction Validation Exceptions
    @ExceptionHandler({
        TransactionValidationException.class,
        TransactionAmountNotSpecifiedException.class,
        TransactionTypeNotSpecifiedException.class,
        TransactionSourceAccountNotSpecifiedException.class,
        TransactionDestinationAccountNotSpecifiedException.class,
        InvalidTransactionTypeException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleTransactionValidationExceptions(Exception ex) {
        logger.error("Transaction validation error: {}", ex.getMessage(), ex);
        return createErrorModelAndView(
            "Transaction Validation Error",
            ex.getMessage(),
            HttpStatus.BAD_REQUEST
        );
    }

    // Handling Transaction Processing Exceptions
    @ExceptionHandler({
        TransactionAlreadyProcessedException.class,
        TransactionCannotBeProcessedException.class,
        TransactionExecutionException.class,
        NoTransactionsForAccountException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ModelAndView handleTransactionProcessingExceptions(Exception ex) {
        logger.error("Transaction processing error: {}", ex.getMessage(), ex);
        return createErrorModelAndView(
            "Transaction Processing Error",
            ex.getMessage(),
            HttpStatus.CONFLICT
        );
    }

    // Handling Transaction Business Logic Exceptions
    @ExceptionHandler({
        InsufficientFundsException.class,
        InvalidOperationException.class,
    })
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ModelAndView handleTransactionBusinessExceptions(Exception ex) {
        logger.error("Transaction business error: {}", ex.getMessage(), ex);
        return createErrorModelAndView(
            "Transaction Business Error",
            ex.getMessage(),
            HttpStatus.UNPROCESSABLE_ENTITY
        );
    }

    // Global Exception Handler for all other exceptions
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleAllExceptions(Exception ex) {
        logger.error("An exception occurred: {}", ex.getMessage(), ex);
        return createErrorModelAndView(
            "Internal Server Error",
            "An unexpected error occurred. Please try again later.",
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    private ModelAndView createErrorModelAndView(String title, String message, HttpStatus status) {
        ModelAndView mav = new ModelAndView("error");
        ErrorResponse errorResponse = new ErrorResponse(
            title,
            message,
            "/", // TODO: Add request path when available
            status.value(),
            status.getReasonPhrase()
        );
        mav.addObject("error", errorResponse);
        return mav;
    }
}