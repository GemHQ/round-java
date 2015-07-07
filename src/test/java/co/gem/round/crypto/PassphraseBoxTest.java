package co.gem.round.crypto;

import org.junit.Assert;
import org.junit.Test;
import org.spongycastle.crypto.InvalidCipherTextException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by Julian on 7/30/14.
 */
public class PassphraseBoxTest {
  private static String passphrase = "passphrase";
  private static String clearText = "0123456789abcdef";

  @Test
  public void testEncryptAndDecrypt() throws NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, InvalidAlgorithmParameterException, BadPaddingException, NoSuchPaddingException, InvalidKeyException, NoSuchProviderException, UnsupportedEncodingException, InvalidCipherTextException {
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
  public void testWalletDecrypt() throws NoSuchAlgorithmException, InvalidKeySpecException, IllegalBlockSizeException, InvalidAlgorithmParameterException, BadPaddingException, NoSuchPaddingException, InvalidKeyException, NoSuchProviderException, UnsupportedEncodingException, InvalidCipherTextException {
    EncryptedMessage encrypted = new EncryptedMessage();
    encrypted.ciphertext = "718877e7aed7ef43c8aefbfce3a856b4a85a092cafc88a85a22a14c7ce632ac3f83beef10ac0797441209039ebd947c2";
    encrypted.iv = "680546fd230d044778c9e7da09712946";
    encrypted.salt = "e713895c90c226cb4d46c4ba4ac60371";
    encrypted.iterations = 99307;

    String result = PassphraseBox.decrypt("veryveryveryveryverylongpassphrase", encrypted);
    System.out.println(result);
    Assert.assertEquals("hellohellohello!", result);
  }

}