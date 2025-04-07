package info.mackiewicz.bankapp.system.locking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import info.mackiewicz.bankapp.account.exception.AccountLockException;

@SpringBootTest
class AccountLockingStrategyTest {

    @Autowired
    private AccountLockingStrategy lockingStrategy;

    @Autowired
    private LockingConfig lockingConfig;

    private static final Integer TEST_RESOURCE_ID = 1;

    @BeforeEach
    void setUp() {
        // Reset any previous test state
        cleanupLock();
        // Reset counters
        lockingStrategy.getLockCounter().set(0);
        lockingStrategy.getUnlockCounter().set(0);
    }

    private void cleanupLock() {
        ReentrantLock lock = LockingUtils.getOrCreateLock(TEST_RESOURCE_ID);
        if (lock.isHeldByCurrentThread()) {
            lockingStrategy.unlock(TEST_RESOURCE_ID);
        }
    }

    @Nested
    @DisplayName("Basic Locking Operations")
    class BasicLockingOperations {

        @Test
        @DisplayName("Should successfully acquire and release lock")
        void shouldAcquireAndReleaseLock() {
            // When
            lockingStrategy.lock(TEST_RESOURCE_ID);

            // Then
            assertThat(lockingStrategy.getLockCounter().get()).isEqualTo(1);

            // When
            lockingStrategy.unlock(TEST_RESOURCE_ID);

            // Then
            assertThat(lockingStrategy.getUnlockCounter().get()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should throw exception when unlocking non-held lock")
        void shouldThrowExceptionWhenUnlockingNonHeldLock() {
            assertThatThrownBy(() -> lockingStrategy.unlock(TEST_RESOURCE_ID))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot release lock that is not held for resource ID: " + TEST_RESOURCE_ID);
        }
    }

    @Nested
    @DisplayName("Lock Acquisition Retry Behavior")
    class LockAcquisitionRetryBehavior {

        @Test
        @DisplayName("Should throw AccountLockException after maximum attempts")
        void shouldThrowExceptionAfterMaxAttempts() throws InterruptedException, ExecutionException, TimeoutException {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            try {
                // First thread holds the lock
                lockingStrategy.lock(TEST_RESOURCE_ID);

                // Second thread tries to acquire the same lock
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    lockingStrategy.lock(TEST_RESOURCE_ID);
                }, executor);

                assertThatThrownBy(() -> future.get(5, TimeUnit.SECONDS))
                        .hasCauseInstanceOf(AccountLockException.class)
                        .getCause()
                        .hasMessageContaining("Failed to acquire lock after maximum attempts");
            } finally {
                executor.shutdownNow();
                lockingStrategy.unlock(TEST_RESOURCE_ID);
            }
        }

        @Test
        @DisplayName("Should increment counters correctly during retry attempts")
        void shouldIncrementCountersCorrectlyDuringRetryAttempts() {
            // Given
            int initialLockCounter = lockingStrategy.getLockCounter().get();
            int initialUnlockCounter = lockingStrategy.getUnlockCounter().get();

            // When
            lockingStrategy.lock(TEST_RESOURCE_ID);
            lockingStrategy.unlock(TEST_RESOURCE_ID);

            // Then
            assertThat(lockingStrategy.getLockCounter().get())
                    .isEqualTo(initialLockCounter + 1);
            assertThat(lockingStrategy.getUnlockCounter().get())
                    .isEqualTo(initialUnlockCounter + 1);
        }
    }

    @Nested
    @DisplayName("Concurrent Access Tests")
    class ConcurrentAccessTests {

        @Test
        @DisplayName("Should handle concurrent lock requests")
        void shouldHandleConcurrentLockRequests() throws Exception {
            // Given
            int numberOfThreads = 5;
            ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            // When
            for (int i = 0; i < numberOfThreads; i++) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        lockingStrategy.lock(TEST_RESOURCE_ID);
                        Thread.sleep(500); // Simulate some work
                        lockingStrategy.unlock(TEST_RESOURCE_ID);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException(e);
                    }
                }, executor);
                futures.add(future);
            }

            // Then
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .orTimeout(5, TimeUnit.SECONDS)
                    .join();

            executor.shutdown();
            boolean terminated = executor.awaitTermination(5, TimeUnit.SECONDS);
            assertThat(terminated).isTrue();

            // Verify counters
            assertThat(lockingStrategy.getLockCounter().get()).isEqualTo(numberOfThreads);
            assertThat(lockingStrategy.getUnlockCounter().get()).isEqualTo(numberOfThreads);
        }

        @Test
        @DisplayName("Should maintain lock exclusivity under concurrent access")
        void shouldMaintainLockExclusivity() throws Exception {
            // Given
            int numberOfThreads = 3;
            ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            List<Exception> exceptions = new ArrayList<>();

            // When
            // First thread acquires and holds the lock
            CompletableFuture<Void> lockHolder = CompletableFuture.runAsync(() -> {
                try {
                    lockingStrategy.lock(TEST_RESOURCE_ID);
                    // Hold the lock for enough time to ensure other threads attempt acquisition
                    Thread.sleep(10000);
                    lockingStrategy.unlock(TEST_RESOURCE_ID);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

            }, executor);
            futures.add(lockHolder);

            // Other threads try to acquire the lock while it's held
            for (int i = 0; i < numberOfThreads - 1; i++) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        // Try to acquire the lock - this should fail because it's held
                        lockingStrategy.lock(TEST_RESOURCE_ID);
                        lockingStrategy.unlock(TEST_RESOURCE_ID);
                    } catch (Exception e) {
                        synchronized (exceptions) {
                            exceptions.add(e);
                        }
                    }
                }, executor);
                futures.add(future);
            }

            // Wait for all futures to complete
            // Then
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .orTimeout(5, TimeUnit.SECONDS)
                    .join();

            executor.shutdown();
            assertThat(executor.awaitTermination(15, TimeUnit.SECONDS)).isTrue();

            assertThat(exceptions)
                    .isNotEmpty()
                    .allMatch(e -> e instanceof AccountLockException);
        }
    }

    @Nested
    @DisplayName("Interruption Handling")
    class InterruptionHandling {

        @Test
        @DisplayName("Should handle thread interruption properly")
        void shouldHandleThreadInterruptionProperly() {
            // Given
            Thread.currentThread().interrupt();

            // When & Then
            assertThatThrownBy(() -> lockingStrategy.lock(TEST_RESOURCE_ID))
                    .isInstanceOf(AccountLockException.class)
                    .hasMessageContaining("Thread was interrupted")
                    .matches(e -> ((AccountLockException) e).wasInterrupted());

            // Clear interrupted status
            assertThat(Thread.interrupted()).isTrue();
        }
    }
}