package info.mackiewicz.bankapp.system.error.handling.core;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import info.mackiewicz.bankapp.system.error.handling.core.error.ErrorCode;
import info.mackiewicz.bankapp.system.error.handling.logger.ApiErrorLogger;
import info.mackiewicz.bankapp.system.error.handling.dto.BaseApiError;
import info.mackiewicz.bankapp.system.error.handling.dto.ValidationApiError;
import info.mackiewicz.bankapp.system.error.handling.dto.ValidationError;
import info.mackiewicz.bankapp.system.error.handling.mapping.ApiExceptionToErrorMapper;
import info.mackiewicz.bankapp.system.error.handling.service.ValidationErrorProcessor;
import info.mackiewicz.bankapp.system.error.handling.util.RequestUriHandler;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;

/**
 * Global exception handler for the BankApp application that processes and transforms
 * exceptions into standardized API responses.
 *
 * <p>This handler manages both general exceptions and validation-specific exceptions,
 * providing consistent error responses across the application. It works in conjunction
 * with the logging system to ensure all errors are properly logged.</p>
 *
 * <p>Thread-safe: This class is thread-safe as it's stateless and dependencies are immutable.</p>
 *
 * @see BaseApiError
 * @see ValidationApiError
 * @see ApiErrorLogger
 * @see ApiExceptionToErrorMapper
 */
@RequiredArgsConstructor
@RestControllerAdvice
public class ApiExceptionHandler {

    private final RequestUriHandler uriHandler;
    private final ApiErrorLogger logger;
    private final ApiExceptionToErrorMapper exceptionMapper;
    private final ValidationErrorProcessor validationErrorProcessor;

    /**
     * Handles all uncaught exceptions in the application and converts them to a standardized error response.
     *
     * <p>This method serves as a catch-all handler for exceptions that don't have specific handlers.
     * It maps the exception to an appropriate error code and creates a standardized error response.</p>
     *
     * @param ex the exception to handle
     * @param request the current web request
     * @return ResponseEntity containing the error details and appropriate HTTP status
     * @see BaseApiError
     * @see ApiExceptionToErrorMapper
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseApiError> handleException(Exception ex, WebRequest request) {
        String path = uriHandler.getRequestURI(request);

        ErrorCode errorCode = exceptionMapper.map(ex);
        BaseApiError error = new BaseApiError(errorCode, path);
        logger.logError(errorCode, ex, path);

        return new ResponseEntity<>(error, error.getStatus());
    }

    /**
     * Handles validation exceptions thrown by Spring's validation framework.
     *
     * <p>This method processes validation failures that occur during request body
     * or method parameter validation. It extracts detailed validation errors and
     * converts them into a structured format.</p>
     *
     * @param ex the validation exception containing binding and validation errors
     * @param request the current web request
     * @return ResponseEntity containing validation error details and HTTP status 400
     * @see ValidationApiError
     * @see ValidationErrorProcessor
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
            WebRequest request) {
        List<ValidationError> errors = validationErrorProcessor.extractValidationErrors(ex);
        return createValidationErrorResponse(errors, ex, request);
    }

    /**
     * Handles validation exceptions thrown by Jakarta Bean Validation.
     *
     * <p>This method processes constraint violations that occur during request
     * parameter validation or manual validation. It extracts the validation
     * errors and converts them into a standardized format.</p>
     *
     * @param ex the constraint violation exception
     * @param request the current web request
     * @return ResponseEntity containing validation error details and HTTP status 400
     * @see ValidationApiError
     * @see ValidationErrorProcessor
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationApiError> handleConstraintViolationException(ConstraintViolationException ex,
            WebRequest request) {
        List<ValidationError> errors = validationErrorProcessor.extractValidationErrors(ex);
        return createValidationErrorResponse(errors, ex, request);
    }

    /**
     * Creates a standardized validation error response.
     *
     * <p>This helper method consolidates the common logic for creating validation
     * error responses across different validation exception types. It logs the error
     * and creates a response with consistent structure.</p>
     *
     * @param errors list of validation errors to include in the response
     * @param ex the original exception that triggered the validation error
     * @param request the current web request
     * @return ResponseEntity containing the validation errors and HTTP status 400
     * @see ValidationApiError
     * @see ValidationError
     */
    private ResponseEntity<ValidationApiError> createValidationErrorResponse(List<ValidationError> errors, Exception ex,
            WebRequest request) {
        String path = uriHandler.getRequestURI(request);
        ValidationApiError apiError = new ValidationApiError(path, errors);
        logger.logError(ErrorCode.VALIDATION_ERROR, ex, path);

        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

}
