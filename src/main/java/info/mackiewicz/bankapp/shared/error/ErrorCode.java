package info.mackiewicz.bankapp.shared.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // Common validation
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, ErrorDomain.COMMON),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorDomain.COMMON),
    TOO_MANY_ATTEMPTS(HttpStatus.TOO_MANY_REQUESTS, ErrorDomain.COMMON),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, ErrorDomain.COMMON),
    
    // Security domain
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, ErrorDomain.SECURITY),
    TOKEN_USED(HttpStatus.BAD_REQUEST, ErrorDomain.SECURITY),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, ErrorDomain.SECURITY),
    
    // Transaction domain
    INSUFFICIENT_FUNDS(HttpStatus.UNPROCESSABLE_ENTITY, ErrorDomain.TRANSACTION),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, ErrorDomain.TRANSACTION);

    private final HttpStatus status;
    private final ErrorDomain domain;
    
    ErrorCode(HttpStatus status, ErrorDomain domain) {
        this.status = status;
        this.domain = domain;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public ErrorDomain getDomain() {
        return domain;
    }

    /**
     * Get the string representation of the domain.
     * @return domain value as string
     */
    public String getDomainValue() {
        return domain.getValue();
    }
}