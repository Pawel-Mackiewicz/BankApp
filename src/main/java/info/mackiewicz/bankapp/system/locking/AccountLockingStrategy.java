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
     * Attempts to lock a resource with the given ID.
     * Uses exponential backoff with jitter in case of failure.
     *
     * @param accountId ID of the account to lock
     * @throws AccountLockException if failed to acquire the lock
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
     * Attempts to acquire the provided lock within the configured timeout.
     *
     * <p>This method leverages the timeout defined in the locking configuration to try acquiring the lock.
     * It returns immediately with a boolean status indicating whether the lock was successfully obtained.
     * The additional parameters provide context for the locking attempt but do not influence the timeout behavior.</p>
     *
     * @param lock the ReentrantLock to acquire
     * @param resourceId the identifier of the resource associated with this lock attempt
     * @param attempts the current count of lock acquisition attempts
     * @return true if the lock was successfully acquired within the timeout, false otherwise
     * @throws InterruptedException if the thread is interrupted while waiting to acquire the lock
     */
    private boolean tryAcquireLock(ReentrantLock lock, Integer resourceId, int attempts) throws InterruptedException {
        return lock.tryLock(lockingConfig.timeout(), TimeUnit.MILLISECONDS);
    }

    /**
     * Applies an exponential backoff delay based on the number of lock acquisition attempts.
     * 
     * <p>This method computes the delay using the current attempt count and locking configuration,
     * then pauses execution for that duration. It throws an InterruptedException if the sleep is interrupted.</p>
     *
     * @param attempts the number of consecutive lock attempts used to calculate the backoff delay
     * @param resourceId the identifier of the resource associated with the backoff operation
     * @throws InterruptedException if the thread is interrupted during the delay period
     */
    private void handleBackoff(int attempts, Integer resourceId) throws InterruptedException {
        long backoffDelay = LockingUtils.calculateBackoffDelay(attempts, lockingConfig);
        Thread.sleep(backoffDelay);
    }

    /**
     * Handles an interruption that occurs while attempting to acquire a lock.
     *
     * <p>This method resets the thread's interrupt status, releases the lock if it is held by the current thread,
     * increments the unlock counter, and throws an AccountLockException with details about the interruption.
     *
     * @param resourceId the identifier of the resource for which the lock was being acquired
     * @param attempts the number of lock acquisition attempts made before the interruption
     * @param startTime the timestamp (in milliseconds) when the lock acquisition started
     * @param e the InterruptedException that was caught during lock acquisition
     * @throws AccountLockException always thrown to indicate that the thread was interrupted while acquiring the lock
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
     * Handles the scenario where the maximum number of lock acquisition attempts has been exceeded.
     * <p>
     * This method always throws an {@link AccountLockException} which includes details such as the
     * resource identifier, the configured maximum attempts, and the elapsed time since the lock attempt began.
     *
     * @param resourceId the identifier of the resource for which the lock was attempted
     * @param startTime  the timestamp (in milliseconds) marking the beginning of the lock acquisition attempt
     * @throws AccountLockException always thrown to signal that the maximum number of lock attempts has been reached
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
     * Releases the lock for the specified resource.
     *
     * <p>This method retrieves the lock associated with the given resource ID using a utility
     * method and attempts to release it by calling {@code tryUnlock}. Debug messages are logged
     * before and after the unlock operation.
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
     * Attempts to release the lock associated with the specified resource.
     *
     * <p>This method calls {@code unlock} on the provided {@link ReentrantLock} and increments the unlock
     * counter on a successful release. If the current thread does not hold the lock, an
     * {@link IllegalMonitorStateException} is caught and delegated to {@code handleUnlockException}
     * for further handling.
     *
     * @param resourceId the identifier of the resource whose lock is to be released
     * @param lock the lock instance to be released
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
     * Handles the exception encountered when an attempt is made to unlock a resource that is not held.
     * <p>
     * This method creates a descriptive error message using the provided resource identifier and
     * throws an IllegalStateException that wraps the original IllegalMonitorStateException.
     *
     * @param resourceId the identifier of the resource that failed to unlock
     * @param e the IllegalMonitorStateException thrown due to an illegal unlock attempt
     * @throws IllegalStateException always thrown to indicate the failure to release the lock
     */
    private void handleUnlockException(Integer resourceId, IllegalMonitorStateException e) {
        String message = String.format("Cannot release lock that is not held for resource ID: %d", resourceId);
        throw new IllegalStateException(message, e);
    }
}