package com.protegrity.devedition.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.protegrity.ap.java.SessionObject;

import org.slf4j.Logger;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Module for processing PII entities in text, including redaction, masking, and protection.
 */
public class PiiProcessing {
    
    private static final Logger logger = LoggerUtil.getLogger(PiiProcessing.class);
    
    /**
     * Convert code point offset to char offset for proper handling of Unicode characters
     * including emojis and other multi-byte characters.
     * 
     * @param text The text string
     * @param codePointOffset The offset in code points
     * @return The offset in chars (UTF-16 code units)
     */
    private static int codePointOffsetToCharOffset(String text, int codePointOffset) {
        try {
            return text.offsetByCodePoints(0, codePointOffset);
        } catch (IndexOutOfBoundsException e) {
            // If the offset is out of bounds, return the string length
            logger.warn("Code point offset {} is out of bounds for text length {}, using text length",
                    codePointOffset, text.codePointCount(0, text.length()));
            return text.length();
        }
    }
    
    /**
     * Class to represent an entity span with entity name and score.
     */
    public static class EntitySpan {
        private final int start;
        private final int end;
        private final String entity;
        private final double score;
        
        public EntitySpan(int start, int end, String entity, double score) {
            this.start = start;
            this.end = end;
            this.entity = entity;
            this.score = score;
        }
        
        public int getStart() { return start; }
        public int getEnd() { return end; }
        public String getEntity() { return entity; }
        public double getScore() { return score; }
    }
    
    /**
     * Merge overlapping entity spans.
     * 
     * Merges overlapping or adjacent spans by combining entity labels with '|' and
     * retaining the highest score. Matches Python's _merge_overlapping_entities() behavior.
     *
     * @param entitySpans List of entity spans
     * @return List of merged entity spans sorted in reverse order by start index
     */
    private static List<EntitySpan> mergeOverlappingEntities(List<EntitySpan> entitySpans) {
        if (entitySpans.isEmpty()) {
            return entitySpans;
        }
        
        // Sort by start position (ascending order)
        List<EntitySpan> sorted = new ArrayList<>(entitySpans);
        sorted.sort(Comparator.comparingInt(EntitySpan::getStart));
        
        List<EntitySpan> merged = new ArrayList<>();
        EntitySpan current = sorted.get(0);
        
        for (int i = 1; i < sorted.size(); i++) {
            EntitySpan next = sorted.get(i);
            
            // Check for overlap: next starts before or at current end
            if (next.getStart() <= current.getEnd() && next.getEnd() >= current.getStart()) {
                // Merge ranges
                int newStart = Math.min(current.getStart(), next.getStart());
                int newEnd = Math.max(current.getEnd(), next.getEnd());
                
                // Merge entities (combine with | if different, sorted by score then lexicographically)
                String newEntity;
                if (!current.getEntity().equals(next.getEntity())) {
                    // Sort by score (descending), then lexicographically
                    String firstEntity, secondEntity;
                    if (current.getScore() > next.getScore()) {
                        firstEntity = current.getEntity();
                        secondEntity = next.getEntity();
                    } else if (current.getScore() < next.getScore()) {
                        firstEntity = next.getEntity();
                        secondEntity = current.getEntity();
                    } else {
                        // Scores equal, sort lexicographically
                        if (current.getEntity().compareTo(next.getEntity()) < 0) {
                            firstEntity = current.getEntity();
                            secondEntity = next.getEntity();
                        } else {
                            firstEntity = next.getEntity();
                            secondEntity = current.getEntity();
                        }
                    }
                    newEntity = firstEntity + "|" + secondEntity;
                } else {
                    newEntity = current.getEntity();
                }
                
                // Take max score
                double newScore = Math.max(current.getScore(), next.getScore());
                
                current = new EntitySpan(newStart, newEnd, newEntity, newScore);
            } else {
                // No overlap, add current and move to next
                merged.add(current);
                current = next;
            }
        }
        merged.add(current);
        
        // Sort merged entities in reverse order by start index (for text replacement)
        merged.sort((a, b) -> Integer.compare(b.getStart(), a.getStart()));
        
        return merged;
    }
    
    /**
     * Collect entity spans for redaction or masking based on score.
     *
     * @param entities Dictionary of detected PII entities
     * @return List of entity spans sorted in reverse order by start index
     */
    public static List<EntitySpan> collectEntitySpans(JsonNode entities, String text) {
        Map<String, EntitySpan> entityMap = new LinkedHashMap<>();
        
        Iterator<String> fieldNames = entities.fieldNames();
        while (fieldNames.hasNext()) {
            String entityName = fieldNames.next();
            JsonNode entityDetails = entities.get(entityName);
            
            if (entityDetails.isArray()) {
                for (JsonNode obj : entityDetails) {
                    double score = obj.has("score") ? obj.get("score").asDouble() : 0.0;
                    JsonNode location = obj.get("location");
                    
                    if (location != null) {
                        // API returns code point offsets, convert to char offsets for Java
                        int codePointStart = location.has("start_index") ? location.get("start_index").asInt() : 0;
                        int codePointEnd = location.has("end_index") ? location.get("end_index").asInt() : 0;
                        
                        int start = codePointOffsetToCharOffset(text, codePointStart);
                        int end = codePointOffsetToCharOffset(text, codePointEnd);
                        
                        String key = start + "-" + end;
                        if (entityMap.containsKey(key)) {
                            if (score > entityMap.get(key).getScore()) {
                                entityMap.put(key, new EntitySpan(start, end, entityName, score));
                            }
                        } else {
                            entityMap.put(key, new EntitySpan(start, end, entityName, score));
                        }
                    }
                }
            }
        }
        
        // Collect entity spans (no sorting yet, let merge handle it)
        List<EntitySpan> entitySpans = new ArrayList<>(entityMap.values());
        
        return mergeOverlappingEntities(entitySpans);
    }
    
    /**
     * Unprotect (detokenize) to get PII entities.
     *
     * @param text Input text to process
     * @return Unprotected text
     * @throws Exception if unprotection fails
     */
    public static String unprotectData(String text) throws Exception {
        SessionObject session = ProtectorUtil.getProtectorSession();
        com.protegrity.ap.java.Protector protector = ProtectorUtil.getProtector();
        
        try {
            // Regex pattern to match [entity]data[/entity]
            Pattern pattern = Pattern.compile("\\[([^\\]]+)\\]([^\\[]*)\\[\\/\\1\\]");
            Matcher matcher = pattern.matcher(text);
            StringBuffer result = new StringBuffer();
            
            while (matcher.find()) {
                String entity = matcher.group(1);
                String data = matcher.group(2);
                String dataElement = Constants.DATA_ELEMENT_MAPPING.get(entity);
                
                try {
                    String unprotected = "";
                    if ("CREDIT_CARD".equals(entity)) {
                        CcnProcessing.CcnCleanResult cleanResult = CcnProcessing.cleanCcn(data);
                        unprotected = unprotectString(protector, session, cleanResult.getCleanedString(), dataElement);
                        unprotected = CcnProcessing.reconstructCcn(unprotected, cleanResult.getSeparatorMap());
                    } else {
                        unprotected = unprotectString(protector, session, data, dataElement);
                    }
                    logger.info("Unprotected {}: {}", dataElement, data);
                    matcher.appendReplacement(result, Matcher.quoteReplacement(unprotected));
                } catch (Exception e) {
                    logger.warn("Failed to unprotect {}: {}", dataElement, data);
                    matcher.appendReplacement(result, Matcher.quoteReplacement(data));
                }
            }
            matcher.appendTail(result);
            return result.toString();
        } catch (Exception e) {
            logger.error("Failed to process text: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Protect the identified PII entities in the input text.
     *
     * @param mergedEntities List of merged entity spans
     * @param text Original input text
     * @return Protected PII text
     */
    public static String protectData(List<EntitySpan> mergedEntities, String text) {
        SessionObject session = ProtectorUtil.getProtectorSession();
        com.protegrity.ap.java.Protector protector = ProtectorUtil.getProtector();
        
        // Sort entities by start position in reverse order to avoid index shifting
        List<EntitySpan> sortedEntities = new ArrayList<>(mergedEntities);
        sortedEntities.sort((a, b) -> Integer.compare(b.getStart(), a.getStart()));
        
        StringBuilder result = new StringBuilder(text);
        
        for (EntitySpan span : sortedEntities) {
            String entity = span.getEntity();
            String dataElement = null;
            String entitySelected = null;
            String namedEntityMapping = null;
            
            if (entity.contains("|")) {
                String firstEntity = entity.split("\\|")[0];
                dataElement = Constants.DATA_ELEMENT_MAPPING.get(firstEntity);
                entitySelected = firstEntity;
                namedEntityMapping = Config.getNamedEntityMap().get(firstEntity);
            } else {
                dataElement = Constants.DATA_ELEMENT_MAPPING.get(entity);
                entitySelected = entity;
                namedEntityMapping = Config.getNamedEntityMap().get(entity);
            }
            
            String inputData = result.substring(span.getStart(), span.getEnd());
            
            if (namedEntityMapping != null) {
                try {
                    String protected_ = "";
                    if ("CREDIT_CARD".equals(entity)) {
                        CcnProcessing.CcnCleanResult cleanResult = CcnProcessing.cleanCcn(inputData);
                        protected_ = protectString(protector, session, cleanResult.getCleanedString(), dataElement);
                        protected_ = CcnProcessing.reconstructCcn(protected_, cleanResult.getSeparatorMap());
                    } else {
                        protected_ = protectString(protector, session, inputData, dataElement);
                    }
                    
                    logger.info("Entity '{}' at span [{}:{}] protected successfully.", 
                            entity, span.getStart(), span.getEnd());
                    
                    String replacement = "[" + entitySelected + "]" + protected_ + "[/" + entitySelected + "]";
                    result.replace(span.getStart(), span.getEnd(), replacement);
                } catch (Exception e) {
                    logger.warn("Failed to protect entity '{}' at span [{}:{}] having value {}.",
                            entity, span.getStart(), span.getEnd(), inputData);
                }
            } else {
                logger.warn("Entity '{}' detected at span [{}:{}] but no mapping found in named_entity_map - skipping protection",
                        entity, span.getStart(), span.getEnd());
            }
        }
        
        return result.toString();
    }
    
    /**
     * Redact the identified PII entities in the input text.
     *
     * @param mergedEntities List of merged entity spans
     * @param text Original input text
     * @return Redacted PII text
     */
    public static String redactData(List<EntitySpan> mergedEntities, String text) {
        // Sort entities by start position in reverse order to avoid index shifting
        List<EntitySpan> sortedEntities = new ArrayList<>(mergedEntities);
        sortedEntities.sort((a, b) -> Integer.compare(b.getStart(), a.getStart()));
        
        String result = text;
        
        for (EntitySpan span : sortedEntities) {
            String entityName = span.getEntity();
            
            if ("redact".equals(Config.getMethod())) {
                String label;
                if (entityName.contains("|")) {
                    StringBuilder labelBuilder = new StringBuilder();
                    String[] entities = entityName.split("\\|");
                    for (int i = 0; i < entities.length; i++) {
                        String mappedLabel = Config.getNamedEntityMap().getOrDefault(entities[i], entities[i]);
                        labelBuilder.append(mappedLabel);
                        if (i < entities.length - 1) {
                            labelBuilder.append("|");
                        }
                    }
                    label = labelBuilder.toString();
                } else {
                    label = Config.getNamedEntityMap().getOrDefault(entityName, "");
                }
                
                if (!label.isEmpty()) {
                    label = "[" + label + "]";
                    logger.info("Entity '{}' found at span [{}:{}]... redacted as {}",
                            entityName, span.getStart(), span.getEnd(), label);
                    // Replace using string concatenation like Python does: text[:start] + label + text[end:]
                    result = result.substring(0, span.getStart()) + label + result.substring(span.getEnd());
                } else {
                    label = "[" + entityName + "]";
                    logger.warn("Entity '{}' detected at span [{}:{}] but no mapping found in named_entity_map - skipping redaction",
                            entityName, span.getStart(), span.getEnd());
                }
            } else if ("mask".equals(Config.getMethod())) {
                logger.info("Entity '{}' found at span [{}:{}]... masked",
                        entityName, span.getStart(), span.getEnd());
                int length = span.getEnd() - span.getStart();
                StringBuilder maskBuilder = new StringBuilder();
                String maskChar = Config.getMaskingChar();
                for (int i = 0; i < length; i++) {
                    maskBuilder.append(maskChar);
                }
                String mask = maskBuilder.toString();
                // Replace using string concatenation like Python does
                result = result.substring(0, span.getStart()) + mask + result.substring(span.getEnd());
            }
        }
        
        return result;
    }
    
    /**
     * Helper method to unprotect a String using char arrays.
     */
    private static String unprotectString(com.protegrity.ap.java.Protector protector, SessionObject session, 
                                         String input, String dataElement) throws Exception {
        char[][] inputArray = {input.toCharArray()};
        char[][] outputArray = new char[1][input.length()];
        protector.unprotect(session, dataElement, inputArray, outputArray);
        return new String(outputArray[0]);
    }
    
    /**
     * Helper method to protect a String using char arrays.
     */
    private static String protectString(com.protegrity.ap.java.Protector protector, SessionObject session, 
                                       String input, String dataElement) throws Exception {
        char[][] inputArray = {input.toCharArray()};
        char[][] outputArray = new char[1][input.length()];
        protector.protect(session, dataElement, inputArray, outputArray);
        return new String(outputArray[0]);
    }
}
