package com.protegrity.devedition.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Module for logging configuration and logger retrieval.
 */
public class LoggerUtil {
    
    private static final Logger logger = LoggerFactory.getLogger("com.protegrity.developer");
    
    /**
     * Get the logger instance for the Protegrity Developer module.
     *
     * @return Logger instance
     */
    public static Logger getLogger() {
        return logger;
    }
    
    /**
     * Get a logger instance for a specific class.
     *
     * @param clazz The class to get logger for
     * @return Logger instance
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
}
