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

    private boolean tryAcquireLock(ReentrantLock lock, Integer resourceId, int attempts) throws InterruptedException {
        return lock.tryLock(lockingConfig.timeout(), TimeUnit.MILLISECONDS);
    }

    private void handleBackoff(int attempts, Integer resourceId) throws InterruptedException {
        long backoffDelay = LockingUtils.calculateBackoffDelay(attempts, lockingConfig);
        Thread.sleep(backoffDelay);
    }

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

    private void handleMaxAttemptsExceeded(Integer resourceId, long startTime) {
        throw new AccountLockException(
                "Failed to acquire lock after maximum attempts",
                resourceId,
                lockingConfig.maxAttempts(),
                System.currentTimeMillis() - startTime,
                false);
    }

    /**
     * Releases the lock on a resource.
     *
     * @param resourceId ID of the resource to unlock
     */
    @Override
    public void unlock(Integer resourceId) {
        log.debug("Releasing lock for resource ID: {}", resourceId);
        ReentrantLock lock = LockingUtils.getOrCreateLock(resourceId);
        tryUnlock(resourceId, lock);
        log.debug("Successfully released lock for resource ID: {}", resourceId);
    }

    private void tryUnlock(Integer resourceId, ReentrantLock lock) {
        try {
            lock.unlock();
            unlockCounter.incrementAndGet();
        } catch (IllegalMonitorStateException e) {
            handleUnlockException(resourceId, e);
        }
    }

    private void handleUnlockException(Integer resourceId, IllegalMonitorStateException e) {
        String message = String.format("Cannot release lock that is not held for resource ID: %d", resourceId);
        throw new IllegalStateException(message, e);
    }
}