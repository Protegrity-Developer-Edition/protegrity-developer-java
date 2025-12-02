package com.protegrity.ap.java;

//import com.protegrity.jcorelite.jni.JcoreliteConfig;
import java.lang.reflect.Field;

public class SessionHandler {
  private int timeToLive = 0;

  public SessionHandler(int sessionTimeout) {
    timeToLive = sessionTimeout * 60 * 1000;
  }

  public SessionObject createSession(String apiUser) throws ProtectorException {
    if (null == apiUser) {
      throw new ProtectorException("Null input");
    }
    if (true == "".equals(apiUser)) {
      throw new ProtectorException("Empty input");
    }

    return new SessionObject(apiUser);
  }

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

  public String getUser(SessionObject session) throws ProtectorException {
    if (!isValid(session)) {
      throw new SessionTimeoutException("Session timeout or invalid");
    }
    return session.getUser();
  }

  public void setLastError(SessionObject session, String errorMessage) throws ProtectorException {
    session.setLastError(errorMessage);
  }

  public String getLastError(SessionObject session) throws ProtectorException {
    return session.getLastError();
  }
}
