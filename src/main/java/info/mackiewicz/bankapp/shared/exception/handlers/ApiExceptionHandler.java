package info.mackiewicz.bankapp.shared.exception.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import info.mackiewicz.bankapp.account.exception.AccountNotFoundByIdException;
import info.mackiewicz.bankapp.account.exception.OwnerAccountsNotFoundException;
import info.mackiewicz.bankapp.presentation.exception.InvalidUserException;
import info.mackiewicz.bankapp.transaction.exception.NoTransactionsForAccountException;
import info.mackiewicz.bankapp.transaction.exception.TransactionAlreadyProcessedException;
import info.mackiewicz.bankapp.transaction.exception.TransactionAmountNotSpecifiedException;
import info.mackiewicz.bankapp.transaction.exception.TransactionCannotBeProcessedException;
import info.mackiewicz.bankapp.transaction.exception.TransactionDestinationAccountNotSpecifiedException;
import info.mackiewicz.bankapp.transaction.exception.TransactionNotFoundException;
import info.mackiewicz.bankapp.transaction.exception.TransactionSourceAccountNotSpecifiedException;
import info.mackiewicz.bankapp.transaction.exception.TransactionTypeNotSpecifiedException;
import info.mackiewicz.bankapp.shared.dto.ApiResponse;
import info.mackiewicz.bankapp.user.exception.DuplicatedUserException;
import info.mackiewicz.bankapp.user.exception.UserNotFoundException;
import info.mackiewicz.bankapp.user.exception.UserValidationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

//TODO: " CO Z TYM ZROBIĆ? =.=" "
@RestControllerAdvice(basePackages = "info.mackiewicz.bankapp.user.controller")
public class ApiExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    // Handling User Exceptions
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleUserNotFoundException(UserNotFoundException ex) {
        logger.error("User not found: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(DuplicatedUserException.class)
    public ResponseEntity<String> handleDuplicatedUserException(DuplicatedUserException ex) {
        logger.error("Duplicate user error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidUserException.class)
    public ResponseEntity<String> handleInvalidUserException(InvalidUserException ex) {
        logger.error("Invalid user error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // Handling Account Exceptions
    @ExceptionHandler(AccountNotFoundByIdException.class)
    public ResponseEntity<String> handleAccountNotFoundByIdException(AccountNotFoundByIdException ex) {
        logger.error("Account not found by ID: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(OwnerAccountsNotFoundException.class)
    public ResponseEntity<String> handleOwnerAccountsNotFoundException(OwnerAccountsNotFoundException ex) {
        logger.error("Owner accounts not found: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    // Handling Transaction Exceptions
    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<String> handleTransactionNotFoundException(TransactionNotFoundException ex) {
        logger.error("Transaction not found: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(NoTransactionsForAccountException.class)
    public ResponseEntity<String> handleNoTransactionsForAccountException(NoTransactionsForAccountException ex) {
        logger.error("No transactions found for account: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(TransactionAlreadyProcessedException.class)
    public ResponseEntity<String> handleTransactionAlreadyProcessedException(TransactionAlreadyProcessedException ex) {
        logger.error("Transaction already processed: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(TransactionCannotBeProcessedException.class)
    public ResponseEntity<String> handleTransactionCannotBeProcessedException(TransactionCannotBeProcessedException ex) {
        logger.error("Transaction cannot be processed: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(TransactionAmountNotSpecifiedException.class)
    public ResponseEntity<String> handleTransactionAmountNotSpecifiedException(TransactionAmountNotSpecifiedException ex) {
        logger.error("Transaction amount not specified: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(TransactionTypeNotSpecifiedException.class)
    public ResponseEntity<String> handleTransactionTypeNotSpecifiedException(TransactionTypeNotSpecifiedException ex) {
        logger.error("Transaction type not specified: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(TransactionSourceAccountNotSpecifiedException.class)
    public ResponseEntity<String> handleTransactionSourceAccountNotSpecifiedException(TransactionSourceAccountNotSpecifiedException ex) {
        logger.error("Transaction source account not specified: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(TransactionDestinationAccountNotSpecifiedException.class)
    public ResponseEntity<String> handleTransactionDestinationAccountNotSpecifiedException(TransactionDestinationAccountNotSpecifiedException ex) {
        logger.error("Transaction destination account not specified: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(UserValidationException.class)
    public ResponseEntity<ApiResponse<?>> handleUserValidationException(UserValidationException ex) {
        logger.error("User validation error: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        String errorMessage = bindingResult.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                .orElse("Validation failed");
                
        logger.error("Validation error: {}", errorMessage);
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(errorMessage, HttpStatus.BAD_REQUEST));
    }

    // Global Exception Handler for all other exceptions
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("An illegal argument error occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleAllExceptions(Exception ex) {
        logger.error("An exception occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR));
    }
}