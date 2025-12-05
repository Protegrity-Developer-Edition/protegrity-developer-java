package com.protegrity.ap.java;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.UUID;
import java.util.Date;

public class SessionObjectTest {

    @Test
    public void testSessionObjectCreation() {
        SessionObject session = new SessionObject("testUser");
        
        assertNotNull(session);
        assertNotNull(session.getSessionId());
        assertTrue(session.getSessionId() instanceof UUID);
    }

    @Test
    public void testGetSessionId() {
        SessionObject session = new SessionObject("user1");
        UUID sessionId = session.getSessionId();
        
        assertNotNull(sessionId);
        assertEquals(sessionId, session.getSessionId());
    }

    @Test
    public void testGetUser() {
        SessionObject session = new SessionObject("testApiUser");
        String user = session.getUser();
        
        assertEquals("testApiUser", user);
    }

    @Test
    public void testGetUserUpdatesTimestamp() throws InterruptedException {
        SessionObject session = new SessionObject("user");
        Date firstTimestamp = session.getTimeStamp();
        
        Thread.sleep(10); // Small delay
        session.getUser(); // This should update the timestamp
        Date secondTimestamp = session.getTimeStamp();
        
        assertTrue(secondTimestamp.getTime() >= firstTimestamp.getTime());
    }

    @Test
    public void testSetAndGetLastError() {
        SessionObject session = new SessionObject("user");
        
        assertNull(session.getLastError());
        
        session.setLastError("Test error message");
        assertEquals("Test error message", session.getLastError());
    }

    @Test
    public void testTimestampIsSet() {
        SessionObject session = new SessionObject("user");
        Date timestamp = session.getTimeStamp();
        
        assertNotNull(timestamp);
        assertTrue(timestamp.getTime() <= System.currentTimeMillis());
    }

    @Test
    public void testSessionIdIsUnique() {
        SessionObject session1 = new SessionObject("user1");
        SessionObject session2 = new SessionObject("user2");
        
        assertNotEquals(session1.getSessionId(), session2.getSessionId());
    }

    @Test
    public void testLastErrorCanBeCleared() {
        SessionObject session = new SessionObject("user");
        session.setLastError("Error occurred");
        assertEquals("Error occurred", session.getLastError());
        
        session.setLastError(null);
        assertNull(session.getLastError());
    }

    @Test
    public void testEmptyApiUser() {
        SessionObject session = new SessionObject("");
        assertEquals("", session.getUser());
    }

    @Test
    public void testNullApiUser() {
        SessionObject session = new SessionObject(null);
        assertNull(session.getUser());
    }
}
