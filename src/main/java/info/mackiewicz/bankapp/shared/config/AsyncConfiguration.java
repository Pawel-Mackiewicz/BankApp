package info.mackiewicz.bankapp.shared.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfiguration {

    /**
     * Creates and configures a ThreadPoolTaskExecutor for asynchronous task execution.
     *
     * <p>This bean sets up a thread pool with a core size of 4 and a maximum size of 12. It waits
     * for tasks to complete on shutdown with a termination period of 60 seconds and prefixes thread
     * names with "AsyncThread-".</p>
     *
     * @return an Executor for handling asynchronous tasks
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
