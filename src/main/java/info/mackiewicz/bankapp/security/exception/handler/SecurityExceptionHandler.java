package info.mackiewicz.bankapp.security.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import info.mackiewicz.bankapp.security.exception.*;
import info.mackiewicz.bankapp.shared.dto.ApiError;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice(basePackages = "info.mackiewicz.bankapp.security.controller")
public class SecurityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception ex) {
        log.error("An error occurred during password reset:", ex);  
        ApiError error = new ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal Server Error",
            ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
