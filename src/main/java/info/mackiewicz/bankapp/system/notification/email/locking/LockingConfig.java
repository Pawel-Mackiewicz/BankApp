package info.mackiewicz.bankapp.system.notification.email.locking;

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
        // Ensure maxDelay is at least equal to baseDelay
        if (maxDelay < baseDelay)
            maxDelay = baseDelay;
        if (timeout <= 0)
            timeout = 200;
    }
}
