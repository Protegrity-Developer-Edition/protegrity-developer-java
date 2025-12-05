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
 * Step definitions for double input data protection operations.
 */
public class DoubleProtectionSteps {
    private final TestContext testContext;
    
    public DoubleProtectionSteps(TestContext testContext) {
        this.testContext = testContext;
    }
    
    @When("the user performs {string} operations on single double input data using the following data elements")
    public void performOperationsOnSingleDoubleData(String operationType, DataTable dataTable) throws ProtectorException {
        Protector protector = testContext.getProtector();
        String currentUser = testContext.getCurrentUser();
        SessionObject session = (currentUser != null && !currentUser.isEmpty()) 
            ? protector.createSession(currentUser)
            : testContext.getSession();
        
        List<Map<String, String>> rows = dataTable.asMaps();
        
        for (Map<String, String> row : rows) {
            String dataElement = row.get("data_element");
            String inputDataStr = row.get("input_data");
            double inputData = Double.parseDouble(inputDataStr);
            
            // Use the original string value in the key to avoid collision when 1.0 and 1.00000000000 both parse to 1.0
            String key = dataElement + "_" + inputDataStr;
            testContext.getDoubleOriginalDataMap().put(key, inputData);
            
            try {
                // For encryption operations with text data element, use double[] input with byte[][] output
                if ("encryption".equalsIgnoreCase(operationType)) {
                    double[] input = {inputData};
                    byte[][] protectedOutput = new byte[1][];
                    
                    boolean protectSuccess = protector.protect(session, dataElement, input, protectedOutput);
                    
                    if (protectSuccess && protectedOutput[0] != null) {
                        // Store the length as a placeholder in the double map for validation
                        testContext.getDoubleProtectedDataMap().put(key, (double) protectedOutput[0].length);
                        
                        // Unprotect operation: byte[][] input with double[] output
                        double[] unprotectedOutput = new double[1];
                        boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput);
                        
                        if (unprotectSuccess) {
                            testContext.getDoubleUnprotectedDataMap().put(key, unprotectedOutput[0]);
                        } else {
                            String error = protector.getLastError(session);
                            testContext.setLastErrorMessage(error);
                        }
                    } else {
                        String error = protector.getLastError(session);
                        testContext.setLastErrorMessage(error);
                    }
                } else {
                    // For tokenization operations, use double[] input and output
                    double[] input = {inputData};
                    double[] protectedOutput = new double[1];
                    boolean protectSuccess = protector.protect(session, dataElement, input, protectedOutput);
                    
                    if (protectSuccess) {
                        testContext.getDoubleProtectedDataMap().put(key, protectedOutput[0]);
                        
                        // Unprotect operation
                        double[] unprotectedOutput = new double[1];
                        boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput);
                        
                        if (unprotectSuccess) {
                            testContext.getDoubleUnprotectedDataMap().put(key, unprotectedOutput[0]);
                        }
                    } else {
                        String error = protector.getLastError(session);
                        testContext.setLastErrorMessage(error);
                        fail("Double protect operation failed: " + error);
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
    
    @When("the user performs {string} operations on bulk double input data using the following data elements")
    public void performOperationsOnBulkDoubleData(String operationType, DataTable dataTable) throws ProtectorException {
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
            double[] inputData = new double[parts.length];
            for (int i = 0; i < parts.length; i++) {
                inputData[i] = Double.parseDouble(parts[i].trim());
            }
            
            String key = dataElement + "_bulk";
            testContext.getBulkDoubleOriginalDataMap().put(key, inputData);
            
            try {
                // For encryption operations with text data element, use double[] input with byte[][] output
                if ("encryption".equalsIgnoreCase(operationType)) {
                    byte[][] protectedOutput = new byte[inputData.length][];
                    
                    boolean protectSuccess = protector.protect(session, dataElement, inputData, protectedOutput);
                    
                    if (protectSuccess) {
                        // Store lengths as placeholders in the double array map for validation
                        double[] lengthArray = new double[inputData.length];
                        for (int i = 0; i < protectedOutput.length; i++) {
                            lengthArray[i] = protectedOutput[i] != null ? (double) protectedOutput[i].length : 0;
                        }
                        testContext.getBulkDoubleProtectedDataMap().put(key, lengthArray);
                        
                        // Unprotect operation: byte[][] input with double[] output
                        double[] unprotectedOutput = new double[inputData.length];
                        boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput);
                        
                        if (unprotectSuccess) {
                            testContext.getBulkDoubleUnprotectedDataMap().put(key, unprotectedOutput);
                        }
                    } else {
                        String error = protector.getLastError(session);
                        testContext.setLastErrorMessage(error);
                        fail("Bulk double protect operation failed: " + error);
                    }
                } else {
                    // For tokenization operations, use double[] input and output
                    double[] protectedOutput = new double[inputData.length];
                    boolean protectSuccess = protector.protect(session, dataElement, inputData, protectedOutput);
                    
                    if (protectSuccess) {
                        testContext.getBulkDoubleProtectedDataMap().put(key, protectedOutput);
                        
                        // Unprotect operation
                        double[] unprotectedOutput = new double[inputData.length];
                        boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput);
                        
                        if (unprotectSuccess) {
                            testContext.getBulkDoubleUnprotectedDataMap().put(key, unprotectedOutput);
                        }
                    } else {
                        String error = protector.getLastError(session);
                        testContext.setLastErrorMessage(error);
                        fail("Bulk double protect operation failed: " + error);
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
    
    @Then("all the double data is validated")
    public void allDoubleDataIsValidated() {
        Map<String, Double> originalMap = testContext.getDoubleOriginalDataMap();
        Map<String, Double> protectedMap = testContext.getDoubleProtectedDataMap();
        Map<String, Double> unprotectedMap = testContext.getDoubleUnprotectedDataMap();
        
        assertEquals("Number of protected double items should match original", originalMap.size(), protectedMap.size());
        assertEquals("Number of unprotected double items should match original", originalMap.size(), unprotectedMap.size());
        
        // All operations should have completed
        for (String key : originalMap.keySet()) {
            assertTrue("Protected double data should exist for: " + key, protectedMap.containsKey(key));
            assertTrue("Unprotected double data should exist for: " + key, unprotectedMap.containsKey(key));
        }
    }
    
    @Then("all the bulk double data is validated")
    public void allBulkDoubleDataIsValidated() {
        Map<String, double[]> originalMap = testContext.getBulkDoubleOriginalDataMap();
        Map<String, double[]> protectedMap = testContext.getBulkDoubleProtectedDataMap();
        Map<String, double[]> unprotectedMap = testContext.getBulkDoubleUnprotectedDataMap();
        
        assertEquals("Number of bulk protected double items should match original", originalMap.size(), protectedMap.size());
        assertEquals("Number of bulk unprotected double items should match original", originalMap.size(), unprotectedMap.size());
        
        // All operations should have completed
        for (String key : originalMap.keySet()) {
            assertTrue("Bulk protected double data should exist for: " + key, protectedMap.containsKey(key));
            assertTrue("Bulk unprotected double data should exist for: " + key, unprotectedMap.containsKey(key));
            
            double[] original = originalMap.get(key);
            double[] protectedData = protectedMap.get(key);
            double[] unprotectedData = unprotectedMap.get(key);
            
            assertEquals("Array lengths should match for: " + key, original.length, protectedData.length);
            assertEquals("Array lengths should match for: " + key, original.length, unprotectedData.length);
        }
    }
}
