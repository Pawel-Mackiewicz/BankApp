package info.mackiewicz.bankapp.account.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import info.mackiewicz.bankapp.account.exception.AccountNotFoundByIdException;
import info.mackiewicz.bankapp.account.exception.AccountNotFoundByIbanException;
import info.mackiewicz.bankapp.account.exception.OwnerAccountsNotFoundException;
import info.mackiewicz.bankapp.shared.dto.ApiError;

@RestControllerAdvice(basePackages = "info.mackiewicz.bankapp.account.controller")
public class AccountExceptionHandler {

    @ExceptionHandler(AccountNotFoundByIdException.class)
    public ResponseEntity<ApiError> handleAccountNotFoundById(AccountNotFoundByIdException ex) {
        ApiError apiError = new ApiError(
            HttpStatus.NOT_FOUND,
            "Account Not Found",
            ex.getMessage()
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AccountNotFoundByIbanException.class)
    public ResponseEntity<ApiError> handleAccountNotFoundByIban(AccountNotFoundByIbanException ex) {
        ApiError apiError = new ApiError(
            HttpStatus.NOT_FOUND,
            "Account Not Found",
            ex.getMessage()
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(OwnerAccountsNotFoundException.class)
    public ResponseEntity<ApiError> handleOwnerAccountsNotFound(OwnerAccountsNotFoundException ex) {
        ApiError apiError = new ApiError(
            HttpStatus.NOT_FOUND,
            "Accounts Not Found",
            ex.getMessage()
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }
}