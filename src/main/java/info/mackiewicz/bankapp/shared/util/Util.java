package info.mackiewicz.bankapp.shared.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * General utility class providing common functionality used across the application.
 * This class contains static utility methods for various operations.
 *
 * @see java.lang.Thread#sleep(long)
 */
@Slf4j
@UtilityClass
public class Util {

    /**
     * Pauses the current thread execution for the specified duration.
     * This method handles InterruptedException internally by logging the interruption.
     * Note that this method does not preserve the thread's interrupted status.
     * 
     * @param milliseconds the length of time to sleep in milliseconds
     * @throws IllegalArgumentException if milliseconds is negative
     * @see java.lang.Thread#sleep(long)
     */
    public static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            log.error("Thread interrupted during sleep: {}", e.getMessage());
        }
    }

    /**
     * Calculates the time in milliseconds from the start of the current month to the current moment.
     * This method determines the beginning of the current month at midnight and computes the
     * duration, in milliseconds, from that point to the current time.
     * It's used for generating requestId's
     *
     * @return the elapsed time in milliseconds from the start of the current month to now
     */
    public static long getTimeFromStartOfTheMonth() {
        return ChronoUnit.MILLIS.between(
                LocalDateTime.now().withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS),
                LocalDateTime.now()
        );
    }
}