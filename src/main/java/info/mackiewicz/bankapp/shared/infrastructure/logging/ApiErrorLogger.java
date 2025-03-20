package info.mackiewicz.bankapp.shared.infrastructure.logging;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.shared.core.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;

/**
 * Component responsible for standardized error logging across the application.
 *
 * <p>This logger provides consistent error logging functionality with configurable
 * stack trace inclusion. It differentiates between internal errors (logged as ERROR)
 * and other types of errors (logged as WARN).</p>
 *
 * <p>The logging format includes:
 * <ul>
 *   <li>Error code and name</li>
 *   <li>Request path where the error occurred</li>
 *   <li>Error message</li>
 *   <li>Stack trace (if enabled)</li>
 * </ul></p>
 *
 * <p>Thread-safe: This class is thread-safe as it uses SLF4J's thread-safe logging methods.</p>
 *
 * @see ErrorCode
 * @see info.mackiewicz.bankapp.shared.core.ApiExceptionHandler
 */
@Slf4j
@Component
public class ApiErrorLogger {

    /**
     * Flag to determine whether to log the stack trace or not.
     * Set to true for development and false for production.
     */
    private static final boolean LOG_STACKTRACE = false;



    /**
     * Logs an error with appropriate severity level based on the error code.
     *
     * <p>Internal errors (ErrorCode.INTERNAL_ERROR) are logged at ERROR level,
     * while all other errors are logged at WARN level. The log entry includes
     * the error details and optionally the stack trace based on configuration.</p>
     *
     * @param error the error code categorizing the error
     * @param ex the exception that occurred
     * @param path the request path where the error occurred
     * @see ErrorCode#INTERNAL_ERROR
     */
    public void logError(ErrorCode error, Exception ex, String path) {
        String message = formatErrorMessage(error, ex, path, LOG_STACKTRACE);

        if (error == ErrorCode.INTERNAL_ERROR) {
            log.error(message, ex);
        } else {
            log.warn(message);
        }
    }

    /**
     * Formats the error message with a consistent structure.
     *
     * <p>Creates a standardized error message string containing:
     * <ul>
     *   <li>Error code name</li>
     *   <li>Request path</li>
     *   <li>Exception message</li>
     *   <li>Stack trace (if enabled)</li>
     * </ul></p>
     *
     * @param error the error code to include in the message
     * @param ex the exception containing the error details
     * @param path the request path where the error occurred
     * @param logStackTrace whether to include the stack trace in the message
     * @return formatted error message string
     */
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
     * Converts an exception's stack trace into a string representation.
     *
     * <p>Uses StringWriter and PrintWriter to capture the stack trace output.
     * This method ensures proper resource cleanup by using local variables
     * that will be garbage collected.</p>
     *
     * @param ex the exception to get the stack trace from
     * @return complete stack trace as a formatted string
     * @see StringWriter
     * @see PrintWriter#printStackTrace()
     */
        private String getStackTrace(Exception ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            String stackTrace = sw.toString();
            return stackTrace;
        }

}
