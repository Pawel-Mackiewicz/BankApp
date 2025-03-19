package info.mackiewicz.bankapp.shared.exception.handler;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import info.mackiewicz.bankapp.shared.dto.BaseApiError;
import info.mackiewicz.bankapp.shared.dto.ValidationApiError;
import info.mackiewicz.bankapp.shared.dto.ValidationError;
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
