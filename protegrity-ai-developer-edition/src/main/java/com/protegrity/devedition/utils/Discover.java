package com.protegrity.devedition.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

import java.net.URI;

/**
 * PII entity discovery service for detecting and classifying sensitive information.
 * 
 * <p>This class provides functionality to discover Personally Identifiable Information (PII)
 * in unstructured text by communicating with the Protegrity Data Discovery API.
 * 
 * <p>The discovery service identifies entities such as email addresses, phone numbers,
 * social security numbers, credit card numbers, and other sensitive data types based on
 * the configured classification threshold.
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * String text = "Contact John at john.doe@example.com or 555-123-4567";
 * JsonNode entities = Discover.discover(text);
 * }</pre>
 * 
 * @since 1.0.1
 */
public class Discover {
    
    private static final Logger logger = LoggerUtil.getLogger(Discover.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Discover PII entities in the input text using the configured REST endpoint.
     *
     * @param text Input text to classify
     * @return JsonNode containing classifications from the API
     * @throws Exception if HTTP request fails or JSON parsing fails
     */
    public static JsonNode discover(String text) throws Exception {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // Build URI with query parameters
            URI uri = new URIBuilder(Config.getEndpointUrl())
                    .addParameter("score_threshold", String.valueOf(Config.getClassificationScoreThreshold()))
                    .build();
            
            // Create HTTP POST request
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setHeader("Content-Type", "text/plain; charset=utf-8");
            httpPost.setEntity(new StringEntity(text, "UTF-8"));
            
            // Execute request
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity());
                
                if (statusCode >= 200 && statusCode < 300) {
                    JsonNode responseJson = objectMapper.readTree(responseBody);
                    JsonNode classifications = responseJson.get("classifications");
                    JsonNode classificationNode = classifications != null ? classifications : objectMapper.createObjectNode();
                    logger.debug("Discovery result: {}", classificationNode.toString());
                    return classificationNode;
                } else {
                    String errorMsg = String.format("HTTP request failed with status code: %d, response: %s", 
                            statusCode, responseBody);
                    logger.error(errorMsg);
                    throw new RuntimeException(errorMsg);
                }
            }
        } catch (Exception e) {
            logger.error("Error during discovery: {}", e.getMessage());
            throw e;
        }
    }
}
