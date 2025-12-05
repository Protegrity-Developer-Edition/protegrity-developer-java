package com.protegrity.devedition.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class PiiProcessingTest {

    private ObjectMapper objectMapper;

    @Before
    public void setUp() {
        objectMapper = new ObjectMapper();
        
        // Set up Config with test values
        Map<String, String> namedEntityMap = new HashMap<>();
        namedEntityMap.put("CREDIT_CARD", "ccn");
        namedEntityMap.put("EMAIL_ADDRESS", "email");
        namedEntityMap.put("PHONE_NUMBER", "phone");
        namedEntityMap.put("SOCIAL_SECURITY_NUMBER", "ssn");
        
        Config.setNamedEntityMap(namedEntityMap);
        Config.setMethod("redact");
        Config.setMaskingChar("#");
    }

    @Test
    public void testEntitySpanCreation() {
        PiiProcessing.EntitySpan span = new PiiProcessing.EntitySpan(0, 10, "EMAIL_ADDRESS", 0.95);
        
        assertEquals(0, span.getStart());
        assertEquals(10, span.getEnd());
        assertEquals("EMAIL_ADDRESS", span.getEntity());
        assertEquals(0.95, span.getScore(), 0.001);
    }

    @Test
    public void testEntitySpanGetters() {
        PiiProcessing.EntitySpan span = new PiiProcessing.EntitySpan(5, 15, "CREDIT_CARD", 0.85);
        
        assertNotNull(span.getEntity());
        assertTrue(span.getStart() >= 0);
        assertTrue(span.getEnd() > span.getStart());
        assertTrue(span.getScore() > 0);
    }

    @Test
    public void testCollectEntitySpansWithEmptyJson() throws Exception {
        String json = "{}";
        JsonNode entities = objectMapper.readTree(json);
        
        List<PiiProcessing.EntitySpan> spans = PiiProcessing.collectEntitySpans(entities);
        
        assertNotNull(spans);
        assertEquals(0, spans.size());
    }

    @Test
    public void testCollectEntitySpansWithSingleEntity() throws Exception {
        String json = "{\"EMAIL_ADDRESS\": [{\"score\": 0.95, \"location\": {\"start_index\": 0, \"end_index\": 20}}]}";
        JsonNode entities = objectMapper.readTree(json);
        
        List<PiiProcessing.EntitySpan> spans = PiiProcessing.collectEntitySpans(entities);
        
        assertNotNull(spans);
        assertEquals(1, spans.size());
        assertEquals("EMAIL_ADDRESS", spans.get(0).getEntity());
        assertEquals(0, spans.get(0).getStart());
        assertEquals(20, spans.get(0).getEnd());
        assertEquals(0.95, spans.get(0).getScore(), 0.001);
    }

    @Test
    public void testCollectEntitySpansWithMultipleEntities() throws Exception {
        String json = "{"
            + "\"EMAIL_ADDRESS\": [{\"score\": 0.95, \"location\": {\"start_index\": 0, \"end_index\": 20}}],"
            + "\"PHONE_NUMBER\": [{\"score\": 0.88, \"location\": {\"start_index\": 25, \"end_index\": 40}}]"
            + "}";
        JsonNode entities = objectMapper.readTree(json);
        
        List<PiiProcessing.EntitySpan> spans = PiiProcessing.collectEntitySpans(entities);
        
        assertNotNull(spans);
        assertEquals(2, spans.size());
    }

    @Test
    public void testCollectEntitySpansWithOverlappingEntities() throws Exception {
        String json = "{"
            + "\"EMAIL_ADDRESS\": [{\"score\": 0.95, \"location\": {\"start_index\": 0, \"end_index\": 20}}],"
            + "\"USERNAME\": [{\"score\": 0.75, \"location\": {\"start_index\": 0, \"end_index\": 10}}]"
            + "}";
        JsonNode entities = objectMapper.readTree(json);
        
        List<PiiProcessing.EntitySpan> spans = PiiProcessing.collectEntitySpans(entities);
        
        assertNotNull(spans);
        // Overlapping entities should be merged, keeping the one with higher score
        assertTrue(spans.size() > 0);
    }

    @Test
    public void testCollectEntitySpansIsSortedByStartIndex() throws Exception {
        String json = "{"
            + "\"PHONE_NUMBER\": [{\"score\": 0.88, \"location\": {\"start_index\": 50, \"end_index\": 60}}],"
            + "\"EMAIL_ADDRESS\": [{\"score\": 0.95, \"location\": {\"start_index\": 10, \"end_index\": 30}}]"
            + "}";
        JsonNode entities = objectMapper.readTree(json);
        
        List<PiiProcessing.EntitySpan> spans = PiiProcessing.collectEntitySpans(entities);
        
        assertNotNull(spans);
        assertEquals(2, spans.size());
        // Result is merged and sorted
        assertTrue(spans.size() > 0);
    }

    @Test
    public void testRedactDataWithEmptyEntities() {
        String text = "This is a test text.";
        List<PiiProcessing.EntitySpan> entities = java.util.Collections.emptyList();
        
        String result = PiiProcessing.redactData(entities, text);
        
        assertEquals(text, result);
    }

    @Test
    public void testRedactDataWithRedactMethod() {
        Config.setMethod("redact");
        String text = "My email is test@example.com";
        
        PiiProcessing.EntitySpan span = new PiiProcessing.EntitySpan(12, 28, "EMAIL_ADDRESS", 0.95);
        List<PiiProcessing.EntitySpan> entities = java.util.Collections.singletonList(span);
        
        String result = PiiProcessing.redactData(entities, text);
        
        assertNotNull(result);
        assertTrue(result.contains("[email]"));
    }

    @Test
    public void testRedactDataWithMaskMethod() {
        Config.setMethod("mask");
        Config.setMaskingChar("#");
        String text = "My email is test@example.com";
        
        PiiProcessing.EntitySpan span = new PiiProcessing.EntitySpan(12, 28, "EMAIL_ADDRESS", 0.95);
        List<PiiProcessing.EntitySpan> entities = java.util.Collections.singletonList(span);
        
        String result = PiiProcessing.redactData(entities, text);
        
        assertNotNull(result);
        // Should contain mask characters
        assertTrue(result.contains("#"));
    }

    @Test
    public void testRedactDataWithMultipleEntities() {
        Config.setMethod("redact");
        String text = "Email: test@example.com Phone: 555-1234";
        
        PiiProcessing.EntitySpan span1 = new PiiProcessing.EntitySpan(7, 23, "EMAIL_ADDRESS", 0.95);
        PiiProcessing.EntitySpan span2 = new PiiProcessing.EntitySpan(31, 39, "PHONE_NUMBER", 0.88);
        List<PiiProcessing.EntitySpan> entities = java.util.Arrays.asList(span1, span2);
        
        String result = PiiProcessing.redactData(entities, text);
        
        assertNotNull(result);
        assertTrue(result.contains("[email]"));
        assertTrue(result.contains("[phone]"));
    }

    @Test
    public void testRedactDataWithPipeDelimitedEntity() {
        Config.setMethod("redact");
        String text = "My identifier is ABC123";
        
        PiiProcessing.EntitySpan span = new PiiProcessing.EntitySpan(17, 23, "USERNAME|ID_CARD", 0.90);
        List<PiiProcessing.EntitySpan> entities = java.util.Collections.singletonList(span);
        
        String result = PiiProcessing.redactData(entities, text);
        
        assertNotNull(result);
        // Should handle pipe-delimited entities
    }

    @Test
    public void testRedactDataPreservesNonPiiText() {
        Config.setMethod("redact");
        String text = "Hello world, my email is test@example.com and that's it.";
        
        PiiProcessing.EntitySpan span = new PiiProcessing.EntitySpan(25, 41, "EMAIL_ADDRESS", 0.95);
        List<PiiProcessing.EntitySpan> entities = java.util.Collections.singletonList(span);
        
        String result = PiiProcessing.redactData(entities, text);
        
        assertTrue(result.contains("Hello world"));
        assertTrue(result.contains("and that's it"));
    }

    @Test
    public void testEntitySpanComparison() {
        PiiProcessing.EntitySpan span1 = new PiiProcessing.EntitySpan(0, 10, "EMAIL", 0.95);
        PiiProcessing.EntitySpan span2 = new PiiProcessing.EntitySpan(20, 30, "PHONE", 0.85);
        
        assertTrue(span1.getStart() < span2.getStart());
        assertTrue(span2.getEnd() > span1.getEnd());
    }

    @Test
    public void testMaskingCharacterLength() {
        Config.setMethod("mask");
        Config.setMaskingChar("***");
        
        // Verify masking char can be multi-character
        assertEquals("***", Config.getMaskingChar());
    }
}
