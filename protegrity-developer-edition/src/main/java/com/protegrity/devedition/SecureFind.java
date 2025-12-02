package com.protegrity.devedition;

import com.fasterxml.jackson.databind.JsonNode;
import com.protegrity.devedition.utils.*;
import org.slf4j.Logger;

import java.util.List;

/**
 * Module for discovering and redacting, masking or protecting PII entities in text.
 */
public class SecureFind {
    
    private static final Logger logger = LoggerUtil.getLogger(SecureFind.class);
    
    /**
     * Protect (tokenize) PII entities in the input text.
     * Uses index-based slicing to ensure precise replacement of PII entities
     * at known character positions. This avoids accidental replacement of repeated
     * entities and ensures correctness when multiple PII spans are present.
     *
     * @param text Input text to process
     * @return Protected text
     * @throws Exception if processing fails
     */
    public static String findAndProtect(String text) throws Exception {
        try {
            JsonNode piiEntities = Discover.discover(text);
            if (piiEntities != null && piiEntities.size() > 0) {
                List<PiiProcessing.EntitySpan> piiEntitySpans = PiiProcessing.collectEntitySpans(piiEntities);
                return PiiProcessing.protectData(piiEntitySpans, text);
            }
            logger.info("No PII entities found.");
            return text;
        } catch (Exception e) {
            logger.error("Failed to process text: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Unprotect (detokenize) to get PII entities.
     * Uses index-based slicing to ensure precise replacement of detokenized PII entities
     * at known character positions.
     *
     * @param text Input text to process
     * @return Unprotected text
     * @throws Exception if processing fails
     */
    public static String findAndUnprotect(String text) throws Exception {
        try {
            return PiiProcessing.unprotectData(text);
        } catch (Exception e) {
            logger.error("Failed to process text: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Redact or mask PII entities in the input text.
     * Uses index-based slicing to ensure precise replacement of PII entities
     * at known character positions. This avoids accidental replacement of repeated
     * entities and ensures correctness when multiple PII spans are present.
     *
     * @param text Input text to process
     * @return Redacted or masked text
     * @throws Exception if processing fails
     */
    public static String findAndRedact(String text) throws Exception {
        try {
            JsonNode piiEntities = Discover.discover(text);
            if (piiEntities != null && piiEntities.size() > 0) {
                List<PiiProcessing.EntitySpan> piiEntitySpans = PiiProcessing.collectEntitySpans(piiEntities);
                return PiiProcessing.redactData(piiEntitySpans, text);
            }
            logger.info("No PII entities found.");
            return text;
        } catch (Exception e) {
            logger.error("Failed to process text: {}", e.getMessage());
            throw e;
        }
    }
}
