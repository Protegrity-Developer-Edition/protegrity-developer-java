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
 * Step definitions for char input data protection operations.
 */
public class CharProtectionSteps {
    private final TestContext testContext;
    
    public CharProtectionSteps(TestContext testContext) {
        this.testContext = testContext;
    }
    
    @When("the user performs {string} operations on single char input data using the following data elements")
    public void performOperationsOnSingleCharData(String operationType, DataTable dataTable) throws ProtectorException {
        Protector protector = testContext.getProtector();
        String currentUser = testContext.getCurrentUser();
        SessionObject session = (currentUser != null && !currentUser.isEmpty()) 
            ? protector.createSession(currentUser)
            : testContext.getSession();
        
        List<Map<String, String>> rows = dataTable.asMaps();
        
        for (Map<String, String> row : rows) {
            String dataElement = row.get("data_element");
            String inputData = row.get("input_data");
            
            String key = dataElement + "_" + inputData;
            // Store original as string in the string map since we're working with string data
            testContext.getOriginalDataMap().put(key, inputData);
            
            try {
                // Convert string to char array
                char[] inputChars = inputData.toCharArray();
                
                // For encryption operations, use byte array methods
                if ("encryption".equalsIgnoreCase(operationType)) {
                    char[][] input = {inputChars};
                    byte[][] protectedOutput = new byte[1][];
                    boolean protectSuccess = protector.protect(session, dataElement, input, protectedOutput);
                    
                    if (protectSuccess && protectedOutput[0] != null) {
                        // Convert to hex string for storage
                        testContext.getProtectedDataMap().put(key, bytesToHex(protectedOutput[0]));
                        
                        // Unprotect operation
                        char[][] unprotectedOutput = new char[1][];
                        boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput);
                        
                        if (unprotectSuccess && unprotectedOutput[0] != null) {
                            testContext.getUnprotectedDataMap().put(key, new String(unprotectedOutput[0]));
                        } else {
                            String error = protector.getLastError(session);
                            testContext.setLastErrorMessage(error);
                        }
                    } else {
                        String error = protector.getLastError(session);
                        testContext.setLastErrorMessage(error);
                    }
                } else {
                    // For tokenization operations, use char[][] array methods
                    char[][] input = {inputChars};
                    char[][] protectedOutput = new char[1][];
                    boolean protectSuccess = protector.protect(session, dataElement, input, protectedOutput);
                    
                    if (protectSuccess && protectedOutput[0] != null) {
                        testContext.getProtectedDataMap().put(key, new String(protectedOutput[0]));
                        
                        // Unprotect operation
                        char[][] unprotectedOutput = new char[1][];
                        boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput);
                        
                        if (unprotectSuccess && unprotectedOutput[0] != null) {
                            testContext.getUnprotectedDataMap().put(key, new String(unprotectedOutput[0]));
                        }
                    } else {
                        String error = protector.getLastError(session);
                        testContext.setLastErrorMessage(error);
                    }
                }
            } catch (Exception e) {
                testContext.setLastException(e);
                testContext.setOperationResult("exception");
                testContext.setLastErrorMessage(e.getMessage());
                fail("Exception during single char operation: " + e.getMessage());
                throw e;
            }
        }
    }
    
    // Helper method to convert byte array to hex string
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString().toUpperCase();
    }
    
    @When("the user performs {string} operations on bulk char input data using the following data elements")
    public void performOperationsOnBulkCharData(String operationType, DataTable dataTable) throws ProtectorException {
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
            String[] inputData = inputDataStr.split(",");
            for (int i = 0; i < inputData.length; i++) {
                inputData[i] = inputData[i].trim();
            }
            
            String key = dataElement + "_bulk";
            testContext.getBulkOriginalDataMap().put(key, inputData);
            
            try {
                // For encryption operations, use byte array methods
                if ("encryption".equalsIgnoreCase(operationType)) {
                    char[][] inputChars = new char[inputData.length][];
                    for (int i = 0; i < inputData.length; i++) {
                        inputChars[i] = inputData[i].toCharArray();
                    }
                    
                    byte[][] protectedOutput = new byte[inputData.length][];
                    boolean protectSuccess = protector.protect(session, dataElement, inputChars, protectedOutput);
                    
                    if (protectSuccess) {
                        // Convert to string array for storage (as hex strings)
                        String[] hexProtectedArray = new String[inputData.length];
                        for (int i = 0; i < protectedOutput.length; i++) {
                            hexProtectedArray[i] = protectedOutput[i] != null ? bytesToHex(protectedOutput[i]) : null;
                        }
                        testContext.getBulkProtectedDataMap().put(key, hexProtectedArray);
                        
                        // Unprotect operation
                        char[][] unprotectedOutput = new char[inputData.length][];
                        boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput);
                        
                        if (unprotectSuccess) {
                            String[] unprotectedStrings = new String[inputData.length];
                            for (int i = 0; i < unprotectedOutput.length; i++) {
                                unprotectedStrings[i] = unprotectedOutput[i] != null ? new String(unprotectedOutput[i]) : null;
                            }
                            testContext.getBulkUnprotectedDataMap().put(key, unprotectedStrings);
                        }
                    } else {
                        String error = protector.getLastError(session);
                        testContext.setLastErrorMessage(error);
                        fail("Bulk char protect operation failed: " + error);
                    }
                } else {
                    // For tokenization operations, use char[][] array methods
                    char[][] inputChars = new char[inputData.length][];
                    for (int i = 0; i < inputData.length; i++) {
                        inputChars[i] = inputData[i].toCharArray();
                    }
                    
                    char[][] protectedOutput = new char[inputData.length][];
                    boolean protectSuccess = protector.protect(session, dataElement, inputChars, protectedOutput);
                    
                    if (protectSuccess) {
                        // Convert char arrays to strings
                        String[] protectedStrings = new String[inputData.length];
                        for (int i = 0; i < protectedOutput.length; i++) {
                            protectedStrings[i] = protectedOutput[i] != null ? new String(protectedOutput[i]) : null;
                        }
                        testContext.getBulkProtectedDataMap().put(key, protectedStrings);
                        
                        // Unprotect operation
                        char[][] unprotectedOutput = new char[inputData.length][];
                        boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput);
                        
                        if (unprotectSuccess) {
                            String[] unprotectedStrings = new String[inputData.length];
                            for (int i = 0; i < unprotectedOutput.length; i++) {
                                unprotectedStrings[i] = unprotectedOutput[i] != null ? new String(unprotectedOutput[i]) : null;
                            }
                            testContext.getBulkUnprotectedDataMap().put(key, unprotectedStrings);
                        }
                    } else {
                        String error = protector.getLastError(session);
                        testContext.setLastErrorMessage(error);
                        fail("Bulk char protect operation failed: " + error);
                    }
                }
            } catch (Exception e) {
                testContext.setLastException(e);
                testContext.setOperationResult("exception");
                testContext.setLastErrorMessage(e.getMessage());
                fail("Exception during bulk char operation: " + e.getMessage());
                throw e;
            }
        }
    }
    
    @When("the user performs {string} operations on single char input data with external IV using the following data elements")
    public void performOperationsWithExternalIV(String operationType, DataTable dataTable) throws ProtectorException {
        Protector protector = testContext.getProtector();
        String currentUser = testContext.getCurrentUser();
        SessionObject session = (currentUser != null && !currentUser.isEmpty()) 
            ? protector.createSession(currentUser)
            : testContext.getSession();
        
        List<Map<String, String>> rows = dataTable.asMaps();
        
        for (Map<String, String> row : rows) {
            String dataElement = row.get("data_element");
            String inputData = row.get("input_data");
            String externalIV = row.get("external_iv");
            
            String key = dataElement + "_" + inputData + "_" + externalIV;
            testContext.getOriginalDataMap().put(key, inputData);
            testContext.getExternalIVMap().put(key, externalIV);
            
            try {
                // Convert external IV to bytes
                byte[] ivBytes = externalIV.getBytes(StandardCharsets.UTF_8);
                
                // Convert string to char array
                char[] inputChars = inputData.toCharArray();
                char[][] input = {inputChars};
                
                // Protect operation with external IV
                char[][] protectedOutput = new char[1][];
                boolean protectSuccess = protector.protect(session, dataElement, input, protectedOutput, ivBytes);
                
                if (protectSuccess && protectedOutput[0] != null) {
                    testContext.getProtectedDataMap().put(key, new String(protectedOutput[0]));
                    
                    // Unprotect operation with external IV
                    char[][] unprotectedOutput = new char[1][];
                    boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput, ivBytes);
                    
                    if (unprotectSuccess && unprotectedOutput[0] != null) {
                        testContext.getUnprotectedDataMap().put(key, new String(unprotectedOutput[0]));
                    }
                } else {
                    String error = protector.getLastError(session);
                    testContext.setLastErrorMessage(error);
                    fail("Char protect operation with EIV failed: " + error);
                }
            } catch (Exception e) {
                testContext.setLastException(e);
                testContext.setOperationResult("exception");
                testContext.setLastErrorMessage(e.getMessage());
                fail("Exception during single char operation with EIV: " + e.getMessage());
                throw e;
            }
        }
    }
    
    @When("the user performs {string} operations on bulk char input data with external IV using the following data elements")
    public void performOperationsOnBulkCharDataWithExternalIV(String operationType, DataTable dataTable) throws ProtectorException {
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
            
            // Parse comma-separated values
            String[] inputData = inputDataStr.split(",");
            for (int i = 0; i < inputData.length; i++) {
                inputData[i] = inputData[i].trim();
            }
            
            String key = dataElement + "_bulk_" + externalIV;
            testContext.getBulkOriginalDataMap().put(key, inputData);
            testContext.getExternalIVMap().put(key, externalIV);
            
            try {
                // Convert external IV to bytes
                byte[] ivBytes = externalIV.getBytes(StandardCharsets.UTF_8);
                
                // Convert each string to char array
                char[][] inputChars = new char[inputData.length][];
                for (int i = 0; i < inputData.length; i++) {
                    inputChars[i] = inputData[i].toCharArray();
                }
                
                // Protect operation with external IV
                char[][] protectedOutput = new char[inputData.length][];
                boolean protectSuccess = protector.protect(session, dataElement, inputChars, protectedOutput, ivBytes);
                
                if (protectSuccess) {
                    // Convert char arrays to strings
                    String[] protectedStrings = new String[inputData.length];
                    for (int i = 0; i < protectedOutput.length; i++) {
                        protectedStrings[i] = protectedOutput[i] != null ? new String(protectedOutput[i]) : null;
                    }
                    testContext.getBulkProtectedDataMap().put(key, protectedStrings);
                    
                    // Unprotect operation with external IV
                    char[][] unprotectedOutput = new char[inputData.length][];
                    boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput, ivBytes);
                    
                    if (unprotectSuccess) {
                        String[] unprotectedStrings = new String[inputData.length];
                        for (int i = 0; i < unprotectedOutput.length; i++) {
                            unprotectedStrings[i] = unprotectedOutput[i] != null ? new String(unprotectedOutput[i]) : null;
                        }
                        testContext.getBulkUnprotectedDataMap().put(key, unprotectedStrings);
                    }
                } else {
                    String error = protector.getLastError(session);
                    testContext.setLastErrorMessage(error);
                    fail("Bulk char protect operation with EIV failed: " + error);
                }
            } catch (Exception e) {
                testContext.setLastException(e);
                testContext.setOperationResult("exception");
                testContext.setLastErrorMessage(e.getMessage());
                fail("Exception during bulk char operation with EIV: " + e.getMessage());
                throw e;
            }
        }
    }
}
