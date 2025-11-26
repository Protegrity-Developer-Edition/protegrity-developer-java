package com.protegrity.ap.integration;

import com.protegrity.ap.java.Protector;
import com.protegrity.ap.java.ProtectorException;
import com.protegrity.ap.java.SessionObject;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Step definitions for byte array input data protection operations.
 */
public class ByteProtectionSteps {
    private final TestContext testContext;
    
    public ByteProtectionSteps(TestContext testContext) {
        this.testContext = testContext;
    }
    
    @When("the user performs {string} operations on single bytes input data using the following data elements")
    public void performOperationsOnSingleBytesData(String operationType, DataTable dataTable) throws ProtectorException {
        Protector protector = testContext.getProtector();
        String currentUser = testContext.getCurrentUser();
        SessionObject session = (currentUser != null && !currentUser.isEmpty()) 
            ? protector.createSession(currentUser)
            : testContext.getSession();
        
        List<Map<String, String>> rows = dataTable.asMaps();
        
        for (Map<String, String> row : rows) {
            String dataElement = row.get("data_element");
            String inputDataStr = row.get("input_data");
            byte[] inputData = inputDataStr.getBytes(StandardCharsets.UTF_8);
            
            String key = dataElement + "_" + inputDataStr;
            testContext.getByteOriginalDataMap().put(key, inputData);
            
            try {
                // Protect operation
                byte[][] input = {inputData};
                byte[][] protectedOutput = new byte[1][];
                boolean protectSuccess = protector.protect(session, dataElement, input, protectedOutput);
                
                if (protectSuccess) {
                    testContext.getByteProtectedDataMap().put(key, protectedOutput[0]);
                    
                    // Unprotect operation
                    byte[][] unprotectedOutput = new byte[1][];
                    boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput);
                    
                    if (unprotectSuccess) {
                        testContext.getByteUnprotectedDataMap().put(key, unprotectedOutput[0]);
                    }
                } else {
                    String error = protector.getLastError(session);
                    testContext.setLastErrorMessage(error);
                    fail("Bytes protect operation failed: " + error);
                }
            } catch (Exception e) {
                testContext.setLastException(e);
                testContext.setOperationResult("exception");
                testContext.setLastErrorMessage(e.getMessage());
            }
        }
    }
    
    @When("the user performs {string} operations on bulk bytes input data using the following data elements")
    public void performOperationsOnBulkBytesData(String operationType, DataTable dataTable) throws ProtectorException {
        Protector protector = testContext.getProtector();
        String currentUser = testContext.getCurrentUser();
        SessionObject session = (currentUser != null && !currentUser.isEmpty()) 
            ? protector.createSession(currentUser)
            : testContext.getSession();
        
        List<Map<String, String>> rows = dataTable.asMaps();
        
        for (Map<String, String> row : rows) {
            String dataElement = row.get("data_element");
            String inputDataStr = row.get("input_data");
            
            // Parse comma-separated values
            String[] parts = inputDataStr.split(",");
            byte[][] inputData = new byte[parts.length][];
            for (int i = 0; i < parts.length; i++) {
                inputData[i] = parts[i].trim().getBytes(StandardCharsets.UTF_8);
            }
            
            String key = dataElement + "_bulk";
            testContext.getBulkByteOriginalDataMap().put(key, inputData);
            
            try {
                // Protect operation
                byte[][] protectedOutput = new byte[inputData.length][];
                boolean protectSuccess = protector.protect(session, dataElement, inputData, protectedOutput);
                
                if (protectSuccess) {
                    testContext.getBulkByteProtectedDataMap().put(key, protectedOutput);
                    
                    // Unprotect operation
                    byte[][] unprotectedOutput = new byte[inputData.length][];
                    boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput);
                    
                    if (unprotectSuccess) {
                        testContext.getBulkByteUnprotectedDataMap().put(key, unprotectedOutput);
                    }
                } else {
                    String error = protector.getLastError(session);
                    testContext.setLastErrorMessage(error);
                    fail("Bulk bytes protect operation failed: " + error);
                }
            } catch (Exception e) {
                testContext.setLastException(e);
                testContext.setOperationResult("exception");
                testContext.setLastErrorMessage(e.getMessage());
            }
        }
    }
    
    @Then("all the bytes data is validated")
    public void allBytesDataIsValidated() {
        Map<String, byte[]> originalMap = testContext.getByteOriginalDataMap();
        Map<String, byte[]> protectedMap = testContext.getByteProtectedDataMap();
        Map<String, byte[]> unprotectedMap = testContext.getByteUnprotectedDataMap();
        
        assertEquals("Number of protected byte items should match original", originalMap.size(), protectedMap.size());
        assertEquals("Number of unprotected byte items should match original", originalMap.size(), unprotectedMap.size());
        
        // All operations should have completed
        for (String key : originalMap.keySet()) {
            assertTrue("Protected byte data should exist for: " + key, protectedMap.containsKey(key));
            assertTrue("Unprotected byte data should exist for: " + key, unprotectedMap.containsKey(key));
        }
    }
    
    @Then("all the bulk bytes data is validated")
    public void allBulkBytesDataIsValidated() {
        Map<String, byte[][]> originalMap = testContext.getBulkByteOriginalDataMap();
        Map<String, byte[][]> protectedMap = testContext.getBulkByteProtectedDataMap();
        Map<String, byte[][]> unprotectedMap = testContext.getBulkByteUnprotectedDataMap();
        
        assertEquals("Number of bulk protected byte items should match original", originalMap.size(), protectedMap.size());
        assertEquals("Number of bulk unprotected byte items should match original", originalMap.size(), unprotectedMap.size());
        
        // All operations should have completed
        for (String key : originalMap.keySet()) {
            assertTrue("Bulk protected byte data should exist for: " + key, protectedMap.containsKey(key));
            assertTrue("Bulk unprotected byte data should exist for: " + key, unprotectedMap.containsKey(key));
            
            byte[][] original = originalMap.get(key);
            byte[][] protectedData = protectedMap.get(key);
            byte[][] unprotectedData = unprotectedMap.get(key);
            
            assertEquals("Array lengths should match for: " + key, original.length, protectedData.length);
            assertEquals("Array lengths should match for: " + key, original.length, unprotectedData.length);
            
            // Verify unprotected equals original
            for (int i = 0; i < original.length; i++) {
                assertArrayEquals("Unprotected bytes should match original at index " + i + " for: " + key, 
                    original[i], unprotectedData[i]);
            }
        }
    }
}
