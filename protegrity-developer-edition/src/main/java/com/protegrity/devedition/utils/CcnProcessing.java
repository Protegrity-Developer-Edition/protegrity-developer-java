package com.protegrity.devedition.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for processing credit card numbers (CCNs).
 * 
 * <p>This class provides methods for removing and reconstructing separators
 * (such as spaces or dashes) from credit card numbers. This is useful for
 * normalizing CCNs before protection and restoring the original format afterward.
 * 
 * @since 1.0.0
 */
public class CcnProcessing {
    
    /**
     * Result class to hold cleaned CCN and separator map.
     */
    public static class CcnCleanResult {
        private final String cleanedString;
        private final Map<Integer, Character> separatorMap;
        
        /**
         * Constructs a new CcnCleanResult.
         * 
         * @param cleanedString the credit card number with separators removed
         * @param separatorMap map of original positions to separator characters
         */
        public CcnCleanResult(String cleanedString, Map<Integer, Character> separatorMap) {
            this.cleanedString = cleanedString;
            this.separatorMap = separatorMap;
        }
        
        /**
         * Returns the cleaned credit card number string.
         * 
         * @return the CCN with all separators removed
         */
        public String getCleanedString() {
            return cleanedString;
        }
        
        /**
         * Returns the separator map.
         * 
         * @return map of original positions to separator characters
         */
        public Map<Integer, Character> getSeparatorMap() {
            return separatorMap;
        }
    }
    
    /**
     * Remove separators from credit card number and store their positions.
     *
     * @param ccnString Credit card number with separators
     * @return CcnCleanResult containing cleaned string and separator map
     */
    public static CcnCleanResult cleanCcn(String ccnString) {
        StringBuilder cleaned = new StringBuilder();
        Map<Integer, Character> separatorMap = new HashMap<>();
        
        for (int i = 0; i < ccnString.length(); i++) {
            char ch = ccnString.charAt(i);
            if (Character.isDigit(ch)) {
                cleaned.append(ch);
            } else {
                // Store the original position and the separator character
                separatorMap.put(i, ch);
            }
        }
        
        return new CcnCleanResult(cleaned.toString(), separatorMap);
    }
    
    /**
     * Reconstruct original CCN format using the separator map.
     *
     * @param cleanedCcn CCN with only digits
     * @param separatorMap Mapping of position to separator character
     * @return CCN with separators restored to original positions
     */
    public static String reconstructCcn(String cleanedCcn, Map<Integer, Character> separatorMap) {
        StringBuilder result = new StringBuilder();
        int originalPos = 0;
        
        for (int cleanedPos = 0; cleanedPos < cleanedCcn.length(); cleanedPos++) {
            // Add any separators that should come before this digit
            while (separatorMap.containsKey(originalPos)) {
                result.append(separatorMap.get(originalPos));
                originalPos++;
            }
            
            result.append(cleanedCcn.charAt(cleanedPos));
            originalPos++;
        }
        
        // Add any trailing separators
        while (separatorMap.containsKey(originalPos)) {
            result.append(separatorMap.get(originalPos));
            originalPos++;
        }
        
        return result.toString();
    }
}
