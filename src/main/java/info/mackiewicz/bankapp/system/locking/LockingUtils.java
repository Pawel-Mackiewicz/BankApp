package info.mackiewicz.bankapp.system.locking;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LockingUtils {
    private static final Map<Integer, ReentrantLock> locks = new ConcurrentHashMap<>();
    private static final Random random = new Random();

    /**
     * Returns the lock associated with the specified resource ID, creating a new lock if one does not already exist.
     *
     * <p>This method uses a concurrent map to ensure that each resource ID is paired with a unique
     * {@link java.util.concurrent.locks.ReentrantLock}, guaranteeing thread-safe lock management.</p>
     *
     * @param resourceId the unique identifier of the resource for which the lock is required
     * @return the existing or newly-created lock associated with the given resource ID
     */
    public static ReentrantLock getOrCreateLock(Integer resourceId) {
        return locks.computeIfAbsent(resourceId, k -> new ReentrantLock());
    }

    /**
     * Calculates a backoff delay using exponential growth and random jitter.
     *
     * <p>The delay is determined by taking the minimum between the maximum delay specified in the locking configuration
     * and the exponential backoff delay (base delay multiplied by 2 raised to the number of attempts). A random jitter,
     * equal to 25% of the computed delay, is then added (or subtracted) to reduce lock contention in concurrent environments.</p>
     *
     * @param attempt the current retry attempt count, where higher values exponentially increase the delay
     * @param lockingConfig configuration providing the base and maximum delay values for backoff calculation
     * @return the computed backoff delay with applied jitter
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
