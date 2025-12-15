package com.protegrity.ap.integration;

import com.protegrity.ap.java.Protector;
import com.protegrity.ap.java.ProtectorException;
import com.protegrity.ap.java.SessionObject;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Step definitions for date input data protection operations.
 */
public class DateProtectionSteps {
    private final TestContext testContext;
    
    public DateProtectionSteps(TestContext testContext) {
        this.testContext = testContext;
    }
    
    /**
     * Extract date separators from the date string based on its format
     */
    private char[] extractSeparators(String dateStr) {
        // Defensive: Check input length before accessing indices
        char dateSep1 = '-';
        char dateSep2 = '-';
        char msSep = '.';
        if (dateStr.length() > 4) {
            dateSep1 = dateStr.charAt(4);
        }
        // Find second separator after MM (could be '.', '/', or '-')
        int mmStart = 5;
        while (mmStart < dateStr.length() && Character.isDigit(dateStr.charAt(mmStart))) {
            mmStart++;
        }
        if (mmStart < dateStr.length()) {
            dateSep2 = dateStr.charAt(mmStart);
        }
        // Find separator after seconds (could be '.' or ',')
        int msSepIdx = dateStr.indexOf('.') > 0 ? dateStr.indexOf('.') : dateStr.indexOf(',');
        if (msSepIdx > 0 && msSepIdx < dateStr.length()) {
            msSep = dateStr.charAt(msSepIdx);
        }
        if (dateStr.length() > 10) {
            return new char[]{dateSep1, dateSep2, msSep};
        } else {
            return new char[]{dateSep1, dateSep2};
        }
    }
    
    private String determineDatePattern(String dateStr) {
        char[] separators = extractSeparators(dateStr);
        String pattern;

        // Detect time separator (space or 'T')
        boolean hasT = dateStr.contains("T");
        boolean hasSpace = dateStr.contains(" ");
        boolean hasMs = dateStr.contains(".") || dateStr.contains(",");

        String timeSep = hasT ? "'T'" : (hasSpace ? " " : "");

        // Detect ms separator (dot or comma) after seconds
        String msSep = "";
        if (hasMs) {
            // Find separator after seconds
            int secIdx = dateStr.indexOf(":", dateStr.indexOf(":") + 1); // second ':' in time
            int msIdx = secIdx > 0 ? secIdx + 3 : dateStr.length(); // after ss
            // Find first non-digit after ss
            while (msIdx < dateStr.length() && Character.isDigit(dateStr.charAt(msIdx))) {
                msIdx++;
            }
            if (msIdx < dateStr.length() && (dateStr.charAt(msIdx) == '.' || dateStr.charAt(msIdx) == ',')) {
                msSep = String.valueOf(dateStr.charAt(msIdx)) + "SSS";
            } else {
                msSep = ".SSS";
            }
        }

        if (separators.length == 3) {
            // Has time component
            if (hasMs) {
                pattern = "yyyy" + separators[0] + "MM" + separators[1] + "dd" + timeSep + "HH:mm:ss" + msSep;
            } else {
                pattern = "yyyy" + separators[0] + "MM" + separators[1] + "dd" + timeSep + "HH:mm:ss";
            }
        } else {
            // Date only - YMD format
            pattern = "yyyy" + separators[0] + "MM" + separators[1] + "dd";
        }

        return pattern;
    }

    private Date parseDateWithDynamicPattern(String dateStr) throws ParseException {
        String pattern = determineDatePattern(dateStr);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(false);
        return sdf.parse(dateStr);
    }
    
    @When("the user performs {string} operations on single date input data using the following data elements")
    public void performOperationsOnSingleDateData(String operationType, DataTable dataTable) throws ProtectorException {
        Protector protector = testContext.getProtector();
        String currentUser = testContext.getCurrentUser();
        SessionObject session = (currentUser != null && !currentUser.isEmpty()) 
            ? protector.createSession(currentUser)
            : testContext.getSession();

        List<Map<String, String>> rows = dataTable.asMaps();

        for (Map<String, String> row : rows) {
            String dataElement = row.get("data_element");
            String inputDataStr = row.get("input_data");

            try {
                Date inputData = parseDateWithDynamicPattern(inputDataStr);
                String pattern = determineDatePattern(inputDataStr);
                SimpleDateFormat formatter = new SimpleDateFormat(pattern);

                String key = dataElement + "_" + inputDataStr;
                testContext.getDateOriginalDataMap().put(key, inputData);

                try {
                    // For tokenization operations, use Date[] input and output
                    Date[] input = {inputData};
                    Date[] protectedOutput = new Date[1];
                    boolean protectSuccess = protector.protect(session, dataElement, input, protectedOutput);

                    if (protectSuccess) {
                        // Update protected data map
                        testContext.getDateProtectedDataMap().put(key, protectedOutput[0]);

                        // Unprotect operation
                        Date[] unprotectedOutput = new Date[1];
                        boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput);

                        if (unprotectSuccess) {
                            testContext.getDateUnprotectedDataMap().put(key, unprotectedOutput[0]);
                        }
                    } else {
                        String error = protector.getLastError(session);
                        testContext.setLastErrorMessage(error);
                        fail("Date protect operation failed: " + error);
                    }
                } catch (Exception e) {
                    testContext.setLastException(e);
                    testContext.setOperationResult("exception");
                    testContext.setLastErrorMessage(e.getMessage());
                    throw e;
                }
            } catch (ParseException e) {
                testContext.setLastException(e);
                testContext.setOperationResult("exception");
                testContext.setLastErrorMessage("Failed to parse date: " + inputDataStr + " - " + e.getMessage());
                throw new ProtectorException("Failed to parse date: " + inputDataStr, e);
            }
        }
    }

    @When("the user performs {string} operations on bulk date input data using the following data elements")
    public void performOperationsOnBulkDateData(String operationType, DataTable dataTable) throws ProtectorException {
        Protector protector = testContext.getProtector();
        String currentUser = testContext.getCurrentUser();
        SessionObject session = (currentUser != null && !currentUser.isEmpty()) 
            ? protector.createSession(currentUser)
            : testContext.getSession();

        List<Map<String, String>> rows = dataTable.asMaps();

        for (Map<String, String> row : rows) {
            String dataElement = row.get("data_element");
            String inputDataStr = row.get("input_data");

            try {
                // Parse semicolon-separated values (bulk separator)
                String[] parts = Arrays.stream(inputDataStr.split(";")).map(String::trim).toArray(String[]::new);
                Date[] inputData = new Date[parts.length];

                for (int i = 0; i < parts.length; i++) {
                    String part = parts[i].trim();
                    try {
                        inputData[i] = parseDateWithDynamicPattern(part);
                    } catch (ParseException pe) {
                        throw pe;
                    }
                }

                String key = dataElement + "_" + inputDataStr;
                testContext.getBulkDateOriginalDataMap().put(key, inputData);

                try {
                    // For tokenization operations, use Date[] input and output
                    Date[] protectedOutput = new Date[inputData.length];
                    boolean protectSuccess = protector.protect(session, dataElement, inputData, protectedOutput);

                    if (protectSuccess) {
                        // Update protected data map
                        testContext.getBulkDateProtectedDataMap().put(key, protectedOutput);

                        // Unprotect operation
                        Date[] unprotectedOutput = new Date[inputData.length];
                        boolean unprotectSuccess = protector.unprotect(session, dataElement, protectedOutput, unprotectedOutput);

                        if (unprotectSuccess) {
                            testContext.getBulkDateUnprotectedDataMap().put(key, unprotectedOutput);
                        }
                    } else {
                        String error = protector.getLastError(session);
                        testContext.setLastErrorMessage(error);
                        fail("Bulk date protect operation failed: " + error);
                    }
                } catch (Exception e) {
                    testContext.setLastException(e);
                    testContext.setOperationResult("exception");
                    testContext.setLastErrorMessage(e.getMessage());
                    throw e;
                }
            } catch (ParseException e) {
                testContext.setLastException(e);
                testContext.setOperationResult("exception");
                testContext.setLastErrorMessage("Failed to parse date in bulk data: " + inputDataStr + " - " + e.getMessage());
                throw new ProtectorException("Failed to parse date in bulk data: " + inputDataStr, e);
            }
        }
    }
    
    @Then("all the date data is validated")
    public void allDateDataIsValidated() {
        Map<String, Date> originalMap = testContext.getDateOriginalDataMap();
        Map<String, Date> protectedMap = testContext.getDateProtectedDataMap();
        Map<String, Date> unprotectedMap = testContext.getDateUnprotectedDataMap();

        assertEquals("Number of protected date items should match original", originalMap.size(), protectedMap.size());
        assertEquals("Number of unprotected date items should match original", originalMap.size(), unprotectedMap.size());

        // All operations should have completed
        for (String key : originalMap.keySet()) {
            assertTrue("Protected date data should exist for: " + key, protectedMap.containsKey(key));
            assertTrue("Unprotected date data should exist for: " + key, unprotectedMap.containsKey(key));

            Date original = originalMap.get(key);
            Date unprotected = unprotectedMap.get(key);

            // Retrieve the original input string from the key
            String[] keyParts = key.split("_");
            String inputString = keyParts[keyParts.length - 1];
            String pattern = null;
            try {
                pattern = determineDatePattern(inputString);
                SimpleDateFormat formatter = new SimpleDateFormat(pattern);
                String originalFormatted = formatter.format(original);
                String unprotectedFormatted = formatter.format(unprotected);
                if (!originalFormatted.equals(unprotectedFormatted)) {
                    fail("Unprotected data does not match original for key: " + key + ".\nExpected: " + originalFormatted + "\nActual: " + unprotectedFormatted);
                }
            } catch (IllegalArgumentException ex) {
                fail("Validation error for key: " + key + ". Could not format using pattern: '" + pattern + "'.\nError: " + ex.getMessage());
            }
        }
    }
    
    @Then("all the bulk date data is validated")
    public void allBulkDateDataIsValidated() {
        Map<String, Date[]> originalMap = testContext.getBulkDateOriginalDataMap();
        Map<String, Date[]> protectedMap = testContext.getBulkDateProtectedDataMap();
        Map<String, Date[]> unprotectedMap = testContext.getBulkDateUnprotectedDataMap();

        assertEquals("Number of bulk protected date items should match original", originalMap.size(), protectedMap.size());
        assertEquals("Number of bulk unprotected date items should match original", originalMap.size(), unprotectedMap.size());

        // All operations should have completed
        for (String key : originalMap.keySet()) {
            assertTrue("Bulk protected date data should exist for: " + key, protectedMap.containsKey(key));
            assertTrue("Bulk unprotected date data should exist for: " + key, unprotectedMap.containsKey(key));

            Date[] original = originalMap.get(key);
            Date[] unprotected = unprotectedMap.get(key);

            assertEquals("Array lengths should match for: " + key, original.length, unprotected.length);

            // Retrieve the original input string from the key
            String[] keyParts = key.split("_");
            String inputString = keyParts[keyParts.length - 1];
            String[] originalInputs = Arrays.stream(inputString.split(";")).map(String::trim).toArray(String[]::new);
                String[] expectedArray = originalInputs;
                String[] actualArray = new String[unprotected.length];
                for (int i = 0; i < unprotected.length; i++) {
                    String pattern = determineDatePattern(originalInputs[i]);
                    SimpleDateFormat formatter = new SimpleDateFormat(pattern);
                    actualArray[i] = formatter.format(unprotected[i]);
                }
                if (!Arrays.equals(expectedArray, actualArray)) {
                    fail("Bulk unprotected date should equal original for: " + key +
                        "\nExpected: " + Arrays.toString(expectedArray) +
                        "\nActual:   " + Arrays.toString(actualArray));
                }
        }
    }
}
