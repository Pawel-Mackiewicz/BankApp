package info.mackiewicz.bankapp.system.locking;

import java.util.concurrent.atomic.AtomicInteger;

import info.mackiewicz.bankapp.account.exception.AccountLockException;

public interface LockingStrategy {

    AtomicInteger getLockCounter();

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