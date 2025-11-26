package com.protegrity.ap.java;

/**
 * Contains all methods available in this protector API.
 *
 * <p>Methods includes:
 *
 * <ul>
 *   <li>Get product version
 *   <li>Get last error
 *   <li>Protect (including HMAC)
 *   <li>Un-protect
 *   <li>Re-protect
 * </ul>
 *
 * <p>
 *
 * @author <a href="http://www.protegrity.com">Protegrity</a> {@inheritDoc}
 */
public class Protector {
  
  public static final String HOST = "api.developer-edition.protegrity.com";
  public static final String VERSION = "1";
  
  // Encoding constants
  private static final String ENCODING_UTF8 = "utf8";
  private static final String ENCODING_BASE64 = "base64";
  
  // Operation constants
  private static final String OPERATION_PROTECT = "protect";
  private static final String OPERATION_UNPROTECT = "unprotect";
  private static final String OPERATION_REPROTECT = "reprotect";
  
  private static Protector instance = null;
  private CoreproviderAdapter coreproviderAdapter = null;
  private static SessionHandler sessionHandler = null;
  private String apiKey ;
  private String jwtToken;

  private Protector(SessionHandler sessionHandler) throws ProtectorException {
   coreproviderAdapter = new CoreproviderAdapter(sessionHandler);
  }

  public static synchronized Protector getProtector() throws ProtectorException {
    if (instance == null) {
     try {
            Authenticator authenticator = new Authenticator();
            String apiKey = authenticator.getApiKey();
            String jwtToken = authenticator.getJwtToken();
            sessionHandler = new SessionHandler(15);
            instance = new Protector(sessionHandler);
            instance.apiKey = apiKey;
            instance.jwtToken = jwtToken;
        } catch (InitializationException e) {
         throw new ProtectorException("Failed to Initialize Protector", e);
      }
    }
    return instance;
  }

  /**
   * Get product version.
   *
   * @return String The version
   */
  public String getVersion() {
    return coreproviderAdapter.getVersion();
  }

  /**
   * Returns the extended version of the Product in use.
   *
   * <p>The extended version consists of Product version number and Core version number. Core
   * version number can be communicated to the Protegrity Support while troubleshooting AP Java
   * related issues.
   *
   * @return String Product and the Core version
   */
  public String getVersionEx() {
    return String.format(
        "SDK Version: %s, Core Version: %s",
        coreproviderAdapter.getVersion(), coreproviderAdapter.getCoreVersion());
  }

  /**
   * Returns the Protectors' core version. Core version will be useful during debugging Protector
   * issues. It is only applicable for Protector version 8.1 or above.
   *
   * @return String core version used in the Protector
   */
  @Deprecated
  public String getCoreVersion() {
    return coreproviderAdapter.getCoreVersion();
  }

  /**
   * Get last error with a description of why the method returned false. (example "The username
   * could not be found in the policy. in shared memory." ).
   *
   * @param session The session
   * @return String The error message
   * @throws ProtectorException When input is null
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public String getLastError(SessionObject session)
      throws ProtectorException, SessionTimeoutException {
      return sessionHandler.getLastError(session);
  }

  /**
   * Create a session. Sessions that are not utilized are automatically removed.
   *
   * @param policyUser String containing the user name defined in policy
   * @return SessionObject The session
   * @throws ProtectorException When input is empty or null
   */
  public synchronized SessionObject createSession(String policyUser) throws ProtectorException {
    
    return sessionHandler.createSession(policyUser);
  }

  /**
   * Close session. Use this method with caution since you do not want to create too many audit
   * records.
   *
   * @param session Obtained by calling createSession method
   * @throws ProtectorException When input is empty or null
   * @deprecated As of release 9.0.
   */
  @Deprecated
  public synchronized void closeSession(SessionObject session) throws ProtectorException {
    sessionHandler.closeSession(session);
  }

  /**
   * Protect short using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy name defined
   *     in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean protect(
      SessionObject sessionObj, String dataElementName, short[] input, short[] output)
      throws ProtectorException, SessionTimeoutException {
          String[] stringInput = coreproviderAdapter.convertToStringArray(input);
          String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_UTF8);
          String response = coreproviderAdapter.sendApiRequest(OPERATION_PROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
          ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  short[].class, ENCODING_UTF8);
          boolean success = result.isSuccess();
          if(success){
             short[] converted = (short[]) result.getConvertedArray();
             System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
          }
          
          return success;
  }

  /**
   * Protect short using encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy name defined
   *     in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean protect(
      SessionObject sessionObj, String dataElementName, short[] input, byte[][] output)
      throws ProtectorException, SessionTimeoutException {
         String[] stringInput = coreproviderAdapter.convertToStringArray(input);
          String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_UTF8);
          String response = coreproviderAdapter.sendApiRequest(OPERATION_PROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
          ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj,response,  byte[][].class, ENCODING_UTF8);
          boolean success = result.isSuccess();
          if(success){
          byte[][] converted = (byte[][]) result.getConvertedArray();
          System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
          }
          return success;
  }

  /**
   * Protect short using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @param externalIv Buffer containing data that will be used as external IV, when externalIv =
   *     null the value is ignored, when externalIv = null the value is ignored
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean protect(
      SessionObject sessionObj,
      String dataElementName,
      short[] input,
      short[] output,
      byte[] externalIv)
      throws ProtectorException, SessionTimeoutException {
     String[] stringInput = coreproviderAdapter.convertToStringArray(input);
          String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, externalIv, ENCODING_UTF8);
          String response = coreproviderAdapter.sendApiRequest(OPERATION_PROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
          ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  short[].class, ENCODING_UTF8);
          boolean success = result.isSuccess();
          short[] converted = (short[]) result.getConvertedArray();
          System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
          return success;
  }

  /**
   * Protect short using encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @param externalIv Buffer containing data that will be used as external IV, when externalIv =
   *     null the value is ignored
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean protect(
      SessionObject sessionObj,
      String dataElementName,
      short[] input,
      byte[][] output,
      byte[] externalIv)
      throws ProtectorException, SessionTimeoutException {
       String[] stringInput = coreproviderAdapter.convertToStringArray(input);
          String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, externalIv, ENCODING_UTF8);
          String response = coreproviderAdapter.sendApiRequest(OPERATION_PROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
          ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  byte[][].class, ENCODING_UTF8);
          boolean success = result.isSuccess();
          byte[][] converted = (byte[][]) result.getConvertedArray();
          System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
          return success;
  }

  /**
   * Protect int using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy name defined
   *     in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean protect(
      SessionObject sessionObj, String dataElementName, int[] input, int[] output)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_PROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  int[].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        if(success){
          int[] converted = (int[]) result.getConvertedArray();
          System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        }
        return success;
  }

  /**
   * Protect int using encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy name defined
   *     in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean protect(
      SessionObject sessionObj, String dataElementName, int[] input, byte[][] output)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_PROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  byte[][].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        byte[][] converted = (byte[][]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Protect int using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @param externalIv Buffer containing data that will be used as external IV, when externalIv =
   *     null the value is ignored
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean protect(
      SessionObject sessionObj,
      String dataElementName,
      int[] input,
      int[] output,
      byte[] externalIv)
      throws ProtectorException, SessionTimeoutException {
      String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, externalIv, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_PROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  int[].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        int[] converted = (int[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Protect int using encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @param externalIv Buffer containing data that will be used as external IV, when externalIv =
   *     null the value is ignored
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean protect(
      SessionObject sessionObj,
      String dataElementName,
      int[] input,
      byte[][] output,
      byte[] externalIv)
      throws ProtectorException, SessionTimeoutException {
      String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, externalIv, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_PROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  byte[][].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        byte[][] converted = (byte[][]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Protect long using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy name defined
   *     in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean protect(
      SessionObject sessionObj, String dataElementName, long[] input, long[] output)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_PROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  long[].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        long[] converted = (long[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Protect long using encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy name defined
   *     in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean protect(
      SessionObject sessionObj, String dataElementName, long[] input, byte[][] output)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_PROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  byte[][].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        byte[][] converted = (byte[][]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Protect long using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @param externalIv Buffer containing data that will be used as external IV, when externalIv =
   *     null the value is ignored
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean protect(
      SessionObject sessionObj,
      String dataElementName,
      long[] input,
      long[] output,
      byte[] externalIv)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, externalIv, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_PROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  long[].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        long[] converted = (long[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Protect long using encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @param externalIv Buffer containing data that will be used as external IV, when externalIv =
   *     null the value is ignored
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean protect(
      SessionObject sessionObj,
      String dataElementName,
      long[] input,
      byte[][] output,
      byte[] externalIv)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, externalIv, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_PROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  byte[][].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        byte[][] converted = (byte[][]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Protect float using no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean protect(
      SessionObject sessionObj, String dataElementName, float[] input, float[] output)
      throws ProtectorException, SessionTimeoutException {
          String[] stringInput = coreproviderAdapter.convertToStringArray(input);
          String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_UTF8);
          String response = coreproviderAdapter.sendApiRequest(OPERATION_PROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
          ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  float[].class, ENCODING_UTF8);
          boolean success = result.isSuccess();
          float[] converted = (float[]) result.getConvertedArray();
          System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
          return success;
  }

  /**
   * Protect float using encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean protect(
      SessionObject sessionObj, String dataElementName, float[] input, byte[][] output)
      throws ProtectorException, SessionTimeoutException {
          String[] stringInput = coreproviderAdapter.convertToStringArray(input);
          String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_UTF8);
          String response = coreproviderAdapter.sendApiRequest(OPERATION_PROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
          ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  byte[][].class, ENCODING_UTF8);
          boolean success = result.isSuccess();
          byte[][] converted = (byte[][]) result.getConvertedArray();
          System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
          return success;
  }

  /**
   * Protect double using no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean protect(
      SessionObject sessionObj, String dataElementName, double[] input, double[] output)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_PROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  double[].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        double[] converted = (double[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Protect double using encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean protect(
      SessionObject sessionObj, String dataElementName, double[] input, byte[][] output)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_PROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  byte[][].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        byte[][] converted = (byte[][]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Protect java.util.Date using data type preservation.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean protect(
      SessionObject sessionObj,
      String dataElementName,
      java.util.Date[] input,
      java.util.Date[] output)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_PROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  java.util.Date[].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        java.util.Date[] converted = (java.util.Date[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Protect String using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean protect(
      SessionObject sessionObj, String dataElementName, String[] input, String[] output)
      throws ProtectorException, SessionTimeoutException {
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, input, null, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_PROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  String[].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        String[] converted = (String[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
      }

  /**
   * Protect String using encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean protect(
      SessionObject sessionObj, String dataElementName, String[] input, byte[][] output)
      throws ProtectorException, SessionTimeoutException {
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, input, null, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_PROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  byte[][].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        byte[][] converted = (byte[][]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Protect String using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @param externalIv Buffer containing data that will be used as external IV, when externalIv =
   *     null the value is ignored
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean protect(
      SessionObject sessionObj,
      String dataElementName,
      String[] input,
      String[] output,
      byte[] externalIv)
      throws ProtectorException, SessionTimeoutException {
      String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, input, externalIv, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_PROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  String[].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        String[] converted = (String[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Protect String using encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @param externalIv Buffer containing data that will be used as external IV, when externalIv =
   *     null the value is ignored
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean protect(
      SessionObject sessionObj,
      String dataElementName,
      String[] input,
      byte[][] output,
      byte[] externalIv)
      throws ProtectorException, SessionTimeoutException {
      String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, input, externalIv, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_PROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  byte[][].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        byte[][] converted = (byte[][]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Protect char using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean protect(
      SessionObject sessionObj, String dataElementName, char[][] input, char[][] output)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_PROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  char[][].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        char[][] converted = (char[][]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Protect char using encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean protect(
      SessionObject sessionObj, String dataElementName, char[][] input, byte[][] output)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_PROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  byte[][].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        byte[][] converted = (byte[][]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Protect char using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @param externalIv Buffer containing data that will be used as external IV, when externalIv =
   *     null the value is ignored
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean protect(
      SessionObject sessionObj,
      String dataElementName,
      char[][] input,
      char[][] output,
      byte[] externalIv)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, externalIv, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_PROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  char[][].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        char[][] converted = (char[][]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Protect char using encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @param externalIv Buffer containing data that will be used as external IV, when externalIv =
   *     null the value is ignored
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean protect(
      SessionObject sessionObj,
      String dataElementName,
      char[][] input,
      byte[][] output,
      byte[] externalIv)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, externalIv, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_PROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  byte[][].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        byte[][] converted = (byte[][]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
      }

  /**
   * Protect byte using all DE supported (Encryption, NoEncryption and DataType Preservation).
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @param ptyCharsets Charset that will used to understand byte encoding
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean protect(
      SessionObject sessionObj,
      String dataElementName,
      byte[][] input,
      byte[][] output,
      PTYCharset... ptyCharsets)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_BASE64);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_PROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  byte[][].class, ENCODING_BASE64);
        boolean success = result.isSuccess();
        byte[][] converted = (byte[][]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Protect byte using all DE supported (Encryption, NoEncryption and DataType Preservation).
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @param externalIv Buffer containing data that will be used as external IV, when externalIv =
   *     null the value is ignored
   * @param ptyCharsets Charset that will used to understand byte encoding
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean protect(
      SessionObject sessionObj,
      String dataElementName,
      byte[][] input,
      byte[][] output,
      byte[] externalIv,
      PTYCharset... ptyCharsets)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, externalIv, ENCODING_BASE64);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_PROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  byte[][].class, ENCODING_BASE64);
        boolean success = result.isSuccess();
        byte[][] converted = (byte[][]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Protect API supported tweak for FPE(FF1) algo Protect string using only FPE DE supported (Data
   * Type Preservation).
   *
   * @param sessionObj Obtained by Calling createSession method
   * @param dataElementName String conatining the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @param externalIv Buffer conatining data that will be used as external IV, when externalIv =
   *     null the value is ignored.
   * @param externalTweak Buffer conatining data that will be used as Tweak, when externalTweak =
   *     null the value is take as 0.
   * @return boolean If the operation is sucessful
   * @throws ProtectorException when input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean protect(
      SessionObject sessionObj,
      String dataElementName,
      String[] input,
      String[] output,
      byte[] externalIv,
      byte[] externalTweak)
      throws ProtectorException, SessionTimeoutException {
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, input, externalIv, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_PROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  String[].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        String[] converted = (String[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Unprotect short using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean unprotect(
      SessionObject sessionObj, String dataElementName, short[] input, short[] output)
      throws ProtectorException, SessionTimeoutException {
          String[] stringInput = coreproviderAdapter.convertToStringArray(input);
          String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_UTF8);
          String response = coreproviderAdapter.sendApiRequest(OPERATION_UNPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
          ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response, short[].class, ENCODING_UTF8);
          boolean success = result.isSuccess();
          short[] converted = (short[]) result.getConvertedArray();
          System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
          return success;
  }

  /**
   * Unprotect short using encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean unprotect(
      SessionObject sessionObj, String dataElementName, byte[][] input, short[] output)
      throws ProtectorException, SessionTimeoutException {
          String[] stringInput = coreproviderAdapter.convertToStringArray(input);
          for(int i=0;i<stringInput.length;i++){
              System.out.println("base64 input"+ stringInput[i]);
          }
          
          String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_BASE64);
          String response = coreproviderAdapter.sendApiRequest(OPERATION_UNPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
          ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response, short[].class, ENCODING_BASE64);
          boolean success = result.isSuccess();
          short[] converted = (short[]) result.getConvertedArray();
          System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
          return success;
  }

  /**
   * Unprotect short using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @param externalIv Buffer containing data that will be used as external IV, when externalIv =
   *     null the value is ignored
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean unprotect(
      SessionObject sessionObj,
      String dataElementName,
      short[] input,
      short[] output,
      byte[] externalIv)
      throws ProtectorException, SessionTimeoutException {
          String[] stringInput = coreproviderAdapter.convertToStringArray(input);
          String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, externalIv, ENCODING_UTF8);
          String response = coreproviderAdapter.sendApiRequest(OPERATION_UNPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
          ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response, short[].class, ENCODING_UTF8);
          boolean success = result.isSuccess();
          short[] converted = (short[]) result.getConvertedArray();
          System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
          return success;
  }

  /**
   * Unprotect short using encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @param externalIv Buffer containing data that will be used as external IV, when externalIv =
   *     null the value is ignored
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean unprotect(
      SessionObject sessionObj,
      String dataElementName,
      byte[][] input,
      short[] output,
      byte[] externalIv)
      throws ProtectorException, SessionTimeoutException {
      String[] stringInput = coreproviderAdapter.convertToStringArray(input);
          String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, externalIv, ENCODING_BASE64);
          String response = coreproviderAdapter.sendApiRequest(OPERATION_UNPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
          ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response, short[].class, ENCODING_BASE64);
          boolean success = result.isSuccess();
          short[] converted = (short[]) result.getConvertedArray();
          System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
          return success;
  }

  /**
   * Unprotect int using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean unprotect(
      SessionObject sessionObj, String dataElementName, int[] input, int[] output)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_UNPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response, int[].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        int[] converted = (int[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Unprotect int using encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean unprotect(
      SessionObject sessionObj, String dataElementName, byte[][] input, int[] output)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_BASE64);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_UNPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response, int[].class, ENCODING_BASE64);
        boolean success = result.isSuccess();
        int[] converted = (int[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Unprotect int using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @param externalIv Buffer containing data that will be used as external IV, when externalIv =
   *     null the value is ignored
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean unprotect(
      SessionObject sessionObj,
      String dataElementName,
      int[] input,
      int[] output,
      byte[] externalIv)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, externalIv, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_UNPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response, int[].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        int[] converted = (int[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Unprotect int using encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @param externalIv Buffer containing data that will be used as external IV, when externalIv =
   *     null the value is ignored
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean unprotect(
      SessionObject sessionObj,
      String dataElementName,
      byte[][] input,
      int[] output,
      byte[] externalIv)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, externalIv, ENCODING_BASE64);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_UNPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response, int[].class, ENCODING_BASE64);
        boolean success = result.isSuccess();
        int[] converted = (int[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Unprotect long using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean unprotect(
      SessionObject sessionObj, String dataElementName, long[] input, long[] output)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_UNPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response, long[].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        long[] converted = (long[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Unprotect long using encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean unprotect(
      SessionObject sessionObj, String dataElementName, byte[][] input, long[] output)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_BASE64);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_UNPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response, long[].class, ENCODING_BASE64);
        boolean success = result.isSuccess();
        long[] converted = (long[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Unprotect long using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @param externalIv Buffer containing data that will be used as external IV, when externalIv =
   *     null the value is ignored
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean unprotect(
      SessionObject sessionObj,
      String dataElementName,
      long[] input,
      long[] output,
      byte[] externalIv)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, externalIv, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_UNPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response, long[].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        long[] converted = (long[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Unprotect long using encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @param externalIv Buffer containing data that will be used as external IV, when externalIv =
   *     null the value is ignored
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean unprotect(
      SessionObject sessionObj,
      String dataElementName,
      byte[][] input,
      long[] output,
      byte[] externalIv)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, externalIv, ENCODING_BASE64);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_UNPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response, long[].class, ENCODING_BASE64);
        boolean success = result.isSuccess();
        long[] converted = (long[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Unprotect float using no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean unprotect(
      SessionObject sessionObj, String dataElementName, float[] input, float[] output)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_UNPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response, float[].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        float[] converted = (float[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Unprotect float using encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean unprotect(
      SessionObject sessionObj, String dataElementName, byte[][] input, float[] output)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_BASE64);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_UNPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response, float[].class, ENCODING_BASE64);
        boolean success = result.isSuccess();
        float[] converted = (float[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Unprotect double using no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean unprotect(
      SessionObject sessionObj, String dataElementName, double[] input, double[] output)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_UNPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response, double[].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        double[] converted = (double[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Unprotect double using encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean unprotect(
      SessionObject sessionObj, String dataElementName, byte[][] input, double[] output)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_BASE64);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_UNPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response, double[].class, ENCODING_BASE64);
        boolean success = result.isSuccess();
        double[] converted = (double[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Unprotect java.util.Date using data type preservation.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean unprotect(
      SessionObject sessionObj,
      String dataElementName,
      java.util.Date[] input,
      java.util.Date[] output)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_UNPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  java.util.Date[].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        java.util.Date[] converted = (java.util.Date[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Unprotect String using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean unprotect(
      SessionObject sessionObj, String dataElementName, String[] input, String[] output)
      throws ProtectorException, SessionTimeoutException {
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, input, null, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_UNPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  String[].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        String[] converted = (String[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
      }

  /**
   * Unprotect String using encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean unprotect(
      SessionObject sessionObj, String dataElementName, byte[][] input, String[] output)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_BASE64);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_UNPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  String[].class, ENCODING_BASE64);
        boolean success = result.isSuccess();
        String[] converted = (String[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Unprotect String using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @param externalIv Buffer containing data that will be used as external IV, when externalIv =
   *     null the value is ignored
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean unprotect(
      SessionObject sessionObj,
      String dataElementName,
      String[] input,
      String[] output,
      byte[] externalIv)
      throws ProtectorException, SessionTimeoutException {
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, input, externalIv, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_UNPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  String[].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        String[] converted = (String[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Unprotect String using encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @param externalIv Buffer containing data that will be used as external IV, when externalIv =
   *     null the value is ignored
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean unprotect(
      SessionObject sessionObj,
      String dataElementName,
      byte[][] input,
      String[] output,
      byte[] externalIv)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, externalIv, ENCODING_BASE64);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_UNPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  String[].class, ENCODING_BASE64);
        boolean success = result.isSuccess();
        String[] converted = (String[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Unprotect char using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean unprotect(
      SessionObject sessionObj, String dataElementName, char[][] input, char[][] output)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_UNPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  char[][].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        char[][] converted = (char[][]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Unprotect char using encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean unprotect(
      SessionObject sessionObj, String dataElementName, byte[][] input, char[][] output)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_BASE64);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_UNPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response, char[][].class, ENCODING_BASE64);
        boolean success = result.isSuccess();
        char[][] converted = (char[][]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Unprotect char using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @param externalIv Buffer containing data that will be used as external IV, when externalIv =
   *     null the value is ignored
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean unprotect(
      SessionObject sessionObj,
      String dataElementName,
      char[][] input,
      char[][] output,
      byte[] externalIv)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, externalIv, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_UNPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  char[][].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        char[][] converted = (char[][]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Unprotect char using encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @param externalIv Buffer containing data that will be used as external IV, when externalIv =
   *     null the value is ignored
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean unprotect(
      SessionObject sessionObj,
      String dataElementName,
      byte[][] input,
      char[][] output,
      byte[] externalIv)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, externalIv, ENCODING_BASE64);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_UNPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response, char[][].class, ENCODING_BASE64);
        boolean success = result.isSuccess();
        char[][] converted = (char[][]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Unprotect byte using all DE supported (Encryption, NoEncryption and DataType Preservation).
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @param ptyCharsets Charset that will used to understand byte encoding
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   * @throws SessionTimeoutException When session is invalid or has timedout
   */
  public boolean unprotect(
      SessionObject sessionObj,
      String dataElementName,
      byte[][] input,
      byte[][] output,
      PTYCharset... ptyCharsets)
      throws ProtectorException, SessionTimeoutException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, null, ENCODING_BASE64);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_UNPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  byte[][].class, ENCODING_BASE64);
        boolean success = result.isSuccess();
        byte[][] converted = (byte[][]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Unprotect byte using all DE supported (Encryption, NoEncryption and DataType Preservation).
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @param externalIv Buffer containing data that will be used as external IV, when externalIv =
   *     null the value is ignored
   * @param ptyCharsets Charset that will used to understand byte encoding
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean unprotect(
      SessionObject sessionObj,
      String dataElementName,
      byte[][] input,
      byte[][] output,
      byte[] externalIv,
      PTYCharset... ptyCharsets)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, stringInput, externalIv, ENCODING_BASE64);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_UNPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  byte[][].class, ENCODING_BASE64);
        boolean success = result.isSuccess();
        byte[][] converted = (byte[][]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Unprotect API supports Tweak for FPE(FF1) Unprotect String using FPE DE supported (Data Type
   * Preservation).
   *
   * @param sessionObj Obtained by calling createSession method
   * @param dataElementName String containing the data element name defined in policy
   * @param input Input array
   * @param output Result output array
   * @param externalIv Buffer containing data that will be used as external IV, when externalIv =
   *     null the value is ignored
   * @param externalTweak Buffer conatining data that will be used as externalTweak, when
   *     externalTweak = null the balue is 0.
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean unprotect(
      SessionObject sessionObj,
      String dataElementName,
      String[] input,
      String[] output,
      byte[] externalIv,
      byte[] externalTweak)
      throws ProtectorException, SessionTimeoutException {
        String jsonPayload = coreproviderAdapter.buildProtectPayload(sessionObj.getUser(), dataElementName, input, externalIv, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_UNPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  String[].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        String[] converted = (String[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Reprotect short using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param newDataElementName String containing the data element name defined in policy used to
   *     create the output data
   * @param oldDataElementName String containing the data element name defined in policy for the
   *     input data
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean reprotect(
      SessionObject sessionObj,
      String newDataElementName,
      String oldDataElementName,
      short[] input,
      short[] output)
      throws ProtectorException, SessionTimeoutException {
          String[] stringInput = coreproviderAdapter.convertToStringArray(input);
          String jsonPayload = coreproviderAdapter.buildReprotectPayload(sessionObj.getUser(), newDataElementName, oldDataElementName,stringInput, null, null, ENCODING_UTF8);
          String response = coreproviderAdapter.sendApiRequest(OPERATION_REPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
          ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  short[].class, ENCODING_UTF8);
          boolean success = result.isSuccess();
          short[] converted = (short[]) result.getConvertedArray();
          System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
          return success;
  }

  /**
   * Reprotect short using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param newDataElementName String containing the data element name defined in policy used to
   *     create the output data
   * @param oldDataElementName String containing the data element name defined in policy for the
   *     input data
   * @param input Input array
   * @param output Result output array
   * @param newExternalIv Buffer containing data that will be used as external IV on new data
   *     element, when newExternalIv = null the value is ignored
   * @param oldExternalIv Buffer containing data that will be used as external IV on old data
   *     element, when oldExternalIv = null the value is ignored
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean reprotect(
      SessionObject sessionObj,
      String newDataElementName,
      String oldDataElementName,
      short[] input,
      short[] output,
      byte[] newExternalIv,
      byte[] oldExternalIv)
      throws ProtectorException, SessionTimeoutException {
          String[] stringInput = coreproviderAdapter.convertToStringArray(input);
          String jsonPayload = coreproviderAdapter.buildReprotectPayload(sessionObj.getUser(), newDataElementName, oldDataElementName,stringInput, newExternalIv, oldExternalIv, ENCODING_UTF8);
          String response = coreproviderAdapter.sendApiRequest(OPERATION_REPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
          ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  short[].class, ENCODING_UTF8);
          boolean success = result.isSuccess();
          short[] converted = (short[]) result.getConvertedArray();
          System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
          return success;
  }

  /**
   * Reprotect int using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param newDataElementName String containing the data element name defined in policy used to
   *     create the output data
   * @param oldDataElementName String containing the data element name defined in policy for the
   *     input data
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean reprotect(
      SessionObject sessionObj,
      String newDataElementName,
      String oldDataElementName,
      int[] input,
      int[] output)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildReprotectPayload(sessionObj.getUser(), newDataElementName, oldDataElementName, stringInput, null, null, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_REPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  int[].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        int[] converted = (int[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Reprotect int using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param newDataElementName String containing the data element name defined in policy used to
   *     create the output data
   * @param oldDataElementName String containing the data element name defined in policy for the
   *     input data
   * @param input Input array
   * @param output Result output array
   * @param newExternalIv Buffer containing data that will be used as external IV on new data
   *     element, when newExternalIv = null the value is ignored
   * @param oldExternalIv Buffer containing data that will be used as external IV on old data
   *     element, when oldExternalIv = null the value is ignored
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean reprotect(
      SessionObject sessionObj,
      String newDataElementName,
      String oldDataElementName,
      int[] input,
      int[] output,
      byte[] newExternalIv,
      byte[] oldExternalIv)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildReprotectPayload(sessionObj.getUser(), newDataElementName, oldDataElementName, stringInput, newExternalIv, oldExternalIv, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_REPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  int[].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        int[] converted = (int[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Reprotect long using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param newDataElementName String containing the data element name defined in policy used to
   *     create the output data
   * @param oldDataElementName String containing the data element name defined in policy for the
   *     input data
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean reprotect(
      SessionObject sessionObj,
      String newDataElementName,
      String oldDataElementName,
      long[] input,
      long[] output)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildReprotectPayload(sessionObj.getUser(), newDataElementName, oldDataElementName, stringInput, null, null, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_REPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  long[].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        long[] converted = (long[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Reprotect long using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param newDataElementName String containing the data element name defined in policy used to
   *     create the output data
   * @param oldDataElementName String containing the data element name defined in policy for the
   *     input data
   * @param input Input array
   * @param output Result output array
   * @param newExternalIv Buffer containing data that will be used as external IV on new data
   *     element, when newExternalIv = null the value is ignored
   * @param oldExternalIv Buffer containing data that will be used as external IV on old data
   *     element, when oldExternalIv = null the value is ignored
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean reprotect(
      SessionObject sessionObj,
      String newDataElementName,
      String oldDataElementName,
      long[] input,
      long[] output,
      byte[] newExternalIv,
      byte[] oldExternalIv)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildReprotectPayload(sessionObj.getUser(), newDataElementName, oldDataElementName, stringInput, newExternalIv, oldExternalIv, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_REPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  long[].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        long[] converted = (long[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
       
  }

  /**
   * Reprotect float using no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param newDataElementName String containing the data element name defined in policy used to
   *     create the output data
   * @param oldDataElementName String containing the data element name defined in policy for the
   *     input data
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean reprotect(
      SessionObject sessionObj,
      String newDataElementName,
      String oldDataElementName,
      float[] input,
      float[] output)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildReprotectPayload(sessionObj.getUser(), newDataElementName, oldDataElementName, stringInput, null, null, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_REPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  float[].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        float[] converted = (float[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Reprotect double using no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param newDataElementName String containing the data element name defined in policy used to
   *     create the output data
   * @param oldDataElementName String containing the data element name defined in policy for the
   *     input data
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean reprotect(
      SessionObject sessionObj,
      String newDataElementName,
      String oldDataElementName,
      double[] input,
      double[] output)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildReprotectPayload(sessionObj.getUser(), newDataElementName, oldDataElementName, stringInput, null, null, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_REPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  double[].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        double[] converted = (double[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Reprotect date using data type preservation.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param newDataElementName String containing the data element name defined in policy used to
   *     create the output data
   * @param oldDataElementName String containing the data element name defined in policy for the
   *     input data
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean reprotect(
      SessionObject sessionObj,
      String newDataElementName,
      String oldDataElementName,
      java.util.Date[] input,
      java.util.Date[] output)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildReprotectPayload(sessionObj.getUser(), newDataElementName, oldDataElementName, stringInput, null, null, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_REPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  java.util.Date[].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        java.util.Date[] converted = (java.util.Date[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Reprotect String using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param newDataElementName String containing the data element name defined in policy used to
   *     create the output data
   * @param oldDataElementName String containing the data element name defined in policy for the
   *     input data
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean reprotect(
      SessionObject sessionObj,
      String newDataElementName,
      String oldDataElementName,
      String[] input,
      String[] output)
      throws ProtectorException, SessionTimeoutException {
        String jsonPayload = coreproviderAdapter.buildReprotectPayload(sessionObj.getUser(), newDataElementName, oldDataElementName, input, null, null, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_REPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  String[].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        String[] converted = (String[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Reprotect String using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param newDataElementName String containing the data element name defined in policy used to
   *     create the output data
   * @param oldDataElementName String containing the data element name defined in policy for the
   *     input data
   * @param input Input array
   * @param output Result output array
   * @param newExternalIv Buffer containing data that will be used as external IV on new data
   *     element, when newExternalIv = null the value is ignored
   * @param oldExternalIv Buffer containing data that will be used as external IV on old data
   *     element, when oldExternalIv = null the value is ignored
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean reprotect(
      SessionObject sessionObj,
      String newDataElementName,
      String oldDataElementName,
      String[] input,
      String[] output,
      byte[] newExternalIv,
      byte[] oldExternalIv)
      throws ProtectorException, SessionTimeoutException {
        String jsonPayload = coreproviderAdapter.buildReprotectPayload(sessionObj.getUser(), newDataElementName, oldDataElementName, input, newExternalIv, oldExternalIv, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_REPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  String[].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        String[] converted = (String[]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Reprotect char using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param newDataElementName String containing the data element name defined in policy used to
   *     create the output data
   * @param oldDataElementName String containing the data element name defined in policy for the
   *     input data
   * @param input Input array
   * @param output Result output array
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean reprotect(
      SessionObject sessionObj,
      String newDataElementName,
      String oldDataElementName,
      char[][] input,
      char[][] output)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildReprotectPayload(sessionObj.getUser(), newDataElementName, oldDataElementName, stringInput, null, null, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_REPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  char[][].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        char[][] converted = (char[][]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Reprotect char using data type preservation or no encryption data element.
   *
   * @param sessionObj Obtained by calling createSession method
   * @param newDataElementName String containing the data element name defined in policy used to
   *     create the output data
   * @param oldDataElementName String containing the data element name defined in policy for the
   *     input data
   * @param input Input array
   * @param output Result output array
   * @param newExternalIv Buffer containing data that will be used as external IV on new data
   *     element, when newExternalIv = null the value is ignored
   * @param oldExternalIv Buffer containing data that will be used as external IV on old data
   *     element, when oldExternalIv = null the value is ignored
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean reprotect(
      SessionObject sessionObj,
      String newDataElementName,
      String oldDataElementName,
      char[][] input,
      char[][] output,
      byte[] newExternalIv,
      byte[] oldExternalIv)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildReprotectPayload(sessionObj.getUser(), newDataElementName, oldDataElementName, stringInput, newExternalIv, oldExternalIv, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_REPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  char[][].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        char[][] converted = (char[][]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Reprotect byte using all DE supported (Encryption, NoEncryption and DataType Preservation).
   *
   * @param sessionObj Obtained by calling createSession method
   * @param newDataElementName String containing the data element name defined in policy used to
   *     create the output data
   * @param oldDataElementName String containing the data element name defined in policy for the
   *     input data
   * @param input Input array
   * @param output Result output array
   * @param ptyCharsets Charset that will used to understand byte encoding
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean reprotect(
      SessionObject sessionObj,
      String newDataElementName,
      String oldDataElementName,
      byte[][] input,
      byte[][] output,
      PTYCharset... ptyCharsets)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildReprotectPayload(sessionObj.getUser(), newDataElementName, oldDataElementName, stringInput, null, null, ENCODING_BASE64);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_REPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  byte[][].class, ENCODING_BASE64);
        boolean success = result.isSuccess();
        byte[][] converted = (byte[][]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Reprotect byte using all DE supported (Encryption, NoEncryption and DataType Preservation).
   *
   * @param sessionObj Obtained by calling createSession method
   * @param newDataElementName String containing the data element name defined in policy used to
   *     create the output data
   * @param oldDataElementName String containing the data element name defined in policy for the
   *     input data
   * @param input Input array
   * @param output Result output array
   * @param newExternalIv Buffer containing data that will be used as external IV on new data
   *     element, when newExternalIv = null the value is ignored
   * @param oldExternalIv Buffer containing data that will be used as external IV on old data
   *     element, when oldExternalIv = null the value is ignored
   * @param ptyCharsets Charset that will used to understand byte encoding
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean reprotect(
      SessionObject sessionObj,
      String newDataElementName,
      String oldDataElementName,
      byte[][] input,
      byte[][] output,
      byte[] newExternalIv,
      byte[] oldExternalIv,
      PTYCharset... ptyCharsets)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildReprotectPayload(sessionObj.getUser(), newDataElementName, oldDataElementName, stringInput, newExternalIv, oldExternalIv, ENCODING_BASE64);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_REPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  byte[][].class, ENCODING_BASE64);
        boolean success = result.isSuccess();
        byte[][] converted = (byte[][]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Reprotect API supported Tweak for FPE(FF1) Reprotect String using FPE DE supported (DataType
   * Preservation).
   *
   * @param sessionObj Obtained by calling createSession method
   * @param newDataElementName String containing the data element name defined in policy used to
   *     create the output data
   * @param oldDataElementName String containing the data element name defined in policy for the
   *     input data
   * @param input Input array
   * @param output Result output array
   * @param newExternalIv Buffer containing data that will be used as external IV on new data
   *     element, when newExternalIv = null the value is ignored
   * @param oldExternalIv Buffer containing data that will be used as external IV on old data
   *     element, when oldExternalIv = null the value is ignored
   * @param newExternalTweak Buffer conatining data that will be used as external Tweak on new data
   *     element.
   * @param oldExternalTweak Buffer conatining data that will be used as external Tweak on old data
   *     element.
   * @return boolean If the operation is successful
   * @throws ProtectorException When input is empty, null or when the policy is configured that way.
   * @throws SessionTimeoutException When session has expired or is invalid.
   */
  public boolean reprotect(
      SessionObject sessionObj,
      String newDataElementName,
      String oldDataElementName,
      String[] input,
      String[] output,
      byte[] newExternalIv,
      byte[] oldExternalIv,
      byte[] newExternalTweak,
      byte[] oldExternalTweak)
      throws ProtectorException, SessionTimeoutException {
        String[] stringInput = coreproviderAdapter.convertToStringArray(input);
        String jsonPayload = coreproviderAdapter.buildReprotectPayload(sessionObj.getUser(), newDataElementName, oldDataElementName, stringInput, newExternalIv, oldExternalIv, ENCODING_UTF8);
        String response = coreproviderAdapter.sendApiRequest(OPERATION_REPROTECT, instance.jwtToken, instance.apiKey, jsonPayload);
        ParseResult result = coreproviderAdapter.parseResultsToOutput(sessionObj, response,  byte[][].class, ENCODING_UTF8);
        boolean success = result.isSuccess();
        byte[][] converted = (byte[][]) result.getConvertedArray();
        System.arraycopy(converted, 0, output, 0, Math.min(output.length, converted.length));
        return success;
  }

  /**
   * Note: As the FlushAudits API is an advanced functionality, you must contact the Protegrity
   * Professional Services team for more information about its usage.
   *
   * @throws ProtectorException When configuration is invalid.
   */
  public void flushAudits() throws ProtectorException {
    coreproviderAdapter.flush();
  }
}
