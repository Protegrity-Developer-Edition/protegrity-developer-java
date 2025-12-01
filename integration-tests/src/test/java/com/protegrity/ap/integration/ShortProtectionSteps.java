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
 * Step definitions for short input data protection operations.
 */
public class ShortProtectionSteps {
    private final TestContext testContext;
    
    public ShortProtectionSteps(TestContext testContext) {
        this.testContext = testContext;
    }
    
    @When("the user performs {string} operations on single short input data using the following data elements")
    public void performOperationsOnSingleShortData(String operationType, DataTable dataTable) throws ProtectorException {
        Protector protector = testContext.getProtector();
        String currentUser = testContext.getCurrentUser();
        SessionObject session = (currentUser != null && !currentUser.isEmpty()) 
            ? protector.createSession(currentUser)
            : testContext.getSession();
        
        List<Map<String, String>> rows = dataTable.asMaps();
        
        for (Map<String, String> row : rows) {
            String dataElement = row.get("data_element");
            String inputDataStr = row.get("input_data");
            short inputData = Short.parseShort(inputDataStr);
            
            String key = dataElement + "_" + inputData;
            testContext.getShortOriginalDataMap().put(key, inputData);
            
            try {
                // For encryption operations with text data element, use short[] input with byte[][] output
                if ("encryption".equalsIgnoreCase(operationType)) {
                    short[] input = {inputData};
                    byte[][] protectedOutput = new byte[1][];
                    
                    boolean protectSuccess = protector.protect(session, dataElement, input, protectedOutput);
                    
                    if (protectSuccess && protectedOutput[0] != null) {
                        // Store the length as a placeholder in the short map for validation
                        testContext.getShortProtectedDataMap().put(key, (short) protectedOutput[0].length);
                        
                        // Unprotect operation: byte[][] input with short[] output
                        short[] unprotectedOutput = new short[1];
                        boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput);
                        
                        if (unprotectSuccess) {
                            testContext.getShortUnprotectedDataMap().put(key, unprotectedOutput[0]);
                        } else {
                            String error = protector.getLastError(session);
                            testContext.setLastErrorMessage(error);
                        }
                    } else {
                        String error = protector.getLastError(session);
                        testContext.setLastErrorMessage(error);
                    }
                } else {
                    // For tokenization operations, use short[] input and output
                    short[] input = {inputData};
                    short[] protectedOutput = new short[1];
                    boolean protectSuccess = protector.protect(session, dataElement, input, protectedOutput);
                    
                    if (protectSuccess) {
                        testContext.getShortProtectedDataMap().put(key, protectedOutput[0]);
                        
                        // Unprotect operation
                        short[] unprotectedOutput = new short[1];
                        boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput);
                        
                        if (unprotectSuccess) {
                            testContext.getShortUnprotectedDataMap().put(key, unprotectedOutput[0]);
                        }
                    } else {
                        String error = protector.getLastError(session);
                        testContext.setLastErrorMessage(error);
                        fail("Short protect operation failed: " + error);
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
    
    @When("the user performs {string} operations on bulk short input data using the following data elements")
    public void performOperationsOnBulkShortData(String operationType, DataTable dataTable) throws ProtectorException {
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
            short[] inputData = new short[parts.length];
            for (int i = 0; i < parts.length; i++) {
                inputData[i] = Short.parseShort(parts[i].trim());
            }
            
            String key = dataElement + "_bulk";
            testContext.getBulkShortOriginalDataMap().put(key, inputData);
            
            try {
                // For encryption operations with text data element, use short[] input with byte[][] output
                if ("encryption".equalsIgnoreCase(operationType)) {
                    byte[][] protectedOutput = new byte[inputData.length][];
                    
                    boolean protectSuccess = protector.protect(session, dataElement, inputData, protectedOutput);
                    
                    if (protectSuccess) {
                        // Store lengths as placeholders in the short array map for validation
                        short[] lengthArray = new short[inputData.length];
                        for (int i = 0; i < protectedOutput.length; i++) {
                            lengthArray[i] = protectedOutput[i] != null ? (short) protectedOutput[i].length : 0;
                        }
                        testContext.getBulkShortProtectedDataMap().put(key, lengthArray);
                        
                        // Unprotect operation: byte[][] input with short[] output
                        short[] unprotectedOutput = new short[inputData.length];
                        boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput);
                        
                        if (unprotectSuccess) {
                            testContext.getBulkShortUnprotectedDataMap().put(key, unprotectedOutput);
                        }
                    } else {
                        String error = protector.getLastError(session);
                        testContext.setLastErrorMessage(error);
                        fail("Bulk short protect operation failed: " + error);
                    }
                } else {
                    // For tokenization operations, use short[] input and output
                    short[] protectedOutput = new short[inputData.length];
                    boolean protectSuccess = protector.protect(session, dataElement, inputData, protectedOutput);
                    
                    if (protectSuccess) {
                        testContext.getBulkShortProtectedDataMap().put(key, protectedOutput);
                        
                        // Unprotect operation
                        short[] unprotectedOutput = new short[inputData.length];
                        boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput);
                        
                        if (unprotectSuccess) {
                            testContext.getBulkShortUnprotectedDataMap().put(key, unprotectedOutput);
                        }
                    } else {
                        String error = protector.getLastError(session);
                        testContext.setLastErrorMessage(error);
                        fail("Bulk short protect operation failed: " + error);
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
    
    @When("the user performs {string} operations on single short input data with external IV using the following data elements")
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
            short inputData = Short.parseShort(inputDataStr);
            
            String key = dataElement + "_" + inputData + "_" + externalIV;
            testContext.getShortOriginalDataMap().put(key, inputData);
            testContext.getExternalIVMap().put(key, externalIV);
            
            try {
                // Convert external IV to bytes
                byte[] ivBytes = externalIV.getBytes(StandardCharsets.UTF_8);
                
                // Protect operation with external IV
                short[] input = {inputData};
                short[] protectedOutput = new short[1];
                boolean protectSuccess = protector.protect(session, dataElement, input, protectedOutput, ivBytes);
                
                if (protectSuccess) {
                    testContext.getShortProtectedDataMap().put(key, protectedOutput[0]);
                    
                    // Unprotect operation with external IV
                    short[] unprotectedOutput = new short[1];
                    boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput, ivBytes);
                    
                    if (unprotectSuccess) {
                        testContext.getShortUnprotectedDataMap().put(key, unprotectedOutput[0]);
                    }
                } else {
                    String error = protector.getLastError(session);
                    testContext.setLastErrorMessage(error);
                    fail("Short protect operation with EIV failed: " + error);
                }
            } catch (Exception e) {
                testContext.setLastException(e);
                testContext.setOperationResult("exception");
                testContext.setLastErrorMessage(e.getMessage());
                throw e;
            }
        }
    }
    
    @Then("all the short data is validated")
    public void allShortDataIsValidated() {
        Map<String, Short> originalMap = testContext.getShortOriginalDataMap();
        Map<String, Short> protectedMap = testContext.getShortProtectedDataMap();
        Map<String, Short> unprotectedMap = testContext.getShortUnprotectedDataMap();
        
        assertEquals("Number of protected short items should match original", originalMap.size(), protectedMap.size());
        assertEquals("Number of unprotected short items should match original", originalMap.size(), unprotectedMap.size());
        
        // All operations should have completed
        for (String key : originalMap.keySet()) {
            assertTrue("Protected short data should exist for: " + key, protectedMap.containsKey(key));
            assertTrue("Unprotected short data should exist for: " + key, unprotectedMap.containsKey(key));
        }
    }
    
    @Then("all the bulk short data is validated")
    public void allBulkShortDataIsValidated() {
        Map<String, short[]> originalMap = testContext.getBulkShortOriginalDataMap();
        Map<String, short[]> protectedMap = testContext.getBulkShortProtectedDataMap();
        Map<String, short[]> unprotectedMap = testContext.getBulkShortUnprotectedDataMap();
        
        assertEquals("Number of bulk protected short items should match original", originalMap.size(), protectedMap.size());
        assertEquals("Number of bulk unprotected short items should match original", originalMap.size(), unprotectedMap.size());
        
        // All operations should have completed
        for (String key : originalMap.keySet()) {
            assertTrue("Bulk protected short data should exist for: " + key, protectedMap.containsKey(key));
            assertTrue("Bulk unprotected short data should exist for: " + key, unprotectedMap.containsKey(key));
            
            short[] original = originalMap.get(key);
            short[] protectedData = protectedMap.get(key);
            short[] unprotectedData = unprotectedMap.get(key);
            
            assertEquals("Array lengths should match for: " + key, original.length, protectedData.length);
            assertEquals("Array lengths should match for: " + key, original.length, unprotectedData.length);
        }
    }
    
    @Then("all the short data is validated for eiv")
    public void allShortDataIsValidatedForEIV() {
        Map<String, String> externalIVMap = testContext.getExternalIVMap();
        assertFalse("External IV data should be present", externalIVMap.isEmpty());
        allShortDataIsValidated();
    }
}
