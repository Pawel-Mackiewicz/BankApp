package info.mackiewicz.bankapp.shared.exception.handler;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ApiErrorLogger {

        public void logError(ErrorCode error, Exception ex, String path) {
        String message = formatErrorMessage(error, ex, path);

        if (error == ErrorCode.INTERNAL_ERROR) {
            log.error(message, ex);
        } else {
            log.warn(message);
        }
    }

        private String formatErrorMessage(ErrorCode error, Exception ex, String path) {
            String message = String.format(
                    "Error occurred: %s, Path: %s, Message: %s",
                    error.name(),
                    path,
                    ex.getMessage());
            return message;
        }

}
