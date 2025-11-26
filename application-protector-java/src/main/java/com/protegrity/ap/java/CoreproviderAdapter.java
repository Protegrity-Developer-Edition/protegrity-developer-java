package com.protegrity.ap.java;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class CoreproviderAdapter {
    private static final Logger logger = LoggerFactory.getLogger(CoreproviderAdapter.class);
    private SessionHandler sessionHandler = null;

    public CoreproviderAdapter(SessionHandler sessionHandler) throws ProtectorException {
    this.sessionHandler = sessionHandler;
    }

    public String[] convertToStringArray(Object input) {
        if (input == null) return new String[0];

        if (input instanceof short[]) {
            short[] arr = (short[]) input;
            String[] result = new String[arr.length];
            for (int i = 0; i < arr.length; i++) {
            result[i] = Short.toString(arr[i]);
            }
            return result;
        } else if (input instanceof int[]) {
            int[] arr = (int[]) input;
            String[] result = new String[arr.length];
            for (int i = 0; i < arr.length; i++) {
                result[i] = Integer.toString(arr[i]);
            }
            return result;
        } else if (input instanceof long[]) {
            long[] arr = (long[]) input;
            String[] result = new String[arr.length];
            for (int i = 0; i < arr.length; i++) {
                result[i] = Long.toString(arr[i]);
            }
            return result;
        } else if (input instanceof float[]) {
            float[] arr = (float[]) input;
            String[] result = new String[arr.length];
            for (int i = 0; i < arr.length; i++) {
                result[i] = Float.toString(arr[i]);
            }
            return result;
        } else if (input instanceof double[]) {
            double[] arr = (double[]) input;
            String[] result = new String[arr.length];
            for (int i = 0; i < arr.length; i++) {
                result[i] = Double.toString(arr[i]);
            }
            return result;
        } else if (input instanceof char[][]) {
            char[][] arr = (char[][]) input;
            String[] result = new String[arr.length];
            for (int i = 0; i < arr.length; i++) {
                result[i] = new String(arr[i]);
            }
            return result;
        } else if (input instanceof byte[][]) {
            byte[][] arr = (byte[][]) input;
            String[] result = new String[arr.length];
            for (int i = 0; i < arr.length; i++) {
                result[i] = Base64.getEncoder().encodeToString(arr[i]);
            }
            return result;
        } else if (input instanceof Date[]) {
            Date[] arr = (Date[]) input;
            String[] result = new String[arr.length];
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (int i = 0; i < arr.length; i++) {
                String dateOnly = dateFormat.format(arr[i]);
                String dateTime = dateTimeFormat.format(arr[i]);
                result[i] = dateOnly + " | " + dateTime; // Combine both formats
            }
            return result;
    
        } else {
            throw new IllegalArgumentException("Unsupported input type: " + input.getClass());
        }
    }

    public String[] decodeBase64ToUtf8Strings(String[] base64Input) {
        String[] result = new String[base64Input.length];
        for (int i = 0; i < base64Input.length; i++) {
            byte[] decoded = Base64.getDecoder().decode(base64Input[i]);
            result[i] = new String(decoded, StandardCharsets.UTF_8);
        }
        return result;
    }

    public String sendApiRequest(String operationType, String jwtToken, String apiKey, String jsonPayload) throws ProtectorException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Construct URL
            String runtimeHost = System.getenv().getOrDefault("DEV_EDITION_HOST", "api.developer-edition.protegrity.com");
            String runtimeVersion = System.getenv().getOrDefault("DEV_EDITION_VERSION", "1");
            String urlStr = String.format("https://%s/v%s/%s", runtimeHost, runtimeVersion, operationType);
            
            // Create POST request
            HttpPost httpPost = new HttpPost(urlStr);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("x-api-key", apiKey);
            httpPost.setHeader("Authorization", "Bearer " + jwtToken);
            httpPost.setEntity(new StringEntity(jsonPayload, StandardCharsets.UTF_8));

            // Execute request
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                return responseBody;
            }
        } catch (Exception e) {
            logger.error("Error during API request: {}", e.getMessage(), e);
            throw new ProtectorException("Error during API request: " + e.getMessage());
        }
    }

    public String buildProtectPayload(String user, String dataElementName, String[] input, byte[] externalIv, String encodingType) throws ProtectorException {
        StringBuilder jsonBuilder = new StringBuilder();
        if(user == null)
        {
           throw new ProtectorException("User name can not be null or empty.");
        }
        if(dataElementName == null)
        {
            throw new ProtectorException("DataElement name can not be null or empty.");
        }
        jsonBuilder.append("{")
            .append("\"encoding\": \"").append(encodingType).append("\",")
            .append("\"query_id\": \"1\",")
            .append("\"user\": \"").append(user).append("\",")
            .append("\"data_element\": \"").append(dataElementName).append("\",");
        if (externalIv != null && externalIv.length > 0) {
            String ivEncoded = Base64.getEncoder().encodeToString(externalIv); // Encode bytes to Base64
            jsonBuilder.append("\"external_iv\": \"").append(ivEncoded).append("\",");
        }
        jsonBuilder.append("\"data\": [");
        for (int i = 0; i < input.length; i++) {
        jsonBuilder.append("\"").append(input[i]).append("\"");
        if (i < input.length - 1) jsonBuilder.append(",");
        }
        jsonBuilder.append("]}");
        return jsonBuilder.toString();
    }

    public String buildReprotectPayload(String user, String newDataElementName,String oldDataElementName, String[] input, byte[] newExternalIv, byte[] oldExternalIv, String encodingType) throws ProtectorException{
        if(user == null) 
        {
            throw new ProtectorException("User name can not be null or empty.");
        }
        if(oldDataElementName == null)
        {
            throw new ProtectorException( "Old DataElement name can not be null or empty.");
        }
        if(newDataElementName == null)
        {
            throw new ProtectorException("New DataElement name can not be null or empty.");
        }      
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{")
        .append("\"encoding\": \"").append(encodingType).append("\",")
        .append("\"query_id\": \"1\",")
        .append("\"user\": \"").append(user).append("\",")
        .append("\"old_data_element\": \"").append(oldDataElementName).append("\",")
        .append("\"data_element\": \"").append(newDataElementName).append("\",");
        if (oldExternalIv != null && oldExternalIv.length > 0) {
        String ivEncoded = Base64.getEncoder().encodeToString(oldExternalIv); // Encode bytes to Base64
        jsonBuilder.append("\"old_external_iv\": \"").append(ivEncoded).append("\",");
        }
        if (newExternalIv != null && newExternalIv.length > 0) {
        String ivEncoded = Base64.getEncoder().encodeToString(newExternalIv); // Encode bytes to Base64
        jsonBuilder.append("\"new_external_iv\": \"").append(ivEncoded).append("\",");
        }
        jsonBuilder.append("\"data\": [");
        for (int i = 0; i < input.length; i++) {
        jsonBuilder.append("\"").append(input[i]).append("\"");
        if (i < input.length - 1) jsonBuilder.append(",");
        }
        jsonBuilder.append("]}");
        return jsonBuilder.toString();
    }


    public ParseResult parseResultsToOutput( SessionObject sessionObj, String jsonResponse, Class<?> targetType, String encodingType) throws ProtectorException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonResponse);

            // Extract results as String[]
            String[] stringResults = new String[0];
            if (rootNode.has("results")) {
                JsonNode resultsNode = rootNode.get("results");
                stringResults = new String[resultsNode.size()];
                for (int i = 0; i < resultsNode.size(); i++) {
                    stringResults[i] = resultsNode.get(i).asText();
                }
            }

            Object convertedArray;
            boolean success = rootNode.has("success") && rootNode.get("success").asBoolean();
           
            if (!success) {
            String errorMessage = rootNode.has("error_msg") ? rootNode.get("error_msg").asText() : "Unknown error";

            // Get mapped error details
            ErrorMapper.ErrorDetail detail = ErrorMapper.getErrorDetail(errorMessage);
            int errorCode = detail.getErrorCode();
            String finalErrorMessage = detail.getErrorMessage();

            // Save to session
            saveResultInSession(sessionObj, null, finalErrorMessage, errorCode);

            // Throw exception immediately
            throw new ProtectorException(finalErrorMessage);
            }

            if (targetType == null || targetType.equals(String[].class)) {
                String[] stringResult;
                if (encodingType.equals("base64")) {
                    stringResult = decodeBase64ToUtf8Strings(stringResults);
                    convertedArray = stringResult;
                }
                else{
                    convertedArray = stringResults;
                }
            } else if (targetType.equals(short[].class)) {
                String[] stringResult;
                if (encodingType.equals("base64")) {
                    stringResult = decodeBase64ToUtf8Strings(stringResults);
                } else {
                    stringResult = stringResults;
                }
                short[] arr = new short[stringResult.length];
                for (int i = 0; i < stringResult.length; i++) {
                    arr[i] = Short.parseShort(stringResult[i]);
                }
                convertedArray = arr;
            } else if (targetType.equals(int[].class)) {
                String[] stringResult;
                if (encodingType.equals("base64")) {
                    stringResult = decodeBase64ToUtf8Strings(stringResults);
                } else {
                    stringResult = stringResults;
                }
                int[] arr = new int[stringResult.length];
                for (int i = 0; i < stringResult.length; i++) {
                    arr[i] = Integer.parseInt(stringResult[i]);
                }
                convertedArray = arr;
            } else if (targetType.equals(long[].class)) {
                String[] stringResult;
                if (encodingType.equals("base64")) {
                    stringResult = decodeBase64ToUtf8Strings(stringResults);
                } else {
                    stringResult = stringResults;
                }
                long[] arr = new long[stringResult.length];
                for (int i = 0; i < stringResult.length; i++) {
                arr[i] = Long.parseLong(stringResult[i]);
                }
                convertedArray = arr;
            } else if (targetType.equals(float[].class)) {
                String[] stringResult;
                if (encodingType.equals("base64")) {
                    stringResult = decodeBase64ToUtf8Strings(stringResults);
                } else {
                    stringResult = stringResults;
                }
                float[] arr = new float[stringResult.length];
                for (int i = 0; i < stringResult.length; i++) {
                    arr[i] = Float.parseFloat(stringResult[i]);
                }
                convertedArray = arr;
            } else if (targetType.equals(double[].class)) {
                String[] stringResult;
                if (encodingType.equals("base64")) {
                    stringResult = decodeBase64ToUtf8Strings(stringResults);
                } else {
                    stringResult = stringResults;
                }
                double[] arr = new double[stringResult.length];
                for (int i = 0; i < stringResult.length; i++) {
                    arr[i] = Double.parseDouble(stringResult[i]);
                }
                convertedArray = arr;
            } else if (targetType.equals(char[][].class)) {
                String[] stringResult;
                if (encodingType.equals("base64")) {
                    stringResult = decodeBase64ToUtf8Strings(stringResults);
                } else {
                    stringResult = stringResults;
                }
                char[][] arr = new char[stringResult.length][];
                for (int i = 0; i < stringResult.length; i++) {
                    arr[i] = stringResults[i].toCharArray();
                }
                convertedArray = arr;
            }else if (targetType.equals(byte[][].class)) {
                byte[][] arr = new byte[stringResults.length][];
                for (int i = 0; i < stringResults.length; i++){
                    String s = stringResults[i];
                    if(encodingType.equals("base64")){
                        arr[i] = Base64.getDecoder().decode(s);
                    }
                    else{
                        arr[i] = s.getBytes();
                    }
                }
                convertedArray = arr;
            }else if (targetType.equals(java.util.Date[].class)) {
                Date[] arr = new Date[stringResults.length];
                SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (int i = 0; i < stringResults.length; i++) {
                    String[] parts = stringResults[i].split("\\|");
                    String dateTimeStr = parts.length > 1 ? parts[1].trim() : parts[0].trim(); // fallback to dateOnly if needed
                    arr[i] = dateTimeFormat.parse(dateTimeStr); // may throw ParseException
                }
                convertedArray = arr;
            } 
            else {
                throw new IllegalArgumentException("Unsupported target type: " + targetType);
            }
            return new ParseResult(success, convertedArray);

        } catch (Exception e) {
            throw new ProtectorException(e.getMessage());
        }
    }
  

    public String getVersion() {
        return "1.0.0";
    }

    public String getCoreVersion() {
        return "1.0.0";
    }
  
    public void flush() throws ProtectorException {
        return;
    }
 

    private void saveResultInSession(SessionObject session, int[] errorList, String errorMessage, int errorCode) throws ProtectorException {
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append(errorMessage);
        sessionHandler.setLastError(session, errorMsg.toString());
    }

}