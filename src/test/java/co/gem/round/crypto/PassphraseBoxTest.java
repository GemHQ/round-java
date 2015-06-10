package co.gem.round.crypto;

import org.junit.Assert;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by Julian on 7/30/14.
 */
public class PassphraseBoxTest {
  private static String passphrase = "passphrase";
  private static String clearText = "Hello there!";

  @Test
  public void testEncryptAndDecrypt() throws NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, InvalidAlgorithmParameterException, BadPaddingException, NoSuchPaddingException, InvalidKeyException {
    EncryptedMessage encrypted = PassphraseBox.encrypt(passphrase, clearText);
    String decrypted = PassphraseBox.decrypt(passphrase, encrypted);

    Assert.assertEquals(clearText, decrypted);
  }

  private static String privateSeed = "tprv8ZgxMBicQKsPdNEYgZf36mKTrVaRwmZYvBZPYjo7WvCv8Y7gveBfEBtE29BrNu" +
      "MGeFkkXhZZe25XR5fVNHu2mMpD6wBZtnix5cMSenNEbAi";
  private static String salt = "84ec1df2cf8ee18f64972694b76d96d0";
  private static String iv = "4cee52b034f390a20e7cc6a6fc5d4fa82cdbb079e471269c";
  private static int iterations = 100000;
  private static String ciphertext = "b8e5691bc0c17b3429bedd819b9be296fe9409e" +
      "6eec437699e1f3792b713dbe62c18e29b50c5421387a28d062bc41b07d3bf27c34" +
      "179a590b212588f84e10369cec37b89a676b066f5893dbbaba263695db29e70a30" +
      "b0f8d3877960ba37c3da225cc23463b9623fb4292be3834a60ca8c79f237560670" +
      "0fd09cdbcbcd885fd";

  @Test
  public void testWalletDecrypt() throws NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, InvalidAlgorithmParameterException, BadPaddingException, NoSuchPaddingException, InvalidKeyException {
    EncryptedMessage encrypted = new EncryptedMessage();
    encrypted.ciphertext = ciphertext;
    encrypted.iv = iv;
    encrypted.salt = salt;
    encrypted.iterations = iterations;

    String result = PassphraseBox.decrypt(passphrase, encrypted);
    Assert.assertEquals(privateSeed, result);
  }

}