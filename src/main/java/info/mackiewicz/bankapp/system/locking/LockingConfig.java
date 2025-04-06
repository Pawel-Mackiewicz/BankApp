package info.mackiewicz.bankapp.system.locking;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.locking")
public record LockingConfig(int maxAttempts,
        long baseDelay,
        long maxDelay,
        long timeout) {

    // Default values for the properties
    // These values can be overridden in application.properties or application.yml
    public LockingConfig {
        if (maxAttempts <= 0)
            maxAttempts = 5;
        if (baseDelay <= 0)
            baseDelay = 100;
        if (maxDelay <= 0)
            maxDelay = 2000;
        if (timeout <= 0)
            timeout = 200;
    }
}
