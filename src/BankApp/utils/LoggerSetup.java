package BankApp.utils;

import java.io.IOException;
import java.util.logging.*;

public class LoggerSetup {

    public static Logger getLogger(String filename) {
        Logger logger = Logger.getLogger(filename + ".log");

        configureConsoleOutput(logger);
        setFileOutput(logger, filename);

        return logger;
    }

    private static void configureConsoleOutput(Logger logger) {
        logger.setUseParentHandlers(false);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter());
        consoleHandler.setLevel(Level.ALL);

        logger.addHandler(consoleHandler);
    }

    private static void setFileOutput(Logger logger, String filename) {
        try {
            FileHandler fileHandler = new FileHandler(filename, true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            logger.severe("Error in creating logging file: " + e.getMessage());
        }
    }
}
