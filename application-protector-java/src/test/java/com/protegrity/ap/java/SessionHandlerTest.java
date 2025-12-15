package com.protegrity.ap.java;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

public class SessionHandlerTest {

    private SessionHandler sessionHandler;

    @Before
    public void setUp() {
        sessionHandler = new SessionHandler(5); // 5 minute timeout
    }

    @Test
    public void testSessionHandlerCreation() {
        SessionHandler handler = new SessionHandler(10);
        assertNotNull(handler);
    }

    @Test
    public void testCreateSessionSuccess() throws ProtectorException {
        SessionObject session = sessionHandler.createSession("testUser");
        
        assertNotNull(session);
        assertNotNull(session.getSessionId());
    }

    @Test(expected = ProtectorException.class)
    public void testCreateSessionWithNull() throws ProtectorException {
        sessionHandler.createSession(null);
    }

    @Test(expected = ProtectorException.class)
    public void testCreateSessionWithEmptyString() throws ProtectorException {
        sessionHandler.createSession("");
    }

    @Test
    public void testCreateSessionWithWhitespace() throws ProtectorException {
        SessionObject session = sessionHandler.createSession("  user  ");
        assertNotNull(session);
    }

    @Test
    public void testMultipleSessionsAreUnique() throws ProtectorException {
        SessionObject session1 = sessionHandler.createSession("user1");
        SessionObject session2 = sessionHandler.createSession("user2");
        
        assertNotEquals(session1.getSessionId(), session2.getSessionId());
    }

    @Test
    public void testSessionHandlerWithZeroTimeout() {
        SessionHandler handler = new SessionHandler(0);
        assertNotNull(handler);
    }

    @Test
    public void testSessionHandlerWithNegativeTimeout() {
        SessionHandler handler = new SessionHandler(-1);
        assertNotNull(handler);
    }

    @Test
    public void testCreateSessionWithSpecialCharacters() throws ProtectorException {
        SessionObject session = sessionHandler.createSession("user@domain.com");
        assertNotNull(session);
        assertEquals("user@domain.com", session.getUser());
    }

    @Test
    public void testCreateSessionWithLongUsername() throws ProtectorException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("a");
        }
        String longUsername = sb.toString();
        SessionObject session = sessionHandler.createSession(longUsername);
        assertNotNull(session);
        assertEquals(longUsername, session.getUser());
    }
}
