package info.mackiewicz.bankapp.shared.util;

import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for handling retry operations.
 * <p>
 * This class provides functionality to retry operations that might fail temporarily,
 * with configurable retry attempts and delay between retries.
 * </p>
 */
@Slf4j
public class RetryUtil {

    /**
     * Executes the provided operation with retry logic.
     * <p>
     * If the operation fails, it will be retried up to the specified number of times
     * with an increasing delay between retries.
     * </p>
     *
     * @param <T>           The return type of the operation
     * @param operation     The operation to execute
     * @param maxRetries    Maximum number of retry attempts
     * @param retryDelayMs  Base delay in milliseconds between retries
     * @param operationName Name of the operation for logging purposes
     * @param context       Additional context information for logging
     * @return The result of the successful operation
     * @throws RuntimeException if all retry attempts fail
     */
    public static <T> T executeWithRetry(
            Supplier<T> operation,
            int maxRetries,
            long retryDelayMs,
            String operationName,
            String context) {
        
        if (maxRetries < 1) {
            throw new IllegalArgumentException("maxRetries must be at least 1");
        }
        
        if (retryDelayMs < 0) {
            throw new IllegalArgumentException("retryDelayMs must be non-negative");
        }
        
        int attempts = 0;
        Exception lastException = null;
        
        while (attempts < maxRetries) {
            try {
                T result = operation.get();
                if (attempts > 0) {
                    log.info("Operation {} succeeded on attempt {}. Context: {}",
                            operationName, attempts + 1, context);
                }
                return result;
            } catch (Exception e) {
                lastException = e;
                attempts++;
                
                if (attempts == maxRetries) {
                    log.error("Failed to {} after {} retries. Context: {}",
                            operationName, maxRetries, context, e);
                    throw new RuntimeException("Operation failed after " + maxRetries + " attempts: " + e.getMessage(), e);
                }
                
                log.warn("Attempt {} failed for {}. Context: {}. Retrying after delay.",
                        attempts, operationName, context, e);
                
                try {
                    // Use exponential backoff with a small random factor
                    long delay = retryDelayMs * attempts + (long)(Math.random() * retryDelayMs * 0.1);
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while waiting to retry " + operationName, ie);
                }
            }
        }
        
        // This should never be reached due to the throw in the catch block
        // But we add this as a safety net
        if (lastException != null) {
            throw new RuntimeException("Unexpected error in retry loop for " + operationName, lastException);
        } else {
            throw new RuntimeException("Unexpected error in retry loop for " + operationName);
        }
    }
}