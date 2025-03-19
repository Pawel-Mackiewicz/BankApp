package info.mackiewicz.bankapp.shared.exception.handler;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ApiErrorLogger {

    /**
     * Flag to determine whether to log the stack trace or not.
     * Set to true for development and false for production.
     */
    private static final boolean LOG_STACKTRACE = true;



        public void logError(ErrorCode error, Exception ex, String path) {
        String message = formatErrorMessage(error, ex, path, LOG_STACKTRACE);

        if (error == ErrorCode.INTERNAL_ERROR) {
            log.error(message, ex);
        } else {
            log.warn(message);
        }
    }

        private String formatErrorMessage(ErrorCode error, Exception ex, String path, boolean logStackTrace) {
            
            String message = String.format(
                    "Error occurred: %s, Path: %s, Message: %s\nStackTrace: %s",
                    error.name(),
                    path,
                    ex.getMessage(),
                    logStackTrace ? getStackTrace(ex) : "Stack trace logging is disabled.");
            return message;
        }

        /**
         * Returns the stack trace of the exception as a string.
         *
         * @param ex the exception to get the stack trace from
         * @return the stack trace as a string
         */
        private String getStackTrace(Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String stackTrace = sw.toString();
            return stackTrace;
        }

}
