package com.protegrity.ap.integration;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for test operations like file writing and logging.
 */
public class TestUtils {
    
    private static final String RESULTS_DIR = "target/test-results/";
    
    /**
     * Write test results to a file.
     *
     * @param fileName Name of the output file
     * @param content Content to write
     * @throws IOException If file writing fails
     */
    public static void writeResultsToFile(String fileName, String content) throws IOException {
        // Create results directory if it doesn't exist
        java.io.File dir = new java.io.File(RESULTS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        String filePath = RESULTS_DIR + fileName;
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println(content);
        }
    }
    
    /**
     * Generate a timestamp for test runs.
     *
     * @return Formatted timestamp string
     */
    public static String generateTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
    
    /**
     * Convert byte array to hex string for logging.
     *
     * @param bytes Byte array to convert
     * @return Hex string representation
     */
    public static String bytesToHex(byte[] bytes) {
        if (bytes == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    /**
     * Mask sensitive data for logging purposes.
     *
     * @param data Original data
     * @param visibleChars Number of characters to keep visible
     * @return Masked string
     */
    public static String maskData(String data, int visibleChars) {
        if (data == null || data.length() <= visibleChars) {
            return data;
        }
        
        String visible = data.substring(0, visibleChars);
        int maskedLength = data.length() - visibleChars;
        StringBuilder masked = new StringBuilder(visible);
        for (int i = 0; i < maskedLength; i++) {
            masked.append("*");
        }
        return masked.toString();
    }
    
    /**
     * Compare two arrays and return differences.
     *
     * @param expected Expected array
     * @param actual Actual array
     * @return String describing differences, or null if equal
     */
    public static String compareArrays(String[] expected, String[] actual) {
        if (expected == null && actual == null) {
            return null;
        }
        if (expected == null) {
            return "Expected array is null but actual is not";
        }
        if (actual == null) {
            return "Actual array is null but expected is not";
        }
        if (expected.length != actual.length) {
            return String.format("Array lengths differ: expected %d, actual %d", 
                expected.length, actual.length);
        }
        
        for (int i = 0; i < expected.length; i++) {
            if (expected[i] == null && actual[i] == null) {
                continue;
            }
            if (expected[i] == null || !expected[i].equals(actual[i])) {
                return String.format("Difference at index %d: expected '%s', actual '%s'",
                    i, expected[i], actual[i]);
            }
        }
        
        return null;
    }
    
    /**
     * Validate that a string is not null or empty.
     *
     * @param value String to validate
     * @param fieldName Name of the field for error message
     * @throws IllegalArgumentException If validation fails
     */
    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }
    
    /**
     * Safe substring that doesn't throw exceptions.
     *
     * @param str String to extract from
     * @param start Start index
     * @param end End index
     * @return Substring or original string if indices are invalid
     */
    public static String safeSubstring(String str, int start, int end) {
        if (str == null) {
            return null;
        }
        if (start < 0) {
            start = 0;
        }
        if (end > str.length()) {
            end = str.length();
        }
        if (start >= end) {
            return "";
        }
        return str.substring(start, end);
    }
}
