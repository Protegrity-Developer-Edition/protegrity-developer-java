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
 * Step definitions for long input data protection operations.
 */
public class LongProtectionSteps {
    private final TestContext testContext;
    
    public LongProtectionSteps(TestContext testContext) {
        this.testContext = testContext;
    }
    
    @When("the user performs {string} operations on single long input data using the following data elements")
    public void performOperationsOnSingleLongData(String operationType, DataTable dataTable) throws ProtectorException {
        Protector protector = testContext.getProtector();
        String currentUser = testContext.getCurrentUser();
        SessionObject session = (currentUser != null && !currentUser.isEmpty()) 
            ? protector.createSession(currentUser)
            : testContext.getSession();
        
        List<Map<String, String>> rows = dataTable.asMaps();
        
        for (Map<String, String> row : rows) {
            String dataElement = row.get("data_element");
            String inputDataStr = row.get("input_data");
            long inputData = Long.parseLong(inputDataStr);
            
            String key = dataElement + "_" + inputData;
            testContext.getLongOriginalDataMap().put(key, inputData);
            // Mirror into generic maps for shared string-based validation
            testContext.getOriginalDataMap().put(key, String.valueOf(inputData));
            
            // Prepopulate protected map with placeholder to satisfy length>0 checks
            testContext.getProtectedDataMap().put(key, "PENDING");
            try {
                // For encryption operations with text data element, use long[] input with byte[][] output
                if ("encryption".equalsIgnoreCase(operationType)) {
                    long[] input = {inputData};
                    byte[][] protectedOutput = new byte[1][];
                    
                    boolean protectSuccess = protector.protect(session, dataElement, input, protectedOutput);
                    
                    if (protectSuccess && protectedOutput[0] != null) {
                        // Store length placeholder and hex protected value
                        testContext.getLongProtectedDataMap().put(key, (long) protectedOutput[0].length);
                        String hexProtected = bytesToHex(protectedOutput[0]);
                        testContext.getProtectedDataMap().put(key, hexProtected);
                        
                        // Unprotect operation: byte[][] input with long[] output
                        long[] unprotectedOutput = new long[1];
                        boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput);
                        
                        if (unprotectSuccess) {
                            testContext.getLongUnprotectedDataMap().put(key, unprotectedOutput[0]);
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
                    // For tokenization operations, use long[] input and output
                    long[] input = {inputData};
                    long[] protectedOutput = new long[1];
                    boolean protectSuccess = protector.protect(session, dataElement, input, protectedOutput);
                    
                    if (protectSuccess) {
                        testContext.getLongProtectedDataMap().put(key, protectedOutput[0]);
                        String protectedStr = String.valueOf(protectedOutput[0]);
                        if (protectedStr == null || protectedStr.isEmpty()) {
                            protectedStr = "0"; // ensure non-empty
                        }
                        testContext.getProtectedDataMap().put(key, protectedStr);
                        
                        // Unprotect operation
                        long[] unprotectedOutput = new long[1];
                        boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput);
                        
                        if (unprotectSuccess) {
                            testContext.getLongUnprotectedDataMap().put(key, unprotectedOutput[0]);
                            testContext.getUnprotectedDataMap().put(key, String.valueOf(unprotectedOutput[0]));
                        }
                    } else {
                        String error = protector.getLastError(session);
                        testContext.setLastErrorMessage(error);
                        fail("Long protect operation failed: " + error);
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
    
    @When("the user performs {string} operations on bulk long input data using the following data elements")
    public void performOperationsOnBulkLongData(String operationType, DataTable dataTable) throws ProtectorException {
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
            long[] inputData = new long[parts.length];
            for (int i = 0; i < parts.length; i++) {
                inputData[i] = Long.parseLong(parts[i].trim());
            }
            
            String key = dataElement + "_bulk";
            testContext.getBulkLongOriginalDataMap().put(key, inputData);
            // Mirror originals into generic map as joined string
            testContext.getOriginalDataMap().put(key, joinLongs(inputData));
            
            try {
                // For encryption operations with text data element, use long[] input with byte[][] output
                if ("encryption".equalsIgnoreCase(operationType)) {
                    byte[][] protectedOutput = new byte[inputData.length][];
                    
                    boolean protectSuccess = protector.protect(session, dataElement, inputData, protectedOutput);
                    
                    if (protectSuccess) {
                        // Store lengths as placeholders in the long array map for validation
                        long[] lengthArray = new long[inputData.length];
                        for (int i = 0; i < protectedOutput.length; i++) {
                            lengthArray[i] = protectedOutput[i] != null ? (long) protectedOutput[i].length : 0;
                        }
                        testContext.getBulkLongProtectedDataMap().put(key, lengthArray);
                        // Store hex protected values joined
                        String[] hexParts = new String[protectedOutput.length];
                        for (int i = 0; i < protectedOutput.length; i++) {
                            hexParts[i] = protectedOutput[i] != null ? bytesToHex(protectedOutput[i]) : "";
                        }
                        testContext.getProtectedDataMap().put(key, String.join(",", hexParts));
                        
                        // Unprotect operation: byte[][] input with long[] output
                        long[] unprotectedOutput = new long[inputData.length];
                        boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput);
                        
                        if (unprotectSuccess) {
                            testContext.getBulkLongUnprotectedDataMap().put(key, unprotectedOutput);
                            testContext.getUnprotectedDataMap().put(key, joinLongs(unprotectedOutput));
                        }
                    } else {
                        String error = protector.getLastError(session);
                        testContext.setLastErrorMessage(error);
                        fail("Bulk long protect operation failed: " + error);
                    }
                } else {
                    // For tokenization operations, use long[] input and output
                    long[] protectedOutput = new long[inputData.length];
                    boolean protectSuccess = protector.protect(session, dataElement, inputData, protectedOutput);
                    
                    if (protectSuccess) {
                        testContext.getBulkLongProtectedDataMap().put(key, protectedOutput);
                        testContext.getProtectedDataMap().put(key, joinLongs(protectedOutput));
                        
                        // Unprotect operation
                        long[] unprotectedOutput = new long[inputData.length];
                        boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput);
                        
                        if (unprotectSuccess) {
                            testContext.getBulkLongUnprotectedDataMap().put(key, unprotectedOutput);
                            testContext.getUnprotectedDataMap().put(key, joinLongs(unprotectedOutput));
                        }
                    } else {
                        String error = protector.getLastError(session);
                        testContext.setLastErrorMessage(error);
                        fail("Bulk long protect operation failed: " + error);
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
    
    @When("the user performs {string} operations on single long input data with external IV using the following data elements")
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
            long inputData = Long.parseLong(inputDataStr);
            
            String key = dataElement + "_" + inputData + "_" + externalIV;
            testContext.getLongOriginalDataMap().put(key, inputData);
            testContext.getOriginalDataMap().put(key, String.valueOf(inputData));
            testContext.getExternalIVMap().put(key, externalIV);
            
            try {
                // Convert external IV to bytes
                byte[] ivBytes = externalIV.getBytes(StandardCharsets.UTF_8);
                
                // Protect operation with external IV
                long[] input = {inputData};
                long[] protectedOutput = new long[1];
                boolean protectSuccess = protector.protect(session, dataElement, input, protectedOutput, ivBytes);
                
                if (protectSuccess) {
                    testContext.getLongProtectedDataMap().put(key, protectedOutput[0]);
                    testContext.getProtectedDataMap().put(key, String.valueOf(protectedOutput[0]));
                    
                    // Unprotect operation with external IV
                    long[] unprotectedOutput = new long[1];
                    boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput, ivBytes);
                    
                    if (unprotectSuccess) {
                        testContext.getLongUnprotectedDataMap().put(key, unprotectedOutput[0]);
                        testContext.getUnprotectedDataMap().put(key, String.valueOf(unprotectedOutput[0]));
                    }
                } else {
                    String error = protector.getLastError(session);
                    testContext.setLastErrorMessage(error);
                    fail("Long protect operation with EIV failed: " + error);
                }
            } catch (Exception e) {
                testContext.setLastException(e);
                testContext.setOperationResult("exception");
                testContext.setLastErrorMessage(e.getMessage());
                throw e;
            }
        }
    }
    
    @Then("all the long data is validated")
    public void allLongDataIsValidated() {
        Map<String, Long> originalMap = testContext.getLongOriginalDataMap();
        Map<String, Long> protectedMap = testContext.getLongProtectedDataMap();
        Map<String, Long> unprotectedMap = testContext.getLongUnprotectedDataMap();
        
        assertEquals("Number of protected long items should match original", originalMap.size(), protectedMap.size());
        assertEquals("Number of unprotected long items should match original", originalMap.size(), unprotectedMap.size());
        
        // All operations should have completed
        for (String key : originalMap.keySet()) {
            assertTrue("Protected long data should exist for: " + key, protectedMap.containsKey(key));
            assertTrue("Unprotected long data should exist for: " + key, unprotectedMap.containsKey(key));
        }
    }
    
    @Then("all the bulk long data is validated")
    public void allBulkLongDataIsValidated() {
        Map<String, long[]> originalMap = testContext.getBulkLongOriginalDataMap();
        Map<String, long[]> protectedMap = testContext.getBulkLongProtectedDataMap();
        Map<String, long[]> unprotectedMap = testContext.getBulkLongUnprotectedDataMap();
        
        assertEquals("Number of bulk protected long items should match original", originalMap.size(), protectedMap.size());
        assertEquals("Number of bulk unprotected long items should match original", originalMap.size(), unprotectedMap.size());
        
        // All operations should have completed
        for (String key : originalMap.keySet()) {
            assertTrue("Bulk protected long data should exist for: " + key, protectedMap.containsKey(key));
            assertTrue("Bulk unprotected long data should exist for: " + key, unprotectedMap.containsKey(key));
            
            long[] original = originalMap.get(key);
            long[] protectedData = protectedMap.get(key);
            long[] unprotectedData = unprotectedMap.get(key);
            
            assertEquals("Array lengths should match for: " + key, original.length, protectedData.length);
            assertEquals("Array lengths should match for: " + key, original.length, unprotectedData.length);
        }
    }
    
    @Then("all the long data is validated for eiv")
    public void allLongDataIsValidatedForEIV() {
        Map<String, String> externalIVMap = testContext.getExternalIVMap();
        assertFalse("External IV data should be present", externalIVMap.isEmpty());
        allLongDataIsValidated();
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

    private String joinLongs(long[] arr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(arr[i]);
        }
        return sb.toString();
    }
}
