package info.mackiewicz.bankapp.system.notification.email.locking;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class LockingUtils {
    private static final Map<Integer, ReentrantLock> locks = new ConcurrentHashMap<>();
    private static final Random random = new Random();

    public static ReentrantLock getOrCreateLock(Integer resourceId) {
        return locks.computeIfAbsent(resourceId, k -> new ReentrantLock());
    }

    public static long calculateBackoffDelay(int attempt, long maxDelay, long baseDelay) {
        long delay = Math.min(
                maxDelay,
                baseDelay * (long) Math.pow(2, attempt)
        );
        long jitter = (long) (delay * 0.25);
        return delay + random.nextLong(-jitter, jitter);
    }
}
