package com.protegrity.devedition.utils;

import com.protegrity.ap.java.Protector;
import com.protegrity.ap.java.SessionObject;
import com.protegrity.ap.java.ProtectorException;
import java.io.IOException;
import java.text.ParseException;

/**
 * Utility class for interfacing with Protegrity's Protector API.
 * 
 * <p>This class provides convenience methods for initializing the Protector
 * and managing sessions for data protection operations. It maintains singleton
 * instances of both the Protector and SessionObject.
 * 
 * @since 1.0.0
 */
public class ProtectorUtil {
    
    private static Protector instance = null;
    private static SessionObject session = null;
    
    /**
     * Initialize the Protector instance.
     *
     * @return Protector instance
     */
    private static Protector initializeProtector() throws ProtectorException, IOException, InterruptedException, ParseException {
        if (instance == null) {
            instance =  Protector.getProtector();
        }
        return instance;
    }
    
    /**
     * Get or create a Protector session.
     *
     * @return Session instance
     */
    public static SessionObject getProtectorSession() {
        if (instance == null) {
            try {
                initializeProtector();
            } catch (ProtectorException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (session == null) {
            try {
                session = instance.createSession("superuser");
            } catch (ProtectorException e) {
                e.printStackTrace();
            }
        }
        return session;
    }
    
    /**
     * Get the Protector instance.
     *
     * @return Protector instance
     */
    public static Protector getProtector() {
        if (instance == null) {
            try {
                initializeProtector();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
}
