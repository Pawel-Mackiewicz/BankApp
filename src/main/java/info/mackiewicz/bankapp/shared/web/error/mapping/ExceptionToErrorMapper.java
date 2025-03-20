package info.mackiewicz.bankapp.shared.web.error.mapping;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;

public interface ExceptionToErrorMapper {

    ErrorCode map(Exception ex);

}
