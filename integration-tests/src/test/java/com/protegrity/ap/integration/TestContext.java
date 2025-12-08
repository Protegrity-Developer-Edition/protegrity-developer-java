package com.protegrity.ap.integration;

import com.protegrity.ap.java.Protector;
import com.protegrity.ap.java.ProtectorException;
import com.protegrity.ap.java.SessionObject;

import java.util.*;

/**
 * Common test context shared across all step definitions.
 * This class stores state between step executions.
 */
public class TestContext {
    private Protector protector;
    private SessionObject session;
    private String currentUser = "superuser";
    private Map<String, String> originalDataMap = new HashMap<>();
    private Map<String, String> protectedDataMap = new HashMap<>();
    private Map<String, String> unprotectedDataMap = new HashMap<>();
    private Map<String, String> reprotectedDataMap = new HashMap<>();
    private Map<String, String> externalIVMap = new HashMap<>();
    
    // For bulk operations
    private Map<String, String[]> bulkOriginalDataMap = new HashMap<>();
    private Map<String, String[]> bulkProtectedDataMap = new HashMap<>();
    private Map<String, String[]> bulkUnprotectedDataMap = new HashMap<>();
    private Map<String, String[]> bulkReprotectedDataMap = new HashMap<>();
    
    // For integer operations
    private Map<String, Integer> intOriginalDataMap = new HashMap<>();
    private Map<String, Integer> intProtectedDataMap = new HashMap<>();
    private Map<String, Integer> intUnprotectedDataMap = new HashMap<>();
    
    // For bulk integer operations
    private Map<String, int[]> bulkIntOriginalDataMap = new HashMap<>();
    private Map<String, int[]> bulkIntProtectedDataMap = new HashMap<>();
    private Map<String, int[]> bulkIntUnprotectedDataMap = new HashMap<>();
    
    // For byte operations
    private Map<String, byte[]> byteOriginalDataMap = new HashMap<>();
    private Map<String, byte[]> byteProtectedDataMap = new HashMap<>();
    private Map<String, byte[]> byteUnprotectedDataMap = new HashMap<>();
    
    // For bulk byte operations
    private Map<String, byte[][]> bulkByteOriginalDataMap = new HashMap<>();
    private Map<String, byte[][]> bulkByteProtectedDataMap = new HashMap<>();
    private Map<String, byte[][]> bulkByteUnprotectedDataMap = new HashMap<>();
    
    // For float operations
    private Map<String, Float> floatOriginalDataMap = new HashMap<>();
    private Map<String, Float> floatProtectedDataMap = new HashMap<>();
    private Map<String, Float> floatUnprotectedDataMap = new HashMap<>();
    
    // For bulk float operations
    private Map<String, float[]> bulkFloatOriginalDataMap = new HashMap<>();
    private Map<String, float[]> bulkFloatProtectedDataMap = new HashMap<>();
    private Map<String, float[]> bulkFloatUnprotectedDataMap = new HashMap<>();
    
    // For double operations
    private Map<String, Double> doubleOriginalDataMap = new HashMap<>();
    private Map<String, Double> doubleProtectedDataMap = new HashMap<>();
    private Map<String, Double> doubleUnprotectedDataMap = new HashMap<>();
    
    // For bulk double operations
    private Map<String, double[]> bulkDoubleOriginalDataMap = new HashMap<>();
    private Map<String, double[]> bulkDoubleProtectedDataMap = new HashMap<>();
    private Map<String, double[]> bulkDoubleUnprotectedDataMap = new HashMap<>();
    
    // For char operations
    private Map<String, char[]> charOriginalDataMap = new HashMap<>();
    private Map<String, char[]> charProtectedDataMap = new HashMap<>();
    private Map<String, char[]> charUnprotectedDataMap = new HashMap<>();
    
    // For bulk char operations
    private Map<String, char[][]> bulkCharOriginalDataMap = new HashMap<>();
    private Map<String, char[][]> bulkCharProtectedDataMap = new HashMap<>();
    private Map<String, char[][]> bulkCharUnprotectedDataMap = new HashMap<>();
    
    // For short operations
    private Map<String, Short> shortOriginalDataMap = new HashMap<>();
    private Map<String, Short> shortProtectedDataMap = new HashMap<>();
    private Map<String, Short> shortUnprotectedDataMap = new HashMap<>();
    
    // For bulk short operations
    private Map<String, short[]> bulkShortOriginalDataMap = new HashMap<>();
    private Map<String, short[]> bulkShortProtectedDataMap = new HashMap<>();
    private Map<String, short[]> bulkShortUnprotectedDataMap = new HashMap<>();
    
    // For long operations
    private Map<String, Long> longOriginalDataMap = new HashMap<>();
    private Map<String, Long> longProtectedDataMap = new HashMap<>();
    private Map<String, Long> longUnprotectedDataMap = new HashMap<>();
    
    // For bulk long operations
    private Map<String, long[]> bulkLongOriginalDataMap = new HashMap<>();
    private Map<String, long[]> bulkLongProtectedDataMap = new HashMap<>();
    private Map<String, long[]> bulkLongUnprotectedDataMap = new HashMap<>();
    
    // For date operations
    private Map<String, Date> dateOriginalDataMap = new HashMap<>();
    private Map<String, Date> dateProtectedDataMap = new HashMap<>();
    private Map<String, Date> dateUnprotectedDataMap = new HashMap<>();
    
    // For bulk date operations
    private Map<String, Date[]> bulkDateOriginalDataMap = new HashMap<>();
    private Map<String, Date[]> bulkDateProtectedDataMap = new HashMap<>();
    private Map<String, Date[]> bulkDateUnprotectedDataMap = new HashMap<>();
    
    // For error handling
    private Exception lastException;
    private String lastErrorMessage;
    private String operationResult = "success";
    private String operationOutput;
    
    public Protector getProtector() {
        return protector;
    }
    
    public void setProtector(Protector protector) {
        this.protector = protector;
    }
    
    public SessionObject getSession() {
        return session;
    }
    
    public void setSession(SessionObject session) {
        this.session = session;
    }
    
    public String getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(String user) {
        this.currentUser = user;
    }
    
    public Map<String, String> getOriginalDataMap() {
        return originalDataMap;
    }
    
    public Map<String, String> getProtectedDataMap() {
        return protectedDataMap;
    }
    
    public Map<String, String> getUnprotectedDataMap() {
        return unprotectedDataMap;
    }
    
    public Map<String, String> getReprotectedDataMap() {
        return reprotectedDataMap;
    }
    
    public Map<String, String> getExternalIVMap() {
        return externalIVMap;
    }
    
    public Map<String, String[]> getBulkOriginalDataMap() {
        return bulkOriginalDataMap;
    }
    
    public Map<String, String[]> getBulkProtectedDataMap() {
        return bulkProtectedDataMap;
    }
    
    public Map<String, String[]> getBulkUnprotectedDataMap() {
        return bulkUnprotectedDataMap;
    }
    
    public Map<String, String[]> getBulkReprotectedDataMap() {
        return bulkReprotectedDataMap;
    }
    
    public Map<String, Integer> getIntOriginalDataMap() {
        return intOriginalDataMap;
    }
    
    public Map<String, Integer> getIntProtectedDataMap() {
        return intProtectedDataMap;
    }
    
    public Map<String, Integer> getIntUnprotectedDataMap() {
        return intUnprotectedDataMap;
    }
    
    public Map<String, int[]> getBulkIntOriginalDataMap() {
        return bulkIntOriginalDataMap;
    }
    
    public Map<String, int[]> getBulkIntProtectedDataMap() {
        return bulkIntProtectedDataMap;
    }
    
    public Map<String, int[]> getBulkIntUnprotectedDataMap() {
        return bulkIntUnprotectedDataMap;
    }
    
    public Map<String, byte[]> getByteOriginalDataMap() {
        return byteOriginalDataMap;
    }
    
    public Map<String, byte[]> getByteProtectedDataMap() {
        return byteProtectedDataMap;
    }
    
    public Map<String, byte[]> getByteUnprotectedDataMap() {
        return byteUnprotectedDataMap;
    }
    
    public Map<String, byte[][]> getBulkByteOriginalDataMap() {
        return bulkByteOriginalDataMap;
    }
    
    public Map<String, byte[][]> getBulkByteProtectedDataMap() {
        return bulkByteProtectedDataMap;
    }
    
    public Map<String, byte[][]> getBulkByteUnprotectedDataMap() {
        return bulkByteUnprotectedDataMap;
    }
    
    public Map<String, Float> getFloatOriginalDataMap() {
        return floatOriginalDataMap;
    }
    
    public Map<String, Float> getFloatProtectedDataMap() {
        return floatProtectedDataMap;
    }
    
    public Map<String, Float> getFloatUnprotectedDataMap() {
        return floatUnprotectedDataMap;
    }
    
    public Map<String, float[]> getBulkFloatOriginalDataMap() {
        return bulkFloatOriginalDataMap;
    }
    
    public Map<String, float[]> getBulkFloatProtectedDataMap() {
        return bulkFloatProtectedDataMap;
    }
    
    public Map<String, float[]> getBulkFloatUnprotectedDataMap() {
        return bulkFloatUnprotectedDataMap;
    }
    
    public Map<String, Double> getDoubleOriginalDataMap() {
        return doubleOriginalDataMap;
    }
    
    public Map<String, Double> getDoubleProtectedDataMap() {
        return doubleProtectedDataMap;
    }
    
    public Map<String, Double> getDoubleUnprotectedDataMap() {
        return doubleUnprotectedDataMap;
    }
    
    public Map<String, double[]> getBulkDoubleOriginalDataMap() {
        return bulkDoubleOriginalDataMap;
    }
    
    public Map<String, double[]> getBulkDoubleProtectedDataMap() {
        return bulkDoubleProtectedDataMap;
    }
    
    public Map<String, double[]> getBulkDoubleUnprotectedDataMap() {
        return bulkDoubleUnprotectedDataMap;
    }
    
    public Map<String, char[]> getCharOriginalDataMap() {
        return charOriginalDataMap;
    }
    
    public Map<String, char[]> getCharProtectedDataMap() {
        return charProtectedDataMap;
    }
    
    public Map<String, char[]> getCharUnprotectedDataMap() {
        return charUnprotectedDataMap;
    }
    
    public Map<String, char[][]> getBulkCharOriginalDataMap() {
        return bulkCharOriginalDataMap;
    }
    
    public Map<String, char[][]> getBulkCharProtectedDataMap() {
        return bulkCharProtectedDataMap;
    }
    
    public Map<String, char[][]> getBulkCharUnprotectedDataMap() {
        return bulkCharUnprotectedDataMap;
    }
    
    public Map<String, Short> getShortOriginalDataMap() {
        return shortOriginalDataMap;
    }
    
    public Map<String, Short> getShortProtectedDataMap() {
        return shortProtectedDataMap;
    }
    
    public Map<String, Short> getShortUnprotectedDataMap() {
        return shortUnprotectedDataMap;
    }
    
    public Map<String, short[]> getBulkShortOriginalDataMap() {
        return bulkShortOriginalDataMap;
    }
    
    public Map<String, short[]> getBulkShortProtectedDataMap() {
        return bulkShortProtectedDataMap;
    }
    
    public Map<String, short[]> getBulkShortUnprotectedDataMap() {
        return bulkShortUnprotectedDataMap;
    }
    
    public Map<String, Long> getLongOriginalDataMap() {
        return longOriginalDataMap;
    }
    
    public Map<String, Long> getLongProtectedDataMap() {
        return longProtectedDataMap;
    }
    
    public Map<String, Long> getLongUnprotectedDataMap() {
        return longUnprotectedDataMap;
    }
    
    public Map<String, long[]> getBulkLongOriginalDataMap() {
        return bulkLongOriginalDataMap;
    }
    
    public Map<String, long[]> getBulkLongProtectedDataMap() {
        return bulkLongProtectedDataMap;
    }
    
    public Map<String, long[]> getBulkLongUnprotectedDataMap() {
        return bulkLongUnprotectedDataMap;
    }
    
    public Map<String, Date> getDateOriginalDataMap() {
        return dateOriginalDataMap;
    }
    
    public Map<String, Date> getDateProtectedDataMap() {
        return dateProtectedDataMap;
    }
    
    public Map<String, Date> getDateUnprotectedDataMap() {
        return dateUnprotectedDataMap;
    }
    
    public Map<String, Date[]> getBulkDateOriginalDataMap() {
        return bulkDateOriginalDataMap;
    }
    
    public Map<String, Date[]> getBulkDateProtectedDataMap() {
        return bulkDateProtectedDataMap;
    }
    
    public Map<String, Date[]> getBulkDateUnprotectedDataMap() {
        return bulkDateUnprotectedDataMap;
    }
    
    public Exception getLastException() {
        return lastException;
    }
    
    public void setLastException(Exception e) {
        this.lastException = e;
    }
    
    public String getLastErrorMessage() {
        return lastErrorMessage;
    }
    
    public void setLastErrorMessage(String message) {
        this.lastErrorMessage = message;
    }
    
    public String getOperationResult() {
        return operationResult;
    }
    
    public void setOperationResult(String result) {
        this.operationResult = result;
    }
    
    public String getOperationOutput() {
        return operationOutput;
    }
    
    public void setOperationOutput(String output) {
        this.operationOutput = output;
    }
    
    public void reset() {
        originalDataMap.clear();
        protectedDataMap.clear();
        unprotectedDataMap.clear();
        reprotectedDataMap.clear();
        externalIVMap.clear();
        bulkOriginalDataMap.clear();
        bulkProtectedDataMap.clear();
        bulkUnprotectedDataMap.clear();
        intOriginalDataMap.clear();
        intProtectedDataMap.clear();
        intUnprotectedDataMap.clear();
        bulkIntOriginalDataMap.clear();
        bulkIntProtectedDataMap.clear();
        bulkIntUnprotectedDataMap.clear();
        byteOriginalDataMap.clear();
        byteProtectedDataMap.clear();
        byteUnprotectedDataMap.clear();
        bulkByteOriginalDataMap.clear();
        bulkByteProtectedDataMap.clear();
        bulkByteUnprotectedDataMap.clear();
        floatOriginalDataMap.clear();
        floatProtectedDataMap.clear();
        floatUnprotectedDataMap.clear();
        bulkFloatOriginalDataMap.clear();
        bulkFloatProtectedDataMap.clear();
        bulkFloatUnprotectedDataMap.clear();
        doubleOriginalDataMap.clear();
        doubleProtectedDataMap.clear();
        doubleUnprotectedDataMap.clear();
        bulkDoubleOriginalDataMap.clear();
        bulkDoubleProtectedDataMap.clear();
        bulkDoubleUnprotectedDataMap.clear();
        charOriginalDataMap.clear();
        charProtectedDataMap.clear();
        charUnprotectedDataMap.clear();
        bulkCharOriginalDataMap.clear();
        bulkCharProtectedDataMap.clear();
        bulkCharUnprotectedDataMap.clear();
        shortOriginalDataMap.clear();
        shortProtectedDataMap.clear();
        shortUnprotectedDataMap.clear();
        bulkShortOriginalDataMap.clear();
        bulkShortProtectedDataMap.clear();
        bulkShortUnprotectedDataMap.clear();
        longOriginalDataMap.clear();
        longProtectedDataMap.clear();
        longUnprotectedDataMap.clear();
        bulkLongOriginalDataMap.clear();
        bulkLongProtectedDataMap.clear();
        bulkLongUnprotectedDataMap.clear();
        dateOriginalDataMap.clear();
        dateProtectedDataMap.clear();
        dateUnprotectedDataMap.clear();
        bulkDateOriginalDataMap.clear();
        bulkDateProtectedDataMap.clear();
        bulkDateUnprotectedDataMap.clear();
        lastException = null;
        lastErrorMessage = null;
        operationResult = "success";
        operationOutput = null;
        currentUser = "superuser";
    }
}
