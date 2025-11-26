package com.protegrity.ap.java;

public class ParseResult {
    private final boolean success;
    private final Object convertedArray;

    public ParseResult(boolean success, Object convertedArray) {
        this.success = success;
        this.convertedArray = convertedArray;
    }

    public boolean isSuccess() {
        return success;
    }

    public Object getConvertedArray() {
        return convertedArray;
    }
}