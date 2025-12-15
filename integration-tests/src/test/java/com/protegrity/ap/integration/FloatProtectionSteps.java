package com.protegrity.ap.integration;

import com.protegrity.ap.java.Protector;
import com.protegrity.ap.java.ProtectorException;
import com.protegrity.ap.java.SessionObject;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Step definitions for float input data protection operations.
 */
public class FloatProtectionSteps {
    private final TestContext testContext;
    
    public FloatProtectionSteps(TestContext testContext) {
        this.testContext = testContext;
    }
    
    @When("the user performs {string} operations on single float input data using the following data elements")
    public void performOperationsOnSingleFloatData(String operationType, DataTable dataTable) throws ProtectorException {
        Protector protector = testContext.getProtector();
        String currentUser = testContext.getCurrentUser();
        SessionObject session = (currentUser != null && !currentUser.isEmpty()) 
            ? protector.createSession(currentUser)
            : testContext.getSession();
        
        List<Map<String, String>> rows = dataTable.asMaps();
        
        for (Map<String, String> row : rows) {
            String dataElement = row.get("data_element");
            String inputDataStr = row.get("input_data");
            float inputData = Float.parseFloat(inputDataStr);
            
            // Use the original string value in the key to avoid collision when 1.0 and 1.00000000000 both parse to 1.0
            String key = dataElement + "_" + inputDataStr;
            testContext.getFloatOriginalDataMap().put(key, inputData);
            
            try {
                // For encryption operations with text data element, use float[] input with byte[][] output
                if ("encryption".equalsIgnoreCase(operationType)) {
                    float[] input = {inputData};
                    byte[][] protectedOutput = new byte[1][];
                    
                    boolean protectSuccess = protector.protect(session, dataElement, input, protectedOutput);
                    
                    if (protectSuccess && protectedOutput[0] != null) {
                        // Store the length as a placeholder in the float map for validation
                        testContext.getFloatProtectedDataMap().put(key, (float) protectedOutput[0].length);
                        
                        // Unprotect operation: byte[][] input with float[] output
                        float[] unprotectedOutput = new float[1];
                        boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput);
                        
                        if (unprotectSuccess) {
                            testContext.getFloatUnprotectedDataMap().put(key, unprotectedOutput[0]);
                        } else {
                            String error = protector.getLastError(session);
                            testContext.setLastErrorMessage(error);
                        }
                    } else {
                        String error = protector.getLastError(session);
                        testContext.setLastErrorMessage(error);
                    }
                } else {
                    // For tokenization operations, use float[] input and output
                    float[] input = {inputData};
                    float[] protectedOutput = new float[1];
                    boolean protectSuccess = protector.protect(session, dataElement, input, protectedOutput);
                    
                    if (protectSuccess) {
                        testContext.getFloatProtectedDataMap().put(key, protectedOutput[0]);
                        
                        // Unprotect operation
                        float[] unprotectedOutput = new float[1];
                        boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput);
                        
                        if (unprotectSuccess) {
                            testContext.getFloatUnprotectedDataMap().put(key, unprotectedOutput[0]);
                        }
                    } else {
                        String error = protector.getLastError(session);
                        testContext.setLastErrorMessage(error);
                        fail("Float protect operation failed: " + error);
                    }
                }
            } catch (Exception e) {
                testContext.setLastException(e);
                testContext.setOperationResult("exception");
                testContext.setLastErrorMessage(e.getMessage());
                throw e;
            }
        }
    }
    
    @When("the user performs {string} operations on bulk float input data using the following data elements")
    public void performOperationsOnBulkFloatData(String operationType, DataTable dataTable) throws ProtectorException {
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
            float[] inputData = new float[parts.length];
            for (int i = 0; i < parts.length; i++) {
                inputData[i] = Float.parseFloat(parts[i].trim());
            }
            
            String key = dataElement + "_bulk";
            testContext.getBulkFloatOriginalDataMap().put(key, inputData);
            
            try {
                // For encryption operations with text data element, use float[] input with byte[][] output
                if ("encryption".equalsIgnoreCase(operationType)) {
                    byte[][] protectedOutput = new byte[inputData.length][];
                    
                    boolean protectSuccess = protector.protect(session, dataElement, inputData, protectedOutput);
                    
                    if (protectSuccess) {
                        // Store lengths as placeholders in the float array map for validation
                        float[] lengthArray = new float[inputData.length];
                        for (int i = 0; i < protectedOutput.length; i++) {
                            lengthArray[i] = protectedOutput[i] != null ? (float) protectedOutput[i].length : 0;
                        }
                        testContext.getBulkFloatProtectedDataMap().put(key, lengthArray);
                        
                        // Unprotect operation: byte[][] input with float[] output
                        float[] unprotectedOutput = new float[inputData.length];
                        boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput);
                        
                        if (unprotectSuccess) {
                            testContext.getBulkFloatUnprotectedDataMap().put(key, unprotectedOutput);
                        }
                    } else {
                        String error = protector.getLastError(session);
                        testContext.setLastErrorMessage(error);
                        fail("Bulk float protect operation failed: " + error);
                    }
                } else {
                    // For tokenization operations, use float[] input and output
                    float[] protectedOutput = new float[inputData.length];
                    boolean protectSuccess = protector.protect(session, dataElement, inputData, protectedOutput);
                    
                    if (protectSuccess) {
                        testContext.getBulkFloatProtectedDataMap().put(key, protectedOutput);
                        
                        // Unprotect operation
                        float[] unprotectedOutput = new float[inputData.length];
                        boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput);
                        
                        if (unprotectSuccess) {
                            testContext.getBulkFloatUnprotectedDataMap().put(key, unprotectedOutput);
                        }
                    } else {
                        String error = protector.getLastError(session);
                        testContext.setLastErrorMessage(error);
                        fail("Bulk float protect operation failed: " + error);
                    }
                }
            } catch (Exception e) {
                testContext.setLastException(e);
                testContext.setOperationResult("exception");
                testContext.setLastErrorMessage(e.getMessage());
                throw e;
            }
        }
    }
    
    @Then("all the float data is validated")
    public void allFloatDataIsValidated() {
        Map<String, Float> originalMap = testContext.getFloatOriginalDataMap();
        Map<String, Float> protectedMap = testContext.getFloatProtectedDataMap();
        Map<String, Float> unprotectedMap = testContext.getFloatUnprotectedDataMap();
        
        assertEquals("Number of protected float items should match original", originalMap.size(), protectedMap.size());
        assertEquals("Number of unprotected float items should match original", originalMap.size(), unprotectedMap.size());
        
        // All operations should have completed
        for (String key : originalMap.keySet()) {
            assertTrue("Protected float data should exist for: " + key, protectedMap.containsKey(key));
            assertTrue("Unprotected float data should exist for: " + key, unprotectedMap.containsKey(key));
        }
    }
    
    @Then("all the bulk float data is validated")
    public void allBulkFloatDataIsValidated() {
        Map<String, float[]> originalMap = testContext.getBulkFloatOriginalDataMap();
        Map<String, float[]> protectedMap = testContext.getBulkFloatProtectedDataMap();
        Map<String, float[]> unprotectedMap = testContext.getBulkFloatUnprotectedDataMap();
        
        assertEquals("Number of bulk protected float items should match original", originalMap.size(), protectedMap.size());
        assertEquals("Number of bulk unprotected float items should match original", originalMap.size(), unprotectedMap.size());
        
        // All operations should have completed
        for (String key : originalMap.keySet()) {
            assertTrue("Bulk protected float data should exist for: " + key, protectedMap.containsKey(key));
            assertTrue("Bulk unprotected float data should exist for: " + key, unprotectedMap.containsKey(key));
            
            float[] original = originalMap.get(key);
            float[] protectedData = protectedMap.get(key);
            float[] unprotectedData = unprotectedMap.get(key);
            
            assertEquals("Array lengths should match for: " + key, original.length, protectedData.length);
            assertEquals("Array lengths should match for: " + key, original.length, unprotectedData.length);
        }
    }
}
