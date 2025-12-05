package com.protegrity.ap.java;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.util.Date;
import java.util.Base64;

public class CoreproviderAdapterTest {

    private CoreproviderAdapter adapter;
    private SessionHandler sessionHandler;

    @Before
    public void setUp() throws ProtectorException {
        sessionHandler = new SessionHandler(5);
        adapter = new CoreproviderAdapter(sessionHandler);
    }

    @Test
    public void testConvertToStringArrayWithNull() {
        String[] result = adapter.convertToStringArray(null);
        assertNotNull(result);
        assertEquals(0, result.length);
    }

    @Test
    public void testConvertToStringArrayWithShortArray() {
        short[] input = {1, 2, 3, 4, 5};
        String[] result = adapter.convertToStringArray(input);
        
        assertEquals(5, result.length);
        assertEquals("1", result[0]);
        assertEquals("2", result[1]);
        assertEquals("5", result[4]);
    }

    @Test
    public void testConvertToStringArrayWithIntArray() {
        int[] input = {10, 20, 30};
        String[] result = adapter.convertToStringArray(input);
        
        assertEquals(3, result.length);
        assertEquals("10", result[0]);
        assertEquals("20", result[1]);
        assertEquals("30", result[2]);
    }

    @Test
    public void testConvertToStringArrayWithLongArray() {
        long[] input = {100L, 200L, 300L};
        String[] result = adapter.convertToStringArray(input);
        
        assertEquals(3, result.length);
        assertEquals("100", result[0]);
        assertEquals("200", result[1]);
        assertEquals("300", result[2]);
    }

    @Test
    public void testConvertToStringArrayWithFloatArray() {
        float[] input = {1.5f, 2.5f, 3.5f};
        String[] result = adapter.convertToStringArray(input);
        
        assertEquals(3, result.length);
        assertEquals("1.5", result[0]);
        assertEquals("2.5", result[1]);
        assertEquals("3.5", result[2]);
    }

    @Test
    public void testConvertToStringArrayWithDoubleArray() {
        double[] input = {1.1, 2.2, 3.3};
        String[] result = adapter.convertToStringArray(input);
        
        assertEquals(3, result.length);
        assertEquals("1.1", result[0]);
        assertEquals("2.2", result[1]);
        assertEquals("3.3", result[2]);
    }

    @Test
    public void testConvertToStringArrayWithCharArray() {
        char[][] input = {{'h', 'e', 'l', 'l', 'o'}, {'w', 'o', 'r', 'l', 'd'}};
        String[] result = adapter.convertToStringArray(input);
        
        assertEquals(2, result.length);
        assertEquals("hello", result[0]);
        assertEquals("world", result[1]);
    }

    @Test
    public void testConvertToStringArrayWithByteArray() {
        byte[][] input = {
            {1, 2, 3},
            {4, 5, 6}
        };
        String[] result = adapter.convertToStringArray(input);
        
        assertEquals(2, result.length);
        assertEquals(Base64.getEncoder().encodeToString(new byte[]{1, 2, 3}), result[0]);
        assertEquals(Base64.getEncoder().encodeToString(new byte[]{4, 5, 6}), result[1]);
    }

    @Test
    public void testConvertToStringArrayWithDateArray() {
        Date[] input = {new Date(0), new Date(1000000)};
        String[] result = adapter.convertToStringArray(input);
        
        assertEquals(2, result.length);
        assertTrue(result[0].contains("1970-01-01"));
        assertTrue(result[1].contains("1970-01-01"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertToStringArrayWithUnsupportedType() {
        String[] input = {"unsupported"};
        adapter.convertToStringArray(input);
    }

    @Test
    public void testConvertToStringArrayWithEmptyShortArray() {
        short[] input = {};
        String[] result = adapter.convertToStringArray(input);
        
        assertEquals(0, result.length);
    }

    @Test
    public void testConvertToStringArrayWithNegativeNumbers() {
        int[] input = {-1, -2, -3};
        String[] result = adapter.convertToStringArray(input);
        
        assertEquals(3, result.length);
        assertEquals("-1", result[0]);
        assertEquals("-2", result[1]);
        assertEquals("-3", result[2]);
    }

    @Test
    public void testDecodeBase64ToUtf8Strings() {
        String[] input = {
            Base64.getEncoder().encodeToString("hello".getBytes()),
            Base64.getEncoder().encodeToString("world".getBytes())
        };
        String[] result = adapter.decodeBase64ToUtf8Strings(input);
        
        assertEquals(2, result.length);
        assertEquals("hello", result[0]);
        assertEquals("world", result[1]);
    }

    @Test
    public void testDecodeBase64ToUtf8StringsWithEmptyArray() {
        String[] input = {};
        String[] result = adapter.decodeBase64ToUtf8Strings(input);
        
        assertEquals(0, result.length);
    }
}
