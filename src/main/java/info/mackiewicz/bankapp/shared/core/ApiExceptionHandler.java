package info.mackiewicz.bankapp.shared.core;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;
import info.mackiewicz.bankapp.shared.infrastructure.logging.ApiErrorLogger;
import info.mackiewicz.bankapp.shared.web.dto.BaseApiError;
import info.mackiewicz.bankapp.shared.web.dto.ValidationApiError;
import info.mackiewicz.bankapp.shared.web.dto.ValidationError;
import info.mackiewicz.bankapp.shared.web.error.mapping.ApiExceptionToErrorMapper;
import info.mackiewicz.bankapp.shared.web.error.validation.ValidationErrorProcessor;
import info.mackiewicz.bankapp.shared.web.util.RequestUriHandler;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestControllerAdvice
public class ApiExceptionHandler {

    private final RequestUriHandler uriHandler;
    private final ApiErrorLogger logger;
    private final ApiExceptionToErrorMapper exceptionMapper;
    private final ValidationErrorProcessor validationErrorProcessor;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseApiError> handleException(Exception ex, WebRequest request) {
        String path = uriHandler.getRequestURI(request);

        ErrorCode errorCode = exceptionMapper.map(ex);
        BaseApiError error = new BaseApiError(errorCode, path);
        logger.logError(errorCode, ex, path);

        return new ResponseEntity<>(error, error.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
            WebRequest request) {
        List<ValidationError> errors = validationErrorProcessor.extractValidationErrors(ex);
        return createValidationErrorResponse(errors, ex, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationApiError> handleConstraintViolationException(ConstraintViolationException ex,
            WebRequest request) {
        List<ValidationError> errors = validationErrorProcessor.extractValidationErrors(ex);
        return createValidationErrorResponse(errors, ex, request);
    }

    private ResponseEntity<ValidationApiError> createValidationErrorResponse(List<ValidationError> errors, Exception ex,
            WebRequest request) {
        String path = uriHandler.getRequestURI(request);
        ValidationApiError apiError = new ValidationApiError(path, errors);
        logger.logError(ErrorCode.VALIDATION_ERROR, ex, path);

        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

}
