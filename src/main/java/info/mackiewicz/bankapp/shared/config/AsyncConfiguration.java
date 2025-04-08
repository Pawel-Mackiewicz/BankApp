package info.mackiewicz.bankapp.shared.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfiguration {

    /**
     * Configures and returns a ThreadPoolTaskExecutor bean for asynchronous task execution.
     *
     * <p>This executor is configured with a core pool size of 4 and a maximum pool size of 12.
     * It is set to wait for tasks to complete on shutdown, with a termination timeout of 60 seconds,
     * and uses "AsyncThread-" as the prefix for thread names.</p>
     *
     * @return the configured ThreadPoolTaskExecutor as an Executor
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(12);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.setThreadNamePrefix("AsyncThread-");
        executor.initialize();
        return executor;
    }
}
