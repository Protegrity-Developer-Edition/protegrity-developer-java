package com.protegrity.devedition.utils;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration management for Protegrity Developer Edition.
 * 
 * <p>This class manages configuration settings for PII discovery and protection operations,
 * including endpoint URLs, entity mappings, masking characters, classification thresholds,
 * and logging settings.
 * 
 * <p>Configuration can be loaded from JSON files using {@link #fromFile(String)} or
 * individual settings can be modified using the setter methods.
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
 * @since 1.0.0
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
    
    public static String getEndpointUrl() {
        return endpointUrl;
    }
    
    public static void setEndpointUrl(String url) {
        endpointUrl = url;
    }
    
    public static Map<String, String> getNamedEntityMap() {
        return namedEntityMap;
    }
    
    public static void setNamedEntityMap(Map<String, String> map) {
        namedEntityMap = map;
    }
    
    public static String getMaskingChar() {
        return maskingChar;
    }
    
    public static void setMaskingChar(String character) {
        maskingChar = character;
    }
    
    public static double getClassificationScoreThreshold() {
        return classificationScoreThreshold;
    }
    
    public static void setClassificationScoreThreshold(double threshold) {
        classificationScoreThreshold = threshold;
    }
    
    public static String getMethod() {
        return method;
    }
    
    public static void setMethod(String methodType) {
        if ("redact".equals(methodType) || "mask".equals(methodType)) {
            method = methodType;
        } else {
            Logger logger = LoggerFactory.getLogger(Config.class);
            logger.warn("Invalid method specified: {}. Must be 'redact' or 'mask'.", methodType);
        }
    }
    
    public static boolean isEnableLogging() {
        return enableLogging;
    }
    
    public static void setEnableLogging(boolean enable) {
        enableLogging = enable;
    }
    
    public static String getLogLevel() {
        return logLevel;
    }
    
    public static void setLogLevel(String level) {
        logLevel = level;
    }
    
    /**
     * Configure the protegrity_developer module.
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
