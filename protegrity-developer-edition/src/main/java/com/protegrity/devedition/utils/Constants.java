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
        put("ABA_ROUTING_NUMBER", "number");
        put("ACCOUNT_NAME", "string");
        put("ACCOUNT_NUMBER", "number");
        put("AGE", "number");
        put("AMOUNT", "number");
        put("AU_ABN", "number");
        put("AU_ACN", "number");
        put("AU_MEDICARE", "number");
        put("AU_TFN", "number");
        put("BIC", "number");
        put("BITCOIN_ADDRESS", "address");
        put("BUILDING", "address");
        put("CITY", "city");
        put("COMPANY_NAME", "string");
        put("COUNTRY", "string");
        put("COUNTY", "string");
        put("CREDIT_CARD", "ccn");
        put("CREDIT_CARD_CVV", "number");
        put("CRYPTO", "address");
        put("CURRENCY", "string");
        put("CURRENCY_CODE", "string");
        put("CURRENCY_NAME", "string");
        put("CURRENCY_SYMBOL", "string");
        put("DATE", "datetime");
        put("DATE_OF_BIRTH", "datetime");
        put("DATE_TIME", "datetime");
        put("DRIVER_LICENSE", "number");
        put("EMAIL_ADDRESS", "email");
        put("ES_NIE", "nin");
        put("ES_NIF", "nin");
        put("ETHEREUM_ADDRESS", "address");
        put("FI_PERSONAL_IDENTITY_CODE", "nin");
        put("GENDER", "string");
        put("GEO_CCORDINATE", "address");
        put("IBAN_CODE", "iban");
        put("ID_CARD", "number");
        put("IN_AADHAAR", "nin");
        put("IN_PAN", "number");
        put("IN_PASSPORT", "passport");
        put("IN_VEHICLE_REGISTRATION", "number");
        put("IN_VOTER", "number");
        put("IP_ADDRESS", "address");
        put("IPV4", "address");
        put("IPV6", "address");
        put("IT_DRIVER_LICENSE", "number");
        put("IT_FISCAL_CODE", "nin");
        put("IT_IDENTITY_CARD", "number");
        put("IT_PASSPORT", "passport");
        put("LITECOIN_ADDRESS", "address");
        put("LOCATION", "address");
        put("MAC", "address");
        put("MEDICAL_LICENSE", "number");
        put("NRP", "number");
        put("ORGANIZATION", "string");
        put("PASSPORT", "passport");
        put("PASSWORD", "string");
        put("PERSON", "string");
        put("PHONE_NUMBER", "phone");
        put("PIN", "number");
        put("PL_PESEL", "nin");
        put("SECONDARYADDRESS", "address");
        put("SG_NRIC_FIN", "nin");
        put("SG_UEN", "number");
        put("SOCIAL_SECURITY_NUMBER", "ssn");
        put("STATE", "string");
        put("STREET", "address");
        put("TIME", "datetime");
        put("TITLE", "string");
        put("UK_NHS", "number");
        put("URL", "address");
        put("US_BANK_NUMBER", "number");
        put("US_DRIVER_LICENSE", "number");
        put("US_ITIN", "number");
        put("US_PASSPORT", "passport");
        put("US_SSN", "ssn");
        put("USERNAME", "string");
        put("ZIP_CODE", "zipcode");
    }};
}
