package com.protegrity.ap.integration;

import com.protegrity.ap.java.Protector;
import com.protegrity.ap.java.ProtectorException;
import com.protegrity.ap.java.SessionObject;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Step definitions for integer input data protection operations.
 */
public class IntegerProtectionSteps {
    private final TestContext testContext;
    
    public IntegerProtectionSteps(TestContext testContext) {
        this.testContext = testContext;
    }
    
    @When("the user performs {string} operations on single integer input data using the following data elements")
    public void performOperationsOnSingleIntegerData(String operationType, DataTable dataTable) throws ProtectorException {
        Protector protector = testContext.getProtector();
        String currentUser = testContext.getCurrentUser();
        SessionObject session = (currentUser != null && !currentUser.isEmpty()) 
            ? protector.createSession(currentUser)
            : testContext.getSession();
        
        List<Map<String, String>> rows = dataTable.asMaps();
        
        for (Map<String, String> row : rows) {
            String dataElement = row.get("data_element");
            String inputDataStr = row.get("input_data");
            int inputData = Integer.parseInt(inputDataStr);
            
            String key = dataElement + "_" + inputData;
            testContext.getIntOriginalDataMap().put(key, inputData);
            // Mirror into generic maps for shared string-based validation
            testContext.getOriginalDataMap().put(key, String.valueOf(inputData));
            
            try {
                // For encryption operations with text data element, use int[] input with byte[][] output
                if ("encryption".equalsIgnoreCase(operationType)) {
                    int[] input = {inputData};
                    byte[][] protectedOutput = new byte[1][];
                    
                    boolean protectSuccess = protector.protect(session, dataElement, input, protectedOutput);
                    
                    if (protectSuccess && protectedOutput[0] != null) {
                        // Store the length as a placeholder in the int map for validation
                        testContext.getIntProtectedDataMap().put(key, protectedOutput[0].length);
                        String hexProtected = bytesToHex(protectedOutput[0]);
                        testContext.getProtectedDataMap().put(key, hexProtected);
                        
                        // Unprotect operation: byte[][] input with int[] output
                        int[] unprotectedOutput = new int[1];
                        boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput);
                        
                        if (unprotectSuccess) {
                            testContext.getIntUnprotectedDataMap().put(key, unprotectedOutput[0]);
                            testContext.getUnprotectedDataMap().put(key, String.valueOf(unprotectedOutput[0]));
                        } else {
                            String error = protector.getLastError(session);
                            testContext.setLastErrorMessage(error);
                        }
                    } else {
                        String error = protector.getLastError(session);
                        testContext.setLastErrorMessage(error);
                    }
                } else {
                    // For tokenization operations, use int[] input and output
                    int[] input = {inputData};
                    int[] protectedOutput = new int[1];
                    boolean protectSuccess = protector.protect(session, dataElement, input, protectedOutput);
                    
                    if (protectSuccess) {
                        testContext.getIntProtectedDataMap().put(key, protectedOutput[0]);
                        testContext.getProtectedDataMap().put(key, String.valueOf(protectedOutput[0]));
                        
                        // Unprotect operation
                        int[] unprotectedOutput = new int[1];
                        boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput);
                        
                        if (unprotectSuccess) {
                            testContext.getIntUnprotectedDataMap().put(key, unprotectedOutput[0]);
                            testContext.getUnprotectedDataMap().put(key, String.valueOf(unprotectedOutput[0]));
                        }
                    } else {
                        String error = protector.getLastError(session);
                        testContext.setLastErrorMessage(error);
                        fail("Integer protect operation failed: " + error);
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
    
    @When("the user performs {string} operations on bulk integer input data using the following data elements")
    public void performOperationsOnBulkIntegerData(String operationType, DataTable dataTable) throws ProtectorException {
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
            int[] inputData = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                inputData[i] = Integer.parseInt(parts[i].trim());
            }
            
            String key = dataElement + "_bulk";
            testContext.getBulkIntOriginalDataMap().put(key, inputData);
            // Mirror originals into generic map as joined string
            testContext.getOriginalDataMap().put(key, joinInts(inputData));
            
            try {
                // For encryption operations with text data element, use int[] input with byte[][] output
                if ("encryption".equalsIgnoreCase(operationType)) {
                    byte[][] protectedOutput = new byte[inputData.length][];
                    
                    boolean protectSuccess = protector.protect(session, dataElement, inputData, protectedOutput);
                    
                    if (protectSuccess) {
                        // Store lengths as placeholders in the int array map for validation
                        int[] lengthArray = new int[inputData.length];
                        for (int i = 0; i < protectedOutput.length; i++) {
                            lengthArray[i] = protectedOutput[i] != null ? protectedOutput[i].length : 0;
                        }
                        testContext.getBulkIntProtectedDataMap().put(key, lengthArray);
                        // Store hex protected values joined
                        String[] hexParts = new String[protectedOutput.length];
                        for (int i = 0; i < protectedOutput.length; i++) {
                            hexParts[i] = protectedOutput[i] != null ? bytesToHex(protectedOutput[i]) : "";
                        }
                        testContext.getProtectedDataMap().put(key, String.join(",", hexParts));
                        
                        // Unprotect operation: byte[][] input with int[] output
                        int[] unprotectedOutput = new int[inputData.length];
                        boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput);
                        
                        if (unprotectSuccess) {
                            testContext.getBulkIntUnprotectedDataMap().put(key, unprotectedOutput);
                            testContext.getUnprotectedDataMap().put(key, joinInts(unprotectedOutput));
                        }
                    } else {
                        String error = protector.getLastError(session);
                        testContext.setLastErrorMessage(error);
                        fail("Bulk integer protect operation failed: " + error);
                    }
                } else {
                    // For tokenization operations, use int[] input and output
                    int[] protectedOutput = new int[inputData.length];
                    boolean protectSuccess = protector.protect(session, dataElement, inputData, protectedOutput);
                    
                    if (protectSuccess) {
                        testContext.getBulkIntProtectedDataMap().put(key, protectedOutput);
                        testContext.getProtectedDataMap().put(key, joinInts(protectedOutput));
                        
                        // Unprotect operation
                        int[] unprotectedOutput = new int[inputData.length];
                        boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput);
                        
                        if (unprotectSuccess) {
                            testContext.getBulkIntUnprotectedDataMap().put(key, unprotectedOutput);
                            testContext.getUnprotectedDataMap().put(key, joinInts(unprotectedOutput));
                        }
                    } else {
                        String error = protector.getLastError(session);
                        testContext.setLastErrorMessage(error);
                        fail("Bulk integer protect operation failed: " + error);
                    }
                }
            } catch (Exception e) {
                testContext.setLastException(e);
                testContext.setOperationResult("exception");
                testContext.setLastErrorMessage(e.getMessage());
            }
        }
    }
    
    @When("the user performs {string} operations on single integer input data with external IV using the following data elements")
    public void performOperationsWithExternalIV(String operationType, DataTable dataTable) throws ProtectorException {
        Protector protector = testContext.getProtector();
        String currentUser = testContext.getCurrentUser();
        SessionObject session = (currentUser != null && !currentUser.isEmpty()) 
            ? protector.createSession(currentUser)
            : testContext.getSession();
        
        List<Map<String, String>> rows = dataTable.asMaps();
        
        for (Map<String, String> row : rows) {
            String dataElement = row.get("data_element");
            String inputDataStr = row.get("input_data");
            String externalIV = row.get("external_iv");
            int inputData = Integer.parseInt(inputDataStr);
            
            String key = dataElement + "_" + inputData + "_" + externalIV;
            testContext.getIntOriginalDataMap().put(key, inputData);
            testContext.getOriginalDataMap().put(key, String.valueOf(inputData));
            testContext.getExternalIVMap().put(key, externalIV);
            
            try {
                // Convert external IV to bytes
                byte[] ivBytes = externalIV.getBytes(StandardCharsets.UTF_8);
                
                // Protect operation with external IV
                int[] input = {inputData};
                int[] protectedOutput = new int[1];
                boolean protectSuccess = protector.protect(session, dataElement, input, protectedOutput, ivBytes);
                
                if (protectSuccess) {
                    testContext.getIntProtectedDataMap().put(key, protectedOutput[0]);
                    testContext.getProtectedDataMap().put(key, String.valueOf(protectedOutput[0]));
                    
                    // Unprotect operation with external IV
                    int[] unprotectedOutput = new int[1];
                    boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput, ivBytes);
                    
                    if (unprotectSuccess) {
                        testContext.getIntUnprotectedDataMap().put(key, unprotectedOutput[0]);
                        testContext.getUnprotectedDataMap().put(key, String.valueOf(unprotectedOutput[0]));
                    }
                } else {
                    String error = protector.getLastError(session);
                    testContext.setLastErrorMessage(error);
                    fail("Integer protect operation with EIV failed: " + error);
                }
            } catch (Exception e) {
                testContext.setLastException(e);
                testContext.setOperationResult("exception");
                testContext.setLastErrorMessage(e.getMessage());
                throw e;
            }
        }
    }
    
    @Then("all the integer data is validated")
    public void allIntegerDataIsValidated() {
        Map<String, Integer> originalMap = testContext.getIntOriginalDataMap();
        Map<String, Integer> protectedMap = testContext.getIntProtectedDataMap();
        Map<String, Integer> unprotectedMap = testContext.getIntUnprotectedDataMap();
        
        assertEquals("Number of protected integer items should match original", originalMap.size(), protectedMap.size());
        assertEquals("Number of unprotected integer items should match original", originalMap.size(), unprotectedMap.size());
        
        // All operations should have completed
        for (String key : originalMap.keySet()) {
            assertTrue("Protected integer data should exist for: " + key, protectedMap.containsKey(key));
            assertTrue("Unprotected integer data should exist for: " + key, unprotectedMap.containsKey(key));
        }
    }
    
    @Then("all the bulk integer data is validated")
    public void allBulkIntegerDataIsValidated() {
        Map<String, int[]> originalMap = testContext.getBulkIntOriginalDataMap();
        Map<String, int[]> protectedMap = testContext.getBulkIntProtectedDataMap();
        Map<String, int[]> unprotectedMap = testContext.getBulkIntUnprotectedDataMap();
        
        assertEquals("Number of bulk protected integer items should match original", originalMap.size(), protectedMap.size());
        assertEquals("Number of bulk unprotected integer items should match original", originalMap.size(), unprotectedMap.size());
        
        // All operations should have completed
        for (String key : originalMap.keySet()) {
            assertTrue("Bulk protected integer data should exist for: " + key, protectedMap.containsKey(key));
            assertTrue("Bulk unprotected integer data should exist for: " + key, unprotectedMap.containsKey(key));
            
            int[] original = originalMap.get(key);
            int[] protectedData = protectedMap.get(key);
            int[] unprotectedData = unprotectedMap.get(key);
            
            assertEquals("Array lengths should match for: " + key, original.length, protectedData.length);
            assertEquals("Array lengths should match for: " + key, original.length, unprotectedData.length);
        }
    }
    
    @Then("all the integer data is validated for eiv")
    public void allIntegerDataIsValidatedForEIV() {
        Map<String, String> externalIVMap = testContext.getExternalIVMap();
        assertFalse("External IV data should be present", externalIVMap.isEmpty());
        allIntegerDataIsValidated();
    }

    // Helpers
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) sb.append('0');
            sb.append(hex);
        }
        return sb.toString().toUpperCase();
    }

    private String joinInts(int[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(arr[i]);
        }
        return sb.toString();
    }
}
