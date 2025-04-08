package info.mackiewicz.bankapp.system.locking;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Component;

import info.mackiewicz.bankapp.account.exception.AccountLockException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of resource locking strategy using exponential backoff.
 * Class responsible for low-level implementation of locking mechanism,
 * timeout handling and retry attempts.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccountLockingStrategy implements LockingStrategy {

    private final LockingConfig lockingConfig;

    @Getter
    private final AtomicInteger lockCounter = new AtomicInteger(0);

    @Getter
    private final AtomicInteger unlockCounter = new AtomicInteger(0);

    /**
     * Attempts to acquire a lock on the resource identified by the specified account ID.
     * <p>
     * This method repeatedly tries to obtain the lock using a retry mechanism with exponential backoff and jitter.
     * It will attempt to lock the resource up to the maximum allowed attempts as specified in the locking configuration.
     * If the lock cannot be acquired after the permitted number of attempts or if the thread is interrupted during the process,
     * an AccountLockException is thrown.
     * </p>
     *
     * @param accountId the ID of the account whose lock is to be acquired
     * @throws AccountLockException if the lock cannot be acquired after the maximum attempts or if the acquisition is interrupted
     */
    @Override
    public void lock(Integer accountId) {
        log.debug("Attempting to acquire lock for resource ID: {}", accountId);
        ReentrantLock lock = LockingUtils.getOrCreateLock(accountId);
        final long startTime = System.currentTimeMillis();

        int attempts = 0;
        try {
            while (attempts < lockingConfig.maxAttempts()) {
                if (tryAcquireLock(lock, accountId, attempts)) {
                    log.debug("Successfully acquired lock for resource ID: {} after {} attempts",
                            accountId, attempts + 1);
                    lockCounter.incrementAndGet();
                    return;
                }
                attempts++;
                if (attempts < lockingConfig.maxAttempts()) {
                    handleBackoff(attempts, accountId);
                }
            }

            log.error("Failed to acquire lock for resource ID: {} after {} attempts",
                    accountId, lockingConfig.maxAttempts());
            handleMaxAttemptsExceeded(accountId, startTime);
        } catch (InterruptedException e) {
            long totalTime = System.currentTimeMillis() - startTime;
            log.error("Thread interrupted while acquiring lock for resource ID: {} after {} attempts and {}ms",
                    accountId, attempts + 1, totalTime);
            handleInterruptedException(accountId, attempts, startTime, e);
        }
    }

    /**
     * Attempts to acquire the specified lock within the configured timeout.
     *
     * <p>This method calls {@link ReentrantLock#tryLock(long, TimeUnit)} using the timeout from the locking configuration.
     * It returns {@code true} if the lock is successfully acquired, or {@code false} if the timeout expires before acquisition.
     * </p>
     *
     * @param lock the lock to acquire
     * @param resourceId the identifier of the resource associated with the lock
     * @param attempts the current attempt count (provided for contextual information)
     * @return {@code true} if the lock was acquired within the timeout; {@code false} otherwise
     * @throws InterruptedException if the thread is interrupted while waiting for the lock
     */
    private boolean tryAcquireLock(ReentrantLock lock, Integer resourceId, int attempts) throws InterruptedException {
        return lock.tryLock(lockingConfig.timeout(), TimeUnit.MILLISECONDS);
    }

    /**
     * Applies a calculated backoff delay before the next lock acquisition attempt.
     * 
     * <p>This method computes the delay using the current number of attempts and the locking configuration, 
     * then pauses the thread for the computed duration via {@link Thread#sleep(long)}.
     *
     * @param attempts   the number of attempts made so far to acquire the lock
     * @param resourceId the identifier of the resource (account) for which the lock is being attempted
     * @throws InterruptedException if the thread is interrupted while sleeping
     */
    private void handleBackoff(int attempts, Integer resourceId) throws InterruptedException {
        long backoffDelay = LockingUtils.calculateBackoffDelay(attempts, lockingConfig);
        Thread.sleep(backoffDelay);
    }

    /**
     * Handles an InterruptedException that occurs during lock acquisition.
     * <p>
     * Resets the thread's interrupt status, attempts to release the lock held by the current thread for the specified resource,
     * increments the unlock counter if the lock is released, and then throws an AccountLockException with detailed context.
     * </p>
     *
     * @param resourceId the identifier of the resource for which the lock acquisition was attempted
     * @param attempts the number of attempts made before the interruption occurred
     * @param startTime the timestamp marking the start of the lock acquisition attempt
     * @param e the InterruptedException encountered during the lock acquisition process
     * @throws AccountLockException always thrown after handling the interruption to indicate the failure in acquiring the lock
     */
    private void handleInterruptedException(Integer resourceId, int attempts, long startTime, InterruptedException e) {
        Thread.currentThread().interrupt();
        // release the lock if it was acquired before the interruption
        // this is a best effort approach, as the lock may not be held by this thread
        // anymore
        ReentrantLock lock = LockingUtils.getOrCreateLock(resourceId);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
            unlockCounter.incrementAndGet();
        }
        throw new AccountLockException(
                "Thread was interrupted while trying to acquire lock",
                resourceId,
                attempts + 1,
                System.currentTimeMillis() - startTime,
                true);
    }

    /**
     * Throws an AccountLockException after exceeding the maximum number of lock acquisition attempts.
     *
     * <p>This method calculates the elapsed time since the start of the lock attempt and immediately throws
     * an AccountLockException with details about the resource identifier, the maximum allowed attempts, and
     * the total duration spent attempting to acquire the lock.</p>
     *
     * @param resourceId the identifier of the resource for which the lock acquisition failed
     * @param startTime the timestamp in milliseconds marking the start of lock acquisition attempts
     * @throws AccountLockException always thrown to indicate that the maximum number of locking attempts has been exceeded
     */
    private void handleMaxAttemptsExceeded(Integer resourceId, long startTime) {
        throw new AccountLockException(
                "Failed to acquire lock after maximum attempts",
                resourceId,
                lockingConfig.maxAttempts(),
                System.currentTimeMillis() - startTime,
                false);
    }

    /**
     * Releases the lock associated with the specified resource.
     * <p>
     * This method retrieves (or creates) the lock for the given resource ID and attempts to release it,
     * logging both the attempt and its success. If the lock is not held by the current thread, an
     * IllegalStateException is thrown.
     *
     * @param resourceId the identifier of the resource whose lock should be released
     * @throws IllegalStateException if the current thread does not hold the lock for the specified resource
     */
    @Override
    public void unlock(Integer resourceId) {
        log.debug("Releasing lock for resource ID: {}", resourceId);
        ReentrantLock lock = LockingUtils.getOrCreateLock(resourceId);
        tryUnlock(resourceId, lock);
        log.debug("Successfully released lock for resource ID: {}", resourceId);
    }

    /**
     * Attempts to unlock the specified resource by releasing its associated lock.
     * If the lock is successfully released, the method increments the counter tracking
     * successful unlock operations. If releasing the lock fails due to the current thread
     * not holding it, the resulting IllegalMonitorStateException is caught and handled
     * by invoking {@code handleUnlockException}.
     *
     * @param resourceId the identifier of the resource to unlock
     * @param lock the ReentrantLock instance to be released
     */
    private void tryUnlock(Integer resourceId, ReentrantLock lock) {
        try {
            lock.unlock();
            unlockCounter.incrementAndGet();
        } catch (IllegalMonitorStateException e) {
            handleUnlockException(resourceId, e);
        }
    }

    /**
     * Handles a failed unlock attempt by throwing an IllegalStateException.
     *
     * <p>This method is called when unlocking a resource fails because the lock is not held.
     * It formats an error message using the provided resource ID and rethrows the original
     * IllegalMonitorStateException as an IllegalStateException.
     *
     * @param resourceId the identifier of the resource for which unlocking failed
     * @param e the original IllegalMonitorStateException encountered during unlocking
     * @throws IllegalStateException always thrown to indicate the failed unlock attempt
     */
    private void handleUnlockException(Integer resourceId, IllegalMonitorStateException e) {
        String message = String.format("Cannot release lock that is not held for resource ID: %d", resourceId);
        throw new IllegalStateException(message, e);
    }
}