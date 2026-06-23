package com.protegrity.devedition.utils;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Map;

public class ConstantsTest {

    @Test
    public void testDataElementMappingNotNull() {
        assertNotNull(Constants.DATA_ELEMENT_MAPPING);
    }

    @Test
    public void testDataElementMappingNotEmpty() {
        assertTrue(Constants.DATA_ELEMENT_MAPPING.size() > 0);
    }

    @Test
    public void testCreditCardMapping() {
        assertEquals("ccn", Constants.DATA_ELEMENT_MAPPING.get("CREDIT_CARD"));
    }

    @Test
    public void testEmailAddressMapping() {
        assertEquals("email", Constants.DATA_ELEMENT_MAPPING.get("EMAIL_ADDRESS"));
    }

    @Test
    public void testSsnMapping() {
        assertEquals("ssn", Constants.DATA_ELEMENT_MAPPING.get("SOCIAL_SECURITY_ID"));
    }

    @Test
    public void testPhoneNumberMapping() {
        assertEquals("phone", Constants.DATA_ELEMENT_MAPPING.get("PHONE_NUMBER"));
    }

    @Test
    public void testPassportMapping() {
        assertEquals("passport", Constants.DATA_ELEMENT_MAPPING.get("PASSPORT"));
    }

    @Test
    public void testAddressMappings() {
        assertEquals("address", Constants.DATA_ELEMENT_MAPPING.get("CRYPTO_ADDRESS"));
        assertEquals("address", Constants.DATA_ELEMENT_MAPPING.get("LOCATION"));
        assertEquals("address", Constants.DATA_ELEMENT_MAPPING.get("ADDRESS"));
        assertEquals("address", Constants.DATA_ELEMENT_MAPPING.get("IP_ADDRESS"));
        assertEquals("address", Constants.DATA_ELEMENT_MAPPING.get("MAC_ADDRESS"));
        assertEquals("address", Constants.DATA_ELEMENT_MAPPING.get("URL"));
    }

    @Test
    public void testNumberMappings() {
        assertEquals("number", Constants.DATA_ELEMENT_MAPPING.get("ACCOUNT_NUMBER"));
        assertEquals("number", Constants.DATA_ELEMENT_MAPPING.get("AGE"));
        assertEquals("number", Constants.DATA_ELEMENT_MAPPING.get("AMOUNT"));
        assertEquals("number", Constants.DATA_ELEMENT_MAPPING.get("BANK_ACCOUNT"));
        assertEquals("number", Constants.DATA_ELEMENT_MAPPING.get("DRIVER_LICENSE"));
        assertEquals("number", Constants.DATA_ELEMENT_MAPPING.get("IN_VEHICLE_REGISTRATION"));
        assertEquals("number", Constants.DATA_ELEMENT_MAPPING.get("IN_VOTER"));
        assertEquals("number", Constants.DATA_ELEMENT_MAPPING.get("NRP"));
        assertEquals("number", Constants.DATA_ELEMENT_MAPPING.get("TAX_ID"));
        assertEquals("number", Constants.DATA_ELEMENT_MAPPING.get("HEALTH_CARE_ID"));
        assertEquals("number", Constants.DATA_ELEMENT_MAPPING.get("KR_RRN"));
    }

    @Test
    public void testDateTimeMappings() {
        assertEquals("datetime", Constants.DATA_ELEMENT_MAPPING.get("DATETIME"));
        assertEquals("datetime", Constants.DATA_ELEMENT_MAPPING.get("DOB"));
    }

    @Test
    public void testStringMappings() {
        assertEquals("string", Constants.DATA_ELEMENT_MAPPING.get("ACCOUNT_NAME"));
        assertEquals("string", Constants.DATA_ELEMENT_MAPPING.get("CURRENCY"));
        assertEquals("string", Constants.DATA_ELEMENT_MAPPING.get("CURRENCY_CODE"));
        assertEquals("string", Constants.DATA_ELEMENT_MAPPING.get("CURRENCY_NAME"));
        assertEquals("string", Constants.DATA_ELEMENT_MAPPING.get("CURRENCY_SYMBOL"));
        assertEquals("string", Constants.DATA_ELEMENT_MAPPING.get("GENDER"));
        assertEquals("string", Constants.DATA_ELEMENT_MAPPING.get("ORGANIZATION"));
        assertEquals("string", Constants.DATA_ELEMENT_MAPPING.get("PASSWORD"));
        assertEquals("string", Constants.DATA_ELEMENT_MAPPING.get("PERSON"));
        assertEquals("string", Constants.DATA_ELEMENT_MAPPING.get("TITLE"));
        assertEquals("string", Constants.DATA_ELEMENT_MAPPING.get("USERNAME"));
        assertEquals("string", Constants.DATA_ELEMENT_MAPPING.get("IN_GSTIN"));
    }

    @Test
    public void testNationalIdMapping() {
        assertEquals("nin", Constants.DATA_ELEMENT_MAPPING.get("NATIONAL_ID"));
        assertEquals("nin", Constants.DATA_ELEMENT_MAPPING.get("TH_TNIN"));
    }

    @Test
    public void testNonExistentEntityReturnsNull() {
        assertNull(Constants.DATA_ELEMENT_MAPPING.get("NON_EXISTENT_ENTITY"));
    }

    @Test
    public void testDataElementMappingSize() {
        // HashMap is modifiable, so test that it has expected size
        int size = Constants.DATA_ELEMENT_MAPPING.size();
        assertTrue("Mapping should have entries", size > 30);
    }

    @Test
    public void testAllMappedValuesAreNonEmpty() {
        for (Map.Entry<String, String> entry : Constants.DATA_ELEMENT_MAPPING.entrySet()) {
            assertNotNull("Value should not be null for key: " + entry.getKey(), entry.getValue());
            assertTrue("Value should not be empty for key: " + entry.getKey(), entry.getValue().length() > 0);
        }
    }

    @Test
    public void testAllKeysAreUppercase() {
        for (String key : Constants.DATA_ELEMENT_MAPPING.keySet()) {
            assertEquals("Key should be uppercase: " + key, key, key.toUpperCase());
        }
    }

    @Test
    public void testMappingContainsCommonEntities() {
        // Test that commonly used entities are present
        assertTrue(Constants.DATA_ELEMENT_MAPPING.containsKey("CREDIT_CARD"));
        assertTrue(Constants.DATA_ELEMENT_MAPPING.containsKey("EMAIL_ADDRESS"));
        assertTrue(Constants.DATA_ELEMENT_MAPPING.containsKey("PHONE_NUMBER"));
        assertTrue(Constants.DATA_ELEMENT_MAPPING.containsKey("SOCIAL_SECURITY_ID"));
        assertTrue(Constants.DATA_ELEMENT_MAPPING.containsKey("PASSPORT"));
        assertTrue(Constants.DATA_ELEMENT_MAPPING.containsKey("IP_ADDRESS"));
        assertTrue(Constants.DATA_ELEMENT_MAPPING.containsKey("DOB"));
    }
}
