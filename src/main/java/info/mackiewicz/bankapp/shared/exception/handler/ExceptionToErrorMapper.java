package info.mackiewicz.bankapp.shared.exception.handler;

public interface ExceptionToErrorMapper {

    ErrorCode map(Exception ex);

}
