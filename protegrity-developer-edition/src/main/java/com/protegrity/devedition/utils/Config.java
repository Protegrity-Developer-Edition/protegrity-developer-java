package com.protegrity.devedition.utils;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration class for the Protegrity Developer module.
 */
public class Config {
    
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
            "http://localhost:8580/pty/data-discovery/v1.0/classify"
        );
        namedEntityMap = new HashMap<>();
        maskingChar = "#";
        classificationScoreThreshold = 0.6;
        method = "redact"; // or "mask"
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
