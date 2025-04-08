package info.mackiewicz.bankapp.system.locking;

import java.util.concurrent.atomic.AtomicInteger;

import info.mackiewicz.bankapp.account.exception.AccountLockException;

public interface LockingStrategy {

    /**
 * Returns the counter tracking the number of lock attempts.
 *
 * <p>The returned counter is incremented with every attempt to acquire a lock and can be used for
 * monitoring and debugging locking behavior.</p>
 *
 * @return an AtomicInteger representing the cumulative count of lock attempts
 */
AtomicInteger getLockCounter();

    /**
 * Retrieves the counter that tracks the number of unlock attempts.
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