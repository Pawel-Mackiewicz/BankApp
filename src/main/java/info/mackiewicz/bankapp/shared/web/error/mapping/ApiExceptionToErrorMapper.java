package info.mackiewicz.bankapp.shared.web.error.mapping;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.shared.core.BankAppBaseException;
import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

@Component
public class ApiExceptionToErrorMapper implements ExceptionToErrorMapper {    

    @Override
    public ErrorCode map(Exception ex) {    
        return switch (ex) {
            case BankAppBaseException e -> e.getErrorCode();

            default -> ErrorCode.INTERNAL_ERROR;
        };
    }
}
