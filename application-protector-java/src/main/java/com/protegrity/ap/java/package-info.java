/**
 * Protegrity Application Protector Java API.
 * 
 * <p>This package provides the core Java SDK for protecting and unprotecting sensitive data
 * using Protegrity's Data Protection services. It includes classes for session management,
 * authentication, and data transformation operations.
 * 
 * <h2>Main Classes</h2>
 * <ul>
 *   <li>{@link com.protegrity.ap.java.Protector} - Main entry point for protection operations</li>
 *   <li>{@link com.protegrity.ap.java.SessionObject} - Manages user sessions for protection operations</li>
 *   <li>{@link com.protegrity.ap.java.Authenticator} - Handles authentication with Protegrity services</li>
 * </ul>
 * 
 * <h2>Usage Example</h2>
 * <pre>{@code
 * Protector protector = Protector.getProtector();
 * SessionObject session = protector.createSession("username");
 * String[] protectedData = new String[data.length];
 * protector.protect(session, "dataElement", data, protectedData);
 * }</pre>
 * 
 * @since 1.0.0
 */
package com.protegrity.ap.java;
