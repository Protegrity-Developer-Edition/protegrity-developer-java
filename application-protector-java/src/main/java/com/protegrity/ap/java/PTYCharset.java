package com.protegrity.ap.java;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Encoding types for input data in protection operations.
 *
 * <p>The ptyCharsets parameter is mandatory for the data elements created with Unicode Gen2
 * tokenization method and the FPE encryption method. The encoding set for the ptyCharsets parameter
 * must match the encoding of the input data passed. The default value for the ptyCharsets parameter
 * is UTF-8.
 *
 * <h2>Supported Encodings:</h2>
 * <ul>
 *   <li>UTF-8
 *   <li>UTF-16LE
 *   <li>UTF-16BE
 * </ul>
 *
 * @author <a href="http://www.protegrity.com">Protegrity</a>
 * @since 1.0.0
 */
public enum PTYCharset {
  UTF8("UTF-8"), /* UTF-8 encoding */
  UTF16LE("UTF-16LE"), /* UTF-16LE encoding */
  UTF16BE("UTF-16BE"); /* UTF-16BE encoding */

  private PTYCharset(String encoding) {
    if (encoding == null) {
      Charset.defaultCharset();
    } else {
      Charset.forName(encoding);
    }
  }

  // This method return charset based on PTYCharset
  public static Charset getPTYCharset(PTYCharset... charset) {
    if (validateInputCharset(charset)) {
      PTYCharset ptycharset = charset[0];
      if (ptycharset.equals(UTF8)) {
        return StandardCharsets.UTF_8;
      } else if (ptycharset.equals(UTF16LE)) {
        return StandardCharsets.UTF_16LE;
      } else if (ptycharset.equals(UTF16BE)) {
        return StandardCharsets.UTF_16BE;
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  private static boolean validateInputCharset(PTYCharset... charset) {

    if (charset == null) {
      throw new IllegalArgumentException("Charset cannot be null");
    } else {
      if (charset.length > 1) {
        throw new IllegalArgumentException("The number of charset sent should be 1");
      } else if (charset.length == 1 && charset[0] == null) {
        throw new IllegalArgumentException("Charset cannot be null");
      } else if (charset.length == 1 && charset[0] != null) {
        return true;
      }
    }

    return false;
  }
}
