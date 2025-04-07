package info.mackiewicz.bankapp.system.locking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import info.mackiewicz.bankapp.account.exception.AccountLockException;

@DisplayName("AccountLockingStrategy Tests")
class AccountLockingStrategyTest {
    
    // Configuration constants for tests
    private static final int DEFAULT_TIMEOUT_SECONDS = 2;
    private static final int LONG_TIMEOUT_SECONDS = 5;
    private static final int DEFAULT_THREAD_COUNT = 5;

    private static final int TIMEOUT = 50; // 1 second
    private static final int MAX_ATTEMPTS = 3; // 3 attempts
    private static final int BASE_DELAY = 25; // 500ms
    private static final int MAX_DELAY = 500; // 2 seconds
    
    private final AccountLockingStrategy lockingStrategy;
    
    AccountLockingStrategyTest() {
        // Constructor injection for the locking strategy
        // With `-1` for default values for the LockingConfig (check LockingConfig class for details)
        this.lockingStrategy = new AccountLockingStrategy(new LockingConfig(MAX_ATTEMPTS, BASE_DELAY, MAX_DELAY, TIMEOUT));
    }

    @Nested
    @DisplayName("Basic Locking Operations")
    class BasicLockingOperations {
        // Each test class has its own resource ID
        private Integer testResourceId;
        
        @BeforeEach
        void setUp() {
            // Initialize test resource ID uniquely for this test class
            testResourceId = ThreadLocalRandom.current().nextInt(100000, 199999);
            cleanupLock(testResourceId);
            lockingStrategy.getLockCounter().set(0);
            lockingStrategy.getUnlockCounter().set(0);
        }

        @Test
        @DisplayName("Should successfully acquire and release lock")
        void shouldAcquireAndReleaseLock() {
            // When
            lockingStrategy.lock(testResourceId);

            // Then
            assertThat(lockingStrategy.getLockCounter().get()).isEqualTo(1);

            // When
            lockingStrategy.unlock(testResourceId);

            // Then
            assertThat(lockingStrategy.getUnlockCounter().get()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should throw exception when unlocking non-held lock")
        void shouldThrowExceptionWhenUnlockingNonHeldLock() {
            assertThatThrownBy(() -> lockingStrategy.unlock(testResourceId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot release lock that is not held for resource ID: " + testResourceId);
        }
    }

    @Nested
    @DisplayName("Lock Acquisition Retry Behavior")
    class LockAcquisitionRetryBehavior {
        // Unique resource ID for this test class
        private Integer testResourceId;
        
        @BeforeEach
        void setUp() {
            testResourceId = ThreadLocalRandom.current().nextInt(200000, 299999);
            cleanupLock(testResourceId);
            lockingStrategy.getLockCounter().set(0);
            lockingStrategy.getUnlockCounter().set(0);
        }

        @Test
        @DisplayName("Should throw AccountLockException after maximum attempts")
        void shouldThrowExceptionAfterMaxAttempts() {
            // Given
            ExecutorService executor = Executors.newSingleThreadExecutor();
            CountDownLatch lockAcquired = new CountDownLatch(1);
            AtomicBoolean lockReleased = new AtomicBoolean(false);
            
            try {
                // First thread holds the lock
                lockingStrategy.lock(testResourceId);
                
                // Signal that first thread has acquired the lock
                lockAcquired.countDown();

                // Second thread tries to acquire the same lock
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    try {
                        lockingStrategy.lock(testResourceId);
                    } finally {
                        if (lockReleased.get()) {
                            // Only if the main thread released the lock - we unlock
                            lockingStrategy.unlock(testResourceId);
                        }
                    }
                }, executor);

                assertThatThrownBy(() -> future.get(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS))
                        .hasCauseInstanceOf(AccountLockException.class)
                        .cause()
                        .hasMessageContaining("Failed to acquire lock after maximum attempts");
            } finally {
                // Mark that the lock has been released and release it
                lockReleased.set(true);
                lockingStrategy.unlock(testResourceId);
                
                executor.shutdownNow();
                try {
                    executor.awaitTermination(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        @Test
        @DisplayName("Should increment counters correctly during retry attempts")
        void shouldIncrementCountersCorrectlyDuringRetryAttempts() {
            // Given
            int initialLockCounter = lockingStrategy.getLockCounter().get();
            int initialUnlockCounter = lockingStrategy.getUnlockCounter().get();

            // When
            lockingStrategy.lock(testResourceId);
            lockingStrategy.unlock(testResourceId);

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
        // Unique resource ID for this test class
        private Integer testResourceId;
        
        @BeforeEach
        void setUp() {
            testResourceId = ThreadLocalRandom.current().nextInt(300000, 399999);
            cleanupLock(testResourceId);
            lockingStrategy.getLockCounter().set(0);
            lockingStrategy.getUnlockCounter().set(0);
        }

        @ParameterizedTest(name = "with {0} threads")
        @ValueSource(ints = {2, 5, 10})
        @DisplayName("Should handle concurrent lock requests")
        void shouldHandleConcurrentLockRequests(int numberOfThreads) throws Exception {
            // Given
            ExecutorService executor = null;
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            
            try {
                executor = Executors.newFixedThreadPool(numberOfThreads);

                // When
                for (int i = 0; i < numberOfThreads; i++) {
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        try {
                            lockingStrategy.lock(testResourceId);
                            Thread.sleep(10); // Simulate some work
                            lockingStrategy.unlock(testResourceId);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException(e);
                        }
                    }, executor);
                    futures.add(future);
                }

                // Then
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                        .orTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                        .join();
                
                // Verify counters
                assertThat(lockingStrategy.getLockCounter().get()).isEqualTo(numberOfThreads);
                assertThat(lockingStrategy.getUnlockCounter().get()).isEqualTo(numberOfThreads);
            } finally {
                if (executor != null) {
                    executor.shutdownNow();
                    executor.awaitTermination(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                }
            }
        }

        @Test
        @DisplayName("Should maintain lock exclusivity under concurrent access")
        void shouldMaintainLockExclusivity() throws Exception {
            // Given
            ExecutorService executor = null;
            CountDownLatch lockAcquired = new CountDownLatch(1);
            CountDownLatch threadsStarted = new CountDownLatch(DEFAULT_THREAD_COUNT - 1);
            CountDownLatch testsCompleted = new CountDownLatch(DEFAULT_THREAD_COUNT - 1);
            List<AccountLockException> exceptions = new ArrayList<>();
            
            try {
                executor = Executors.newFixedThreadPool(DEFAULT_THREAD_COUNT);

                // Thread that acquires and holds the lock
                CompletableFuture<Void> lockHolder = CompletableFuture.runAsync(() -> {
                    try {
                        lockingStrategy.lock(testResourceId);
                        lockAcquired.countDown(); // Signal that the lock has been acquired
                        
                        // Wait until all tests start trying to acquire the lock
                        threadsStarted.await(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                        
                        // Wait until all tests try to acquire the lock and throw exceptions
                        testsCompleted.await(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                        
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        lockingStrategy.unlock(testResourceId);
                    }
                }, executor);

                // Other threads try to acquire the lock when it's already taken
                List<CompletableFuture<Void>> competingThreads = new ArrayList<>();
                for (int i = 0; i < DEFAULT_THREAD_COUNT - 1; i++) {
                    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                        try {
                            // Wait until the first thread acquires the lock
                            if (!lockAcquired.await(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                                throw new TimeoutException("Timeout waiting for lock acquisition");
                            }
                            
                            // Mark that the test has started
                            threadsStarted.countDown();
                            
                            // Attempt to acquire the lock - this should fail
                            lockingStrategy.lock(testResourceId);
                            lockingStrategy.unlock(testResourceId);
                            // If we reached here, it means the lock was acquired - this is an error
                            throw new AssertionError("Lock should not be acquired when already held");
                        } catch (Exception e) {
                            if (e instanceof AccountLockException) {
                                synchronized (exceptions) {
                                    exceptions.add((AccountLockException) e);
                                }
                            } else {
                                throw new RuntimeException("Unexpected exception", e);
                            }
                        } finally {
                            testsCompleted.countDown();
                        }
                    }, executor);
                    competingThreads.add(future);
                }

                // Wait for all competing tests to complete
                CompletableFuture.allOf(competingThreads.toArray(new CompletableFuture[0]))
                        .orTimeout(LONG_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                        .join();
                
                // Wait for the main thread holding the lock to complete
                lockHolder.orTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS).join();
                
                // Check if the expected exceptions occurred
                assertThat(exceptions)
                        .as("Should collect AccountLockException from competing threads")
                        .isNotEmpty()
                        .hasSize(DEFAULT_THREAD_COUNT - 1)
                        .allMatch(e -> e.getMessage().contains("Failed to acquire lock after maximum attempts"));
                
            } finally {
                if (executor != null) {
                    executor.shutdownNow();
                    executor.awaitTermination(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                }
            }
        }
    }

    @Nested
    @DisplayName("Interruption Handling")
    class InterruptionHandling {
        // Unique resource ID for this test class
        private Integer testResourceId;
        
        @BeforeEach
        void setUp() {
            testResourceId = ThreadLocalRandom.current().nextInt(400000, 499999);
            cleanupLock(testResourceId);
            lockingStrategy.getLockCounter().set(0);
            lockingStrategy.getUnlockCounter().set(0);
        }

        @Test
        @DisplayName("Should handle thread interruption properly")
        void shouldHandleThreadInterruptionProperly() {
            // Given
            Thread.currentThread().interrupt();

            try {
                // When & Then
                assertThatThrownBy(() -> lockingStrategy.lock(testResourceId))
                        .isInstanceOf(AccountLockException.class)
                        .hasMessageContaining("Thread was interrupted")
                        .matches(e -> ((AccountLockException) e).wasInterrupted());
            } finally {
                // Clear interrupted status - moved to finally for safety
                boolean wasInterrupted = Thread.interrupted();
                assertThat(wasInterrupted).isTrue();
            }
        }
    }
    
    /**
     * Helper method for cleaning up lock for a specific resource ID
     */
    private void cleanupLock(Integer resourceId) {
        ReentrantLock lock = LockingUtils.getOrCreateLock(resourceId);
        if (lock.isHeldByCurrentThread()) {
            lockingStrategy.unlock(resourceId);
        }
    }
}