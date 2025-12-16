package com.protegrity.ap.java;

/**
 * Exception class for Protector API operations.
 *
 * <p>This exception signals error conditions during data protection operations
 * and includes error codes, messages, and operation details for comprehensive
 * error handling.
 *
 * @author <a href="http://www.protegrity.com">Protegrity</a>
 * @since 1.0.1
 */
public class ProtectorException extends Exception {

  private static final long serialVersionUID = 1L;

  /** The error code associated with this exception. */
  public int errorCode;
  
  /** The detailed error message. */
  public String errorMessage;
  
  /** Array of error codes for bulk operations. */
  public int[] errorList;
  
  /** The operation that was not accessible. */
  public int noAccessOperation;

  /**
   * Constructs a new ProtectorException with full error details.
   * 
   * @param errorCode the error code
   * @param errorMessage the error message
   * @param errorList array of error codes for bulk operations
   * @param noAccessOperation the operation that was not accessible
   */
  public ProtectorException(
    int errorCode, String errorMessage, int[] errorList, int noAccessOperation) {
    super(errorMessage);  
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
    this.errorList = errorList;
    this.noAccessOperation = noAccessOperation;
  }

  /**
   * Constructs a new ProtectorException with error code, message, and error list.
   * 
   * @param errorCode the error code
   * @param errorMessage the error message
   * @param errorList array of error codes for bulk operations
   */
  public ProtectorException(int errorCode, String errorMessage, int[] errorList) {
    super(errorMessage);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
    this.errorList = errorList;
  }

  /**
   * Constructs a new ProtectorException with error code and message.
   * 
   * @param errorCode the error code
   * @param errorMessage the error message
   */
  public ProtectorException(int errorCode, String errorMessage) {
    super(errorMessage);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
  }

  /**
   * Constructs a new ProtectorException with only an error message.
   * 
   * @param errorMessage the error message
   */
  public ProtectorException(String errorMessage) {
    super(errorMessage);
    this.errorMessage = errorMessage;
  }
  
  /**
   * Constructs a new ProtectorException with a message and cause.
   * 
   * @param message the error message
   * @param cause the underlying cause of the exception
   */
  public ProtectorException(String message, Throwable cause) {
        super(message, cause);
        this.errorMessage = message;
    }

  /**
   * Returns the array of error codes for bulk operations.
   * 
   * @return the error list array
   */
  public int[] getErrorList() {
    return errorList;
  }

  /**
   * Returns the operation that was not accessible.
   * 
   * @return the no-access operation code
   */
  public int getAccessOperation() {
    return noAccessOperation;
  }

  /**
   * Returns the error code.
   * 
   * @return the error code
   */
  public int getErrorCode() {
    return errorCode;
  }

  /**
   * Sets the error code.
   * 
   * @param errorCode the error code to set
   */
  public void setErrorCode(int errorCode) {
    this.errorCode = errorCode;
  }

  /**
   * Returns the error message.
   * 
   * @return the error message
   */
  public String getErrorMessage() {
    return errorMessage;
  }

  /**
   * Sets the error message.
   * 
   * @param errorMessage the error message to set
   */
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

