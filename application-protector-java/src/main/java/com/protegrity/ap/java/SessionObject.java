package com.protegrity.ap.java;

/**
 * Hold the session object used in the API methods.
 *
 * <p>The session object is obtained by using the API method createSession. An example follows:
 *
 * <p>Sample source:
 *
 * <pre>{@code
 * SessionObject session = protectorApi.createSession( "policy user" );
 * }</pre>
 *
 * @author <a href="http://www.protegrity.com">Protegrity</a> {@inheritDoc}
 */
public class SessionObject {
  private java.util.UUID sessionId;
  private java.util.Date timeStamp;
  private String apiUser;
  private String lastError;

  /**
   * Default construct
   *
   * @param apiUser String containing the user name defined in the policy
   */
  public SessionObject(String apiUser) {
    this.sessionId = java.util.UUID.randomUUID();
    this.apiUser = apiUser;
    this.timeStamp = new java.util.Date();
  }

  /**
   * Get session id. Retrieve the unique session id.
   *
   * @return The session
   */
  public java.util.UUID getSessionId() {
    return sessionId;
  }

  String getUser() {
    this.timeStamp = this.sessionId != null ? new java.util.Date() : null;
    return this.apiUser;
  }

  java.util.Date getTimeStamp() {
    return timeStamp;
  }

  void setLastError(String errorMsg) {
    this.lastError = errorMsg;
  }

  String getLastError() {
    return this.lastError;
  }
}
