package info.mackiewicz.bankapp.system.locking;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LockingUtils {
    private static final Map<Integer, ReentrantLock> locks = new ConcurrentHashMap<>();
    private static final Random random = new Random();

    /**
     * Retrieves the lock associated with the given resource ID, creating a new one if it does not already exist.
     * This ensures that each resource is managed with its own unique lock in a concurrent environment.
     *
     * @param resourceId the unique identifier for the resource
     * @return the ReentrantLock corresponding to the specified resource ID
     */
    public static ReentrantLock getOrCreateLock(Integer resourceId) {
        return locks.computeIfAbsent(resourceId, k -> new ReentrantLock());
    }

    /**
     * Calculates an exponential backoff delay for a retry attempt with added jitter.
     *
     * <p>The delay is determined by taking the minimum between the maximum delay specified in the
     * locking configuration and the product of the base delay with 2 raised to the current attempt
     * number. A random jitter, which is 25% of the computed delay (either positive or negative), is then
     * applied to the final delay to mitigate potential contention.</p>
     *
     * @param attempt the number of retry attempts made so far
     * @param lockingConfig the configuration providing base and maximum delay thresholds
     * @return the calculated backoff delay with jitter
     */
    public static long calculateBackoffDelay(int attempt, LockingConfig lockingConfig) {
        long delay = Math.min(
            lockingConfig.maxDelay(),
            lockingConfig.baseDelay() * (long) Math.pow(2, attempt)
        );
        long jitter = (long) (delay * 0.25);
        return delay + random.nextLong(-jitter, jitter);
    }
}
