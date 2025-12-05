package com.protegrity.ap.java;

/**
 * Holds information about exceptions.
 *
 * <p>Signals an error condition during API usage.
 *
 * @author <a href="http://www.protegrity.com">Protegrity</a> {@inheritDoc}
 */
public class ProtectorException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a new protector exception with the specified detail message.
   *
   * @param s the detail message. The detail message is saved for later retrieval by the
   *     Throwable.getMessage() method.
   */
  
  public int errorCode;
  public String errorMessage;
  public int[] errorList;
  public int noAccessOperation;

  public ProtectorException(
    int errorCode, String errorMessage, int[] errorList, int noAccessOperation) {
    super(errorMessage);  
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
    this.errorList = errorList;
    this.noAccessOperation = noAccessOperation;
  }

  public ProtectorException(int errorCode, String errorMessage, int[] errorList) {
    super(errorMessage);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
    this.errorList = errorList;
  }

  public ProtectorException(int errorCode, String errorMessage) {
    super(errorMessage);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }

  public int[] getErrorList() {
    return errorList;
  }

  public int getAccessOperation() {
    return noAccessOperation;
  }

  public ProtectorException(String errorMessage) {
    super(errorMessage);
    this.errorMessage = errorMessage;
  }
  
  public ProtectorException(String message, Throwable cause) {
        super(message, cause);
        this.errorMessage = message;
    }

  public int getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

@Override
public String getMessage() {
    if (errorMessage != null && errorCode != 0) {
        return "Error Code: " + errorCode + ", " + errorMessage;
    } else if (errorMessage != null) {
        return errorMessage;
    } else {
        return super.getMessage();
    }
}




}

