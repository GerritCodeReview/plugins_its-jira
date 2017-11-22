package com.googlesource.gerrit.plugins.its.jira;

import com.google.common.io.BaseEncoding;
import java.nio.charset.Charset;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Helps generate an encrypted password manually. */
public class Encrypt {
  private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
  private static final String ENCRYPTION_PASSWORD = "JIRA-PROJECT";
  private static Logger log = LoggerFactory.getLogger(Encrypt.class);

  private Encrypt() {}

  /**
   * @param text the text to decrypt
   * @return the decrypted text
   */
  public static String decrypt(String text) {
    try {
      BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
      textEncryptor.setPassword(ENCRYPTION_PASSWORD);
      if (text != null && !text.isEmpty()) return textEncryptor.decrypt(text);
    } catch (EncryptionOperationNotPossibleException e) {
      log.error(
          "Failed to decrypt {} \n Possible cause- this password was never encrytped using set ENCRYPTION_PASSWORD \n Cause:\n",
          text,
          e);
    }
    return text;
  }

  /**
   * Encrypt a text.
   *
   * <p>This method generates a different encrypted text each time, whether or not the input text is
   * the same.
   *
   * @param text the text to encrypt.
   * @return the encrypted text
   */
  static String encrypt(String text) {
    try {
      BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
      textEncryptor.setPassword(ENCRYPTION_PASSWORD);
      if (text != null && !text.isEmpty()) return textEncryptor.encrypt(text);

    } catch (EncryptionOperationNotPossibleException e) {
      log.error("Failed to encrypt {} Cause:\n", text, e);
    }
    return text;
  }

  /**
   * Encode a text using a Base64 algorithm.
   *
   * @param string the text to encode
   * @return the Base64 encoded text
   */
  static String encodeBase64(String string) {
    return BaseEncoding.base64().encode(string.getBytes(DEFAULT_CHARSET));
  }
}
