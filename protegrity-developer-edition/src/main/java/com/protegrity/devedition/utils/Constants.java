package com.protegrity.devedition.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Module for constants and default configurations.
 */
public class Constants {
    
    /**
     * Mapping of entity types to Protegrity data elements
     */
    public static final Map<String, String> DATA_ELEMENT_MAPPING = new HashMap<String, String>() {{
        // Account Information
        put("ACCOUNT_NAME", "string");
        put("ACCOUNT_NUMBER", "number");
        
        // Personal Information
        put("AGE", "number");
        put("GENDER", "string");
        put("TITLE", "string");
        put("PERSON", "string");
        
        // Financial
        put("AMOUNT", "number");
        put("BANK_ACCOUNT", "number");
        put("CREDIT_CARD", "ccn");
        put("CURRENCY", "string");
        put("CURRENCY_CODE", "string");
        put("CURRENCY_NAME", "string");
        put("CURRENCY_SYMBOL", "string");
        
        // Cryptocurrency
        put("CRYPTO_ADDRESS", "address");
        
        // Temporal
        put("DATETIME", "datetime");
        put("DOB", "datetime");
        
        // Identification Documents
        put("DRIVER_LICENSE", "number");
        put("PASSPORT", "passport");
        put("NATIONAL_ID", "nin");
        put("SOCIAL_SECURITY_ID", "ssn");
        
        // Contact Information
        put("EMAIL_ADDRESS", "email");
        put("PHONE_NUMBER", "phone");
        
        // Healthcare
        put("HEALTH_CARE_ID", "number");
        
        // Location Information
        put("LOCATION", "address");
        put("ADDRESS", "address");
        
        // Network
        put("IP_ADDRESS", "address");
        put("MAC_ADDRESS", "address");
        put("URL", "address");
        
        // Tax
        put("TAX_ID", "number");
        
        // India-specific
        put("IN_VEHICLE_REGISTRATION", "number");
        put("IN_VOTER", "number");
        put("IN_GSTIN", "string");
        
        // Security
        put("PASSWORD", "string");
        put("USERNAME", "string");
        
        // Organization
        put("ORGANIZATION", "string");
        
        // National Registration
        put("NRP", "number");
        
        // Korea
        put("KR_RRN", "number");
        
        // Thailand
        put("TH_TNIN", "nin");
    }};
}
