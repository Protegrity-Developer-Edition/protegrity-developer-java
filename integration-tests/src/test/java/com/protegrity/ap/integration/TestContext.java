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
        lastException = null;
        lastErrorMessage = null;
        operationResult = "success";
        operationOutput = null;
        currentUser = "superuser";
    }
}
