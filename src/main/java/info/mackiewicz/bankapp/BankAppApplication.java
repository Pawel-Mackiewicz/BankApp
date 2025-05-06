package info.mackiewicz.bankapp;

import info.mackiewicz.bankapp.system.transaction.processing.locking.LockingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties(LockingConfig.class)
public class BankAppApplication {

    private static final Logger logger = LoggerFactory.getLogger(BankAppApplication.class);

    @Value("${server.port}")
    private int serverPort;

    @Value("${server.address}")
    private String serverAddress;

    public static void main(String[] args) {SpringApplication.run(BankAppApplication.class, args);}

    @EventListener(ApplicationReadyEvent.class)
    public void logServerConfiguration() {
        logger.info("Application is starting up...");
        logger.info("Server configuration: {}:{}", serverAddress, serverPort);
        logger.info("Java version: {}", System.getProperty("java.version"));
        logger.info("Operating system: {} ({})",
            System.getProperty("os.name"),
            System.getProperty("os.arch"));
    }
}
