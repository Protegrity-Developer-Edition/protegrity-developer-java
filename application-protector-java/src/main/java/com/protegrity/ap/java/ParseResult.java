package com.protegrity.ap.java;

/**
 * Represents the result of a parsing operation.
 * 
 * <p>This class encapsulates the success status and the converted array result
 * from data type conversion operations.
 * 
 * @since 1.0.0
 */
public class ParseResult {
    private final boolean success;
    private final Object convertedArray;

    /**
     * Constructs a new ParseResult.
     * 
     * @param success whether the parsing operation succeeded
     * @param convertedArray the converted array result, or null if parsing failed
     */
    public ParseResult(boolean success, Object convertedArray) {
        this.success = success;
        this.convertedArray = convertedArray;
    }

    /**
     * Returns whether the parsing operation was successful.
     * 
     * @return true if parsing succeeded, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Returns the converted array result.
     * 
     * @return the converted array, or null if parsing failed
     */
    public Object getConvertedArray() {
        return convertedArray;
    }
}