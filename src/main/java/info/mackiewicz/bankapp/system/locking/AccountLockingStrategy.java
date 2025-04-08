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
     * Attempts to acquire a lock for the specified account using a retry mechanism with exponential backoff and jitter.
     *
     * <p>This method continuously retries to obtain the lock up to the maximum attempts defined in the locking configuration.
     * If the lock cannot be acquired within these attempts, or if the thread is interrupted during the process,
     * any held lock is released and an AccountLockException is thrown.</p>
     *
     * @param accountId the identifier of the account to lock
     * @throws AccountLockException if the lock cannot be acquired after the maximum number of attempts or if the thread is interrupted
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
     * Attempts to acquire the given lock within the configured timeout period.
     *
     * <p>
     * This method invokes the lock's tryLock method with a timeout (in milliseconds) defined
     * by the locking configuration. It returns {@code true} if the lock is successfully acquired;
     * otherwise, it returns {@code false}.
     * </p>
     *
     * @param lock the lock to be acquired
     * @param resourceId the identifier of the resource (provided for context)
     * @param attempts the current attempt count (provided for context)
     * @return {@code true} if the lock was acquired within the timeout period, {@code false} otherwise
     * @throws InterruptedException if the current thread is interrupted while waiting to acquire the lock
     */
    private boolean tryAcquireLock(ReentrantLock lock, Integer resourceId, int attempts) throws InterruptedException {
        return lock.tryLock(lockingConfig.timeout(), TimeUnit.MILLISECONDS);
    }

    /**
     * Pauses execution for a calculated backoff delay.
     *
     * <p>This method computes a delay using the number of attempts and the locking configuration,
     * then suspends the current thread for that duration. This provides an exponential backoff with
     * jitter for retrying lock acquisition.
     *
     * @param attempts the current number of lock acquisition attempts
     * @param resourceId the identifier of the resource associated with the lock (not used in this method)
     * @throws InterruptedException if the thread is interrupted while sleeping
     */
    private void handleBackoff(int attempts, Integer resourceId) throws InterruptedException {
        long backoffDelay = LockingUtils.calculateBackoffDelay(attempts, lockingConfig);
        Thread.sleep(backoffDelay);
    }

    /**
     * Handles an interruption during lock acquisition.
     * 
     * <p>
     * This method resets the thread's interrupt status, performs a best-effort release of the lock if it is held by the current thread, 
     * updates the unlock counter, and then throws an {@code AccountLockException} to indicate that the lock acquisition was interrupted.
     * </p>
     *
     * @param resourceId the identifier of the resource being locked
     * @param attempts the number of lock acquisition attempts made so far
     * @param startTime the timestamp (in milliseconds) marking the start of the lock acquisition process
     * @param e the {@code InterruptedException} that was caught during the lock acquisition attempt
     * 
     * @throws AccountLockException always thrown to signal that the thread was interrupted during lock acquisition
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
     * Throws an {@code AccountLockException} to indicate that the maximum number
     * of attempts to acquire the lock has been exceeded.
     *
     * <p>This method computes the elapsed time from the provided start time and
     * uses it along with the resource identifier and the configured maximum attempts
     * to populate the exception details.</p>
     *
     * @param resourceId the identifier of the resource for which the lock acquisition failed
     * @param startTime  the timestamp (in milliseconds) when the lock attempt began
     * @throws AccountLockException always thrown to signal that lock acquisition failed after
     *         exhausting the maximum allowed attempts
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
     * Releases the lock on the resource specified by its ID.
     *
     * <p>This method retrieves or creates a lock associated with the resource, attempts to unlock it,
     * and logs the operation at debug level.</p>
     *
     * @param resourceId the identifier of the resource to unlock
     */
    @Override
    public void unlock(Integer resourceId) {
        log.debug("Releasing lock for resource ID: {}", resourceId);
        ReentrantLock lock = LockingUtils.getOrCreateLock(resourceId);
        tryUnlock(resourceId, lock);
        log.debug("Successfully released lock for resource ID: {}", resourceId);
    }

    /**
     * Attempts to release the lock for the specified resource.
     * <p>
     * On successful unlocking, increments the counter tracking unlock operations.
     * If the current thread does not hold the lock, an IllegalMonitorStateException is caught
     * and handled by delegating to {@link #handleUnlockException(Integer, IllegalMonitorStateException)}.
     * </p>
     *
     * @param resourceId the identifier of the resource whose lock is being released
     * @param lock the ReentrantLock instance to unlock
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
     * Handles an unsuccessful unlock attempt by throwing an IllegalStateException.
     *
     * <p>This method is called when a lock cannot be released because it is not held by the current thread.
     * It constructs a detailed error message using the resource identifier and rethrows the underlying
     * IllegalMonitorStateException as an IllegalStateException.
     *
     * @param resourceId the identifier of the resource with the failed unlock attempt
     * @param e the original exception indicating that the lock was not held
     * @throws IllegalStateException always thrown to signal the failed unlock operation
     */
    private void handleUnlockException(Integer resourceId, IllegalMonitorStateException e) {
        String message = String.format("Cannot release lock that is not held for resource ID: %d", resourceId);
        throw new IllegalStateException(message, e);
    }
}