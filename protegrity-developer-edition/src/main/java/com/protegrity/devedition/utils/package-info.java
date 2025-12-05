/**
 * Protegrity Developer Edition utility classes for PII detection and data protection.
 * 
 * <p>This package provides utility classes for discovering, classifying, and protecting
 * Personally Identifiable Information (PII) in unstructured text. It includes functionality
 * for entity detection, redaction, masking, and integration with Protegrity protection services.
 * 
 * <h2>Main Classes</h2>
 * <ul>
 *   <li>{@link com.protegrity.devedition.utils.PiiProcessing} - Core PII processing operations (redact, mask, protect)</li>
 *   <li>{@link com.protegrity.devedition.utils.Discover} - PII entity discovery and classification</li>
 *   <li>{@link com.protegrity.devedition.utils.Config} - Configuration management for discovery and protection</li>
 *   <li>{@link com.protegrity.devedition.utils.CcnProcessing} - Credit card number detection and processing</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * Config config = Config.fromFile("config.json");
 * Discover discover = new Discover(config);
 * JsonNode entities = discover.findEntities(text);
 * String redacted = PiiProcessing.redactData(entities, text);
 * }</pre>
 * 
 * @since 1.0.0
 */
package com.protegrity.devedition.utils;
