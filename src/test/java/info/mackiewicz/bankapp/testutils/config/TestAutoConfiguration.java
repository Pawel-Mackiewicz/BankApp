package info.mackiewicz.bankapp.testutils.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import info.mackiewicz.bankapp.system.locking.LockingConfig;

@AutoConfiguration
@EnableConfigurationProperties(LockingConfig.class)
@Import(TestConfig.class)
public class TestAutoConfiguration {
    // This class enables LockingConfig for all test slices and imports our test configuration
}