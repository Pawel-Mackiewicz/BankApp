package info.mackiewicz.bankapp.shared.exception.handler;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.shared.exception.BankAppBaseException;

@Component
public class ApiExceptionToErrorMapper implements ExceptionToErrorMapper {    

    @Override
    public ErrorCode map(Exception ex) {    
        return switch (ex) {
            case BankAppBaseException e -> ((BankAppBaseException) ex).getErrorCode();
            
            default -> ErrorCode.INTERNAL_ERROR;
        };
    }
}
