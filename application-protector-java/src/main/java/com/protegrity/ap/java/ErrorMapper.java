package com.protegrity.ap.java;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps error messages to standardized error codes and descriptions.
 * 
 * <p>This class maintains a comprehensive mapping of error messages from the
 * Protegrity protection services to standardized error codes and descriptions
 * for consistent error handling.
 * 
 * @since 1.0.1
 */
public class ErrorMapper {
    /**
     * Static mapping of error messages to error codes and descriptions.
     */
    public static final Map<String, String> ERROR_MAPPING = new HashMap<>();

    static {
        ERROR_MAPPING.put("Access Key security groups not found", "ACCESSKEY_NOT_FOUND");
        ERROR_MAPPING.put("Alphabet was not found", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("Application has been authorized.", "27, Application has been authorized.");
        ERROR_MAPPING.put("Application has not been authorized.", "28, Application has not been authorized.");
        ERROR_MAPPING.put("Bulk re-protect is not supported", "51, Failed to send logs, connection refused !");
        ERROR_MAPPING.put("Card type must be invalid input", "44, The content of the input data is not valid.");
        ERROR_MAPPING.put("Card type must be valid input", "44, The content of the input data is not valid.");
        ERROR_MAPPING.put("Create operation failed.", "35, Create operation failed.");
        ERROR_MAPPING.put("Create operation was successful.", "34, Create operation was successful.");
        ERROR_MAPPING.put("Crypto operation failed", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("Data is too long to be protected/unprotected", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("Data is too long to be protected/unprotected.", "23, Data is too long to be protected/unprotected.");
        ERROR_MAPPING.put("Data is too short to be protected/unprotected.", "22, Data is too short to be protected/unprotected.");
        ERROR_MAPPING.put("Data protect operation failed.", "7, Data protection failed.");
        ERROR_MAPPING.put("Data protect operation was successful.", "6, Data protection was successful.");
        ERROR_MAPPING.put("Data reprotect operation was successful.", "50, Data protection was successful.");
        ERROR_MAPPING.put("Data unprotect operation failed.", "9, Data unprotect operation failed.");
        ERROR_MAPPING.put("Data unprotect operation was successful with use of an inactive keyid.", "11, Data unprotect operation was successful with use of an inactive keyid.");
        ERROR_MAPPING.put("Data unprotect operation was successful.", "8, Data unprotect operation was successful.");
        ERROR_MAPPING.put("Delete operation failed.", "33, Delete operation failed.");
        ERROR_MAPPING.put("Delete operation was successful.", "32, Delete operation was successful.");
        ERROR_MAPPING.put("Encoding must be provided", "12, Input is null or not within allowed limits.");
        ERROR_MAPPING.put("Encoding not supported", "UNSUPPORTED_ENCODING");
        ERROR_MAPPING.put("External IV is not supported in this version", "16, External IV is not supported in this version.");
        ERROR_MAPPING.put("FPE value identification position is bigger than the data", "12, Input is null or not within allowed limits.");
        ERROR_MAPPING.put("FPE value identification position is invalid", "44, The content of the input data is not valid.");
        ERROR_MAPPING.put("Failed to acquire policy mutex", "17, Failed to initialize the PEP - This is a fatal error");
        ERROR_MAPPING.put("Failed to allocate memory.", "20, Failed to allocate memory.");
        ERROR_MAPPING.put("Failed to calculate policy hash", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("Failed to check for first call", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("Failed to clear key context", "10, The user has the appropriate permissions to perform the requested operation. This is just a policy check and no data has been protected nor unprotected.");
        ERROR_MAPPING.put("Failed to convert input data", "21, Input or output buffer is too small.");
        ERROR_MAPPING.put("Failed to convert output data", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("Failed to convert padded input data", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("Failed to create Alphabet mutex", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("Failed to create event for flush thread", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("Failed to create key context", "10, The user has the appropriate permissions to perform the requested operation. This is just a policy check and no data has been protected nor unprotected.");
        ERROR_MAPPING.put("Failed to create log mutex", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("Failed to create policy Mutex", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("Failed to get binary alphabet", "21, Input or output buffer is too small.");
        ERROR_MAPPING.put("Failed to get session from cache", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("Failed to initialize crypto library", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("Failed to initialize the PEP - This is a fatal error", "17, Failed to initialize the PEP - This is a fatal error");
        ERROR_MAPPING.put("Failed to load Alphabet from Shmem", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("Failed to load FPE Properties from Shmem", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("Failed to load FPE prop - Internal error", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("Failed to load FPE prop - No such element", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("Failed to load data encryption key", "14, Failed to load data encryption key");
        ERROR_MAPPING.put("Failed to load data encryption key - Cache is full", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("Failed to load data encryption key - Internal error", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("Failed to load data encryption key - No such key", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("Failed to mask output data", "9, Data unprotect operation failed.");
        ERROR_MAPPING.put("Failed to reset policy", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("Failed to send logs, connection refused !", "51, Failed to send logs, connection refused !");
        ERROR_MAPPING.put("Failed to set authorization", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("Failed to set first call in cache", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("Failed to strip date", "21, Input or output buffer is too small.");
        ERROR_MAPPING.put("Failed to unstrip date", "44, The content of the input data is not valid.");
        ERROR_MAPPING.put("Hash operation failed", "26, Unsupported algorithm or unsupported action for the specific data element.");
        ERROR_MAPPING.put("IV can't be used with this token element", "26, Unsupported algorithm or unsupported action for the specific data element.");
        ERROR_MAPPING.put("IV is not supported with used encoding", "26, Unsupported algorithm or unsupported action for the specific data element.");
        ERROR_MAPPING.put("Input is null or not within allowed limits.", "12, Input is null or not within allowed limits.");
        ERROR_MAPPING.put("Input or output buffer is too small.", "21, Input or output buffer is too small.");
        ERROR_MAPPING.put("Integrity check failed", "21, Input or output buffer is too small.");
        ERROR_MAPPING.put("Integrity check failed.", "5, Integrity check failed.");
        ERROR_MAPPING.put("Internal error", "12, Input is null or not within allowed limits.");
        ERROR_MAPPING.put("Internal error occurring in a function call after the Pep Provider has been opened.", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("Invalid CT header format", "7, Data protection failed.");
        ERROR_MAPPING.put("Invalid UNICODE input data", "44, The content of the input data is not valid.");
        ERROR_MAPPING.put("Invalid date format", "44, The content of the input data is not valid.");
        ERROR_MAPPING.put("Invalid date input", "44, The content of the input data is not valid.");
        ERROR_MAPPING.put("Invalid email address", "44, The content of the input data is not valid.");
        ERROR_MAPPING.put("Invalid email address, domain length > 254", "23, Data is too long to be protected/unprotected.");
        ERROR_MAPPING.put("Invalid email address, total length > 256", "23, Data is too long to be protected/unprotected.");
        ERROR_MAPPING.put("Invalid email address, wrong domain length", "23, Data is too long to be protected/unprotected.");
        ERROR_MAPPING.put("Invalid email address, wrong local length", "44, The content of the input data is not valid.");
        ERROR_MAPPING.put("Invalid input data", "44, The content of the input data is not valid.");
        ERROR_MAPPING.put("Invalid input data for FPE creditcard", "44, The content of the input data is not valid.");
        ERROR_MAPPING.put("Invalid input for the creditcard FPE type", "44, The content of the input data is not valid.");
        ERROR_MAPPING.put("Invalid input for the creditcard token type", "44, The content of the input data is not valid.");
        ERROR_MAPPING.put("Invalid input for the decimal token type", "44, The content of the input data is not valid.");
        ERROR_MAPPING.put("Invalid input parameter", "44, The content of the input data is not valid.");
        ERROR_MAPPING.put("Invalid license or time is before licensestart.", "42, Invalid license or time is before licensestart.");
        ERROR_MAPPING.put("Invalid parameter", "21, Input or output buffer is too small.");
        ERROR_MAPPING.put("Invalid shared memory contents", "26, Unsupported algorithm or unsupported action for the specific data element.");
        ERROR_MAPPING.put("Invalid time format", "44, The content of the input data is not valid.");
        ERROR_MAPPING.put("Invalid tokenproc", "12, Input is null or not within allowed limits.");
        ERROR_MAPPING.put("Invalid use of Hmac Data Element", "26, Unsupported algorithm or unsupported action for the specific data element.");
        ERROR_MAPPING.put("Luhn value must be invalid", "44, The content of the input data is not valid.");
        ERROR_MAPPING.put("Luhn value must be valid", "44, The content of the input data is not valid.");
        ERROR_MAPPING.put("Malloc for the JSON type failed.", "30, Malloc for the JSON type failed.");
        ERROR_MAPPING.put("Manage protection operation failed.", "37, Manage protection operation failed.");
        ERROR_MAPPING.put("Manage protection operation was successful.", "36, Manage protection operation was successful.");
        ERROR_MAPPING.put("No such token element", "UNSUPPORTED_ENCODING");
        ERROR_MAPPING.put("No token elements available", "2, The data element could not be found in the policy.");
        ERROR_MAPPING.put("No valid license or current date is beyond the license expiration date.", "40, No valid license or current date is beyond the license expiration date.");
        ERROR_MAPPING.put("Out buffer size is too small", "12, Input is null or not within allowed limits.");
        ERROR_MAPPING.put("Output buffer is to small", "23, Data is too long to be protected/unprotected.");
        ERROR_MAPPING.put("Output buffer is too small", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("Output encoding is not supported for Masking", "12, Input is null or not within allowed limits.");
        ERROR_MAPPING.put("Permission denied", "33, Delete operation failed.");
        ERROR_MAPPING.put("Pointer to the policy shared memory is null", "12, Input is null or not within allowed limits.");
        ERROR_MAPPING.put("Policy not available", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("Protected value can't be returned for this type of algorithm", "10, The user has the appropriate permissions to perform the requested operation. This is just a policy check and no data has been protected nor unprotected.");
        ERROR_MAPPING.put("Provider not initialized", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("Rule Set not found", "RULESET_NOT_FOUND");
        ERROR_MAPPING.put("The IV value is too long", "12, Input is null or not within allowed limits.");
        ERROR_MAPPING.put("The IV value is too short", "12, Input is null or not within allowed limits.");
        ERROR_MAPPING.put("The JSON type is not serializable.", "29, The JSON type is not serializable.");
        ERROR_MAPPING.put("The User has appropriate permissions to perform the requested operation but no data has been protected/unprotected.", "10, The user has the appropriate permissions to perform the requested operation. This is just a policy check and no data has been protected nor unprotected.");
        ERROR_MAPPING.put("The content of the input data is not valid.", "44, The content of the input data is not valid.");
        ERROR_MAPPING.put("The data element could not be found in the policy in shared memory.", "2, The data element could not be found in the policy.");
        ERROR_MAPPING.put("The data element is not using key id", "0, ");
        ERROR_MAPPING.put("The input is too long", "23, Data is too long to be protected/unprotected.");
        ERROR_MAPPING.put("The input is too short", "22, Data is too short to be protected/unprotected.");
        ERROR_MAPPING.put("The policy in shared memory is empty.", "31, Policy not available");
        ERROR_MAPPING.put("The policy in shared memory is locked. This can be caused by a disk full alert.", "39, The policy in memory is locked. This can be caused by a disk full alert.");
        ERROR_MAPPING.put("The requested action is not supported for tokenization", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("The tokenized email became too long", "21, Input or output buffer is too small.");
        ERROR_MAPPING.put("The use of the protection method is restricted by license.", "41, The use of the protection method is restricted by license.");
        ERROR_MAPPING.put("The user does not have the appropriate permissions to perform the requested operation.", "3, The user does not have the appropriate permissions to perform the requested operation.");
        ERROR_MAPPING.put("The username could not be found in the policy in shared memory.", "1, The username could not be found in the policy.");
        ERROR_MAPPING.put("Token value identification position is bigger than the data", "12, Input is null or not within allowed limits.");
        ERROR_MAPPING.put("Token value identification position is invalid", "44, The content of the input data is not valid.");
        ERROR_MAPPING.put("Tokenization is disabled", "26, Unsupported algorithm or unsupported action for the specific data element.");
        ERROR_MAPPING.put("Tweak generation is failed", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("Tweak input is too long", "15, Tweak input is too long.");
        ERROR_MAPPING.put("Tweak is null.", "4, Tweak is null.");
        ERROR_MAPPING.put("Unsupported algorithm or unsupported action for the specific data element.", "26, Unsupported algorithm or unsupported action for the specific data element.");
        ERROR_MAPPING.put("Unsupported input encoding for the specific data element.", "UNSUPPORTED_ENCODING");
        ERROR_MAPPING.put("Unsupported operation for that datatype", "26, Unsupported algorithm or unsupported action for the specific data element.");
        ERROR_MAPPING.put("Unsupported tokenizer type.", "26, Unsupported algorithm or unsupported action for the specific data element.");
        ERROR_MAPPING.put("Unsupported tweak action for the specified fpe dataelement", "19, Unsupported tweak action for the specified fpe dataelement");
        ERROR_MAPPING.put("Unsupported version", "26, Unsupported algorithm or unsupported action for the specific data element.");
        ERROR_MAPPING.put("Used for z/OS Query Default Data element when policy name is not found", "46, Used for z/OS Query Default Data element when policy name is not found.");
        ERROR_MAPPING.put("Username too long.", "9, Data unprotect operation failed.");
        ERROR_MAPPING.put("iconv failed - 8859-15 to system", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("iconv failed - system to 8859-15", "13, Internal error occurring in a function call after the Core Provider has been opened.");
        ERROR_MAPPING.put("User not authorized. Refer to audit log for details.", "3, The user does not have the appropriate permissions to perform the requested operation.");
        ERROR_MAPPING.put("Protect failed. Data element not found. Refer to audit log for details.", "2, The data element could not be found in the policy.");
        ERROR_MAPPING.put("Unprotect failed. Data element not found. Refer to audit log for details.", "2, The data element could not be found in the policy.");
        ERROR_MAPPING.put("Integer input error. Only digits allowed", "44, The content of the input data is not valid.");
        ERROR_MAPPING.put("Integer input out of range. Valid values are -2147483648 to 2147483647", "44, The content of the input data is not valid.");
        ERROR_MAPPING.put("Invalid data length", "44, The content of the input data is not valid.");
        ERROR_MAPPING.put("Invalid base64-encoded data, character out of range", "26, Unsupported algorithm or unsupported action for the specific data element.");
        ERROR_MAPPING.put("Failed to decode hex", "26, Unsupported algorithm or unsupported action for the specific data element.");
        ERROR_MAPPING.put("For input string:", "26, Unsupported algorithm or unsupported action for the specific data element.");
    }
    
    public static ErrorDetail getErrorDetail(String message) {
        if (message == null || message.isEmpty()) {
            return new ErrorDetail(-1, "Unknown error occurred");
        }
        
        // Normalize message for matching
        String normalizedMessage = message.trim();
        
        // Try exact match first
        String mapped = ERROR_MAPPING.get(normalizedMessage);
        
        // If not found, try partial match
        if (mapped == null) {
            for (Map.Entry<String, String> entry : ERROR_MAPPING.entrySet()) {
                if (normalizedMessage.contains(entry.getKey())) {
                    mapped = entry.getValue();
                    break;
                }
            }
        }

        // If still not found, return original message
        if (mapped == null) {
             return new ErrorDetail(-1, normalizedMessage);
        }

        // Parse mapped value
        String[] parts = mapped.split(",", 2);
        int code;
        try {
            code = Integer.parseInt(parts[0].trim());
        } catch (NumberFormatException e) {
            code = -1; // For cases like UNSUPPORTED_ENCODING
        }
        String errorMsg = parts.length > 1 ? parts[1].trim() : normalizedMessage;
        return new ErrorDetail(code, errorMsg);
    }

public static class ErrorDetail {
    private final int errorCode;
    private final String errorMessage;

    public ErrorDetail(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public String toString() {
        return "ErrorDetail{errorMessage='" + errorMessage + "'}";
    }
}

}
