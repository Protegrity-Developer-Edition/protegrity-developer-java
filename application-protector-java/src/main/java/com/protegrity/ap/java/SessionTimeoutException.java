package com.protegrity.ap.java;

//import com.protegrity.jcorelite.jni.JcoreliteConfig;

/**
 * Holds information about exceptions.
 *
 * <p>Signals an error condition during API usage.
 *
 * @author <a href="http://www.protegrity.com">Protegrity</a> {@inheritDoc}
 */
public class SessionTimeoutException extends ProtectorException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new protector exception with the specified detail message.
   *
   * @param s the detail message. The detail message is saved for later retrieval by the
   *     Throwable.getMessage() method.
   */
  public SessionTimeoutException(String s) {
    super(s);
    
  }
}
