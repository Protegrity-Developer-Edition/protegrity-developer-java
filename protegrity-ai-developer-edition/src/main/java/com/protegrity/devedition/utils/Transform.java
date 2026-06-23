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
import java.util.List;

/**
 * Module for redacting PII entities using the Data Discovery transform/label API.
 *
 * <p>This is the Java equivalent of Python's {@code transform.py}. It calls the
 * {@code /pty/data-discovery/v2/transform/label} endpoint and handles both
 * "redact" and "mask" methods.
 *
 * <h2>Redact mode (default)</h2>
 * <p>Posts plain text to the transform API and returns the pre-redacted text
 * from {@code response["transform"]["text"]}.
 *
 * <h2>Mask mode</h2>
 * <p>Adds {@code ?include_classification_details=yes}, reads the
 * {@code classifications} from the response, collects entity spans via
 * {@link PiiProcessing#collectEntitySpans}, and replaces them with the
 * configured masking character via {@link PiiProcessing#redactData}.
 *
 * @since 1.0.2
 */
public class Transform {

    private static final Logger logger = LoggerUtil.getLogger(Transform.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Redact or mask PII entities in the input text using the transform/label API.
     *
     * <p>When method is "redact" (default), calls the Data Discovery v2
     * transform/label endpoint and returns the redacted text with
     * {@code [ENTITY_TYPE]} labels.
     *
     * <p>When method is "mask", uses {@code include_classification_details=yes}
     * to get entity positions from the same API call and replaces them with the
     * masking character.
     *
     * @param text Input text to redact or mask
     * @return Redacted or masked text
     * @throws Exception if the HTTP request fails or the response cannot be parsed
     */
    public static String transformLabel(String text) throws Exception {
        String method = Config.getMethod() != null ? Config.getMethod() : "redact";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URIBuilder uriBuilder = new URIBuilder(Config.getTransformUrl());
            if ("mask".equals(method)) {
                uriBuilder.addParameter("include_classification_details", "yes");
            }
            URI uri = uriBuilder.build();

            HttpPost httpPost = new HttpPost(uri);
            httpPost.setHeader("Content-Type", "text/plain; charset=utf-8");
            httpPost.setEntity(new StringEntity(text, "UTF-8"));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");

                if (statusCode < 200 || statusCode >= 300) {
                    String errorMsg = String.format(
                            "HTTP request failed with status code: %d, response: %s",
                            statusCode, responseBody);
                    logger.error(errorMsg);
                    throw new RuntimeException(errorMsg);
                }

                JsonNode responseJson = objectMapper.readTree(responseBody);
                logger.debug("Transform/label response: {}", responseJson.toString());

                if ("mask".equals(method)) {
                    // Use classifications to collect entity spans and mask them
                    JsonNode classifications = responseJson.get("classifications");
                    if (classifications != null && classifications.size() > 0) {
                        List<PiiProcessing.EntitySpan> piiEntitySpans =
                                PiiProcessing.collectEntitySpans(classifications, text);
                        return PiiProcessing.redactData(piiEntitySpans, text);
                    }
                    logger.info("No PII entities found.");
                    return text;
                }

                // Redact mode: return the pre-redacted text from the API response
                JsonNode transformNode = responseJson.get("transform");
                if (transformNode != null && transformNode.has("text")) {
                    return transformNode.get("text").asText(text);
                }
                return text;
            }
        } catch (Exception e) {
            logger.error("Error during transform/label: {}", e.getMessage());
            throw e;
        }
    }
}
