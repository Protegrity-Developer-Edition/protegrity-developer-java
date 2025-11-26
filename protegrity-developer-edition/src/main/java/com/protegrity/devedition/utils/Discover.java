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
 * Module for discovering PII entities using a discovery API.
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
            httpPost.setHeader("Content-Type", "text/plain");
            httpPost.setEntity(new StringEntity(text));
            
            // Execute request
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity());
                
                if (statusCode >= 200 && statusCode < 300) {
                    JsonNode responseJson = objectMapper.readTree(responseBody);
                    JsonNode classifications = responseJson.get("classifications");
                    return classifications != null ? classifications : objectMapper.createObjectNode();
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
