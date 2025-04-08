package info.mackiewicz.bankapp.system.locking;

import java.util.concurrent.atomic.AtomicInteger;

import info.mackiewicz.bankapp.account.exception.AccountLockException;

public interface LockingStrategy {

    /**
 * Returns the counter tracking the number of lock attempts.
 *
 * @return an AtomicInteger representing the current count of lock attempts
 */
AtomicInteger getLockCounter();

    /**
 * Returns the counter that tracks the number of unlock attempts.
 *
 * @return an {@code AtomicInteger} representing the number of times a resource has been unlocked.
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
 * Unlocks the resource identified by the given resource ID.
 *
 * @param resourceId the unique identifier of the resource to be unlocked
 */
    void unlock(Integer resourceId);

}