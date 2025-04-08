package info.mackiewicz.bankapp.system.locking;

import java.util.concurrent.atomic.AtomicInteger;

import info.mackiewicz.bankapp.account.exception.AccountLockException;

public interface LockingStrategy {

    /**
 * Retrieves the counter that tracks the number of lock attempts.
 *
 * <p>This atomic counter is used to monitor how many times a lock has been attempted on a resource,
 * providing useful metrics for concurrent operations.</p>
 *
 * @return the AtomicInteger representing the number of lock attempts
 */
AtomicInteger getLockCounter();

    /**
 * Returns the counter tracking the number of unlock operations performed.
 *
 * @return an AtomicInteger representing the unlock attempt counter.
 */
AtomicInteger getUnlockCounter();

    /**
     * Attempts to lock the resource with the given ID.
     * Uses exponential backoff with jitter in case of failure.
     *
     * @param resourceId ID of the resource to lock
     * @throws AccountLockException if unable to acquire the lock
     */

    void lock(Integer resourceId);

    /**
     * Releases the resource lock.
     *
     * @param resourceId ID of the resource to unlock
     */
    void unlock(Integer resourceId);

}