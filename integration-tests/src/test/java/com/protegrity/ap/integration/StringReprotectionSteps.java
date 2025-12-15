package com.protegrity.ap.integration;

import com.protegrity.ap.java.Protector;
import com.protegrity.ap.java.ProtectorException;
import com.protegrity.ap.java.SessionObject;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Step definitions for string input data reprotection operations.
 */
public class StringReprotectionSteps {
    private final TestContext testContext;
    
    // Track the last protected and reprotected values for simple scenarios
    private String lastInputData;
    private String lastProtectedData;
    private String lastReprotectedData;
    private String lastOldDataElement;
    private String lastNewDataElement;
    private byte[] lastProtectedBytes;
    private byte[] lastReprotectedBytes;
    
    public StringReprotectionSteps(TestContext testContext) {
        this.testContext = testContext;
    }
    
    @When("the user performs {string} operation on single string input data {string} using {string} data element")
    public void performProtectOperationOnSingleStringData(String operation, String inputData, String dataElement) throws ProtectorException {
        Protector protector = testContext.getProtector();
        String currentUser = testContext.getCurrentUser();
        SessionObject session = (currentUser != null && !currentUser.isEmpty()) 
            ? protector.createSession(currentUser)
            : testContext.getSession();
        
        try {
            String[] input = {inputData};
            String[] protectedOutput = new String[1];
            
            boolean success = protector.protect(session, dataElement, input, protectedOutput);
            
            if (success) {
                lastInputData = inputData;
                lastProtectedData = protectedOutput[0];
                lastOldDataElement = dataElement;
                
                String key = dataElement + "_" + inputData;
                testContext.getOriginalDataMap().put(key, inputData);
                testContext.getProtectedDataMap().put(key, protectedOutput[0]);
            } else {
                String error = protector.getLastError(session);
                testContext.setLastErrorMessage(error);
                fail("Protect operation failed: " + error);
            }
        } catch (Exception e) {
            testContext.setLastException(e);
            testContext.setOperationResult("exception");
            testContext.setLastErrorMessage(e.getMessage());
            throw e;
        }
    }
    
    @When("the user performs {string} operation on single string input data {string} using {string} data element with encryption")
    public void performProtectOperationOnSingleStringDataWithEncryption(String operation, String inputData, String dataElement) throws ProtectorException {
        Protector protector = testContext.getProtector();
        String currentUser = testContext.getCurrentUser();
        SessionObject session = (currentUser != null && !currentUser.isEmpty()) 
            ? protector.createSession(currentUser)
            : testContext.getSession();
        
        try {
            byte[][] input = {inputData.getBytes()};
            byte[][] protectedOutput = new byte[1][];
            
            boolean success = protector.protect(session, dataElement, input, protectedOutput);
            
            if (success) {
                lastInputData = inputData;
                lastProtectedBytes = protectedOutput[0];
                lastProtectedData = new String(protectedOutput[0]);
                lastOldDataElement = dataElement;
            } else {
                String error = protector.getLastError(session);
                testContext.setLastErrorMessage(error);
                fail("Protect operation failed: " + error);
            }
        } catch (Exception e) {
            testContext.setLastException(e);
            testContext.setOperationResult("exception");
            testContext.setLastErrorMessage(e.getMessage());
            throw e;
        }
    }
    
    @When("the user performs {string} operation on the protected data from {string} to {string} data element")
    public void performReprotectOperationOnProtectedData(String operation, String oldDataElement, String newDataElement) throws ProtectorException {
        Protector protector = testContext.getProtector();
        String currentUser = testContext.getCurrentUser();
        SessionObject session = (currentUser != null && !currentUser.isEmpty()) 
            ? protector.createSession(currentUser)
            : testContext.getSession();
        
        try {
            String[] protectedInput = {lastProtectedData};
            String[] reprotectedOutput = new String[1];
            
            boolean success = protector.reprotect(session, newDataElement, oldDataElement, protectedInput, reprotectedOutput);
            
            if (success) {
                String key = oldDataElement + "_to_" + newDataElement + "_" + lastInputData;
                testContext.getReprotectedDataMap().put(key, reprotectedOutput[0]);
                lastReprotectedData = reprotectedOutput[0];
                lastOldDataElement = oldDataElement;
                lastNewDataElement = newDataElement;
            } else {
                String error = protector.getLastError(session);
                testContext.setLastErrorMessage(error);
                fail("Reprotect operation failed: " + error);
            }
        } catch (Exception e) {
            testContext.setLastException(e);
            testContext.setOperationResult("exception");
            testContext.setLastErrorMessage(e.getMessage());
            throw e;
        }
    }
    
    @When("the user performs {string} operation on the encrypted data from {string} to {string} data element with different IV")
    public void performReprotectOperationOnEncryptedDataWithDifferentIV(String operation, String oldDataElement, String newDataElement) throws ProtectorException {
        Protector protector = testContext.getProtector();
        String currentUser = testContext.getCurrentUser();
        SessionObject session = (currentUser != null && !currentUser.isEmpty()) 
            ? protector.createSession(currentUser)
            : testContext.getSession();
        
        try {
            byte[][] protectedInput = {lastProtectedBytes};
            byte[][] reprotectedOutput = new byte[1][];
            
            boolean success = protector.reprotect(session, newDataElement, oldDataElement, protectedInput, reprotectedOutput);
            
            if (success) {
                lastReprotectedBytes = reprotectedOutput[0];
                lastReprotectedData = new String(reprotectedOutput[0]);
                lastOldDataElement = oldDataElement;
                lastNewDataElement = newDataElement;
                
                String key = oldDataElement + "_to_" + newDataElement + "_" + lastInputData;
                testContext.getReprotectedDataMap().put(key, lastReprotectedData);
            } else {
                String error = protector.getLastError(session);
                testContext.setLastErrorMessage(error);
                fail("Reprotect operation failed: " + error);
            }
        } catch (Exception e) {
            testContext.setLastException(e);
            testContext.setOperationResult("exception");
            testContext.setLastErrorMessage(e.getMessage());
            throw e;
        }
    }
    
    @When("the user performs {string} operation on bulk string input data using the following configuration")
    public void performProtectOperationOnBulkStringData(String operation, DataTable dataTable) throws ProtectorException {
        Protector protector = testContext.getProtector();
        String currentUser = testContext.getCurrentUser();
        SessionObject session = (currentUser != null && !currentUser.isEmpty()) 
            ? protector.createSession(currentUser)
            : testContext.getSession();
        
        List<Map<String, String>> rows = dataTable.asMaps();
        
        for (Map<String, String> row : rows) {
            String oldDataElement = row.get("old_data_element");
            String inputDataStr = row.get("input_data");
            
            // Parse comma-separated values
            String[] inputData = inputDataStr.split(",");
            for (int i = 0; i < inputData.length; i++) {
                inputData[i] = inputData[i].trim();
            }
            
            String key = oldDataElement + "_bulk";
            testContext.getBulkOriginalDataMap().put(key, inputData);
            
            try {
                String[] protectedOutput = new String[inputData.length];
                boolean success = protector.protect(session, oldDataElement, inputData, protectedOutput);
                
                if (success) {
                    testContext.getBulkProtectedDataMap().put(key, protectedOutput);
                } else {
                    String error = protector.getLastError(session);
                    testContext.setLastErrorMessage(error);
                    fail("Bulk protect operation failed for " + oldDataElement + ": " + error);
                }
            } catch (Exception e) {
                testContext.setLastException(e);
                testContext.setOperationResult("exception");
                testContext.setLastErrorMessage(e.getMessage());
                throw e;
            }
        }
    }
    
    @When("the user performs {string} operation on the protected data using the following configuration")
    public void performReprotectOperationOnBulkProtectedData(String operation, DataTable dataTable) throws ProtectorException {
        Protector protector = testContext.getProtector();
        String currentUser = testContext.getCurrentUser();
        SessionObject session = (currentUser != null && !currentUser.isEmpty()) 
            ? protector.createSession(currentUser)
            : testContext.getSession();
        
        List<Map<String, String>> rows = dataTable.asMaps();
        
        for (Map<String, String> row : rows) {
            String oldDataElement = row.get("old_data_element");
            String newDataElement = row.get("new_data_element");
            
            String protectedKey = oldDataElement + "_bulk";
            String reprotectKey = oldDataElement + "_to_" + newDataElement + "_bulk";
            
            String[] protectedData = testContext.getBulkProtectedDataMap().get(protectedKey);
            
            if (protectedData == null) {
                fail("No protected data found for bulk reprotect: " + oldDataElement);
                return;
            }
            
            try {
                String[] reprotectedOutput = new String[protectedData.length];
                boolean success = protector.reprotect(session, newDataElement, oldDataElement, protectedData, reprotectedOutput);
                
                if (success) {
                    testContext.getBulkReprotectedDataMap().put(reprotectKey, reprotectedOutput);
                } else {
                    String error = protector.getLastError(session);
                    testContext.setLastErrorMessage(error);
                    fail("Bulk reprotect operation failed for " + oldDataElement + " -> " + newDataElement + ": " + error);
                }
            } catch (Exception e) {
                testContext.setLastException(e);
                testContext.setOperationResult("exception");
                testContext.setLastErrorMessage(e.getMessage());
                throw e;
            }
        }
    }
    
    @Then("the protected data should not equal the original data")
    public void protectedDataShouldNotEqualOriginalData() {
        assertNotNull("Protected data should exist", lastProtectedData);
        assertNotNull("Original data should exist", lastInputData);
        assertNotEquals("Protected data should differ from original", lastInputData, lastProtectedData);
    }
    
    @Then("the reprotected data should not equal the original data")
    public void reprotectedDataShouldNotEqualOriginalData() {
        Map<String, String> reprotectedMap = testContext.getReprotectedDataMap();
        
        assertTrue("Reprotected data should exist", reprotectedMap.size() > 0);
        
        for (String key : reprotectedMap.keySet()) {
            String reprotectedData = reprotectedMap.get(key);
            assertNotEquals("Reprotected data should differ from original", lastInputData, reprotectedData);
        }
    }
    
    @Then("all the reprotected data should not equal the original data")
    public void allReprotectedDataShouldNotEqualOriginalData() {
        Map<String, String[]> bulkOriginalMap = testContext.getBulkOriginalDataMap();
        Map<String, String[]> bulkReprotectedMap = testContext.getBulkReprotectedDataMap();
        
        assertTrue("Bulk reprotected data should exist", bulkReprotectedMap.size() > 0);
        
        for (String reprotectKey : bulkReprotectedMap.keySet()) {
            String[] reprotectedData = bulkReprotectedMap.get(reprotectKey);
            
            // Find the corresponding original data
            // Reprotect key format: old_to_new_bulk
            // Original key format: old_bulk
            for (String origKey : bulkOriginalMap.keySet()) {
                if (reprotectKey.startsWith(origKey.replace("_bulk", ""))) {
                    String[] original = bulkOriginalMap.get(origKey);
                    assertEquals("Array lengths should match for: " + reprotectKey, original.length, reprotectedData.length);
                    
                    for (int i = 0; i < original.length; i++) {
                        assertNotEquals("Reprotected data should differ from original for: " + reprotectKey + "[" + i + "]",
                            original[i], reprotectedData[i]);
                    }
                    break;
                }
            }
        }
    }
    
    @Then("the reprotected data should not equal the protected data")
    public void reprotectedDataShouldNotEqualProtectedData() {
        Map<String, String> reprotectedMap = testContext.getReprotectedDataMap();
        
        assertTrue("Reprotected data should exist", reprotectedMap.size() > 0);
        
        for (String key : reprotectedMap.keySet()) {
            String reprotectedData = reprotectedMap.get(key);
            
            // If old and new data elements are the same, protected and reprotected should match
            if (lastOldDataElement != null && lastNewDataElement != null && lastOldDataElement.equals(lastNewDataElement)) {
                assertEquals("Reprotected data should equal protected when data elements are same", lastProtectedData, reprotectedData);
            } else {
                assertNotEquals("Reprotected data should differ from protected", lastProtectedData, reprotectedData);
            }
        }
    }
    
    @Then("all the reprotected data should not equal the protected data")
    public void allReprotectedDataShouldNotEqualProtectedData() {
        Map<String, String[]> bulkProtectedMap = testContext.getBulkProtectedDataMap();
        Map<String, String[]> bulkReprotectedMap = testContext.getBulkReprotectedDataMap();
        
        assertTrue("Bulk reprotected data should exist", bulkReprotectedMap.size() > 0);
        
        for (String reprotectKey : bulkReprotectedMap.keySet()) {
            String[] reprotectedData = bulkReprotectedMap.get(reprotectKey);
            
            // Find the corresponding protected data
            // Reprotect key format: old_to_new_bulk
            // Protected key format: old_bulk
            for (String protectKey : bulkProtectedMap.keySet()) {
                if (reprotectKey.startsWith(protectKey.replace("_bulk", ""))) {
                    String[] protectedData = bulkProtectedMap.get(protectKey);
                    assertEquals("Array lengths should match for: " + reprotectKey, protectedData.length, reprotectedData.length);
                    
                    for (int i = 0; i < protectedData.length; i++) {
                        assertNotEquals("Reprotected data should differ from protected for: " + reprotectKey + "[" + i + "]",
                            protectedData[i], reprotectedData[i]);
                    }
                    break;
                }
            }
        }
    }
}

