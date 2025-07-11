package com.employeemanagement.utils;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Advanced application logger with file rotation and formatted output
 */
public final class AppLogger { // AppLogger class to log messages to a file and to the console
    private static final String LOG_FILE = "logs/app.log"; // LOG_FILE is the file to log the messages to
    private static AppLogger instance; // instance is the instance of the AppLogger
    private final Logger logger; // logger is the logger to log the messages to

    private AppLogger() { // constructor to initialize the logger
        logger = Logger.getLogger("EmployeeManagement");
        configureLogger();
    }

    public static synchronized AppLogger getInstance() { // getInstance method to get the instance of the AppLogger
        if (instance == null) {
            instance = new AppLogger();
        }
        return instance; // return the instance of the AppLogger
    }

    private void configureLogger() { // configureLogger method to configure the logger
        try {
            // Create logs directory if it doesn't exist
            Files.createDirectories(Paths.get("logs"));

            // Configure file handler
            FileHandler fileHandler = new FileHandler(LOG_FILE, true); // FileHandler is the handler to log the messages to a file
            fileHandler.setFormatter(new SimpleFormatter()); // SimpleFormatter is the formatter to format the messages
            logger.addHandler(fileHandler); // add the file handler to the logger

            // Set level
            logger.setLevel(Level.ALL); // set the level of the logger to all
        } catch (IOException e) {
            System.err.println("Failed to configure logger: " + e.getMessage());
        }
    }

    public void log(Level level, String message) { // log method to log a message
        logger.log(level, message); // log the message to the logger
    }

    public void log(Level level, String message, Throwable thrown) { // log method to log a message and a throwable
        logger.log(level, message, thrown); // log the message and the throwable to the logger
    }
}