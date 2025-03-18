package info.mackiewicz.bankapp.security.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import info.mackiewicz.bankapp.shared.dto.BaseApiError;
import info.mackiewicz.bankapp.shared.exception.handlers.ErrorCode;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestControllerAdvice(basePackages = "info.mackiewicz.bankapp.security.controller")
public class PasswordResetExceptionHandler {

    RequestUriHandler uriHandler;
    ApiErrorLogger logger;
    PasswordResetExceptionToErrorMapper exceptionMapper;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseApiError> handleException(Exception ex, WebRequest request) {
        String path = uriHandler.getRequestURI(request);

        ErrorCode errorCode = exceptionMapper.map(ex);
        BaseApiError error = new BaseApiError(errorCode, path);
        logger.logError(errorCode, ex, path);

        return new ResponseEntity<>(error, error.getStatus());
    }
}
