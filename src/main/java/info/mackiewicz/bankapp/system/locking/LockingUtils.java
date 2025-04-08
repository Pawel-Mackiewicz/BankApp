package info.mackiewicz.bankapp.system.locking;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LockingUtils {
    private static final Map<Integer, ReentrantLock> locks = new ConcurrentHashMap<>();
    private static final Random random = new Random();

    /**
     * Retrieves the lock associated with the specified resource ID, creating a new one if it does not exist.
     *
     * <p>This method checks if a ReentrantLock is already mapped to the given resource ID and returns it. If no lock exists,
     * it creates a new ReentrantLock, stores it in a thread-safe manner, and returns it.
     *
     * @param resourceId the identifier of the resource for which the lock is required
     * @return the existing or newly created ReentrantLock for the specified resource
     */
    public static ReentrantLock getOrCreateLock(Integer resourceId) {
        return locks.computeIfAbsent(resourceId, k -> new ReentrantLock());
    }

    /**
     * Calculates an exponential backoff delay with randomized jitter.
     *
     * <p>This method computes a delay based on the current retry attempt and the specified 
     * locking configuration. The delay is determined by multiplying the base delay by 2 raised 
     * to the power of the attempt, capped at the maximum delay defined in the configuration. 
     * A random jitter (±25% of the computed delay) is then added to help reduce lock contention 
     * in concurrent environments.
     *
     * @param attempt the current retry attempt count
     * @param lockingConfig the configuration providing base and maximum delay values
     * @return the calculated backoff delay with jitter applied
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
