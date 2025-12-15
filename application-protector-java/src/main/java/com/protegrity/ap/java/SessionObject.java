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
   * @return The session UUID
   */
  public java.util.UUID getSessionId() {
    return sessionId;
  }

  /**
   * Get the user associated with this session.
   * Updates the session timestamp.
   * 
   * @return the username, or null if session is invalid
   */
  String getUser() {
    this.timeStamp = this.sessionId != null ? new java.util.Date() : null;
    return this.apiUser;
  }

  /**
   * Get the timestamp of the last session activity.
   * 
   * @return the timestamp
   */
  java.util.Date getTimeStamp() {
    return timeStamp;
  }

  /**
   * Set the last error message for this session.
   * 
   * @param errorMsg the error message
   */
  void setLastError(String errorMsg) {
    this.lastError = errorMsg;
  }

  /**
   * Get the last error message for this session.
   * 
   * @return the last error message, or null if no error occurred
   */
  String getLastError() {
    return this.lastError;
  }
}
