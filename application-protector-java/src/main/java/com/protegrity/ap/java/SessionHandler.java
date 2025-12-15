package com.protegrity.ap.java;

//import com.protegrity.jcorelite.jni.JcoreliteConfig;
import java.lang.reflect.Field;

/**
 * Manages user sessions for Protector operations.
 * 
 * <p>This class handles session creation, validation, and lifecycle management
 * including timeout detection. Sessions are validated based on a configurable
 * time-to-live duration.
 * 
 * @since 1.0.0
 */
public class SessionHandler {
  private int timeToLive = 0;

  /**
   * Constructs a new SessionHandler with the specified timeout.
   * 
   * @param sessionTimeout session timeout in minutes
   */
  public SessionHandler(int sessionTimeout) {
    timeToLive = sessionTimeout * 60 * 1000;
  }

  /**
   * Creates a new session for the specified user.
   * 
   * @param apiUser the username for the session
   * @return a new SessionObject
   * @throws ProtectorException if the username is null or empty
   */
  public SessionObject createSession(String apiUser) throws ProtectorException {
    if (null == apiUser) {
      throw new ProtectorException("Null input");
    }
    if (true == "".equals(apiUser)) {
      throw new ProtectorException("Empty input");
    }

    return new SessionObject(apiUser);
  }

  /**
   * Closes a session by clearing all session fields.
   * 
   * @deprecated This method uses reflection to clear fields and may not be reliable.
   * @param session the session to close
   * @throws ProtectorException if closing the session fails
   */
  @Deprecated
  public void closeSession(SessionObject session) throws ProtectorException {
    try {
      Field[] fields = session.getClass().getDeclaredFields();
      for (Field field : fields) {
        field.setAccessible(true);
        field.set(session, null);
      }
    } catch (Throwable e) {
      throw new ProtectorException(e.getMessage());
    }
  }

  /**
   * Validates whether a session is still active.
   * 
   * @param session the session to validate
   * @return true if the session is valid and not expired, false otherwise
   */
  private boolean isValid(SessionObject session) {
    if (null == session) {
      return false;
    }
    if (session.getSessionId() == null) {
      return false;
    }
    java.util.Date now = new java.util.Date();
    if (now.getTime() - session.getTimeStamp().getTime() >= timeToLive) {
      return false;
    }
    return true;
  }

  /**
   * Gets the username associated with the session.
   * 
   * @param session the session to query
   * @return the username
   * @throws ProtectorException if the session is invalid or expired
   */
  public String getUser(SessionObject session) throws ProtectorException {
    if (!isValid(session)) {
      throw new SessionTimeoutException("Session timeout or invalid");
    }
    return session.getUser();
  }

  /**
   * Sets the last error message for the session.
   * 
   * @param session the session to update
   * @param errorMessage the error message to set
   * @throws ProtectorException if setting the error fails
   */
  public void setLastError(SessionObject session, String errorMessage) throws ProtectorException {
    session.setLastError(errorMessage);
  }

  /**
   * Gets the last error message for the session.
   * 
   * @param session the session to query
   * @return the last error message, or null if no error occurred
   * @throws ProtectorException if getting the error fails
   */
  public String getLastError(SessionObject session) throws ProtectorException {
    return session.getLastError();
  }
}
