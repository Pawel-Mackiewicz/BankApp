package info.mackiewicz.bankapp.shared.exception.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import info.mackiewicz.bankapp.shared.exception.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Handling User Exceptions
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        logger.error("User not found: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
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

    @ExceptionHandler(TooManyPasswordResetAttemptsException.class)
    public ResponseEntity<String> handleTooManyPasswordResetAttemptsException(TooManyPasswordResetAttemptsException ex) {
        logger.error("Too many password reset attempts: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(ex.getMessage());
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

    // Global Exception Handler for all other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        logger.error("An exception occurred: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
    }
}
