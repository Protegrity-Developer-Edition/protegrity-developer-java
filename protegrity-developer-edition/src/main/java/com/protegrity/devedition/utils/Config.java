package com.protegrity.devedition.utils;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration management for Protegrity AI Developer Edition.
 * 
 * <p>This class manages configuration settings for PII discovery and protection operations,
 * including endpoint URLs, entity mappings, masking characters, classification thresholds,
 * and logging settings.
 * 
 * <p>Individual settings can be modified using the setter methods or through the
 * {@link #configure} method for bulk configuration.
 * 
 * <h2>Default Values</h2>
 * <ul>
 *   <li>Classification Threshold: 0.6</li>
 *   <li>Masking Character: #</li>
 *   <li>Method: redact</li>
 *   <li>Enable Logging: true</li>
 *   <li>Log Level: INFO</li>
 * </ul>
 * 
 * @since 1.0.1
 */
public class Config {

    private static final double DEFAULT_CLASSIFICATION_THRESHOLD = 0.6;
    private static final String DEFAULT_MASKING_CHAR = "#";
    private static final String DEFAULT_METHOD = "redact";
    
    private static String endpointUrl;
    private static Map<String, String> namedEntityMap;
    private static String maskingChar;
    private static double classificationScoreThreshold;
    private static String method;
    private static boolean enableLogging;
    private static String logLevel;
    
    static {
        // Initialize default configuration
        endpointUrl = System.getenv().getOrDefault(
            "DISCOVER_URL", 
            "http://localhost:8580/pty/data-discovery/v1.1/classify"
        );
        namedEntityMap = new HashMap<>();
        maskingChar = DEFAULT_MASKING_CHAR;
        classificationScoreThreshold = DEFAULT_CLASSIFICATION_THRESHOLD;
        method = DEFAULT_METHOD; 
        enableLogging = true;
        logLevel = "INFO";
    }
    
    /**
     * Returns the discovery endpoint URL.
     * 
     * @return the endpoint URL
     */
    public static String getEndpointUrl() {
        return endpointUrl;
    }
    
    /**
     * Sets the discovery endpoint URL.
     * 
     * @param url the endpoint URL to set
     */
    public static void setEndpointUrl(String url) {
        endpointUrl = url;
    }
    
    /**
     * Returns the named entity mapping.
     * 
     * @return map of entity names to their display labels
     */
    public static Map<String, String> getNamedEntityMap() {
        return namedEntityMap;
    }
    
    /**
     * Sets the named entity mapping.
     * 
     * @param map map of entity names to their display labels
     */
    public static void setNamedEntityMap(Map<String, String> map) {
        namedEntityMap = map;
    }
    
    /**
     * Returns the masking character used for masking operations.
     * 
     * @return the masking character
     */
    public static String getMaskingChar() {
        return maskingChar;
    }
    
    /**
     * Sets the masking character.
     * 
     * @param character the masking character to use
     */
    public static void setMaskingChar(String character) {
        maskingChar = character;
    }
    
    /**
     * Returns the classification score threshold.
     * 
     * @return the threshold value
     */
    public static double getClassificationScoreThreshold() {
        return classificationScoreThreshold;
    }
    
    /**
     * Sets the classification score threshold.
     * 
     * @param threshold the threshold value (0.0 to 1.0)
     */
    public static void setClassificationScoreThreshold(double threshold) {
        classificationScoreThreshold = threshold;
    }
    
    /**
     * Returns the protection method (redact or mask).
     * 
     * @return the method type
     */
    public static String getMethod() {
        return method;
    }
    
    /**
     * Sets the protection method.
     * 
     * @param methodType the method type ("redact" or "mask")
     */
    public static void setMethod(String methodType) {
        if ("redact".equals(methodType) || "mask".equals(methodType)) {
            method = methodType;
        } else {
            Logger logger = LoggerFactory.getLogger(Config.class);
            logger.warn("Invalid method specified: {}. Must be 'redact' or 'mask'.", methodType);
        }
    }
    
    /**
     * Checks if logging is enabled.
     * 
     * @return true if logging is enabled, false otherwise
     */
    public static boolean isEnableLogging() {
        return enableLogging;
    }
    
    /**
     * Sets whether logging is enabled.
     * 
     * @param enable true to enable logging, false to disable
     */
    public static void setEnableLogging(boolean enable) {
        enableLogging = enable;
    }
    
    /**
     * Returns the log level.
     * 
     * @return the log level (e.g., "INFO", "DEBUG", "ERROR")
     */
    public static String getLogLevel() {
        return logLevel;
    }
    
    /**
     * Sets the log level.
     * 
     * @param level the log level to set (e.g., "INFO", "DEBUG", "ERROR")
     */
    public static void setLogLevel(String level) {
        logLevel = level;
    }
    
    /**
     * Configures multiple settings at once.
     * 
     * <p>This method allows bulk configuration of all settings. Null values are ignored
     * and will not modify the corresponding setting.
     * 
     * @param endpointUrl the discovery endpoint URL, or null to keep current value
     * @param namedEntityMap the named entity mapping, or null to keep current value
     * @param maskingChar the masking character, or null to keep current value
     * @param classificationScoreThreshold the score threshold, or null to keep current value
     * @param method the protection method ("redact" or "mask"), or null to keep current value
     * @param enableLogging whether to enable logging, or null to keep current value
     * @param logLevel the log level, or null to keep current value
     */
    public static void configure(
            String endpointUrl,
            Map<String, String> namedEntityMap,
            String maskingChar,
            Double classificationScoreThreshold,
            String method,
            Boolean enableLogging,
            String logLevel) {
        
        if (endpointUrl != null) {
            setEndpointUrl(endpointUrl);
        }
        if (namedEntityMap != null) {
            setNamedEntityMap(namedEntityMap);
        }
        if (maskingChar != null) {
            setMaskingChar(maskingChar);
        }
        if (classificationScoreThreshold != null) {
            setClassificationScoreThreshold(classificationScoreThreshold);
        }
        if (method != null) {
            setMethod(method);
        }
        if (enableLogging != null) {
            setEnableLogging(enableLogging);
        }
        if (logLevel != null) {
            setLogLevel(logLevel);
        }
    }
}
