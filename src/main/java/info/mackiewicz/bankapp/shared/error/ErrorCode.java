package info.mackiewicz.bankapp.shared.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // Common validation
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "common"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "common"),
    TOO_MANY_ATTEMPTS(HttpStatus.TOO_MANY_REQUESTS, "common"),
    
    // Security domain
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "security"),
    TOKEN_USED(HttpStatus.BAD_REQUEST, "security"),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "security"),
    
    // Transaction domain
    INSUFFICIENT_FUNDS(HttpStatus.UNPROCESSABLE_ENTITY, "transaction"),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "transaction");

    private final HttpStatus status;
    private final String domain;
    
    ErrorCode(HttpStatus status, String domain) {
        this.status = status;
        this.domain = domain;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getDomain() {
        return domain;
    }
}