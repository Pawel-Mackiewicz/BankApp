package info.mackiewicz.bankapp.shared.core.error;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * Defines all possible error codes in the BankApp application along with their corresponding
 * HTTP status codes and user-friendly messages.
 *
 * <p>This enum categorizes errors into different domains (Common, Security, User, Account, Transaction)
 * and provides a standardized way to handle error responses across the application. Each error code
 * is associated with an appropriate HTTP status code and a descriptive message.</p>
 *
 * <p>The error codes are used in conjunction with exception handling to provide consistent
 * error responses throughout the API.</p>
 *
 * @see HttpStatus
 * @see info.mackiewicz.bankapp.shared.core.BankAppBaseException
 * @see info.mackiewicz.bankapp.shared.core.ApiExceptionHandler
 */
@Getter
public enum ErrorCode {
    // Common Errors
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Validation failed. Please check your input and try again."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Requested resource not found."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred. Please try again later."),
    
    // Security Errors
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "Reset link could not be found. Please request a new reset link."),
    TOKEN_EXPIRED(HttpStatus.GONE, "This password reset link has expired. Please request a new one."),
    TOKEN_INVALID(HttpStatus.BAD_REQUEST, "Invalid password reset link"),
    TOKEN_USED(HttpStatus.GONE, "This password reset link has already been used. Please request a new one."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "Your login credentials are invalid. Please try again."),
    TOO_MANY_PASSWORD_RESET_ATTEMPTS(HttpStatus.TOO_MANY_REQUESTS, "You've reached the limit of password reset attempts. Please try again later."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "The provided password is invalid. Please check your input and try again."),
    PASSWORD_SAME(HttpStatus.BAD_REQUEST, "The new password cannot be the same as the current password."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "The provided passwords do not match. Please check your input and try again."),
    PASSWORD_TOO_WEAK(HttpStatus.BAD_REQUEST, "The provided password is too weak. Please choose a stronger password."),
    AUTHENTICATION_ERROR(HttpStatus.UNAUTHORIZED, "Authentication failed. Please check your credentials and try again."),

    // User Errors
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "We couldn't find user with the provided information."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "User with these credentials already exists."),
    USERNAME_TAKEN(HttpStatus.CONFLICT, "Username is already taken. Please choose a different one."),
    EMAIL_TAKEN(HttpStatus.CONFLICT, "Email is already taken. Please choose a different one."),
    USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND, "Username not found."),
    USERNAME_LENGTH(HttpStatus.BAD_REQUEST, "Username must be between 3 and 20 characters long."),
    USERNAME_SAME(HttpStatus.BAD_REQUEST, "New username cannot be the same as the current one."),
    USERNAME_INVALID(HttpStatus.BAD_REQUEST, "Username contains invalid characters. Only alphanumeric characters are allowed."),
    USERNAME_FORBIDDEN(HttpStatus.BAD_REQUEST, "Username is forbidden. Please choose a different one."),
    
    // Account Errors
    ACCOUNT_VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "We can't validate your account. Please check your input and try again."),
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "Account not found."),
    PAYMENT_FAILED(HttpStatus.FORBIDDEN, "Payment processing failed. Please try again."),
    INSUFFICIENT_FUNDS(HttpStatus.FORBIDDEN, "Insufficient funds for this transaction. Please check your balance and try again."), 
    ACCOUNT_OWNER_EXPIRED(HttpStatus.FORBIDDEN, "Account owner is expired."),
    ACCOUNT_OWNER_LOCKED(HttpStatus.FORBIDDEN, "Account owner is locked."),
    ACCOUNT_OWNER_NULL(HttpStatus.BAD_REQUEST, "Account owner is null."),
    ACCOUNT_OWNER_NOT_FOUND(HttpStatus.NOT_FOUND, "Account owner not found."), 
    ACCOUNT_LIMIT_EXCEEDED(HttpStatus.FORBIDDEN, "Account limit exceeded. If you need more accounts, please contact support."),
    ACCOUNT_OWNERSHIP_ERROR(HttpStatus.FORBIDDEN, "You do not have permission to access this account."),
    
    // Transaction Errors
    TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Transaction not found."),
    TRANSACTION_VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "Transaction validation failed. Please check your transaction details."),
    TRANSACTION_EXECUTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to process transaction. Please try again later."),
    TRANSACTION_ALREADY_PROCESSED(HttpStatus.CONFLICT, "This transaction has already been processed."),
    TRANSACTION_CANNOT_BE_PROCESSED(HttpStatus.BAD_REQUEST, "Transaction cannot be processed in its current state."),
    TRANSACTION_SOURCE_ACCOUNT_MISSING(HttpStatus.BAD_REQUEST, "Source account is required for this transaction."),
    TRANSACTION_DESTINATION_ACCOUNT_MISSING(HttpStatus.BAD_REQUEST, "Destination account is required for this transaction."),
    TRANSACTION_AMOUNT_MISSING(HttpStatus.BAD_REQUEST, "Transaction amount is required."),
    TRANSACTION_TYPE_MISSING(HttpStatus.BAD_REQUEST, "Transaction type is required."),
    INVALID_TRANSACTION_TYPE(HttpStatus.BAD_REQUEST, "Invalid transaction type."),
    INVALID_TRANSACTION_OPERATION(HttpStatus.BAD_REQUEST, "Invalid operation for this transaction."),
    NO_TRANSACTIONS_FOR_ACCOUNT(HttpStatus.NOT_FOUND, "No transactions found for this account."),

    // Other errors
    UNSUPPORTED_EXPORTER(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported export format. Please choose a different one.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
