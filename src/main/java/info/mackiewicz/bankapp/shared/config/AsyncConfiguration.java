package info.mackiewicz.bankapp.shared.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfiguration {

    /**
     * Creates and configures a {@link ThreadPoolTaskExecutor} for asynchronous task execution.
     *
     * <p>This executor is set up with a core pool size of 4 threads and a maximum of 12 threads. It is configured to 
     * wait for all tasks to complete on shutdown with an await termination period of 60 seconds, and uses the 
     * "AsyncThread-" prefix for thread naming to simplify debugging.</p>
     *
     * @return a configured {@link Executor} instance for asynchronous task execution
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
