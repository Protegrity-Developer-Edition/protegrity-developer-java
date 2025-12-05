package com.protegrity.devedition.utils;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Map;

public class CcnProcessingTest {

    @Test
    public void testCleanCcnWithDashes() {
        String ccn = "1234-5678-9012-3456";
        CcnProcessing.CcnCleanResult result = CcnProcessing.cleanCcn(ccn);
        
        assertEquals("1234567890123456", result.getCleanedString());
        assertEquals(3, result.getSeparatorMap().size());
        assertEquals('-', (char) result.getSeparatorMap().get(4));
        assertEquals('-', (char) result.getSeparatorMap().get(9));
        assertEquals('-', (char) result.getSeparatorMap().get(14));
    }

    @Test
    public void testCleanCcnWithSpaces() {
        String ccn = "1234 5678 9012 3456";
        CcnProcessing.CcnCleanResult result = CcnProcessing.cleanCcn(ccn);
        
        assertEquals("1234567890123456", result.getCleanedString());
        assertEquals(3, result.getSeparatorMap().size());
        assertEquals(' ', (char) result.getSeparatorMap().get(4));
        assertEquals(' ', (char) result.getSeparatorMap().get(9));
        assertEquals(' ', (char) result.getSeparatorMap().get(14));
    }

    @Test
    public void testCleanCcnWithNoSeparators() {
        String ccn = "1234567890123456";
        CcnProcessing.CcnCleanResult result = CcnProcessing.cleanCcn(ccn);
        
        assertEquals("1234567890123456", result.getCleanedString());
        assertEquals(0, result.getSeparatorMap().size());
    }

    @Test
    public void testCleanCcnWithMixedSeparators() {
        String ccn = "1234-5678 9012.3456";
        CcnProcessing.CcnCleanResult result = CcnProcessing.cleanCcn(ccn);
        
        assertEquals("1234567890123456", result.getCleanedString());
        assertEquals(3, result.getSeparatorMap().size());
        assertEquals('-', (char) result.getSeparatorMap().get(4));
        assertEquals(' ', (char) result.getSeparatorMap().get(9));
        assertEquals('.', (char) result.getSeparatorMap().get(14));
    }

    @Test
    public void testCleanCcnWithLeadingAndTrailingSeparators() {
        String ccn = "-1234567890123456-";
        CcnProcessing.CcnCleanResult result = CcnProcessing.cleanCcn(ccn);
        
        assertEquals("1234567890123456", result.getCleanedString());
        assertEquals(2, result.getSeparatorMap().size());
        assertEquals('-', (char) result.getSeparatorMap().get(0));
        assertEquals('-', (char) result.getSeparatorMap().get(17));
    }

    @Test
    public void testCleanCcnWithEmptyString() {
        String ccn = "";
        CcnProcessing.CcnCleanResult result = CcnProcessing.cleanCcn(ccn);
        
        assertEquals("", result.getCleanedString());
        assertEquals(0, result.getSeparatorMap().size());
    }

    @Test
    public void testCleanCcnWithOnlySeparators() {
        String ccn = "----";
        CcnProcessing.CcnCleanResult result = CcnProcessing.cleanCcn(ccn);
        
        assertEquals("", result.getCleanedString());
        assertEquals(4, result.getSeparatorMap().size());
    }

    @Test
    public void testReconstructCcnWithDashes() {
        String cleanedCcn = "1234567890123456";
        Map<Integer, Character> separatorMap = new java.util.HashMap<>();
        separatorMap.put(4, '-');
        separatorMap.put(9, '-');
        separatorMap.put(14, '-');
        
        String result = CcnProcessing.reconstructCcn(cleanedCcn, separatorMap);
        assertEquals("1234-5678-9012-3456", result);
    }

    @Test
    public void testReconstructCcnWithSpaces() {
        String cleanedCcn = "1234567890123456";
        Map<Integer, Character> separatorMap = new java.util.HashMap<>();
        separatorMap.put(4, ' ');
        separatorMap.put(9, ' ');
        separatorMap.put(14, ' ');
        
        String result = CcnProcessing.reconstructCcn(cleanedCcn, separatorMap);
        assertEquals("1234 5678 9012 3456", result);
    }

    @Test
    public void testReconstructCcnWithNoSeparators() {
        String cleanedCcn = "1234567890123456";
        Map<Integer, Character> separatorMap = new java.util.HashMap<>();
        
        String result = CcnProcessing.reconstructCcn(cleanedCcn, separatorMap);
        assertEquals("1234567890123456", result);
    }

    @Test
    public void testReconstructCcnWithMixedSeparators() {
        String cleanedCcn = "1234567890123456";
        Map<Integer, Character> separatorMap = new java.util.HashMap<>();
        separatorMap.put(4, '-');
        separatorMap.put(9, ' ');
        separatorMap.put(14, '.');
        
        String result = CcnProcessing.reconstructCcn(cleanedCcn, separatorMap);
        assertEquals("1234-5678 9012.3456", result);
    }

    @Test
    public void testReconstructCcnWithLeadingAndTrailingSeparators() {
        String cleanedCcn = "1234567890123456";
        Map<Integer, Character> separatorMap = new java.util.HashMap<>();
        separatorMap.put(0, '-');
        separatorMap.put(17, '-');
        
        String result = CcnProcessing.reconstructCcn(cleanedCcn, separatorMap);
        assertEquals("-1234567890123456-", result);
    }

    @Test
    public void testReconstructCcnWithEmptyString() {
        String cleanedCcn = "";
        Map<Integer, Character> separatorMap = new java.util.HashMap<>();
        
        String result = CcnProcessing.reconstructCcn(cleanedCcn, separatorMap);
        assertEquals("", result);
    }

    @Test
    public void testCleanAndReconstructRoundTrip() {
        String original = "1234-5678-9012-3456";
        
        CcnProcessing.CcnCleanResult cleanResult = CcnProcessing.cleanCcn(original);
        String reconstructed = CcnProcessing.reconstructCcn(
            cleanResult.getCleanedString(), 
            cleanResult.getSeparatorMap()
        );
        
        assertEquals(original, reconstructed);
    }

    @Test
    public void testCleanAndReconstructRoundTripWithSpaces() {
        String original = "4532 1234 5678 9010";
        
        CcnProcessing.CcnCleanResult cleanResult = CcnProcessing.cleanCcn(original);
        String reconstructed = CcnProcessing.reconstructCcn(
            cleanResult.getCleanedString(), 
            cleanResult.getSeparatorMap()
        );
        
        assertEquals(original, reconstructed);
    }

    @Test
    public void testCleanAndReconstructRoundTripWithMixedSeparators() {
        String original = "1234-5678 9012.3456";
        
        CcnProcessing.CcnCleanResult cleanResult = CcnProcessing.cleanCcn(original);
        String reconstructed = CcnProcessing.reconstructCcn(
            cleanResult.getCleanedString(), 
            cleanResult.getSeparatorMap()
        );
        
        assertEquals(original, reconstructed);
    }

    @Test
    public void testCleanCcnResultGetters() {
        String ccn = "1234-5678";
        CcnProcessing.CcnCleanResult result = CcnProcessing.cleanCcn(ccn);
        
        assertNotNull(result.getCleanedString());
        assertNotNull(result.getSeparatorMap());
        assertEquals("12345678", result.getCleanedString());
        assertEquals(1, result.getSeparatorMap().size());
    }

    @Test
    public void testReconstructCcnWithConsecutiveSeparators() {
        String cleanedCcn = "123456";
        Map<Integer, Character> separatorMap = new java.util.HashMap<>();
        separatorMap.put(3, '-');
        separatorMap.put(4, '-');
        separatorMap.put(5, '-');
        
        String result = CcnProcessing.reconstructCcn(cleanedCcn, separatorMap);
        assertEquals("123---456", result);
    }

    @Test
    public void testCleanCcnWithConsecutiveSeparators() {
        String ccn = "123---456";
        CcnProcessing.CcnCleanResult result = CcnProcessing.cleanCcn(ccn);
        
        assertEquals("123456", result.getCleanedString());
        assertEquals(3, result.getSeparatorMap().size());
    }
}
