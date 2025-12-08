package com.protegrity.ap.java;

import org.junit.Test;
import static org.junit.Assert.*;

public class ParseResultTest {

    @Test
    public void testSuccessfulParseResult() {
        String[] testArray = {"a", "b", "c"};
        ParseResult result = new ParseResult(true, testArray);
        
        assertTrue(result.isSuccess());
        assertArrayEquals(testArray, (String[]) result.getConvertedArray());
    }

    @Test
    public void testFailedParseResult() {
        ParseResult result = new ParseResult(false, null);
        
        assertFalse(result.isSuccess());
        assertNull(result.getConvertedArray());
    }

    @Test
    public void testParseResultWithIntArray() {
        int[] testArray = {1, 2, 3};
        ParseResult result = new ParseResult(true, testArray);
        
        assertTrue(result.isSuccess());
        assertArrayEquals(testArray, (int[]) result.getConvertedArray());
    }

    @Test
    public void testParseResultWithEmptyArray() {
        String[] emptyArray = {};
        ParseResult result = new ParseResult(true, emptyArray);
        
        assertTrue(result.isSuccess());
        assertArrayEquals(emptyArray, (String[]) result.getConvertedArray());
    }

    @Test
    public void testParseResultWithObject() {
        Object obj = new Object();
        ParseResult result = new ParseResult(true, obj);
        
        assertTrue(result.isSuccess());
        assertEquals(obj, result.getConvertedArray());
    }
}
